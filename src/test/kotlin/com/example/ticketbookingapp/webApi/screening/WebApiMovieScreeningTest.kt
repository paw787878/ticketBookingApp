package com.example.ticketbookingapp.webApi.screening

import com.example.ticketbookingapp.webApi.screening.dto.MovieScreeningAndRoomDto
import org.junit.jupiter.api.Assertions.*

import com.fasterxml.jackson.databind.ObjectMapper

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebApiMovieScreeningTest {
    @Autowired
    private lateinit var mockMvc: MockMvc;

    @Autowired
    private lateinit var objectMapper: ObjectMapper;

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun returnsFirstScreening() {
        val requestBuilder = MockMvcRequestBuilders.get ("/api/screening")
            .accept(MediaType.APPLICATION_JSON)
            .param("screeningId", "1")
        val result = mockMvc.perform(requestBuilder).andReturn();
        val contentAsString = result.response.contentAsString
        val resultObject = objectMapper.readValue(contentAsString, MovieScreeningAndRoomDto::class.java)

        assertEquals(resultObject.movieScreening.title, "Schindler's List")
    }
}
