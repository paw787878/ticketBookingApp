package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.Exceptions.*
import com.example.ticketbookingapp.config.CustomConfigProperties
import com.example.ticketbookingapp.dataTransferObject.ReservationRequestDto
import com.example.ticketbookingapp.dataTransferObject.ReservationResponseDto
import com.example.ticketbookingapp.dataTransferObject.SeatTicketTypeDto
import com.example.ticketbookingapp.domain.MovieScreeningRepository
import com.example.ticketbookingapp.domain.ScreeningRoomRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
internal class MovieReservationServiceTest {

    @Autowired
    lateinit var reservationService: MovieReservationService;

    @Autowired
    lateinit var seatsService: MovieScreeningSeatsService

    @Autowired
    lateinit var roomRepository: ScreeningRoomRepository

    @Autowired
    lateinit var movieScreeningRepository: MovieScreeningRepository

    @Autowired
    lateinit var customConfigProperties: CustomConfigProperties

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun reservationApiTests() {
        val movieScreening = movieScreeningRepository.findById(1).get()
        val seats = seatsService.getSeatInfos(movieScreening)
        val seat_5a = seats.seats.find { e -> e.rowName == "A" && e.columnName == "5" }!!
        val seat_5b = seats.seats.find { e -> e.rowName == "B" && e.columnName == "5" }!!
        val seat_4a = seats.seats.find { e -> e.rowName == "A" && e.columnName == "4" }!!
        val seat_3a = seats.seats.find { e -> e.rowName == "A" && e.columnName == "3" }!!

        val timeInDistantPast = LocalDateTime.of(1999, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)

        assertThrows<ClientResponseExceptionEntityIdIsWrong> { doReservation(listOf(-1), timeInDistantPast) }
        assertThrows<ClientResponseExceptionEntityNoSeatsInReservation> { doReservation(listOf(), timeInDistantPast) }
        assertThrows<ClientResponseExceptionTwoSameSeatsInReservation> {
            doReservation(
                listOf(seat_5a.id, seat_5a.id),
                timeInDistantPast
            )
        }
        assertThrows<ClientResponseExceptionSelectedReservedSeat> {
            doReservation(
                listOf(seat_4a.id),
                timeInDistantPast
            )
        }
        assertThrows<ClientResponseExceptionSelectedSeatFromWrongScreeningRoom> {
            doReservation(
                listOf(
                    roomRepository.findById(
                        2
                    ).get().seats.first().id
                ), timeInDistantPast
            )
        }

        assertThrows<ClientResponseExceptionWouldLeaveSingleSeatSurrounded> {
            doReservation(
                listOf(seat_3a.id),
                timeInDistantPast
            )
        }

        val response = doReservation(listOf(seat_5a.id, seat_5b.id), timeInDistantPast)
        assertEquals(response.totalAmountToPay.toDouble(), 50.0)
        assertEquals(response.reservationExpirationTime, movieScreening.timeOfStart)
    }

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun reservationPlacedTooLate() {
        val movieScreening = movieScreeningRepository.findById(1).get()
        val seats = seatsService.getSeatInfos(movieScreening)
        val seat_5a = seats.seats.find { e -> e.rowName == "A" && e.columnName == "5" }!!

        val minimalTimeBetweenBookingAndScreeningStart =
            customConfigProperties.minimalTimeBetweenBookingAndScreeningStart

        assertThrows<ClientResponseExceptionReservationIsPlacedTooLate> {
            doReservation(
                listOf(seat_5a.id),
                movieScreening.timeOfStart.minusSeconds((60).toLong() * (minimalTimeBetweenBookingAndScreeningStart - 1))
            )
        }

        doReservation(
            listOf(seat_5a.id),
            LocalDateTime.of(2000, 1, 1, 0, 0).toInstant(ZoneOffset.UTC)
                .minusSeconds((60).toLong() * minimalTimeBetweenBookingAndScreeningStart)
        )
    }

    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
    fun doReservation(seatIds: List<Long>, time: Instant): ReservationResponseDto {
        return reservationService.createReservation(
            ReservationRequestDto(1, "Pawel", "Surname",
                seatIds.map { seat -> SeatTicketTypeDto(seat, 1) }
            ),
            time
        )
    }
}
