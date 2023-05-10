package com.example.ticketbookingapp.exceptions

import org.springframework.http.HttpStatus

abstract class ClientResponseException : RuntimeException() {
    open val responseStatus = HttpStatus.BAD_REQUEST

    abstract fun messageImplementation(): String

    override val message: String
        get() = messageImplementation()
}

class ClientResponseExceptionEntityIdIsWrong(val id: Long, private val entityType: Class<*>) : ClientResponseException() {
    override fun messageImplementation() = "There is no ${entityType.simpleName} with id = $id"
}

class ClientResponseExceptionEntityNoSeatsInReservation : ClientResponseException() {
    override fun messageImplementation() = "Reservation need to reserve at least 1 seat"
}

class ClientResponseExceptionTwoSameSeatsInReservation(val id: Long) : ClientResponseException() {
    override fun messageImplementation() = "Two same seats in reservation, both have id $id"
}

class ClientResponseExceptionSelectedReservedSeat(val id: Long) : ClientResponseException() {
    override fun messageImplementation() = "Selected already reserved seat with id $id"
}

class ClientResponseExceptionSelectedSeatFromWrongScreeningRoom(val id: Long) : ClientResponseException() {
    override fun messageImplementation() = "Selected seat with id $id is from wrong screening room"
}

class ClientResponseExceptionWouldLeaveSingleSeatSurrounded(val id: Long) : ClientResponseException() {
    override fun messageImplementation() = "Seat with id $id would be surrounded by reserved seats"
}

class ClientResponseExceptionReservationIsPlacedTooLate(private val timeInMinutes: Int) : ClientResponseException() {
    override fun messageImplementation() = "You can book only until $timeInMinutes before screening starts"
}
