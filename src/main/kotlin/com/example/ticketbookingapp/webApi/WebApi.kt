package com.example.ticketbookingapp.webApi

import com.example.ticketbookingapp.dataTransferObject.*
import com.example.ticketbookingapp.service.MovieScreeningSeatsService
import com.example.ticketbookingapp.service.MovieScreeningService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// TODO_PAWEL should it be oher?
@CrossOrigin(origins = ["*"])
class WebApi(
    private val movieScreeningService: MovieScreeningService,
    private val seatsService: MovieScreeningSeatsService,
) {

    // TODO_PAWEL sort and add paging
    @GetMapping("/screenings")
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

    @GetMapping("/screening")
    // TODO_PAWEL handle errors in transactions
    fun getScreeningInfo(dto: ScreeningInfoRequestDto): MovieScreeningAndRoomDto {
        return seatsService.getSeatInfos(dto.screeningId)
    }
}
