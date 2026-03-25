package com.zhuxiufeng.stockticker.api

data class StockInfo(
    val symbol: String,
    val name: String,
    val price: Double,
    val changeAmount: Double,
    val changePercent: Double
) {
    fun getDisplayString(): String {
        val sign = if (changePercent > 0) "+" else ""
        return "$name: $price ($sign${String.format("%.2f", changePercent)}%)"
    }
}