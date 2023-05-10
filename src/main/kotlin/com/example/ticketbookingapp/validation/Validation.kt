package com.example.ticketbookingapp.validation

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

        return isCapitalizedWord(value)
    }
}

private fun isCapitalizedWord(word: String): Boolean {
    if (word.isEmpty())
        return false

    if (!word[0].isUpperCase())
        return false

    for (i in 1 until word.length) {
        if (!word[i].isLowerCase())
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

class CapitalizedWordOrTwoCapitalizedWordsWithHyphenValidator :
    ConstraintValidator<CapitalizedWordOrTwoCapitalizedWordsWithHyphen, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return false

        val split = value.split("-")

        if (split.isEmpty()) {
            return false
        }

        if (split.count() == 1) {
            return isCapitalizedWord(split[0])
        }

        if (split.count() == 2) {
            val (word1, word2) = split
            return isCapitalizedWord(word1) && isCapitalizedWord(word2)
        }

        return false
    }
}
