package com.example.elevator;

import java.util.Scanner;

public class InputDevice {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getInput() {
        return scanner.nextLine();
    }
}
