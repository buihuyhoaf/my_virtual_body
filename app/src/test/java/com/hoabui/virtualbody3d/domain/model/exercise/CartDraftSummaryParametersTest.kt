package com.hoabui.virtualbody3d.domain.model.exercise

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CartDraftSummaryParametersTest {

    @Test
    fun strengthSummary_parsesPositiveInts() {
        assertEquals(3 to 10, parseCartStrengthSetsRepsForSummary("3", "10"))
        assertEquals(3 to 10, parseCartStrengthSetsRepsForSummary(" 3 ", " 10 "))
    }

    @Test
    fun strengthSummary_nonPositiveOrInvalid_returnsNull() {
        assertNull(parseCartStrengthSetsRepsForSummary("0", "5"))
        assertNull(parseCartStrengthSetsRepsForSummary("2", "0"))
        assertNull(parseCartStrengthSetsRepsForSummary("", "3"))
        assertNull(parseCartStrengthSetsRepsForSummary("x", "3"))
    }

    @Test
    fun durationSummary_delegatesToNormalizeAndSecondsOnly() {
        val expected = normalizeDurationMinutesSeconds(0, 90)
        assertEquals(expected, parseCartDurationTotalSecondsForSummary("0", "90"))
    }

    @Test
    fun durationSummary_minutesPlusSeconds_rolled() {
        val total = parseCartDurationTotalSecondsForSummary("1", "65")
        assertEquals(normalizeDurationMinutesSeconds(1, 65), total)
    }

    @Test
    fun durationSummary_whitespaceTrimmed() {
        assertEquals(
            normalizeDurationMinutesSeconds(2, 30),
            parseCartDurationTotalSecondsForSummary(" 2 ", " 30 "),
        )
    }

    @Test
    fun durationSummary_invalidPartsFallbackToZeroThenNormalize() {
        assertEquals(
            normalizeDurationMinutesSeconds(0, 45),
            parseCartDurationTotalSecondsForSummary("abc", "45"),
        )
    }

    @Test
    fun durationSummary_zeroTotal_returnsNull() {
        assertNull(parseCartDurationTotalSecondsForSummary("0", "0"))
    }
}
