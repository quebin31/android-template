package com.quebin31.app

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ExampleUnitTest {

    @Test
    @DisplayName("addition is correct")
    fun additionIsCorrect() {
        expectThat(3 + 5).isEqualTo(8)
    }
}
