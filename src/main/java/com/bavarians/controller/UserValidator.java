package com.bavarians.controller;


import com.bavarians.graphql.model.Klient;
import com.bavarians.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Klient.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Klient user = (Klient) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        if (user.getEmail().length() < 6 || user.getEmail().length() > 32) {
            errors.rejectValue("email", "Size.userForm.email");
        }
        if (userService.findByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "Duplicate.userForm.email");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "haslo", "NotEmpty");
        if (user.getHaslo().length() < 8 || user.getHaslo().length() > 32) {
            errors.rejectValue("haslo", "Size.userForm.haslo");
        }

        if (!user.getHaslo2().equals(user.getHaslo())) {
            errors.rejectValue("haslo2", "Diff.userForm.haslo2");
        }
    }
}