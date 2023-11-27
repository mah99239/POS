package com.casecode.domain.model.subscriptions

data class Subscription(
    val cost: Double,
    val duration: Int,
    val permissions: List<String>,
    val type: String
)