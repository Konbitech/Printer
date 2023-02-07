package com.konbini.printerlibrary

import android.content.Context
import com.telpo.tps550.api.TelpoException
import com.telpo.tps550.api.printer.ThermalPrinter

class Printer(private val context: Context) : PrinterService {

    override suspend fun getPrinterVersion(): String {
        try {
            ThermalPrinter.start(context)
            ThermalPrinter.reset()
            val printVersion = ThermalPrinter.getVersion()
            val index: Int = printVersion.indexOf("+")
            return if (index > 0) {
                "hardware version:${
                    printVersion.substring(
                        0,
                        index
                    )
                } Software version:${printVersion.substring(index + 1, printVersion.length)}"
            } else {
                printVersion
            }
        } catch (exTelpo: TelpoException) {
            exTelpo.printStackTrace()
            return "N/A"
        }
    }

    override suspend fun printContent(printContent: String): String {
        try {
            ThermalPrinter.start(context)
            ThermalPrinter.errorStop = false
            ThermalPrinter.checkStatus()
            ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT)
            ThermalPrinter.setLeftIndent(0)
            ThermalPrinter.setLineSpace(8)
            ThermalPrinter.setFontSize(1)
            ThermalPrinter.setGray(6)
            ThermalPrinter.addString(printContent)
            ThermalPrinter.printString()
            ThermalPrinter.walkPaper(200)
            ThermalPrinter.paperCutAll()
            return ""
        } catch (ex: Exception) {
            ex.printStackTrace()
            val result = ex.toString()
            return when {
                result.contains("com.telpo.tps550.api.printer.NoPaperException") -> {
                    "Printer no paper"
                }
                result.contains("com.telpo.tps550.api.printer.OverHeatException") -> {
                    "Printer overheat"
                }
                else -> {
                    "Operation failed"
                }
            }
        }
    }
}