package com.example.ticketbookingapp.domain

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertTrue

internal class UserTest {
    private fun getValidator(): Validator {
        val validator = Validation.buildDefaultValidatorFactory().validator;
        return validator
    }

    @Test
    fun usernameTests() {
        val validSurname = "Smith"
        val validator = getValidator()

        fun testName(name: String, shouldBeValid: Boolean) {
            val user = User(name, validSurname)
            val issues = validator.validate(user)
            if (shouldBeValid) {
                assertTrue(issues.isEmpty())
            } else {
                assertTrue(issues.isNotEmpty())
            }
        }

        testName("As", false)
        testName("Aą", false)
        testName("Ąśń", true)
        testName("Pawel-Pawel", false)
        testName("Pawel-", false)
        testName("-Pawel", false)
        testName("PaWeL", false)
        testName("Pa&el", false)
        testName("pawel", false)
    }
    
    @Test
    fun surnameTests() {
        val validName = "Pawel"
        val validator = getValidator()

        fun testSurname(surname: String, shouldBeValid: Boolean) {
            val user = User(validName, surname)
            val issues = validator.validate(user)
            if (shouldBeValid) {
                assertTrue(issues.isEmpty())
            } else {
                assertTrue(issues.isNotEmpty())
            }
        }

        testSurname("As", false)
        testSurname("Aą", false)
        testSurname("Ąśń", true)
        testSurname("Pawel-", false)
        testSurname("-Pawel", false)
        testSurname("PaWeL", false)
        testSurname("Pa&el", false)
        testSurname("pawel", false)

        testSurname("Pawel-Pawel", true)
        testSurname("Ąawel-Ąawel", true)
        testSurname("Pawel-pawel", false)
        testSurname("pawel-Pawel", false)
        testSurname("Pawel-Pawel-Pawel", false)
        testSurname("Pawel-Pawel-Pawel", false)
        testSurname("Pawel-", false)
        testSurname("-Pawel", false)
        testSurname("Pawel--", false)
        testSurname("Pawel--Pawel", false)
    }

    @Test
    fun userIsValidatedInReservation() {
        val validator = getValidator()

        fun checkName(name: String, shouldBeValid: Boolean) {
            val reservation = Reservation(
                User(
                    name,
                    surname = "Smith",
                ),
                MovieScreening(
                    Movie(
                        "Asdf",
                        100
                    ),
                    ScreeningRoom("Asdf"),
                    Instant.now(),
                    Instant.now(),
                ),
                Instant.now()
            )
            val issues = validator.validate(reservation)

            if (shouldBeValid) {
                assertTrue(issues.isEmpty())
            } else {
                assertTrue(issues.isNotEmpty())
            }
        }

        checkName("Pawel", true)
        checkName("Pa", false)
    }
}
