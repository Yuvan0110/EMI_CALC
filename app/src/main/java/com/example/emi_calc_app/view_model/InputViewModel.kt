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
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.pow

class InputViewModel : ViewModel() {


    private val _input = mutableStateOf(InputState())
    val inputState: InputState get() = _input.value


    private val _table = mutableStateListOf<EmiBreakdown>()
    val table: List<EmiBreakdown> get() = _table


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTable() : MutableList<EmiBreakdown>{

        if(_table.isNotEmpty()){
            _table.clear()
        }
        return calcAmortisationTable(
            inputState.principal.toDouble(),
            inputState.interest.toDouble(),
            inputState.tenure.toInt()
        )
    }


    fun setPrincipalAmount(value : String){
        _input.value = _input.value.copy(principal = value)
    }

    fun setInterestRate(value : String){
        _input.value = _input.value.copy(interest = value)
    }

    fun setTenure(value : String){
        _input.value = _input.value.copy(tenure = value)
    }


    fun calcEmi(): Double {
        val r: Double = inputState.interest.toDoubleOrNull()?.div(12)?.div(100) ?: 0.0
        val p: Int = inputState.principal.toIntOrNull() ?: 0
        val t: Int = inputState.tenure.toIntOrNull() ?: 0


        val n = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> t * 12
            TenureUnit.MONTHS -> t
        }

        if (r == 0.0 || n == 0 || p == 0) return 0.0

        val num = (1 + r).pow(n)
        val res: Double = (p * r * num) / (num - 1)

        return res
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


    fun setTenureUnit(unit: TenureUnit) {
        _input.value = _input.value.copy(tenureUnit = unit)
    }


    var startDateTenure by mutableLongStateOf(System.currentTimeMillis())

    fun setStartDate(stDate : Long){
        startDateTenure = stDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calcAmortisationTable(
        loanAmount: Double,
        monthlyInterest: Double,
        tenureMonths: Int,
        startDate: LocalDate = LocalDate.now()
    ): MutableList<EmiBreakdown> {
        val mI = monthlyInterest / 12 / 100

        val months = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> tenureMonths * 12
            TenureUnit.MONTHS -> tenureMonths
        }

        val emi = loanAmount * mI * (1 + mI).pow(months) / (((1 + mI).pow(months)) - 1)
        var balance = loanAmount
        var loanPercent = 0.0


        val startDate = Instant.ofEpochMilli(startDateTenure).atZone(ZoneId.systemDefault()).toLocalDate()

        for (month in 0..months-1) {
            val interest = balance * mI
            val principal = emi - interest
            balance -= principal
            if (balance < 0) balance *= -1
            loanPercent += principal / loanAmount * 100

            val currentDate = startDate.plusMonths(month.toLong())
            val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy"))

            _table.add(
                EmiBreakdown(
                    month = formattedDate,
                    principalComponent = principal,
                    interestComponent = interest,
                    balance = balance,
                    loanPercentPaid = loanPercent
                )
            )

        }

        return _table
    }

    private val _yearlyTable = mutableStateListOf<EmiBreakdown>()
    val yearlyTable: List<EmiBreakdown> get() = _yearlyTable


    @RequiresApi(Build.VERSION_CODES.O)
    fun loadYearlyTable() : MutableList<EmiBreakdown>{

        if(_yearlyTable.isNotEmpty()){
            _yearlyTable.clear()
        }
        return calcAmortisationTableYearly(
            inputState.principal.toDouble(),
            inputState.interest.toDouble(),
            inputState.tenure.toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calcAmortisationTableYearly(
        loanAmount: Double,
        monthlyInterest: Double,
        tenureMonths: Int
    ): MutableList<EmiBreakdown> {
        val mI = monthlyInterest / 12 / 100

        val months = when (inputState.tenureUnit) {
            TenureUnit.YEARS -> tenureMonths * 12
            TenureUnit.MONTHS -> tenureMonths
        }

        val emi = loanAmount * mI * (1 + mI).pow(months) / (((1 + mI).pow(months)) - 1)
        var balance = loanAmount
        var loanPercent = 0.0


        val startDate = Instant.ofEpochMilli(startDateTenure).atZone(ZoneId.systemDefault()).toLocalDate()

        var cummP = 0.0
        var cummI = 0.0
        var formattedDate = ""

        for (month in 0..months-1) {
            val interest = balance * mI
            val principal = emi - interest


            val currentDate = startDate.plusMonths(month.toLong())
            val prevDate = startDate.plusMonths(month.toLong() - 1)
            formattedDate = prevDate.format(DateTimeFormatter.ofPattern("yyyy"))

            if(prevDate.year != currentDate.year){
                _yearlyTable.add(
                    EmiBreakdown(
                        month = formattedDate,
                        principalComponent = cummP,
                        interestComponent = cummI,
                        balance = balance,
                        loanPercentPaid = loanPercent
                    )
                )
                cummI = 0.0
                cummP = 0.0
            }
            cummI += interest
            cummP += principal
            balance -= principal
            if (balance < 0) balance *= -1
            loanPercent += principal / loanAmount * 100

        }

        _yearlyTable.add(
            EmiBreakdown(
                month = formattedDate,
                principalComponent = cummP,
                interestComponent = cummI,
                balance = balance,
                loanPercentPaid = loanPercent
            )
        )

        return _yearlyTable
    }


    fun convertMillisToDate(millis: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    fun monthlySplitGroupedByYear(){
        if(_table.isNotEmpty()){
            _table.clear()
        }
        _table.groupBy {

        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun monthlyBreakdownGroupedByYear(): Map<String, List<EmiBreakdown>> {
        if (_table.isEmpty()) {
            loadTable()
        }
        return _table.groupBy { it.month.takeLast(4) }
    }
}