package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.domain.MovieScreeningRepository
import com.example.ticketbookingapp.domain.MovieScreening_
import com.example.ticketbookingapp.domain.Movie_
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MovieScreeningService(
    private val movieScreeningRepository: MovieScreeningRepository,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getScreeningsInPeriod(
        beginningOfPeriod: Instant,
        endOfPeriod: Instant,
        offset: Int,
        limit: Int
    ): Pair<List<MovieScreening>, Long> {

        val cb = entityManager.criteriaBuilder

        fun <T> CriteriaQuery<T>.addWhereInInterval(i: Root<MovieScreening>) =
            where(
                cb.and(
                    cb.greaterThanOrEqualTo(i.get(MovieScreening_.timeOfStart), beginningOfPeriod),
                    cb.lessThanOrEqualTo(i.get(MovieScreening_.timeOfEnd), endOfPeriod)
                )
            )

        val list: List<MovieScreening> = run {
            val criteria = cb.createQuery(MovieScreening::class.java)
            val i = criteria.from(MovieScreening::class.java)

            criteria
                .select(i)
                .addWhereInInterval(i)
                .orderBy(
                    cb.asc(i.get(MovieScreening_.movie).get(Movie_.title)),
                    cb.asc(i.get(MovieScreening_.timeOfStart))
                )

            val query = entityManager.createQuery(criteria)
            query.apply {
                firstResult = offset
                maxResults = limit
            }
            query.resultList
        }

        val count = run {
            val criteria = cb.createQuery(Long::class.java)
            val i = criteria.from(MovieScreening::class.java)

            criteria
                .select(cb.count(i))
                .addWhereInInterval(i)

            val query = entityManager.createQuery(criteria)
            query.singleResult
        }

        return Pair(list, count)
    }
}
