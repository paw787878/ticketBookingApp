package com.example.ticketbookingapp.webApi.createReservation

import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.domain.Seat
import com.example.ticketbookingapp.exceptions.ApiError
import org.junit.jupiter.api.Assertions.*

import com.example.ticketbookingapp.repositories.MovieScreeningRepository
import com.example.ticketbookingapp.webApi.createReservation.dto.ReservationRequestDto
import com.example.ticketbookingapp.webApi.createReservation.dto.ReservationResponseDto
import com.example.ticketbookingapp.webApi.createReservation.dto.SeatTicketTypeDto

import com.fasterxml.jackson.databind.ObjectMapper

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebApiCreateReservationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var screeningRepository: MovieScreeningRepository

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun performsReservation() {
        val screening = screeningRepository.findById(4).get()
        val seat = screening.screeningRoom.seats.find { e -> e.columnName == "5" && e.rowName == "A" }!!

        run {
            val result = runRequest("Pawel", screening, seat)
            val contentAsString = result.contentAsString
            val resultObject = objectMapper.readValue(contentAsString, ReservationResponseDto::class.java)

            assertEquals(resultObject.reservationExpirationTime, screening.timeOfStart)
        }

        run {
            val result = runRequest("Pawel", screening, seat)
            val contentAsString = result.contentAsString
            val resultObject = objectMapper.readValue(contentAsString, ApiError::class.java)

            assertEquals(resultObject.message, "Selected already reserved seat with id 5")
        }
    }

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun performsValidation() {
        val screening = screeningRepository.findById(4).get()
        val seat = screening.screeningRoom.seats.find { e -> e.columnName == "5" && e.rowName == "A" }!!


        run {
            val result = runRequest("Pa", screening, seat)
            val contentAsString = result.contentAsString
            val resultObject = objectMapper.readValue(contentAsString, ApiError::class.java)

            assertEquals(resultObject.message, "Pa should satisfy size must be between 3 and 255")
        }
    }

    private fun runRequest(name: String, screening: MovieScreening, seat: Seat): MockHttpServletResponse {
        val requestBuilder = MockMvcRequestBuilders.post ("/api/createReservation")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ReservationRequestDto(
                movieScreeningId = screening.id,
                name = name,
                surname = "Surname",
                seats = listOf(SeatTicketTypeDto(seat.id, 1))

            )))
        return mockMvc.perform(requestBuilder).andReturn().response
    }
}
