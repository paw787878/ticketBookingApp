package com.example.ticketbookingapp.databaseInitialization

import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.service.MovieReservationService
import com.example.ticketbookingapp.service.SeatTicketType
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class DatabaseInitializationForTest(
    private val reservationService: MovieReservationService,
    private val databaseInitializationUtils: DatabaseInitializationUtils,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Transactional(rollbackFor = [Exception::class])
    fun initializeDatabase() {
        val adultTicketType = databaseInitializationUtils.initializeTicketType("adult", BigDecimal("25"))
        val studentTicketType = databaseInitializationUtils.initializeTicketType("student", BigDecimal("18"))
        val childTicketType = databaseInitializationUtils.initializeTicketType("child", BigDecimal("12.5"))

        val room1 = databaseInitializationUtils.initializeRoom("room 1", 5, 5)
        val room2 = databaseInitializationUtils.initializeRoom("room 2", 10, 10)
        val room3 = databaseInitializationUtils.initializeRoom("room 3", 15, 15)

        val movie1 = databaseInitializationUtils.initializeMovie("Schindler's List", 90)
        val movie2 = databaseInitializationUtils.initializeMovie("The Shawshank Redemption", 90)
        val movie3 = databaseInitializationUtils.initializeMovie("The Lord of the Rings: The Return of the King", 90)

        val screening_1_1 = initializeScreening(room1, movie1, 10)
        val screening_1_2 = initializeScreening(room1, movie2, 20)

        val screening_2_2 = initializeScreening(room2, movie2, 9)
        initializeScreening(room2, movie3, 21)

        initializeScreening(room3, movie3, 11)
        initializeScreening(room3, movie1, 19)

        // to make sure ids of movie screenings are accessible
        entityManager.flush()

        val instantInThePast = LocalDateTime.of(1999, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)

        reservationService.createReservation(
            screening_1_1,
            User("Name", "Surname"),
            screening_1_1.screeningRoom.seats
                .filter { seat -> seat.columnName == "1" }
                .map { e -> SeatTicketType(e, studentTicketType) },
            instantInThePast
        )

        reservationService.createReservation(
            screening_1_1,
            User(
                "Namee",
                "Surnamee",
            ),
            screening_1_1.screeningRoom.seats
                .filter { seat -> seat.columnName == "4" }
                .map { e -> SeatTicketType(e, adultTicketType) },
            instantInThePast
        )

        reservationService.createReservation(
            screening_1_2,
            User(
                "Nameee",
                "Surnameee",
            ),
            screening_1_2.screeningRoom.seats
                .filter { seat -> seat.columnName == "5" }
                .map { e -> SeatTicketType(e, childTicketType) },
            instantInThePast
        )

        reservationService.createReservation(
            screening_2_2,
            User(
                "Nameee",
                "Surnameee",
            ),
            screening_2_2.screeningRoom.seats
                .filter { seat -> seat.columnName == "2" }
                .map { e -> SeatTicketType(e, childTicketType) },
            instantInThePast
        )
    }

    private fun initializeScreening(
        screeningRoom: ScreeningRoom,
        movie: Movie,
        hour: Int,
    ): MovieScreening {
        return databaseInitializationUtils.initializeScreening(
            screeningRoom,
            movie,
            LocalDateTime.of(2000, 1, 1, hour, 0, 0),
            30
        )
    }
}
