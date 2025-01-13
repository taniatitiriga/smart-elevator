package com.example.elevator;

public class TextColor {
    public static final String RESET = "\u001B[0m";      // Reset to default
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";

    public static String infoLabel() {
        return "[" + CYAN + "INFO" + RESET + "]";
    }

    public static String errorLabel() {
        return "[" + RED + "ERROR" + RESET + "]";
    }

    public static String warningLabel() {
        return "[" + YELLOW + "WARNING" + RESET + "]";
    }

    public static String usageLabel() {
        return "[" + GREEN + "USAGE" + RESET + "]";
    }
}
