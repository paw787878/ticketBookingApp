﻿package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.domain.MovieScreeningRepository
import com.example.ticketbookingapp.domain.MovieScreening_
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MovieScreeningService(
    private val movieScreeningRepository: MovieScreeningRepository,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional
    fun getScreeningsInPeriod(
        beginningOfPeriod: Instant,
        endOfPeriod: Instant,
        offset: Int,
        limit: Int
    ): Pair<List<MovieScreening>, Long> {
        val cb = entityManager.criteriaBuilder

        fun <T> addWhere(
            criteria: CriteriaQuery<T>,
            i: Root<MovieScreening>,
        ) {
            criteria
                .where(
                    cb.and(
                        cb.greaterThan(i.get(MovieScreening_.timeOfStart), beginningOfPeriod),
                        cb.lessThan(i.get(MovieScreening_.timeOfEnd), endOfPeriod)
                    )
                )
        }

        val list: List<MovieScreening> = run {
            val criteria = cb.createQuery(MovieScreening::class.java)
            val i = criteria.from(MovieScreening::class.java)

            criteria.select(i)
            addWhere(
                criteria, i)
            val query = entityManager.createQuery(criteria)
            query.apply {
                firstResult = offset
                maxResults = limit
            }
            query.resultList
        }

        val count = kotlin.run {
            val criteria = cb.createQuery(Long::class.java)
            val i = criteria.from(MovieScreening::class.java)

            criteria.select(cb.count(i))
            addWhere(
                criteria.select(cb.count(i)), i)
            val query = entityManager.createQuery(criteria)
            query.singleResult
        }

        return Pair(list, count)
    }
}
