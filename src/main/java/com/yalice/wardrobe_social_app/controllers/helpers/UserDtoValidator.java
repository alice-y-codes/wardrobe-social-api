package com.yalice.wardrobe_social_app.controllers.helpers;

import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Validated
public class UserDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDto userDto = (UserDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username is required");
        if (userDto.getUsername().length() < 3 || userDto.getUsername().length() > 20) {
            errors.rejectValue("username", "Username must be between 3 and 20 characters");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password is required");
        if (userDto.getPassword().length() < 6) {
            errors.rejectValue("password", "Password must be at least 6 characters");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email is required");
        if (!userDto.getEmail().contains("@")) {
            errors.rejectValue("email", "Invalid email format");
        }
    }
}
