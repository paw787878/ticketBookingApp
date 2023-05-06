package com.example.ticketbookingapp.domain.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Constraint(validatedBy = [FirstLetterIsCapitalValidator::class])
annotation class FirstLetterIsCapital(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val message: String = "Should be capitalized word"
)

class FirstLetterIsCapitalValidator : ConstraintValidator<FirstLetterIsCapital, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null)
            return false

        return wordIsCapitalized(value)
    }
}

private fun wordIsCapitalized(value: String): Boolean {
    if (value.isEmpty())
        return false

    if (!value[0].isUpperCase())
        return false

    for (i in 1 until value.length) {
        if (!value[i].isLowerCase())
            return false
    }

    return true
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Constraint(validatedBy = [CapitalizedWordOrTwoCapitalizedWordsWithHyphenValidator::class])
annotation class CapitalizedWordOrTwoCapitalizedWordsWithHyphen(
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val message: String = "Should be capitalized word or two capitalized words separated by hyphen"
)

class CapitalizedWordOrTwoCapitalizedWordsWithHyphenValidator : ConstraintValidator<CapitalizedWordOrTwoCapitalizedWordsWithHyphen, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false

        val splitted = value.split("-")

        if (splitted.isEmpty()) {
            return false
        }

        if (splitted.count() == 1) {
            return wordIsCapitalized(splitted[0])
        }

        if (splitted.count() == 2) {
            val (word1, word2) = splitted
            return wordIsCapitalized(word1) && wordIsCapitalized(word2)
        }

        return false
    }
}
