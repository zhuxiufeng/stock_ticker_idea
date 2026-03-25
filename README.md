# Stock Ticker (IntelliJ IDEA Plugin)

A simple, lightweight IntelliJ platform plugin that displays real-time stock and financial data directly in your IDE's status bar. 

## Features
- **Real-Time Data**: Fetches the latest market prices using the Sina Finance API.
- **Multiple Markets**: Supports tracking A-Shares, Hong Kong Stocks, US Stocks, and Commodities (Gold).
- **Clean UI**: Automatically rotates the displayed stocks every 5 seconds to keep the status bar uncluttered (showing 3 at a time).
- **Hover Details**: Hover your mouse over the status bar widget to see the full list of tracked stocks immediately.

## Installation
1. Build the project using Gradle or download the `.zip` from releases.
2. Open your IntelliJ-based IDE (IDEA, WebStorm, PyCharm, etc.).
3. Navigate to **File** -> **Settings** -> **Plugins**.
4. Click the ⚙️ icon and select **Install Plugin from Disk...**.
5. Choose the `.zip` file and restart the IDE.

## Development
This plugin is developed using Kotlin and the Gradle IntelliJ Plugin.

- **Build**: `./gradlew buildPlugin` (Output will be in `build/distributions/`)
- **Run in Sandbox IDE**: `./gradlew runIde`
