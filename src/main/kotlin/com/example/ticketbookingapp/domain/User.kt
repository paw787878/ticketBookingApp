package com.example.ticketbookingapp.domain

import com.example.ticketbookingapp.domain.validation.CapitalizedWordOrTwoCapitalizedWordsWithHyphen
import com.example.ticketbookingapp.domain.validation.FirstLetterIsCapital
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

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
