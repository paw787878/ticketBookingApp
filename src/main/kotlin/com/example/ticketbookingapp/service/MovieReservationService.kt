﻿package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.exceptions.*
import com.example.ticketbookingapp.config.CustomConfigProperties
import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.repositories.ReservationRepository
import com.example.ticketbookingapp.repositories.ReservationSeatRepository
import com.example.ticketbookingapp.repositories.TicketTypeRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class MovieReservationService(
    private val screeningSeatsService: MovieScreeningSeatsService,
    private val reservationRepository: ReservationRepository,
    private val ticketTypeRepository: TicketTypeRepository,
    private val reservationSeatRepository: ReservationSeatRepository,
    private val customConfigProperties: CustomConfigProperties,
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class])
    fun createReservation(movieScreening: MovieScreening, user: User, seatsAndTicketTypes: List<SeatTicketType>, currentTime: Instant): ReservationAndPrice {
        if (seatsAndTicketTypes.isEmpty()) {
            throw ClientResponseExceptionEntityNoSeatsInReservation()
        }
        run {
            val minimalTimeBetweenBookingAndScreeningStart =
                customConfigProperties.minimalTimeBetweenBookingAndScreeningStart
            val maximalTimeWhenOneCanBook = movieScreening.timeOfStart.minusSeconds(
                (60).toLong() * minimalTimeBetweenBookingAndScreeningStart
            )
            if (currentTime > maximalTimeWhenOneCanBook) {
                throw ClientResponseExceptionReservationIsPlacedTooLate(minimalTimeBetweenBookingAndScreeningStart)
            }
        }
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
            fun checkNeighbour(neighbour: Seat?) {
                neighbour ?: return

                fun isOrWillBeReserved(seat: Seat?) =
                    seat?.let { it in reservedSeats || it in chosenSeats } ?: false

                if (!isOrWillBeReserved(neighbour) &&
                    isOrWillBeReserved(neighbour.seatToLeft) &&
                    isOrWillBeReserved(neighbour.seatToRight)
                ) {
                    throw ClientResponseExceptionWouldLeaveSingleSeatSurrounded(neighbour.id)
                }
            }

            checkNeighbour(chosenSeat.seatToLeft)
            checkNeighbour(chosenSeat.seatToRight)
        }

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
            ticketTypeRepository
            ReservationSeat(reservation, seat, ticketType).let { reservationSeatRepository.save(it) }
            amountToPay += ticketType.price
        }

        return ReservationAndPrice(reservation, amountToPay)
    }
}

data class SeatTicketType(
    val seat: Seat,
    val ticketType: TicketType,
)

data class ReservationAndPrice(
    val reservation: Reservation,
    val priceToPay: BigDecimal,
)
