﻿package com.example.ticketbookingapp.utils

import com.example.ticketbookingapp.Exceptions.ClientResponseEntityIdIsWrong
import org.springframework.data.jpa.repository.JpaRepository


inline fun <reified T> JpaRepository<T, Long>.findByIdOrClientError(id: Long): T {
    val optional = findById(id)
    if (optional.isEmpty) {
        throw ClientResponseEntityIdIsWrong(id, T::class.java)
    }
    return optional.get()
}
