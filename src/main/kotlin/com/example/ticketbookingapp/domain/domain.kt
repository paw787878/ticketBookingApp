package com.example.ticketbookingapp.domain

import com.example.ticketbookingapp.domain.validation.CapitalizedWordOrTwoCapitalizedWordsWithHyphen
import com.example.ticketbookingapp.domain.validation.FirstLetterIsCapital
import jakarta.persistence.*
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.Instant

// TODO_PAWEL move to separate files

@Embeddable
class User(
    @Column(nullable = false)
    @get:Pattern(regexp="[a-zA-ZAęóąśłżźćńĘÓĄŚŁŻŹĆŃ]*", message="Must contain only letters")
    @get:Size(min = 3, max = 255)
    @get:FirstLetterIsCapital
    var name: String,

    @Column(nullable = false)
    @get:Pattern(regexp="[a-zA-ZAęóąśłżźćńĘÓĄŚŁŻŹĆŃ-]*", message="Must contain only letters and hyphens")
    @get:CapitalizedWordOrTwoCapitalizedWordsWithHyphen
    @get:Size(min = 3, max = 255)
    var surname: String,
)

@Entity
class Movie(
    @Column(nullable = false)
    @get:Size(max = 255)
    val title: String,

    @Column(nullable = false)
    val lengthInMinutes: Int,

    @Id @GeneratedValue
    val id: Long = 0,
)

interface MovieRepository : JpaRepository<Movie, Long>

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

interface ScreeningRoomRepository : JpaRepository<ScreeningRoom, Long>

@Entity
class MovieScreening(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val movie: Movie,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val screeningRoom: ScreeningRoom,

    @Column(nullable = false)
    val timeOfStart: Instant,

    @Column(nullable = false)
    val timeOfEnd: Instant,

    @Id @GeneratedValue
    val id: Long = 0,
    @Version
    val version: Int = 0,
)

interface MovieScreeningRepository : JpaRepository<MovieScreening, Long>

@Entity
class Reservation(
    @get:Valid
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val movieScreening: MovieScreening,

    @Column(nullable = false)
    val expirationDate: Instant,

    @Id @GeneratedValue
    val id: Long = 0,
)

interface ReservationRepository : JpaRepository<Reservation, Long>

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

interface ReservationSeatRepository : JpaRepository<ReservationSeat, Long>

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

interface TicketTypeRepository : JpaRepository<TicketType, Long>

@Entity
class Seat(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    val screeningRoom: ScreeningRoom,

    @Column(nullable = false)
    @get:Size(max = 255)
    val rowName: String,

    @Column(nullable = false)
    @get:Size(max = 255)
    val columnName: String,

    @Id @GeneratedValue
    val id: Long = 0,
){
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    var seatToLeft: Seat? = null
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    var seatToRight: Seat? = null

    init {
        screeningRoom.seats.add(this)
    }
}

interface SeatRepository : JpaRepository<Seat, Long>
