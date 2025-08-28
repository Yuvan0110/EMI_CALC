package com.example.emi_calc_app.viewModelRepository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import com.example.emi_calc_app.data.InputState
import com.example.emi_calc_app.data.TenureUnit
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ViewModelRepository {
    private val _input = mutableStateOf(InputState())
    val inputState: InputState get() = _input.value

    var startDateTenure by mutableStateOf(System.currentTimeMillis())

    fun setStartDate(stDate: Long) {
        startDateTenure = stDate
    }

    fun onPrincipalChange(value: String) {
        if (value.isDigitsOnly() && value.length <= 10) {
            setPrincipal(value)
        }
    }

    fun onInterestChange(value: String) {
        val num = value.toDoubleOrNull() ?: 0.0
        if (num in 0.0..100.0 && value.matches(Regex("(^\\d+)?(\\.\\d*)?$"))) {
            setInterest(value)
        }
    }

    fun onTenureChange(value: String) {
        if (value.isDigitsOnly() && value.length <= 4) {
            setTenure(value)
        }
    }

    fun setPrincipal(principal : String) {
        _input.value = _input.value.copy(principal = principal)
    }

    fun setInterest(interestRate : String) {
        _input.value = _input.value.copy(interest = interestRate)
    }

    fun setTenure(tenure : String) {
        _input.value = _input.value.copy(tenure = tenure)
    }

    fun setTenureUnit(unit: TenureUnit) {
        _input.value = _input.value.copy(tenureUnit = unit)
    }

    fun getPrincipal() : Double {
        return _input.value.principal.toDoubleOrNull() ?: 0.0
    }
    fun getInterest() : Double{
        return _input.value.interest.toDoubleOrNull() ?: 0.0
    }
    fun getTenure() : Int {
        return _input.value.tenure.toIntOrNull() ?: 0
    }
    fun getTenureUnit() : TenureUnit {
        return _input.value.tenureUnit
    }

    fun getStartDate(): LocalDate {
        return Instant.ofEpochMilli(startDateTenure).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun getMonths(): Int {
        return when (getTenureUnit()) {
            TenureUnit.YEARS -> getTenure() * 12
            TenureUnit.MONTHS -> getTenure()
        }
    }

    fun isValid(): Boolean {
        return inputState.principal.isNotEmpty() && inputState.interest.isNotEmpty() && inputState.tenure.isNotEmpty()
    }

}