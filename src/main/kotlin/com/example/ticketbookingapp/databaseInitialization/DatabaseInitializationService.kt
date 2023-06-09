﻿package com.example.ticketbookingapp.databaseInitialization

import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class DatabaseInitializationService(
    private val databaseInitializationUtils: DatabaseInitializationUtils,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class])
    fun initializeDatabase() {
        databaseInitializationUtils.initializeTicketType("adult", BigDecimal("25"))
        databaseInitializationUtils.initializeTicketType("student", BigDecimal("18"))
        databaseInitializationUtils.initializeTicketType("child", BigDecimal("12.5"))

        val room1 = databaseInitializationUtils.initializeRoom("room 1", 3, 3)
        val room2 = databaseInitializationUtils.initializeRoom("room 2", 15, 15)
        val room3 = databaseInitializationUtils.initializeRoom("room 3", 20, 20)

        val movie1 = databaseInitializationUtils.initializeMovie("Schindler's List ęóąśłżźćń", 100)
        val movie2 = databaseInitializationUtils.initializeMovie("The Shawshank Redemption", 120)
        val movie3 = databaseInitializationUtils.initializeMovie("The Lord of the Rings: The Return of the King", 140)

        initializeScreening(room1, movie1, 10)
        initializeScreening(room1, movie2, 20)

        initializeScreening(room2, movie2, 9)
        initializeScreening(room2, movie3, 21)

        initializeScreening(room3, movie3, 11)
        initializeScreening(room3, movie1, 19)

        // to make sure ids of movie screenings are accessible
        entityManager.flush()
    }

    private fun initializeScreening(
        screeningRoom: ScreeningRoom,
        movie: Movie,
        hour: Int
    ): MovieScreening {
        return databaseInitializationUtils.initializeScreening(
            screeningRoom,
            movie,
            LocalDateTime.of(2100, 1, 1, hour, 0, 0),
            30
        )
    }
}
