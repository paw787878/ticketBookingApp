package com.example.ticketbookingapp.webApi

import com.example.ticketbookingapp.dataTransferObject.ScreeningSelectionDTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(
    path = ["/api"],
    produces = ["application/json"]
)
// TODO_PAWEL should it be oher?
@CrossOrigin(origins= ["*"])
class WebApi {

    @GetMapping("/screenings", consumes = ["application/json"])
    fun getScreeningsInSelectedTime(dto: ScreeningSelectionDTO): Int {
        // TODO_PAWEL
        val a = 5;
        return 5
    }
}
