package com.example.ticketbookingapp.webApi.screening

import com.example.ticketbookingapp.domain.MovieScreeningRepository
import com.example.ticketbookingapp.service.MovieScreeningSeatsService
import com.example.ticketbookingapp.utils.findByIdOrClientError
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// cors would be changed in final app
@CrossOrigin(origins = ["*"])
class WebApiMovieScreening(
    private val seatsService: MovieScreeningSeatsService,
    private val screeningRepository: MovieScreeningRepository,
) {

    @GetMapping("/screening")
    @Transactional(rollbackFor = [Exception::class], readOnly = true)
    fun getScreeningInfo(dto: ScreeningInfoRequestDto): MovieScreeningAndRoomDto {
        val screening = screeningRepository.findByIdOrClientError(dto.screeningId)
        val seatAvailabilities = seatsService.getSeatInfos(screening)

        val movie = screening.movie
        return MovieScreeningAndRoomDto(
            MovieScreeningDto(
                movie.id,
                movie.title,
                screening.timeOfStart,
                screening.timeOfEnd,
            ),
            screening.screeningRoom.name,
            seatAvailabilities.map { seatAvailability ->
                val seat = seatAvailability.seat
                SeatDto(seat.rowName, seat.columnName, seat.id, seatAvailability.available)
            }
        )
    }
}
