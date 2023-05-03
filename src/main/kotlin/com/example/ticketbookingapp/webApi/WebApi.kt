package com.example.ticketbookingapp.webApi

import com.example.ticketbookingapp.dataTransferObject.ScreeningSelectionDTO
import com.example.ticketbookingapp.domain.MovieScreening
import com.example.ticketbookingapp.service.MovieScreeningService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// TODO_PAWEL should it be oher?
@CrossOrigin(origins= ["*"])
class WebApi(private val movieScreeningService: MovieScreeningService) {

    // TODO_PAWEL sort and add paging
    @GetMapping("/screenings", consumes = ["application/json"])
    fun getScreeningsInSelectedTime(dto: ScreeningSelectionDTO): List<MovieScreening> {
        val result = movieScreeningService.getScreeningsInPeriod(dto.minimalStartTime, dto.maximalEndTime)
        // TODO_PAWEL
        val a = 5
        return result
    }
}
