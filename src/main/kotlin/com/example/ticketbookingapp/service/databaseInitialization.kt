package com.example.ticketbookingapp.service

import com.example.ticketbookingapp.domain.Movie
import com.example.ticketbookingapp.domain.MovieRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DatabaseInitializationService(
    private val movieRepository: MovieRepository,
) {
    // TODO_PAWEL i think it needs to specify some exceptions or esle it is wrong
    @Transactional
    fun initializeDatabase() {
        movieRepository.save(Movie("Schindler's List"))
        movieRepository.save(Movie("The Shawshank Redemption"))
        movieRepository.save(Movie("The Lord of the Rings: The Return of the King"))
    }
}
