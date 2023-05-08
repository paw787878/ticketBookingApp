package com.example.ticketbookingapp.domain

import org.springframework.data.jpa.repository.JpaRepository

interface MovieRepository : JpaRepository<Movie, Long>

interface ScreeningRoomRepository : JpaRepository<ScreeningRoom, Long>

interface MovieScreeningRepository : JpaRepository<MovieScreening, Long>

interface ReservationRepository : JpaRepository<Reservation, Long>

interface ReservationSeatRepository : JpaRepository<ReservationSeat, Long>

interface TicketTypeRepository : JpaRepository<TicketType, Long>

interface SeatRepository : JpaRepository<Seat, Long>
