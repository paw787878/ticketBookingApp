package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.MovieScreeningAndRoomDto
import com.example.ticketbookingapp.dataTransferObject.MovieScreeningDto
import com.example.ticketbookingapp.dataTransferObject.SeatDto
import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MovieScreeningSeatsService(private val movieScreeningRepository: MovieScreeningRepository) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    // TODO_PAWEL shouldl those be read only transactions?
    @Transactional(rollbackOn = [Exception::class])
    fun getReservedSeats(movieScreening: MovieScreening): List<Seat> {
        val cb = entityManager.criteriaBuilder
        val criteria = cb.createQuery(Seat::class.java)

        val reservationRoot = criteria.from(Reservation::class.java)
        val screeningsRoot = reservationRoot.join(Reservation_.movieScreening)
        val seatRoot = reservationRoot.join(Reservation_.seats)
        criteria
            .select(seatRoot)
            .where(cb.equal(screeningsRoot, movieScreening))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }

    @Transactional(rollbackOn = [Exception::class])
    fun getSeatInfos(movieScreeningId: Long): MovieScreeningAndRoomDto {
        // TODO_PAWEL handle if invalid id
        return getSeatInfos(movieScreeningRepository.findById(movieScreeningId).get())
    }

    @Transactional(rollbackOn = [Exception::class])
    fun getSeatInfos(movieScreening: MovieScreening): MovieScreeningAndRoomDto {
        val reservedSeats = getReservedSeats(movieScreening).toSet()
        val allSeats = movieScreening.screeningRoom.seats

        // TODO_PAWEL i could do this work after closing transaction
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
