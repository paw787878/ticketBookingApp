package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.ReservationRequestDto
import com.example.ticketbookingapp.dataTransferObject.SeatTicketTypeDto
import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DatabaseInitializationUtils(
    private val movieRepository: MovieRepository,
    private val screeningRoomRepository: ScreeningRoomRepository,
    private val seatRepository: SeatRepository,
    private val movieScreeningRepository: MovieScreeningRepository,
    private val reservationService: MovieReservationService,
    private val seatsService: MovieScreeningSeatsService,
    private val ticketTypeRepository: TicketTypeRepository,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun initializeTicketType(name: String, price: BigDecimal): TicketType {
        return TicketType(name, price).let { ticketTypeRepository.save(it) }
    }

    fun initializeRoom(roomName: String, numberOfRows: Int, numberOfColumns: Int): ScreeningRoom {
        val screeningRoom = ScreeningRoom(roomName).let { screeningRoomRepository.save(it) }
        val rowNames = ('A'..'Z').toList()
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

    fun initializeMovie(
        movieName: String, movieLengthInMinutes: Int
    ): Movie {
        return Movie(movieName, movieLengthInMinutes)
            .let { movieRepository.save(it) }
    }

    fun initializeScreening(
        screeningRoom: ScreeningRoom,
        movie: Movie,
        dateTimeOfStart: LocalDateTime,
        commercialsLengthInMinutes: Int,
    ): MovieScreening {
        val instantOfStart = dateTimeOfStart
            .toInstant(ZoneOffset.UTC)
        val instantOfEnd = instantOfStart.plusSeconds((commercialsLengthInMinutes + movie.lengthInMinutes).toLong() * 60)

        return MovieScreening(movie, screeningRoom, instantOfStart, instantOfEnd)
            .let { movieScreeningRepository.save(it) }
    }
}
