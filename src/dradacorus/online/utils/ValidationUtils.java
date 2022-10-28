/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

public final class ValidationUtils {

    private static String ACTION_PREFIX = "/";

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static boolean validateAction(String input) {
        if (input.isEmpty()) {
            return false;
        }

        try {
            return input.substring(0, 1).equals(ACTION_PREFIX);
        } catch (StringIndexOutOfBoundsException exception) {
            return false;
        }
    }

    public static void setActionPrefix(char prefix) {
        ACTION_PREFIX = Character.toString(prefix);
    }

    private ValidationUtils() {
    }
}
