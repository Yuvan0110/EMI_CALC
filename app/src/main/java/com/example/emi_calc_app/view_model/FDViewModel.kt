package com.example.emi_calc_app.view_model

import androidx.lifecycle.ViewModel
import com.example.emi_calc_app.data.FdBreakdown
import com.example.emi_calc_app.viewModelRepository.ViewModelRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class FdViewModel : ViewModel() {

    val viewModelRepository = ViewModelRepository()

    private val _table = mutableListOf<FdBreakdown>()
    val table: List<FdBreakdown> get() = _table

    fun loadFdTable(): List<FdBreakdown> {
        _table.clear()
        return calcFdTable(viewModelRepository.getPrincipal(), viewModelRepository.getInterest(), viewModelRepository.getMonths(), viewModelRepository.getStartDate())
    }

    private fun calcFdTable(
        principal: Double,
        annualInterest: Double,
        months: Int,
        startDate: LocalDate
    ): MutableList<FdBreakdown> {
        val monthlyRate = annualInterest / 12 / 100
        var balance = principal

        for (month in 0 ..< months) {
            val interestComponent = balance * monthlyRate
            balance += interestComponent // compound monthly

            val growthPercent = (balance - principal) / principal * 100
            val formattedDate = startDate.plusMonths(month.toLong())
                .format(DateTimeFormatter.ofPattern("MMM yyyy"))

            _table.add(
                FdBreakdown(
                    month = formattedDate,
                    principalComponent = principal, // stays constant
                    interestComponent = interestComponent,
                    totalAmount = balance,
                    growthPercent = growthPercent
                )
            )
        }
        return _table
    }

    fun calcMaturityAmount(): Double {
        val monthlyRate = viewModelRepository.getInterest() / 12 / 100
        return viewModelRepository.getPrincipal() * (1 + monthlyRate).pow(viewModelRepository.getMonths().toDouble())
    }
}
