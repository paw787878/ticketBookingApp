package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class MovieScreeningSeatsService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getReservedSeats(movieScreening: MovieScreening): List<Seat> {
        val cb = entityManager.criteriaBuilder
        val criteria = cb.createQuery(Seat::class.java)

        val reservationSeatRoot = criteria.from(ReservationSeat::class.java)
        val seatRoot = reservationSeatRoot.join(ReservationSeat_.seat)
        val reservationRoot = reservationSeatRoot.join(ReservationSeat_.reservation)
        val screeningsRoot = reservationRoot.join(Reservation_.movieScreening)

        criteria
            .select(seatRoot)
            .where(cb.equal(screeningsRoot, movieScreening))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getSeatInfos(movieScreening: MovieScreening): List<SeatAndAvailability> {
        val reservedSeats = getReservedSeats(movieScreening).toSet()
        val allSeats = movieScreening.screeningRoom.seats

        return allSeats.map { seat ->
            SeatAndAvailability(seat, seat !in reservedSeats)
        }
    }
}

data class SeatAndAvailability(
    val seat: Seat,
    val available: Boolean,
)
