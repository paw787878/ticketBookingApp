package com.example.ticketbookingapp.webApi

import com.example.ticketbookingapp.dataTransferObject.MovieScreeningDto
import com.example.ticketbookingapp.dataTransferObject.ScreeningSelectionRequestDto
import com.example.ticketbookingapp.dataTransferObject.ScreeningSelectionResponseDto
import com.example.ticketbookingapp.service.MovieScreeningService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// TODO_PAWEL should it be oher?
@CrossOrigin(origins = ["*"])
class WebApi(private val movieScreeningService: MovieScreeningService) {

    // TODO_PAWEL sort and add paging
    @GetMapping("/screenings", consumes = ["application/json"])
    fun getScreeningsInSelectedTime(dto: ScreeningSelectionRequestDto): ScreeningSelectionResponseDto {
        val (list, count) = movieScreeningService.getScreeningsInPeriod(
            dto.minimalStartTime,
            dto.maximalEndTime,
            dto.offset, dto.limit
        )

        return ScreeningSelectionResponseDto(
            list.map { e ->
                MovieScreeningDto(e.id, e.movie.title, starts = e.timeOfStart, ends = e.timeOfEnd)
            },
            count)
    }
}
