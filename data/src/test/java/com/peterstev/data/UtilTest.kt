package com.peterstev.data

import com.peterstev.data.util.WeatherType
import com.peterstev.data.util.dateTimeConverter
import com.peterstev.data.util.round
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UtilTest : Spek({

    describe("dateTimeConverter") {
        describe("formatting todays timestamp") {
            lateinit var result: String
            beforeEachTest {
                result = dateTimeConverter(1674000655, WeatherType.TODAY)
            }

            it("should return the timestamp in HH:MM a format") {
                assertThat(result).isEqualTo("01:10 AM")
            }
        }

        describe("formatting future timestamp") {
            lateinit var result: String
            beforeEachTest {
                result = dateTimeConverter(1674000655, WeatherType.FUTURE)
            }

            it("should return the timestamp in EEE dd-M-yyyy-hh:mm a format") {
                assertThat(result).isEqualTo("Wed 18-1-2023-01:10 AM")
            }
        }
    }

    describe("rounding up numbers") {
        var result = 0.0
        beforeEachTest {
            result = round(12.345678)
        }

        it("should round up to 2 decimal places") {
            assertThat(result).isEqualTo(12.35)
        }
    }
})
