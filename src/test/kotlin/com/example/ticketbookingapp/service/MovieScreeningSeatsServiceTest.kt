package com.example.ticketbookingapp.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
internal class MovieScreeningSeatsServiceTest {

    @Autowired
    lateinit var movieScreeningSeatsService: MovieScreeningSeatsService

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun checkNumberOfSeats() {
        val movieInfos = movieScreeningSeatsService.getSeatInfos(1)

        val reservedCount = movieInfos.seats.count { e -> !e.available }

        assertEquals(reservedCount, 10)
    }
}
