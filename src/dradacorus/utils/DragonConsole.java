/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.utils;

import java.time.LocalDateTime;

public class DragonConsole {

    public static <T> void WriteLine(Class<?> context, final String string) {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        int second = now.getSecond();
        //int millis = now.get(ChronoField.MILLI_OF_SECOND); // Note: no direct getter available.

        System.out.printf("[%02d:%02d:%02d INFO]: [%s] %s\n", hour, minute, second, context.getSimpleName(), string);
    }

    public static class Error {

        public static <T> void WriteLine(Class<?> context, final String string) {
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            //int millis = now.get(ChronoField.MILLI_OF_SECOND); // Note: no direct getter available.

            System.err.printf("[%02d:%02d:%02d ERROR]: [%s] %s\n", hour, minute, second, context.getSimpleName(), string);
        }

        private Error() {
        }

    }

    private DragonConsole() {
    }

}
