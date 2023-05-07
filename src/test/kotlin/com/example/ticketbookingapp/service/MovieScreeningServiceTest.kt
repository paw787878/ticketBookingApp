package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.MovieScreening
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
internal class MovieScreeningServiceTest {

    @Autowired
    lateinit var movieScreeningService: MovieScreeningService;

    fun getInstantFromHour(hour: Int, minute: Int): Instant {
        return LocalDateTime.of(2000, 1, 1, hour, minute, 0)
            .toInstant(ZoneOffset.UTC)
    }

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun selectAllScreeningsMakeSureTheyAreSorted() {

        val (screenings, count) = movieScreeningService.getScreeningsInPeriod(getInstantFromHour(1, 0),
            getInstantFromHour(23, 30), 0, Int.MAX_VALUE)

        assertEquals(count, 6)
        assertEquals(count, screenings.count().toLong())

        fun compareScreenings(movieScreening1: MovieScreening, movieScreening2: MovieScreening): Int {
            val cmp1 = movieScreening1.movie.title.compareTo(movieScreening2.movie.title)
            if (cmp1 != 0) {
                return cmp1
            }

            return movieScreening1.timeOfStart.compareTo(movieScreening2.timeOfStart)
        }

        for (i in 1 until count) {
            assertTrue(compareScreenings(screenings[i.toInt() - 1], screenings[i.toInt()]) < 0)
        }
    }


    fun getNumberOfScreeningsInPeriod(startTime: Instant, endTime: Instant): Int {
        val (screenings, count) = movieScreeningService.getScreeningsInPeriod(startTime,
            endTime, 0, Int.MAX_VALUE)
        return screenings.count()
    }

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun checkEdgeCasesInSelection() {

        val startTimeLow = getInstantFromHour(9, 0)
        val startTimeHigh = getInstantFromHour(9, 1)

        val endTimeLow = getInstantFromHour(22, 59)
        val endTimeHigh = getInstantFromHour(23, 0)

        assertEquals(getNumberOfScreeningsInPeriod(startTimeLow, endTimeLow), 5)
        assertEquals(getNumberOfScreeningsInPeriod(startTimeLow, endTimeHigh), 6)
        assertEquals(getNumberOfScreeningsInPeriod(startTimeHigh, endTimeLow), 4)
        assertEquals(getNumberOfScreeningsInPeriod(startTimeHigh, endTimeHigh), 5)
    }
}
