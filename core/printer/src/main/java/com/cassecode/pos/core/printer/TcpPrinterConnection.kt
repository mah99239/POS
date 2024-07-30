package com.cassecode.pos.core.printer

import android.app.AlertDialog
import android.content.Context
import com.casecode.pos.core.model.data.PrinterConnectionInfo
import com.casecode.pos.core.model.data.PrinterInfo
import com.cassecode.pos.core.printer.base.EscPosPrinterService
import com.cassecode.pos.core.printer.base.OnPrintFinished
import com.cassecode.pos.core.printer.base.TcpEscPosPrint
import com.cassecode.pos.core.printer.utils.PrintUtils
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import timber.log.Timber
import javax.inject.Inject

class TcpPrinterConnection @Inject constructor() : PrinterConnection {

    override fun print(context: Context, printerInfo: PrinterInfo, printContent: PrintContent) {
        val ipAddress = (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Tcp).ipAddress
        val portAddress = (printerInfo.connectionTypeInfo as PrinterConnectionInfo.Tcp).port
        try {
            TcpEscPosPrint(
                context,
                object : OnPrintFinished {
                    override fun onError(asyncEscPosPrinterService: EscPosPrinterService?, codeException: Int) {
                        Timber.tag("OnPrintFinished").e("An error occurred!")
                    }

                    override fun onSuccess(asyncEscPosPrinterService: EscPosPrinterService?) {
                        Timber.tag("OnPrintFinished").i("Print is finished!")
                    }
                }
            ).execute(getAsyncEscPosPrinter( TcpConnection(ipAddress, portAddress, 6000), printContent))
        } catch (e: NumberFormatException) {
            AlertDialog.Builder(context)
                .setTitle("Invalid TCP port address")
                .setMessage("Port field must be an integer.")
                .show()
            e.printStackTrace()
        }
    }

    private fun getAsyncEscPosPrinter(
        printerConnection: TcpConnection,
        printContext: PrintContent,

        ): EscPosPrinterService {
        // TODO:Handle paper size
        return EscPosPrinterService(printerConnection, 203, 48f, 32).apply {
            val textToPrint = when (printContext) {
                is PrintContent.Receipt -> {

                    PrintUtils.generatePrintText(
                        printContext.invoiceId,
                        printContext.phone,
                        printContext.items,
                    )
                }

                is PrintContent.QrCode -> {
                    PrintUtils.generateBarcode(printContext.item)
                }
                is PrintContent.Test ->{
                    PrintUtils.test()
                }
            }
            addTextToPrint(textToPrint)
        }
    }
}