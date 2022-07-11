package net.thumbtack.busserver.exception;

import java.util.ArrayList;
import java.util.List;

import net.thumbtack.busserver.error.Error;

public class ErrorValidation {
    private List<Error> errors = new ArrayList<>();

    public List<Error> getAllErrors() {
        return errors;
    }
}
