package com.zhuxiufeng.stockticker.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class StockWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = "StockTickerStatusBarWidget"
    override fun getDisplayName(): String = "Stock Ticker"
    override fun isAvailable(project: Project): Boolean = true
    override fun createWidget(project: Project): StatusBarWidget = StockWidget(project)
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
