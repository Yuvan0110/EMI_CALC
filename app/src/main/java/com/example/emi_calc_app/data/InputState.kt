package com.example.emi_calc_app.data
enum class TenureUnit {
    MONTHS, YEARS
}
data class InputState(
    val principal : String = "100000",
    val interest : String = "7.5",
    val tenure : String = "5",
    val tenureUnit: TenureUnit = TenureUnit.YEARS
){
    fun isValid(): Boolean {
        return principal.isNotEmpty() && interest.isNotEmpty() && tenure.isNotEmpty()
    }
}


