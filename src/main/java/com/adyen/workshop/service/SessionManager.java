package com.adyen.workshop.service;

public class SessionManager {

    private static String pspReference = null;

    public static void setPspReference(String pspReference) {
        SessionManager.pspReference = pspReference;
    }

    public static String getPspReference() {
        return SessionManager.pspReference;
    }

    public static void removePspRerefence() {
        SessionManager.pspReference = null;
    }

}
