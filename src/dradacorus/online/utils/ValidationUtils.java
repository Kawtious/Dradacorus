/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dradacorus.online.utils;

public final class ValidationUtils {

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static boolean validateCommand(String input) {
        return input.substring(0, 1).equals("/");
    }

    private ValidationUtils() {
    }
}
