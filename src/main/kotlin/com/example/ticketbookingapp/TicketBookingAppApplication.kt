package com.example.ticketbookingapp

import com.example.ticketbookingapp.config.CustomConfigProperties
import com.example.ticketbookingapp.service.databaseInitialization.DatabaseInitializationForTest
import com.example.ticketbookingapp.service.databaseInitialization.DatabaseInitializationService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

@EnableConfigurationProperties(CustomConfigProperties::class)
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
