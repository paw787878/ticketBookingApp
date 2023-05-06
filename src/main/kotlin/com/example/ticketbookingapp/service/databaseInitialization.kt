package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.dataTransferObject.ReservationRequestDto
import com.example.ticketbookingapp.dataTransferObject.SeatTicketTypeDto
import com.example.ticketbookingapp.domain.*
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DatabaseInitializationService(
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

    // TODO_PAWEL i think it needs to specify some exceptions or esle it is wrong
    @Transactional(rollbackOn = [Exception::class])
    fun initializeDatabase() {
        val adultTicketType = initializeTicketType("adult", BigDecimal("25"))
        val studentTicketType = initializeTicketType("student", BigDecimal("18"))
        val childTicketType = initializeTicketType("child", BigDecimal("12.5"))

        val room1 = initializeRoom("room 1", 10, 10)
        val room2 = initializeRoom("room 2", 15, 15)
        val room3 = initializeRoom("room 3", 20, 20)

        val movie1 = initializeMovie("Schindler's List ęóąśłżźćń", 100)
        val movie2 = initializeMovie("The Shawshank Redemption", 120)
        val movie3 = initializeMovie("The Lord of the Rings: The Return of the King", 140)

        val screening_1_1 = initializeScreening(room1, movie1, 10)
        val screening_1_2 = initializeScreening(room1, movie2, 20)

        initializeScreening(room2, movie2, 9)
        initializeScreening(room2, movie3, 21)

        initializeScreening(room3, movie3, 11)
        initializeScreening(room3, movie1, 19)

        // to make sure ids of movie screenings are accessible
        entityManager.flush()

        reservationService.createReservation(ReservationRequestDto(
            screening_1_1.id,
            "Name",
            "Surname",
            screening_1_1.screeningRoom.seats
                .filter { seat -> seat.columnName == "3" }
                .map { e -> SeatTicketTypeDto(e.id, studentTicketType.id) }
        ))

        reservationService.createReservation(ReservationRequestDto(
            screening_1_1.id,
            "Namee",
            "Surnamee",
            screening_1_1.screeningRoom.seats
                .filter { seat -> seat.columnName == "6" }
                .map { e -> SeatTicketTypeDto(e.id, adultTicketType.id) }
        ))

        reservationService.createReservation(ReservationRequestDto(
            screening_1_2.id,
            "Nameee",
            "Surnameee",
            screening_1_2.screeningRoom.seats
                .filter { seat -> seat.columnName == "7" }
                .map { e -> SeatTicketTypeDto(e.id, childTicketType.id) }
        ))
    }

    private fun initializeTicketType(name: String, price: BigDecimal): TicketType {
        return TicketType(name, price).let { ticketTypeRepository.save(it) }
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
