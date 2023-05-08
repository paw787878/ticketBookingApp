package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreeningRepository
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

    @Autowired
    lateinit var movieScreeningRepository: MovieScreeningRepository

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun checkNumberOfSeats() {
        val movieScreening = movieScreeningRepository.findById(1).get()
        val movieInfos = movieScreeningSeatsService.getSeatInfos(movieScreening)

        val reservedCount = movieInfos.count { e -> !e.available }

        assertEquals(reservedCount, 10)
    }
}
