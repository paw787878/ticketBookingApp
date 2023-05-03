package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.*
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class DatabaseInitializationService(
    private val movieRepository: MovieRepository,
    private val screeningRoomRepository: ScreeningRoomRepository,
    private val seatRepository: SeatRepository,
    private val movieScreeningRepository: MovieScreeningRepository
) {
    // TODO_PAWEL i think it needs to specify some exceptions or esle it is wrong
    @Transactional
    fun initializeDatabase() {
        val room1 = initializeRoom("room 1", 10, 10)
        val room2 = initializeRoom("room 2", 15, 15)
        val room3 = initializeRoom("room 3", 20, 20)

        val movie1 = initializeMovie("Schindler's List", 100)
        val movie2 = initializeMovie("The Shawshank Redemption", 120)
        val movie3 = initializeMovie("The Lord of the Rings: The Return of the King", 140)

        initializeScreening(room1, movie1, 10)
        initializeScreening(room1, movie2, 20)

        initializeScreening(room2, movie2, 9)
        initializeScreening(room2, movie3, 21)

        initializeScreening(room3, movie3, 11)
        initializeScreening(room3, movie1, 19)
    }

    private fun initializeRoom(roomName: String, numberOfRows: Int, numberOfColumns: Int): ScreeningRoom {
        val screeningRoom = ScreeningRoom(roomName).let { screeningRoomRepository.save(it) }
        val rowNames = ('A'..'Z').toList()
        // TODO_PAWEL we could have corridors in rooms
        val seatsArray = buildList {
            repeat(numberOfRows) { row ->
                add(buildList {
                    repeat(numberOfColumns) { column ->
                        add(
                            Seat(
                                screeningRoom,
                                rowNames[row].toString(),
                                (column + 1).toString()
                            ).let { seatRepository.save(it) }
                        )
                    }
                })
            }
        }

        for (row in seatsArray) {
            for ((rowIndex, seat) in row.withIndex()) {
                seat.seatToLeft = row.getOrNull(rowIndex - 1)
                seat.seatToRight = row.getOrNull(rowIndex + 1)
            }
        }

        return screeningRoom
    }

    private fun initializeMovie(
        movieName: String, movieLengthInMinutes: Int): Movie {
        return Movie(movieName, movieLengthInMinutes)
            .let { movieRepository.save(it) }
    }

    private fun initializeScreening(screeningRoom: ScreeningRoom, movie: Movie, hour: Int): MovieScreening {
        val instantOfStart = LocalDateTime.of(2000, 1, 1, hour, 0, 0)
            .toInstant(ZoneOffset.UTC)
        val commercialsLength = 30
        val instantOfEnd = instantOfStart.plusSeconds((commercialsLength + movie.lengthInMinutes).toLong() * 60)

        return MovieScreening(movie, screeningRoom, instantOfStart, instantOfEnd)
            .let { movieScreeningRepository.save(it) }
    }
}
