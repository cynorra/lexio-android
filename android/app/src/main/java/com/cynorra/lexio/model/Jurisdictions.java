package com.cynorra.lexio.model;

/** Supported jurisdictions in product-priority order: United States, Europe, then Turkey. */
public final class Jurisdictions {
    private Jurisdictions() {}

    public static final String DEFAULT = "US";
    public static final String[] CODES = {"US", "UK", "DE", "FR", "ES", "IT", "TR"};

    public static boolean isSupported(String code) {
        for (String c : CODES) {
            if (c.equals(code)) return true;
        }
        return false;
    }
}
