package com.example.emi_calc_app.view_model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.emi_calc_app.R
import com.example.emi_calc_app.data.Breakdown
import com.example.emi_calc_app.viewModelRepository.ViewModelRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow

class FdViewModel : ViewModel() {

    private val _table = mutableStateListOf<Breakdown>()
    val table: List<Breakdown> get() = _table

    val viewModelRepository = ViewModelRepository()

    fun loadFdTable(): List<Breakdown> {
        _table.clear()
        return calcFdTable(viewModelRepository.getPrincipal(), viewModelRepository.getInterest(), viewModelRepository.getMonths(), viewModelRepository.getStartDate())
    }

    private fun calcFdTable(
        principal: Double,
        annualInterest: Double,
        months: Int,
        startDate: LocalDate
    ): MutableList<Breakdown> {
        val monthlyRate = annualInterest / 12 / 100
        var balance = principal

        for (month in 0 ..< months) {
            val interestComponent = balance * monthlyRate
            balance += interestComponent

            val growthPercent = (balance - principal) / principal * 100
            val formattedDate = startDate.plusMonths(month.toLong())
                .format(DateTimeFormatter.ofPattern("MMM yyyy"))
            _table.add(
                Breakdown(
                    month = formattedDate,
                    principalComponent = principal,
                    interestComponent = interestComponent,
                    totalAmount = balance,
                    balance = monthlyRate,
                    percent = growthPercent
                )
            )
        }
        return _table
    }

    fun calcMaturityAmount(): Double {
        val monthlyRate = viewModelRepository.getInterest() / 12 / 100
        val maturityAmount = viewModelRepository.getPrincipal() * (1 + monthlyRate)
            .pow(viewModelRepository.getMonths().toDouble())
        return maturityAmount
    }

    fun calcTotalInterest(): Double {
        val monthlyRate = viewModelRepository.getInterest() / 12 / 100
        val maturityAmount = viewModelRepository.getPrincipal() *
                (1 + monthlyRate).pow(viewModelRepository.getMonths().toDouble())
        val totalInterest =  maturityAmount - viewModelRepository.getPrincipal()
        return totalInterest
    }


    val stringVals = listOf(R.string.summary_fd, R.string.initial_amt, R.string.interest_amt, R.string.totalReturns)

    val headers = listOf("Month", "Interest rate", "Principal", "Interest", "Total amount", "Growth %")
}