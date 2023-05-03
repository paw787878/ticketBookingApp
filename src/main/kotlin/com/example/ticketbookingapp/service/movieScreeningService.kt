package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.domain.MovieScreeningRepository
import com.example.ticketbookingapp.domain.MovieScreening_
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MovieScreeningService(
    private val movieScreeningRepository: MovieScreeningRepository,
) {
    @PersistenceContext private lateinit var entityManager: EntityManager

    @Transactional
    fun getScreeningsInPeriod(beginningOfPeriod: Instant, endOfPeriod: Instant): List<MovieScreening> {
        val cb = entityManager.criteriaBuilder
        val criteria = cb.createQuery(MovieScreening::class.java)
        val i = criteria.from(MovieScreening::class.java)
        criteria
            .select(i)
            .where(cb.and(
                cb.greaterThan(i.get(MovieScreening_.timeOfStart), beginningOfPeriod),
                cb.lessThan(i.get(MovieScreening_.timeOfEnd), endOfPeriod)
            ))
        val query = entityManager.createQuery(criteria)
        return query.resultList
    }
}
