package com.vault.application.dto;

public record GeneratePasswordRequest(
        Integer length,
        Boolean symbols,
        Boolean ambiguous
) {
    public int effectiveLength() {
        return (length != null) ? length : 24;
    }

    public boolean effectiveSymbols() {
        return (symbols != null) ? symbols : true;
    }

    public boolean effectiveAmbiguous() {
        return (ambiguous != null) ? ambiguous : false;
    }
}
