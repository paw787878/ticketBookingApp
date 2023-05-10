package com.example.ticketbookingapp.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Entity
class TicketType(
    @Column(nullable = false)
    @get:Size(max = 255)
    val name: String,

    @Column(nullable = false)
    val price: BigDecimal,

    @Id @GeneratedValue
    val id: Long = 0,
)
