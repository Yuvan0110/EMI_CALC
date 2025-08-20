
// Input view model.kt
package com.example.emi_calc_app.view_model

import android.os.Build
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartDate(stDate: Long) {
        startDateTenure = stDate
        refreshTables()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartDateLocalDate(): LocalDate {
        return Instant.ofEpochMilli(startDateTenure)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshTables() {
        loadTable()
        loadYearlyTable()
    }

    fun setPrincipalAmount(value: String) {
        _input.value = _input.value.copy(principal = value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) refreshTables()
    }

    fun setInterestRate(value: String) {
        _input.value = _input.value.copy(interest = value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) refreshTables()
    }

    fun setTenure(value: String) {
        _input.value = _input.value.copy(tenure = value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) refreshTables()
    }

    fun setTenureUnit(unit: TenureUnit) {
        _input.value = _input.value.copy(tenureUnit = unit)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) refreshTables()
    }

    fun calcEmi(): Double {
        val r = inputState.interest.toDoubleOrNull()?.div(12)?.div(100) ?: 0.0
        val p = inputState.principal.toDoubleOrNull() ?: 0.0
        val t = inputState.tenure.toIntOrNull() ?: 0

        val n = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> t * 12
            TenureUnit.MONTHS -> t
        }

        if (r == 0.0 || n == 0 || p == 0.0) return 0.0

        val num = (1 + r).pow(n)
        return (p * r * num) / (num - 1)
    }

    fun calcTotalInterest(emi: Double): Double {
        val t = inputState.tenure.toIntOrNull() ?: 0
        val n = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> t * 12
            TenureUnit.MONTHS -> t
        }
        val totalPaid = emi * n
        val principal = inputState.principal.toDoubleOrNull() ?: 0.0
        return totalPaid - principal
    }

    fun calcTotalAmountPayable(emi: Double): Double {
        val t = inputState.tenure.toIntOrNull() ?: 0
        val n = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> t * 12
            TenureUnit.MONTHS -> t
        }
        return emi * n
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTable(): MutableList<EmiBreakdown> {
        _table.clear()
        val principal = inputState.principal.toDoubleOrNull() ?: return _table
        val interest = inputState.interest.toDoubleOrNull() ?: return _table
        val tenure = inputState.tenure.toIntOrNull() ?: return _table
        return calcAmortisationTable(principal, interest, tenure, getStartDateLocalDate())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadYearlyTable(): MutableList<EmiBreakdown> {
        _yearlyTable.clear()
        val principal = inputState.principal.toDoubleOrNull() ?: return _yearlyTable
        val interest = inputState.interest.toDoubleOrNull() ?: return _yearlyTable
        val tenure = inputState.tenure.toIntOrNull() ?: return _yearlyTable
        return calcAmortisationTableYearly(principal, interest, tenure, getStartDateLocalDate())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcAmortisationTable(
        loanAmount: Double,
        annualInterest: Double,
        tenure: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
        val months = when (inputState.tenureUnit) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calcAmortisationTableYearly(
        loanAmount: Double,
        annualInterest: Double,
        tenure: Int,
        startDate: LocalDate
    ): MutableList<EmiBreakdown> {
        val monthlyInterest = annualInterest / 12 / 100
        val months = when (inputState.tenureUnit) {
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun monthlyBreakdownGroupedByYear(): Map<String, List<EmiBreakdown>> {
        if (_table.isEmpty()) {
            loadTable()
        }
        return _table.groupBy { it.month.takeLast(4) }
    }

}