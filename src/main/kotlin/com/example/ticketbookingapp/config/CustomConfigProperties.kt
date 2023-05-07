package com.example.ticketbookingapp.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom")
data class CustomConfigProperties(
    val minimalTimeBetweenBookingAndScreeningStart: Int
)
