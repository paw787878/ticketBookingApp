﻿package com.example.ticketbookingapp.domain

import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant


// TODO_PAWEL not null and such

// TODO_PAWEL move to separate files

@Embeddable
class User(
    var name: String,
    var surname: String,
)

@Entity
class Movie(
    val title: String,
    val lengthInMinutes: Int,
    @Id @GeneratedValue
    val id: Long = 0,
)

interface MovieRepository : JpaRepository<Movie, Long>

@Entity
class ScreeningRoom(
    val name: String,
    @Id @GeneratedValue
    val id: Long = 0,
){
    // TODO_PAWEL can you use metadata model here?
    @OneToMany(mappedBy = "screeningRoom")
    val seats: MutableSet<Seat> = HashSet()
}

interface ScreeningRoomRepository : JpaRepository<ScreeningRoom, Long>

// TODO_PAWEL czy nadpisywac equals?
@Entity
class MovieScreening(
    @ManyToOne(fetch = FetchType.LAZY)
    val movie: Movie,
    @ManyToOne(fetch = FetchType.LAZY)
    val screeningRoom: ScreeningRoom,
    val timeOfStart: Instant,
    val timeOfEnd: Instant,
    @Id @GeneratedValue
    val id: Long = 0,
    // TODO_PAWEL this does not modify in database, investigate
    @Version
    // TODO_PAWEL renaming does not help
    val version: Int = 0,
)

interface MovieScreeningRepository : JpaRepository<MovieScreening, Long>

@Entity
class Reservation(
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val movieScreening: MovieScreening,
    // TODO_PAWEL chyba jeszcze rodzaj miejsca
    @ManyToMany
    val seats: Set<Seat>,
    val expirationDate: Instant,
    @Id @GeneratedValue
    val id: Long = 0,
)

interface ReservationRepository : JpaRepository<Reservation, Long>

@Entity
class Seat(
    @ManyToOne(fetch = FetchType.LAZY)
    val screeningRoom: ScreeningRoom,
    val rowName: String,
    val columnName: String,
    @Id @GeneratedValue
    val id: Long = 0,
){
    @ManyToOne(fetch = FetchType.LAZY)
    var seatToLeft: Seat? = null
    @ManyToOne(fetch = FetchType.LAZY)
    var seatToRight: Seat? = null

    init {
        screeningRoom.seats.add(this)
    }
}

interface SeatRepository : JpaRepository<Seat, Long>
