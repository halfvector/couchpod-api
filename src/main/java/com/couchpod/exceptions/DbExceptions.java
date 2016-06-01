package com.couchpod.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;

public class DbExceptions {
    public static boolean isConflict(Exception error) {
        Throwable cause = error.getCause();
        while (cause != null) {
            if (cause instanceof SQLIntegrityConstraintViolationException) {
                // Detect unique-constraint violation during insert due to duplicate keys
                if (cause.getMessage().startsWith("Duplicate entry")) {
                    return true;
                }
            }
            cause = cause.getCause();
        }

        return false;
    }
}