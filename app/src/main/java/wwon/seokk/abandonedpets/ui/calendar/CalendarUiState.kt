package wwon.seokk.abandonedpets.ui.calendar

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Created by WonSeok on 2022.08.25
 **/
data class CalendarUiState(
    var selectedStartDate: LocalDate = LocalDate.now().minusDays(7),
    var selectedEndDate: LocalDate = LocalDate.now(),
    var animateDirection: AnimationDirection? = null
) {
    companion object {
        private val SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd")
    }

    val numberSelectedDays = ChronoUnit.DAYS.between(selectedStartDate, selectedEndDate.plusDays(1)).toFloat()

    fun hasSelectedPeriodOverlap(start: LocalDate, end: LocalDate): Boolean {
        return !end.isBefore(selectedStartDate) && !start.isAfter(selectedEndDate)
    }

    fun isDateInSelectedPeriod(date: LocalDate): Boolean {
        if (selectedStartDate == date) return true
        if (date.isBefore(selectedStartDate) ||
            date.isAfter(selectedEndDate)
        ) return false
        return true
    }

    fun getNumberSelectedDaysInWeek(currentWeekStartDate: LocalDate, month: YearMonth): Int {
        var countSelected = 0
        var currentDate = currentWeekStartDate
        for (i in 0 until CalendarState.DAYS_IN_WEEK) {
            if (isDateInSelectedPeriod(currentDate) && currentDate.month == month.month) {
                countSelected++
            }
            currentDate = currentDate.plusDays(1)
        }
        return countSelected
    }

    fun selectedStartOffset(currentWeekStartDate: LocalDate, yearMonth: YearMonth): Int {
        return if (animateDirection == null || animateDirection!!.isForwards()) {
            var startDate = currentWeekStartDate
            var startOffset = 0
            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.plusDays(1)
            }
            startOffset
        } else {
            var startDate = currentWeekStartDate.plusDays(6)
            var startOffset = 0

            for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                    startOffset++
                } else {
                    break
                }
                startDate = startDate.minusDays(1)
            }
            7 - startOffset
        }
    }

    fun isLeftHighlighted(beginningWeek: LocalDate?, month: YearMonth): Boolean {
        return if (beginningWeek != null) {
            if (month.month.value != beginningWeek.month.value) {
                false
            } else {
                val beginningWeekSelected = isDateInSelectedPeriod(beginningWeek)
                val lastDayPreviousWeek = beginningWeek.minusDays(1)
                isDateInSelectedPeriod(lastDayPreviousWeek) && beginningWeekSelected
            }
        } else {
            false
        }
    }

    fun isRightHighlighted(
        beginningWeek: LocalDate?,
        month: YearMonth
    ): Boolean {
        val lastDayOfTheWeek = beginningWeek?.plusDays(6)
        return if (lastDayOfTheWeek != null) {
            if (month.month.value != lastDayOfTheWeek.month.value) {
                false
            } else {
                val lastDayOfTheWeekSelected = isDateInSelectedPeriod(lastDayOfTheWeek)
                val firstDayNextWeek = lastDayOfTheWeek.plusDays(1)
                isDateInSelectedPeriod(firstDayNextWeek) && lastDayOfTheWeekSelected
            }
        } else {
            false
        }
    }

    fun dayDelay(currentWeekStartDate: LocalDate): Int {
        val endWeek = currentWeekStartDate.plusDays(6)
        return if (animateDirection != null && animateDirection!!.isBackwards()) {
            if (selectedEndDate.isBefore(currentWeekStartDate) || selectedEndDate.isAfter(endWeek))
                abs(ChronoUnit.DAYS.between(endWeek, selectedEndDate)).toInt()
            else 0
        } else {
            if (selectedStartDate.isBefore(currentWeekStartDate) || selectedStartDate.isAfter(endWeek))
                abs(ChronoUnit.DAYS.between(currentWeekStartDate, selectedStartDate)).toInt()
            else 0
        }
    }

    fun monthOverlapSelectionDelay(
        currentWeekStartDate: LocalDate,
        week: Week
    ): Int {
        return if (animateDirection?.isBackwards() == true) {
            val endWeek = currentWeekStartDate.plusDays(6)
            val isStartInADifferentMonth = endWeek.month != week.yearMonth.month
            if (isStartInADifferentMonth) {
                var currentDate = endWeek
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.minusDays(1)
                }
                offset
            } else {
                0
            }
        } else {
            val isStartInADifferentMonth = currentWeekStartDate.month != week.yearMonth.month
            return if (isStartInADifferentMonth) {
                var currentDate = currentWeekStartDate
                var offset = 0
                for (i in 0 until CalendarState.DAYS_IN_WEEK) {
                    if (currentDate.month.value != week.yearMonth.month.value &&
                        isDateInSelectedPeriod(currentDate)
                    ) {
                        offset++
                    }
                    currentDate = currentDate.plusDays(1)
                }
                offset
            } else {
                0
            }
        }
    }

    fun setDates(newFrom: LocalDate, newTo: LocalDate?): CalendarUiState {
        return if (newTo == null) {
            copy(selectedStartDate = newFrom, selectedEndDate = newFrom)
        } else {
            copy(selectedStartDate = newFrom, selectedEndDate = newTo)
        }
    }
}