package com.example.ticketbookingapp.webApi.screenings

import org.junit.jupiter.api.Assertions.*

import com.example.ticketbookingapp.webApi.screenings.dto.ScreeningSelectionResponseDto
import com.fasterxml.jackson.databind.ObjectMapper

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebApiMovieScreeningsControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Transactional(rollbackFor = [Exception::class])
    fun returnsScreenings() {
        val requestBuilder = MockMvcRequestBuilders.get ("/api/screenings")
            .accept(MediaType.APPLICATION_JSON)
            .param("minimalStartTime", "2000-01-01T07:00:00.000+00:00")
            .param("maximalEndTime", "2000-01-02T23:40:00.000+00:00")
            .param("offset", "0")
            .param("limit", "1000")
        val result = mockMvc.perform (requestBuilder).andReturn()
        val contentAsString = result.response.contentAsString
        val resultObject = objectMapper.readValue(contentAsString, ScreeningSelectionResponseDto::class.java)

        assertEquals(resultObject.elements.count(), 6)
    }
}
