package com.example.ticketbookingapp.webApi.createReservation

import com.example.ticketbookingapp.domain.*
import com.example.ticketbookingapp.repositories.MovieScreeningRepository
import com.example.ticketbookingapp.repositories.SeatRepository
import com.example.ticketbookingapp.repositories.TicketTypeRepository
import com.example.ticketbookingapp.service.MovieReservationService
import com.example.ticketbookingapp.service.SeatAndTicketType
import com.example.ticketbookingapp.utils.findByIdOrClientError
import com.example.ticketbookingapp.webApi.createReservation.dto.ReservationRequestDto
import com.example.ticketbookingapp.webApi.createReservation.dto.ReservationResponseDto
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// cors would be changed in final app
@CrossOrigin(origins = ["*"])
class WebApiCreateReservation(
    private val movieReservationService: MovieReservationService,
    private val movieScreeningRepository: MovieScreeningRepository,
    private val seatRepository: SeatRepository,
    private val ticketTypeRepository: TicketTypeRepository,
) {

    @PostMapping("/createReservation", consumes = ["application/json"])
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional(rollbackFor = [Exception::class])
    fun createReservation(@RequestBody reservationData: ReservationRequestDto): ReservationResponseDto {
        val movieScreening = movieScreeningRepository.findByIdOrClientError(reservationData.movieScreeningId)
        val seatsAndTicketTypes = reservationData.seats.map { e ->
            SeatAndTicketType(
                seatRepository.findByIdOrClientError(e.seatId),
                ticketTypeRepository.findByIdOrClientError(e.ticketType)
            )
        }
        val reservationAndPrice = movieReservationService.createReservation(
            movieScreening,
            User(
                reservationData.name,
                reservationData.surname,
            ),
            seatsAndTicketTypes,
            Instant.now()
        )

        return ReservationResponseDto(reservationAndPrice.priceToPay, reservationAndPrice.reservation.expirationDate)
    }
}
