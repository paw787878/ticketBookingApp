package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.MovieScreeningAndRoomDto
import com.example.ticketbookingapp.dataTransferObject.MovieScreeningDto
import com.example.ticketbookingapp.dataTransferObject.SeatDto
import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.utils.findByIdOrClientError
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class MovieScreeningSeatsService(private val movieScreeningRepository: MovieScreeningRepository) {

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
    fun getSeatInfos(movieScreeningId: Long): MovieScreeningAndRoomDto {
        return getSeatInfos(movieScreeningRepository.findByIdOrClientError(movieScreeningId))
    }

    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getSeatInfos(movieScreening: MovieScreening): MovieScreeningAndRoomDto {
        val reservedSeats = getReservedSeats(movieScreening).toSet()
        val allSeats = movieScreening.screeningRoom.seats

        val seatInfos = allSeats.map { seat ->
            SeatDto(seat.rowName, seat.columnName, seat.id, seat !in reservedSeats)
        }

        return MovieScreeningAndRoomDto(
            MovieScreeningDto(movieScreening.id, movieScreening.movie.title, movieScreening.timeOfStart, movieScreening.timeOfEnd),
            movieScreening.screeningRoom.name,
            seatInfos
        )
    }
}
