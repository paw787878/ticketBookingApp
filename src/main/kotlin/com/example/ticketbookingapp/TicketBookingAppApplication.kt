package com.example.ticketbookingapp

import com.example.ticketbookingapp.service.DatabaseInitializationService
import com.example.ticketbookingapp.service.DatabaseInitializationForTest
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@SpringBootApplication
class TicketBookingAppApplication {

    @Bean
    @Profile("default")
    fun dataLoader(
        databaseInitializationService: DatabaseInitializationService
    ): CommandLineRunner {
        return CommandLineRunner {
            databaseInitializationService.initializeDatabase()
        }
    }

    @Bean
    @Profile("test")
    fun dataLoaderTest(
        databaseInitializationForTest: DatabaseInitializationForTest
    ): CommandLineRunner {
        return CommandLineRunner {
            databaseInitializationForTest.initializeDatabase()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TicketBookingAppApplication>(*args)
}
