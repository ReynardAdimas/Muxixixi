package com.example.muxixixi.controller;

public class AppSession {
    private static int currentUserId;

    public static void  setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void clear() {
        currentUserId = 0;
    }
}
