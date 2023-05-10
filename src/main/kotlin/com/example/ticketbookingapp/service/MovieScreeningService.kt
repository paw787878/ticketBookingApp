package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
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
class MovieScreeningService {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getScreeningsInPeriod(
        beginningOfPeriod: Instant,
        endOfPeriod: Instant,
        offset: Int,
        limit: Int
    ): FoundMovieScreeningsAndTotalNumberOfScreenings {

        val cb = entityManager.criteriaBuilder

        fun <T> CriteriaQuery<T>.addWhereInInterval(movieScreeningRoot: Root<MovieScreening>) =
            where(
                cb.and(
                    cb.greaterThanOrEqualTo(movieScreeningRoot.get(MovieScreening_.timeOfStart), beginningOfPeriod),
                    cb.lessThanOrEqualTo(movieScreeningRoot.get(MovieScreening_.timeOfEnd), endOfPeriod)
                )
            )

        val list: List<MovieScreening> = run {
            val criteria = cb.createQuery(MovieScreening::class.java)
            val movieScreeningRoot = criteria.from(MovieScreening::class.java)

            criteria
                .select(movieScreeningRoot)
                .addWhereInInterval(movieScreeningRoot)
                .orderBy(
                    cb.asc(movieScreeningRoot.get(MovieScreening_.movie).get(Movie_.title)),
                    cb.asc(movieScreeningRoot.get(MovieScreening_.timeOfStart)),
                    cb.asc(movieScreeningRoot.get(MovieScreening_.id))
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

        return FoundMovieScreeningsAndTotalNumberOfScreenings(list, count.toInt())
    }
}

data class FoundMovieScreeningsAndTotalNumberOfScreenings(
    val movieScreenings: List<MovieScreening>,
    val totalNumberOfScreenings: Int,
)
