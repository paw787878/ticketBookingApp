package com.example.ticketbookingapp.webApi.screenings

import com.example.ticketbookingapp.service.MovieReservationService
import com.example.ticketbookingapp.service.MovieScreeningSeatsService
import com.example.ticketbookingapp.service.MovieScreeningService
import com.example.ticketbookingapp.webApi.dto.MovieScreeningDto
import com.example.ticketbookingapp.webApi.screenings.dto.ScreeningSelectionRequestDto
import com.example.ticketbookingapp.webApi.screenings.dto.ScreeningSelectionResponseDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// cors would be changed in final app
@CrossOrigin(origins = ["*"])
class WebApiMovieScreenings(
    private val movieScreeningService: MovieScreeningService,
    private val seatsService: MovieScreeningSeatsService,
    private val movieReservationService: MovieReservationService,
) {

    @GetMapping("/screenings")
    fun getScreeningsInSelectedTime(dto: ScreeningSelectionRequestDto): ScreeningSelectionResponseDto {
        val (list, count) = movieScreeningService.getScreeningsInPeriod(
            dto.minimalStartTime,
            dto.maximalEndTime,
            dto.offset,
            dto.limit
        )

        return ScreeningSelectionResponseDto(
            list.map { e ->
                MovieScreeningDto(e.id, e.movie.title, starts = e.timeOfStart, ends = e.timeOfEnd)
            },
            count
        )
    }
}
