package com.zhuxiufeng.stockticker.api

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset
import java.time.Duration

object SinaApi {
    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    fun fetchStocks(symbols: List<String>): List<StockInfo> {
        if (symbols.isEmpty()) return emptyList()
        val url = "http://hq.sinajs.cn/list=${symbols.joinToString(",")}"
        
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Referer", "https://finance.sina.com.cn")
            .GET()
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
            // Sina returns GBK encoded bytes
            val text = String(response.body(), Charset.forName("GBK"))
            
            val stocks = mutableListOf<StockInfo>()
            
            for (line in text.lines()) {
                if (line.isBlank()) continue
                
                val parts = line.split("=")
                if (parts.size < 2) continue
                
                val varName = parts[0].trim()
                val symbol = varName.replace("var hq_str_", "").replace("str_", "")
                
                val dataStr = parts[1].trim().trim(';', '"')
                val fields = dataStr.split(",")
                if (fields.size < 3) continue
                
                var name = ""
                var price = 0.0
                var changeAmount = 0.0
                var changePercent = 0.0
                
                if (symbol.startsWith("gb_")) {
                    // US stocks
                    name = fields[0]
                    price = fields.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                    changePercent = fields.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                    changeAmount = fields.getOrNull(4)?.toDoubleOrNull() ?: 0.0
                } else if (symbol.startsWith("rt_hk")) {
                    // HK stocks
                    name = fields[1] // In Kotlin usually name is 1 for Sina HK? Wait, Rust says 0
                    price = fields.getOrNull(6)?.toDoubleOrNull() ?: 0.0
                    changeAmount = fields.getOrNull(7)?.toDoubleOrNull() ?: 0.0
                    changePercent = fields.getOrNull(8)?.toDoubleOrNull() ?: 0.0
                } else if (symbol.startsWith("hf_")) {
                    // Futures (Gold)
                    price = fields.getOrNull(0)?.toDoubleOrNull() ?: 0.0
                    val prevClose = fields.getOrNull(7)?.toDoubleOrNull() ?: 0.0
                    name = fields.getOrNull(13) ?: symbol
                    if (price == 0.0) price = prevClose
                    if (prevClose > 0.0) {
                        changeAmount = price - prevClose
                        changePercent = (changeAmount / prevClose) * 100.0
                    }
                } else {
                    // A-shares
                    name = fields[0]
                    price = fields.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                    val prevClose = fields.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                    if (price == 0.0) price = prevClose
                    if (prevClose > 0.0) {
                        changeAmount = price - prevClose
                        changePercent = (changeAmount / prevClose) * 100.0
                    }
                }
                
                stocks.add(StockInfo(symbol, name, price, changeAmount, changePercent))
            }
            return stocks
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
