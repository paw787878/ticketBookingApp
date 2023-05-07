package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.ReservationRequestDto
import com.example.ticketbookingapp.dataTransferObject.ReservationResponseDto
import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.utils.findByIdOrClientError
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class MovieReservationService(
    private val screeningSeatsService: MovieScreeningSeatsService,
    private val movieScreeningRepository: MovieScreeningRepository,
    private val seatRepository: SeatRepository,
    private val reservationRepository: ReservationRepository,
    private val ticketTypeRepository: TicketTypeRepository,
    private val reservationSeatRepository: ReservationSeatRepository,
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class])
    fun createReservation(reservationData: ReservationRequestDto): ReservationResponseDto {
        require(reservationData.seats.isNotEmpty())
        // TODO_PAWEL also handle ze max 15 minut przed seansem tylko mozna
        val movieScreening = movieScreeningRepository.findByIdOrClientError(reservationData.movieScreeningId)
        entityManager.lock(movieScreening, LockModeType.OPTIMISTIC_FORCE_INCREMENT)

        val roomSeats = movieScreening.screeningRoom.seats
        val reservedSeats = screeningSeatsService.getReservedSeats(movieScreening).toSet()

        val chosenSeats = buildSet {
            for (seatTicket in reservationData.seats) {
                val newSeat = seatRepository.findByIdOrClientError(seatTicket.seatId)
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
            // TODO_PAWEL is this time ok?
            movieScreening.timeOfStart
        )
            .let { reservationRepository.save(it) }

        var amountToPay = 0.toBigDecimal()
        for (ticketSeat in reservationData.seats) {
            val ticketType = ticketTypeRepository.findByIdOrClientError(ticketSeat.ticketType)
            val seat = seatRepository.findByIdOrClientError(ticketSeat.seatId)
            ticketTypeRepository
            ReservationSeat(reservation, seat, ticketType).let { reservationSeatRepository.save(it) }
            amountToPay += ticketType.price
        }

        return ReservationResponseDto(amountToPay, reservation.expirationDate)
    }
}
