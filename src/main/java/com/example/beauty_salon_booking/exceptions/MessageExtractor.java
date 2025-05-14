package com.example.beauty_salon_booking.exceptions;

public class MessageExtractor {
    public static String extractMessage(String input) {
        // Находим индекс начала подстроки после "message":"
        int startIndex = input.indexOf("\"message\":\"") + "\"message\":\"".length();

        // Находим индекс конца подстроки перед следующими кавычками
        int endIndex = input.indexOf("\"", startIndex);

        // Извлекаем подстроку
        return input.substring(startIndex, endIndex);
    }
}