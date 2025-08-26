
package com.example.emi_calc_app.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.emi_calc_app.data.EmiBreakdown
import com.example.emi_calc_app.data.InputState
import com.example.emi_calc_app.data.TenureUnit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.pow


class InputViewModel : ViewModel() {

    private val _input = mutableStateOf(InputState())
    val inputState: InputState get() = _input.value

    private val _table = mutableStateListOf<EmiBreakdown>()
    val table: List<EmiBreakdown> get() = _table

    private val _yearlyTable = mutableStateListOf<EmiBreakdown>()
    val yearlyTable: List<EmiBreakdown> get() = _yearlyTable

    var startDateTenure by mutableLongStateOf(System.currentTimeMillis())

    fun setStartDate(stDate: Long) {
        startDateTenure = stDate
    }

    fun setPrincipalAmount(value: String) {
        _input.value = _input.value.copy(principal = value)
    }

    fun setInterestRate(value: String) {
        _input.value = _input.value.copy(interest = value)
    }

    fun setTenure(value: String) {
        _input.value = _input.value.copy(tenure = value)
    }

    fun setTenureUnit(unit: TenureUnit) {
        _input.value = _input.value.copy(tenureUnit = unit)
    }

    fun getPrincipal(): Double = _input.value.principal.toDoubleOrNull() ?: 0.0
    fun getInterest(): Double = _input.value.interest.toDoubleOrNull() ?: 0.0
    fun getTenure(): Int = _input.value.tenure.toIntOrNull() ?: 0
    fun getTenureUnit(): TenureUnit = _input.value.tenureUnit

    private fun getStartDateLocalDate(): LocalDate {
        return Instant.ofEpochMilli(startDateTenure)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun calcEmi(): Double {
        val r = getInterest().div(12).div(100)
        val p = getPrincipal()
        val n = when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }

        if (r == 0.0 || n == 0 || p == 0.0) return 0.0

        val num = (1 + r).pow(n)
        return (p * r * num) / (num - 1)
    }

    fun calcTotalInterest(emi: Double): Double {
        val n = when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }
        return emi * n - getPrincipal()
    }

    fun calcTotalAmountPayable(emi: Double): Double {
        val n = when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }
        return emi * n
    }

    fun loadTable(): MutableList<EmiBreakdown> {
        _table.clear()
        val p = getPrincipal()
        val r = getInterest()
        val t = getTenure()
        return calcAmortisationTable(p, r, t, getStartDateLocalDate())
    }

    fun loadYearlyTable(): MutableList<EmiBreakdown> {
        _yearlyTable.clear()
        val p = getPrincipal()
        val r = getInterest()
        val t = getTenure()
        return calcAmortisationTableYearly(p, r, t, getStartDateLocalDate())
    }

    private fun calcAmortisationTable(
        loanAmount: Double,
        annualInterest: Double,
        tenure: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
        val months = when (getTenureUnit()) {
            TenureUnit.YEARS -> tenure * 12
            TenureUnit.MONTHS -> tenure
        }

        val emi = loanAmount * monthlyInterest * (1 + monthlyInterest).pow(months) / ((1 + monthlyInterest).pow(months) - 1)

        var balance = loanAmount
        var loanPercentPaid = 0.0

        for (month in 0 until months) {
            val interestComponent = balance * monthlyInterest
            val principalComponent = emi - interestComponent
            balance -= principalComponent
            if (balance < 0) balance = 0.0
            loanPercentPaid += principalComponent / loanAmount * 100

            val currentDate = startDate.plusMonths(month.toLong())
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy"))

            _table.add(
                EmiBreakdown(
                    month = formattedDate,
                    principalComponent = principalComponent,
                    interestComponent = interestComponent,
                    totalAmount = emi,
                    balance = balance,
                    loanPercentPaid = loanPercentPaid
                )
            )
        }
        return _table
    }

    private fun calcAmortisationTableYearly(
        loanAmount: Double,
        annualInterest: Double,
        tenure: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
        val months = when (getTenureUnit()) {
            TenureUnit.YEARS -> tenure * 12
            TenureUnit.MONTHS -> tenure
        }

        val emi = loanAmount * monthlyInterest * (1 + monthlyInterest).pow(months) / ((1 + monthlyInterest).pow(months) - 1)

        var balance = loanAmount
        var loanPercentPaid = 0.0

        var cummPrincipal = 0.0
        var cummInterest = 0.0
        var currentYear = startDate.year

        for (month in 0 until months) {
            val interestComponent = balance * monthlyInterest
            val principalComponent = emi - interestComponent
            val currentDate = startDate.plusMonths(month.toLong())

            if (currentDate.year != currentYear) {
                _yearlyTable.add(
                    EmiBreakdown(
                        month = currentYear.toString(),
                        principalComponent = cummPrincipal,
                        interestComponent = cummInterest,
                        totalAmount = cummPrincipal + cummInterest,
                        balance = balance,
                        loanPercentPaid = loanPercentPaid
                    )
                )
                cummPrincipal = 0.0
                cummInterest = 0.0
                currentYear = currentDate.year
            }

            cummPrincipal += principalComponent
            cummInterest += interestComponent
            balance -= principalComponent
            if (balance < 0) balance = 0.0
            loanPercentPaid += principalComponent / loanAmount * 100
        }

        _yearlyTable.add(
            EmiBreakdown(
                month = currentYear.toString(),
                principalComponent = cummPrincipal,
                interestComponent = cummInterest,
                totalAmount = cummPrincipal + cummInterest,
                balance = balance,
                loanPercentPaid = loanPercentPaid
            )
        )

        return _yearlyTable
    }

    fun monthlyBreakdownGroupedByYear(): Map<String, List<EmiBreakdown>> {
        if (_table.isEmpty()) {
            loadTable()
        }
        return _table.groupBy { it.month.takeLast(4) }
    }
}
