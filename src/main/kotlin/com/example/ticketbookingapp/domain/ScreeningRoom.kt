package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
class ScreeningRoom(
    @Column(nullable = false)
    @get:Size(max = 255)
    val name: String,

    @Id @GeneratedValue
    val id: Long = 0,
){
    @OneToMany(mappedBy = "screeningRoom")
    val seats: MutableSet<Seat> = HashSet()
}
