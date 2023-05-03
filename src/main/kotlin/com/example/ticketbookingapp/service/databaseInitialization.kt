package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.*
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DatabaseInitializationService(
    private val movieRepository: MovieRepository,
    private val screeningRoomRepository: ScreeningRoomRepository,
    private val seatRepository: SeatRepository,
) {
    // TODO_PAWEL i think it needs to specify some exceptions or esle it is wrong
    @Transactional
    fun initializeDatabase() {
        initializeMovies()
        initializeRooms()
    }

    private fun initializeRooms() {
        initializeRoom("room 1", 10, 10)
        initializeRoom("room 2", 15, 15)
        initializeRoom("room 3", 20, 20)
    }

    private fun initializeRoom(roomName: String, numberOfRows: Int, numberOfColumns: Int) {
        val screeningRoom = screeningRoomRepository.save(ScreeningRoom(roomName))
        val rowNames = ('A' .. 'Z').toList()
        // TODO_PAWEL we could have corridors in rooms
        val seatsArray = buildList {
            repeat(numberOfRows) { row ->
                add(buildList {
                    repeat(numberOfColumns) { column ->
                        add(
                            Seat(
                                screeningRoom,
                                rowNames[row].toString(),
                                (column + 1).toString()
                            ).let { seatRepository.save(it) }
                        )
                    }
                })
            }
        }

        for (row in seatsArray) {
            for ((rowIndex, seat) in row.withIndex()) {
                seat.seatToLeft = row.getOrNull(rowIndex - 1)
                seat.seatToRight = row.getOrNull(rowIndex + 1)
            }
        }
    }

    private fun initializeMovies() {
        movieRepository.save(Movie("Schindler's List"))
        movieRepository.save(Movie("The Shawshank Redemption"))
        movieRepository.save(Movie("The Lord of the Rings: The Return of the King"))
    }
}
