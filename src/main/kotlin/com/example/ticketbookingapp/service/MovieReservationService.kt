package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.ReservationRequestDto
import com.example.ticketbookingapp.dataTransferObject.ReservationResponseDto
import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MovieReservationService(
    private val screeningSeatsService: MovieScreeningSeatsService,
    private val movieScreeningRepository: MovieScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservationRepository: ReservationRepository,
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackOn = [Exception::class])
    fun createReservation(reservationData: ReservationRequestDto): ReservationResponseDto {
        // TODO_PAWEL what if null
        val movieScreening = movieScreeningRepository.findById(reservationData.movieScreeningId).get()
        entityManager.lock(movieScreening, LockModeType.OPTIMISTIC_FORCE_INCREMENT)

        val roomSeats = movieScreening.screeningRoom.seats
        val reservedSeats = screeningSeatsService.getReservedSeats(movieScreening).toSet()

        val chosenSeats = buildSet {
            for (seatId in reservationData.seats) {
                // TODO_PAWEL handle fail
                val newSeat = seatRepository.findById(seatId).get()
                // TODO_PAWEL handle fail
                require(!contains(newSeat))

                add(newSeat)
            }
        }

        for (chosenSeat in chosenSeats) {
            require(chosenSeat !in reservedSeats)
            require(chosenSeat in roomSeats)
            fun checkNeighbour(neighbour: Seat?) {
                neighbour ?: return

                fun isOrWillBeReserved(seat: Seat?) =
                    seat?.let { it in reservedSeats || it in chosenSeats } ?: false

                // TODO_PAWEL this thorws

                require(
                    !(!isOrWillBeReserved(neighbour) &&
                            isOrWillBeReserved(neighbour.seatToLeft) &&
                            isOrWillBeReserved(neighbour.seatToRight))
                )
            }

            checkNeighbour(chosenSeat.seatToLeft)
            checkNeighbour(chosenSeat.seatToRight)
        }

        // TODO_PAWEL check if it is up to 15 minuts before screening

        val reservation = Reservation(
            User(reservationData.name, reservationData.surname),
            movieScreening,
            chosenSeats,
            // TODO_PAWEL is this time ok?
            movieScreening.timeOfStart
        )
            .let { reservationRepository.save(it) }

        // TODO_PAWEL calculate amount to pay
        return ReservationResponseDto(0.toBigDecimal(), reservation.expirationDate)
    }
}
