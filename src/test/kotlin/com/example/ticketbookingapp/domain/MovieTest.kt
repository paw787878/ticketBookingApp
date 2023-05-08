package com.example.ticketbookingapp.domain

import com.example.ticketbookingapp.repositories.MovieScreeningRepository
import org.hibernate.Hibernate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
class MovieTest {

    @Autowired
    private lateinit var movieScreeningRepository: MovieScreeningRepository

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun isUsingLazyLoading() {
        val screeningOption = movieScreeningRepository.findById(1)
        assertTrue(!screeningOption.isEmpty)
        val screening = screeningOption.get()
        val proxiedMovie = screening.movie
        assertTrue(proxiedMovie::class.java != Movie::class.java)
        val movie = Hibernate.unproxy(proxiedMovie)
        assertTrue(movie::class.java == Movie::class.java)
    }
}
