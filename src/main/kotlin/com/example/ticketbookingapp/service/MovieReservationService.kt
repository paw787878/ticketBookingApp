package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.exceptions.*
import com.example.ticketbookingapp.config.CustomConfigProperties
import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.repositories.ReservationRepository
import com.example.ticketbookingapp.repositories.ReservationSeatRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import jakarta.validation.Valid
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.math.BigDecimal
import java.time.Instant

@Service
@Validated
class MovieReservationService(
    private val screeningSeatsService: MovieScreeningSeatsService,
    private val reservationRepository: ReservationRepository,
    private val reservationSeatRepository: ReservationSeatRepository,
    private val customConfigProperties: CustomConfigProperties,
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class])
    fun createReservation(
        movieScreening: MovieScreening,
        @Valid user: User,
        seatsAndTicketTypes: List<SeatAndTicketType>,
        currentTime: Instant
    ): ReservationAndPrice {

        if (seatsAndTicketTypes.isEmpty()) {
            throw ClientResponseExceptionEntityNoSeatsInReservation()
        }

        requireReservationIsNotPlacedTooLate(movieScreening, currentTime)

        entityManager.lock(movieScreening, LockModeType.OPTIMISTIC_FORCE_INCREMENT)

        val roomSeats = movieScreening.screeningRoom.seats
        val reservedSeats = screeningSeatsService.getReservedSeats(movieScreening).toSet()

        val chosenSeats = buildSet {
            for (seatTicket in seatsAndTicketTypes) {
                val newSeat = seatTicket.seat
                if (contains(newSeat)) {
                    throw ClientResponseExceptionTwoSameSeatsInReservation(newSeat.id)
                }

                add(newSeat)
            }
        }

        for (chosenSeat in chosenSeats) {
            if (chosenSeat in reservedSeats) {
                throw ClientResponseExceptionSelectedReservedSeat(chosenSeat.id)
            }
            if (chosenSeat !in roomSeats) {
                throw ClientResponseExceptionSelectedSeatFromWrongScreeningRoom(chosenSeat.id)
            }
        }

        requireNoEmptySeatLeftBetweenReservedSeats(chosenSeats, reservedSeats)

        val reservation = Reservation(
            user,
            movieScreening,
            movieScreening.timeOfStart
        )
            .let { reservationRepository.save(it) }

        var amountToPay = 0.toBigDecimal()
        for (ticketSeat in seatsAndTicketTypes) {
            val ticketType = ticketSeat.ticketType
            val seat = ticketSeat.seat
            ReservationSeat(reservation, seat, ticketType).let { reservationSeatRepository.save(it) }
            amountToPay += ticketType.price
        }

        return ReservationAndPrice(reservation, amountToPay)
    }

    private fun requireNoEmptySeatLeftBetweenReservedSeats(
        chosenSeats: Set<Seat>,
        reservedSeats: Set<Seat>,
    ) {
        val futureReservedSeats = chosenSeats.union(reservedSeats)
        for (chosenSeat in chosenSeats) {

            chosenSeat.seatToLeft?.let {
                requireIsNotEmptySeatSurroundedByReserved(it, futureReservedSeats)
            }
            chosenSeat.seatToRight?.let {
                requireIsNotEmptySeatSurroundedByReserved(it, futureReservedSeats)
            }
        }
    }

    private fun requireIsNotEmptySeatSurroundedByReserved(
        seat: Seat,
        futureReservedSeats: Set<Seat>,
    ) {
        fun isOrWillBeReserved(seat: Seat?) =
            seat?.let { it in futureReservedSeats } ?: false

        if (!isOrWillBeReserved(seat) &&
            isOrWillBeReserved(seat.seatToLeft) &&
            isOrWillBeReserved(seat.seatToRight)
        ) {
            throw ClientResponseExceptionWouldLeaveSingleSeatSurrounded(seat.id)
        }
    }

    private fun requireReservationIsNotPlacedTooLate(
        movieScreening: MovieScreening,
        currentTime: Instant
    ) {
        val minimalTimeBetweenBookingAndScreeningStart =
            customConfigProperties.minimalTimeBetweenBookingAndScreeningStart
        val maximalTimeWhenOneCanBook = movieScreening.timeOfStart.minusSeconds(
            (60).toLong() * minimalTimeBetweenBookingAndScreeningStart
        )
        if (currentTime > maximalTimeWhenOneCanBook) {
            throw ClientResponseExceptionReservationIsPlacedTooLate(minimalTimeBetweenBookingAndScreeningStart)
        }
    }
}

data class SeatAndTicketType(
    val seat: Seat,
    val ticketType: TicketType,
)

data class ReservationAndPrice(
    val reservation: Reservation,
    val priceToPay: BigDecimal,
)
