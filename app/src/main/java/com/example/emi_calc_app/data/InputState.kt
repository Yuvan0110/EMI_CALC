package com.example.emi_calc_app.data
enum class TenureUnit {
    MONTHS, YEARS
}
data class InputState(
    val principal : String = "",
    val interest : String = "",
    val tenure : String = "",
    val tenureUnit: TenureUnit = TenureUnit.MONTHS
)


