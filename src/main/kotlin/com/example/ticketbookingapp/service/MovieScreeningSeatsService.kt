package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.domain.MovieScreening_
import com.example.ticketbookingapp.domain.Seat
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class MovieScreeningSeatsService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    // TODO_PAWEL shouldl those be read only transactions?
    @Transactional(rollbackOn = [Exception::class])
    fun getAllSeats(movieScreening: MovieScreening) {
        val cb = entityManager.criteriaBuilder
        val criteria = cb.createQuery(Seat::class.java)
        val movieScreeningRoot = criteria.from(MovieScreening::class.java)
        val seatRoot = criteria.from()

    }
}
