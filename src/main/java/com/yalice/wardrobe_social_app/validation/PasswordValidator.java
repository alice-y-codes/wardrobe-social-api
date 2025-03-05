//package com.yalice.wardrobe_social_app.validation;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//
//// Validator class for password validation
//public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
//
//    @Override
//    public void initialize(ValidPassword constraintAnnotation) {
//        // Initialization logic (optional)
//    }
//
//    @Override
//    public boolean isValid(String password, ConstraintValidatorContext context) {
//        if (password == null) {
//            return false;  // Null password is not valid
//        }
//
//        // Check for letters, numbers, and special characters
//        return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$");
//    }
//}
