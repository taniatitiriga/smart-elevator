package com.example.elevator;

public class OutputDevice {
    public static void print(String message) {
        System.out.println(message);
    }

    public static void printInfo(String message) {
        print(TextColor.infoLabel() + " " + message);
    }

    public static void printError(String message) {
        print(TextColor.errorLabel() + " " + message);
    }

    public static void printWarning(String message) {
        print(TextColor.warningLabel() + " " + message);
    }
    public static void printUsage(String message) {
        print(TextColor.usageLabel() + " " + message);
    }
}
