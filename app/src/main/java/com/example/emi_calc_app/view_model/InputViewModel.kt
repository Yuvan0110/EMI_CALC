package com.example.emi_calc_app.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.emi_calc_app.data.EmiBreakdown
import com.example.emi_calc_app.data.TenureUnit
import com.example.emi_calc_app.viewModelRepository.ViewModelRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class InputViewModel : ViewModel() {

    private val _table = mutableStateListOf<EmiBreakdown>()
    val table: List<EmiBreakdown> get() = _table

    private val _yearlyTable = mutableStateListOf<EmiBreakdown>()
    val yearlyTable: List<EmiBreakdown> get() = _yearlyTable

    val viewModelRepository = ViewModelRepository()
    fun calcEmi(): Double {
        val r = viewModelRepository.getInterest() / 12 / 100
        val p = viewModelRepository.getPrincipal()
        val n = when (viewModelRepository.getTenureUnit()) {
            TenureUnit.YEARS -> viewModelRepository.getTenure() * 12
            TenureUnit.MONTHS -> viewModelRepository.getTenure()
        }
        if (r == 0.0 || n == 0 || p == 0.0) return 0.0
        val num = (1 + r).pow(n)
        return (p * r * num) / (num - 1)
    }

    fun calcTotalInterest(emi: Double): Double = emi * viewModelRepository.getMonths() - viewModelRepository.getPrincipal()

    fun calcTotalAmountPayable(emi: Double): Double = emi * viewModelRepository.getMonths()



    fun loadTable(): MutableList<EmiBreakdown> {
        _table.clear()
        return calcAmortisationTable(viewModelRepository.getPrincipal(), viewModelRepository.getInterest(), viewModelRepository.getMonths(), viewModelRepository.getStartDate())
    }

    fun loadYearlyTable(): MutableList<EmiBreakdown> {
        _yearlyTable.clear()
        return calcAmortisationTableYearly(viewModelRepository.getPrincipal(), viewModelRepository.getInterest(), viewModelRepository.getMonths(), viewModelRepository.getStartDate())
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
