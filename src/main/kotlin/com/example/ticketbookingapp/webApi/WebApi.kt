package com.example.ticketbookingapp.webApi

import com.example.ticketbookingapp.dataTransferObject.*
import com.example.ticketbookingapp.service.MovieReservationService
import com.example.ticketbookingapp.service.MovieScreeningSeatsService
import com.example.ticketbookingapp.service.MovieScreeningService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// cors would be changed in final app
@CrossOrigin(origins = ["*"])
class WebApi(
    private val movieScreeningService: MovieScreeningService,
    private val seatsService: MovieScreeningSeatsService,
    private val movieReservationService: MovieReservationService,
) {

    @PostMapping("/createReservation", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(@RequestBody reservationData: ReservationRequestDto): ReservationResponseDto {
        return movieReservationService.createReservation(reservationData, Instant.now())
    }
}
