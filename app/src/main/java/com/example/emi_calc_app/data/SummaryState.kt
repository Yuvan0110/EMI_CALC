package com.example.emi_calc_app.data

data class EmiBreakdown(
    val month: String,
    val principalComponent: Double,
    val interestComponent: Double,
    val totalAmount : Double,
    val balance: Double,
    val loanPercentPaid: Double
)