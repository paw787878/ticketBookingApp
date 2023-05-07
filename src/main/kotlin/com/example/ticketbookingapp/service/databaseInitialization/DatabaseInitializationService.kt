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

@Service
class DatabaseInitializationService(
    private val reservationService: MovieReservationService,
    private val databaseInitializationUtils: DatabaseInitializationUtils,
) {
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    // TODO_PAWEL i think it needs to specify some exceptions or esle it is wrong
    @Transactional(rollbackFor = [Exception::class])
    fun initializeDatabase() {
        val adultTicketType = databaseInitializationUtils.initializeTicketType("adult", BigDecimal("25"))
        val studentTicketType = databaseInitializationUtils.initializeTicketType("student", BigDecimal("18"))
        val childTicketType = databaseInitializationUtils.initializeTicketType("child", BigDecimal("12.5"))

        val room1 = databaseInitializationUtils.initializeRoom("room 1", 10, 10)
        val room2 = databaseInitializationUtils.initializeRoom("room 2", 15, 15)
        val room3 = databaseInitializationUtils.initializeRoom("room 3", 20, 20)

        val movie1 = databaseInitializationUtils.initializeMovie("Schindler's List ęóąśłżźćń", 100)
        val movie2 = databaseInitializationUtils.initializeMovie("The Shawshank Redemption", 120)
        val movie3 = databaseInitializationUtils.initializeMovie("The Lord of the Rings: The Return of the King", 140)

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

    private fun initializeScreening(
        screeningRoom: ScreeningRoom,
        movie: Movie,
        hour: Int): MovieScreening {
        return databaseInitializationUtils.initializeScreening(screeningRoom, movie, LocalDateTime.of(2000, 1, 1, hour, 0, 0))
    }
}
