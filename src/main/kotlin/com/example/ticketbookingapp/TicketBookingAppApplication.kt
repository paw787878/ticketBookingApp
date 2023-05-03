package com.example.ticketbookingapp

import com.example.ticketbookingapp.service.DatabaseInitializationService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class TicketBookingAppApplication {

    @Bean
    fun dataLoader(
        databaseInitializationService: DatabaseInitializationService
    ): CommandLineRunner {
        return CommandLineRunner {
            databaseInitializationService.initializeDatabase()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TicketBookingAppApplication>(*args)
}
