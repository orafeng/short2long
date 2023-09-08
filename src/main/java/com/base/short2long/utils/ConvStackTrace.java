package com.base.short2long.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ConvStackTrace {

    public static String getExceptionStackTrace(Throwable anexcepObj) {
        StringWriter sw = null;
        PrintWriter printWriter = null;
        try {
            if (anexcepObj != null) {
                sw = new StringWriter();
                printWriter = new PrintWriter(sw);
                anexcepObj.printStackTrace(printWriter);
                printWriter.flush();
                sw.flush();
                return sw.toString();
            } else
                return null;
        } finally {

            try {
                if (sw != null)
                    sw.close();
                if (printWriter != null)
                    printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
