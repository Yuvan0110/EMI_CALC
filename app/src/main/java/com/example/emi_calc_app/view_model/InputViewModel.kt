package com.example.emi_calc_app.view_model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
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

    fun onPrincipalChange(value: String) {
        if (value.isDigitsOnly() && value.length <= 10) {
            _input.value = _input.value.copy(principal = value)
        }
    }

    fun onInterestChange(value: String) {
        val num = value.toDoubleOrNull() ?: 0.0
        if (num in 0.0..100.0 && value.matches(Regex("(^\\d+)?(\\.\\d*)?$"))) {
            _input.value = _input.value.copy(interest = value)
        }
    }

    fun onTenureChange(value: String) {
        if (value.isDigitsOnly() && value.length <= 4) {
            _input.value = _input.value.copy(tenure = value)
        }
    }

    fun setTenureUnit(unit: TenureUnit) {
        _input.value = _input.value.copy(tenureUnit = unit)
    }

    private fun getPrincipal() = _input.value.principal.toDoubleOrNull() ?: 0.0
    private fun getInterest() = _input.value.interest.toDoubleOrNull() ?: 0.0
    private fun getTenure() = _input.value.tenure.toIntOrNull() ?: 0
    private fun getTenureUnit() = _input.value.tenureUnit

    private fun getStartDateLocalDate(): LocalDate =
        Instant.ofEpochMilli(startDateTenure).atZone(ZoneId.systemDefault()).toLocalDate()

    fun calcEmi(): Double {
        val r = getInterest() / 12 / 100
        val p = getPrincipal()
        val n = when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }
        if (r == 0.0 || n == 0 || p == 0.0) return 0.0
        val num = (1 + r).pow(n)
        return (p * r * num) / (num - 1)
    }

    fun calcTotalInterest(emi: Double): Double = emi * getMonths() - getPrincipal()

    fun calcTotalAmountPayable(emi: Double): Double = emi * getMonths()

    private fun getMonths(): Int =
        when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }

    fun loadTable(): MutableList<EmiBreakdown> {
        _table.clear()
        return calcAmortisationTable(getPrincipal(), getInterest(), getMonths(), getStartDateLocalDate())
    }

    fun loadYearlyTable(): MutableList<EmiBreakdown> {
        _yearlyTable.clear()
        return calcAmortisationTableYearly(getPrincipal(), getInterest(), getMonths(), getStartDateLocalDate())
    }

    private fun calcAmortisationTable(
        loanAmount: Double,
        annualInterest: Double,
        months: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
        val emi = loanAmount * monthlyInterest * (1 + monthlyInterest).pow(months) / ((1 + monthlyInterest).pow(months) - 1)
        var balance = loanAmount
        var loanPercentPaid = 0.0

        for (month in 0 until months) {
            val interestComponent = balance * monthlyInterest
            val principalComponent = emi - interestComponent
            balance -= principalComponent
            if (balance < 0) balance = 0.0
            loanPercentPaid += principalComponent / loanAmount * 100

            val formattedDate = startDate.plusMonths(month.toLong()).format(DateTimeFormatter.ofPattern("MMM yyyy"))

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
        months: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
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
        if (_table.isEmpty()) loadTable()
        return _table.groupBy { it.month.takeLast(4) }
    }
}
