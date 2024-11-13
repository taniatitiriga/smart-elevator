package com.example.elevator;

public class CustomExceptions {

    // Exception for invalid number format input
    public static class InvalidNumberFormatException extends Exception {
        public InvalidNumberFormatException(String message) {
            super(message);
        }
    }

    // Exception for invalid command input in demo mode
    public static class InvalidCommandException extends Exception {
        public InvalidCommandException(String message) {
            super(message);
        }
    }
}
