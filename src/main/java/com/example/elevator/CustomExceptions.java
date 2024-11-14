package com.example.elevator;

public class CustomExceptions {

    public static class InvalidNumberFormatException extends Exception {
        public InvalidNumberFormatException(String message) {
            super(message);
        }
    }

    public static class InvalidCommandException extends Exception {
        public InvalidCommandException(String message) {
            super(message);
        }
    }
}
