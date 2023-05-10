package com.example.ticketbookingapp.domain

import jakarta.persistence.*

@Entity
class ReservationSeat(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val reservation: Reservation,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val seat: Seat,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val ticketType: TicketType,

    @Id @GeneratedValue
    val id: Long = 0,
)
