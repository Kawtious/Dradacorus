/*
 * MIT License
 * 
 * Copyright (c) 2022 Kawtious
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.kaw.dradacorus.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class DragonConsole {

    private static Logger logger = Logger.getLogger(DragonConsole.class.getName());

    private static class MyFormatter extends Formatter {
        @Override
        public String format(LogRecord logRecord) {
            return logRecord.getThreadID() + "::" + logRecord.getSourceClassName() + "::"
                    + logRecord.getSourceMethodName() + "::" + new Date(logRecord.getMillis())
                    + "::" + logRecord.getMessage() + "\n";
        }
    }

    private static class MyFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord log) {
            // don't log CONFIG logs in file
            return log.getLevel() == Level.CONFIG;
        }
    }

    private static void log(String msg) {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("logger.properties"));
            // adding custom handler
            logger.addHandler(new StreamHandler());
            logger.setLevel(Level.FINE);

            // FileHandler file name with max size and number of log files limit
            Handler fileHandler = new FileHandler("./tmp/logger.log", true);
            fileHandler.setFormatter(new MyFormatter());
            // setting custom filter for FileHandler
            fileHandler.setFilter(new MyFilter());
            logger.addHandler(fileHandler);

            // logging messages
            logger.log(Level.INFO, msg);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String getOutput(String string) {
        LocalDateTime now = LocalDateTime.now();
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        int millis = now.get(ChronoField.MILLI_OF_SECOND); // Note: no direct getter available.
        return String.format("[%04d-%02d-%02d %02d:%02d:%02d.%03d INFO]: %s%n", year, month, day,
                hour, minute, second, millis, string);
    }

    public static void writeLine(String string) {
        log(string);
    }

    private DragonConsole() {}

    public static class Error {

        public static void writeLine(String string) {
            log(string);
        }

        private Error() {}

    }

}
