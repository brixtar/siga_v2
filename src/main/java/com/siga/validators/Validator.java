// src/main/java/com/siga/validators/Validator.java

package com.siga.validators;

import com.siga.exceptions.SigaException;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern DNI_PATTERN = 
        Pattern.compile("^\\d{8}$");

    public static void validateEmail(String email) {
        if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new SigaException("ERR-VAL-001", "Formato de email inválido");
        }
    }

    public static void validateDNI(String dni) {
        if (dni == null || !DNI_PATTERN.matcher(dni).matches()) {
            throw new SigaException("ERR-VAL-002", "Formato de DNI inválido");
        }
    }

    public static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new SigaException("ERR-VAL-003", 
                String.format("El campo %s es obligatorio", fieldName));
        }
    }
}