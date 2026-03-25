package com.zhuxiufeng.stockticker.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.concurrency.AppExecutorUtil
import com.zhuxiufeng.stockticker.api.SinaApi
import java.awt.event.MouseEvent
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import com.intellij.util.Consumer
import javax.swing.Icon

class StockWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.TextPresentation {
    private var statusBar: StatusBar? = null
    private var scheduledTask: ScheduledFuture<*>? = null
    private var displayText: String = "Stock Ticker: Loading..."
    
    // Track rotation state
    private var currentIndex = 0
    private val displayCount = 3
    private var tooltipText: String = "Stock Ticker"

    // Default symbols to watch: A-shares, HK stocks, US stocks, Gold
    private val symbols = listOf(
        "sh000001", "sz399001", "sh600519", "sz000001",
        "rt_hk00700", "rt_hk03690", "rt_hk00981",
        "gb_aapl", "gb_msft", "gb_tsla", "gb_nvda",
        "hf_GC"
    )

    override fun ID(): String = "StockTickerStatusBarWidget"

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        // Schedule fetching data every 5 seconds
        scheduledTask = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay({
            updateData()
        }, 0, 5, TimeUnit.SECONDS)
    }

    private fun updateData() {
        val stocks = SinaApi.fetchStocks(symbols)
        if (stocks.isNotEmpty()) {
            val texts = stocks.map { it.getDisplayString() }
            
            // Update tooltip with all stock info
            tooltipText = "<html>" + texts.joinToString("<br>") + "</html>"
            
            // Slice the list to show only a few at a time
            val remaining = texts.size - currentIndex
            val count = if (remaining > 0) minOf(displayCount, remaining) else minOf(displayCount, texts.size)
            if (currentIndex >= texts.size) currentIndex = 0
            
            val toDisplay = mutableListOf<String>()
            for (i in 0 until count) {
                toDisplay.add(texts[(currentIndex + i) % texts.size])
            }
            
            displayText = toDisplay.joinToString(" | ") + "  (${currentIndex / displayCount + 1}/${(texts.size + displayCount - 1) / displayCount})"
            
            currentIndex += displayCount
            if (currentIndex >= texts.size) {
                currentIndex = 0
            }
        } else {
            displayText = "Stock Ticker: API Error"
        }
        
        // Notify the UI thread to update the widget
        ApplicationManager.getApplication().invokeLater {
            statusBar?.updateWidget(ID())
        }
    }

    override fun getText(): String = displayText
    override fun getTooltipText(): String = tooltipText
    override fun getClickConsumer(): Consumer<MouseEvent>? = Consumer { updateData() }
    override fun getAlignment(): Float = 0f
    override fun dispose() {
        scheduledTask?.cancel(false)
        statusBar = null
    }
}