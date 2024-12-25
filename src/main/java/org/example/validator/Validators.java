package org.example.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Validators {
    List<Validator> validators;

    Validators(@Autowired List<Validator> validators) {
        this.validators = validators;
    }

    public void validate(String email, String password, String name) {
        for (Validator validator : validators) {
            validator.validate(email, password, name);
        }
    }
}
