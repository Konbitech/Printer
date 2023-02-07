package com.konbini.printerlibrary

interface PrinterService {

    suspend fun getPrinterVersion(): String

    suspend fun printContent(printContent: String): String
}