// src/main/java/com/siga/exceptions/SigaException.java

package com.siga.exceptions;

public class SigaException extends RuntimeException {
    private final String code;

    public SigaException(String message) {
        super(message);
        this.code = "ERR-GEN";
    }

    public SigaException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SigaException(String message, Throwable cause) {
        super(message, cause);
        this.code = "ERR-GEN";
    }

    public String getCode() {
        return code;
    }
}