package com.travelplanner.travelplanner_server.validator;

import com.travelplanner.travelplanner_server.model.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

//Custom Validations
//Spring validator docs: https://docs.spring.io/autorepo/docs/spring/3.2.x/spring-framework-reference/html/validation.html
//helpfull validation video: https://www.youtube.com/watch?v=ok50KbLCYls

//Component is the generic stereotype for any Spring-managed component like Repository/Service/Controller .etc
@Component
public class UserValidator implements Validator {
    // simple regular expression: https://projects.lukehaas.me/regexhub/
    // private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,16}$";

    //1
    @Override
    public boolean supports(Class<?> clazz){
        //supports(Class<?>): Specifies that a instance of the User Domain Model can be validated with this custom validator
        return User.class.equals(clazz);
    }

    //2
    @Override
    public void validate(Object target, Errors errors){
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", null, "username field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", null, "password field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirmation", null, " passwordConfirmation field.required");

        User user = (User) target;
        // adding the checks one by one:
        if(!user.getPasswordConfirmation().equals(user.getPassword())){
            //3
            System.out.println("password not matching!");
            //errors.rejectValue("passwordConfirmation", "Match");
            errors.rejectValue("passwordConfirmation", null, "Password and PasswordConfirmation must match!!!");
        }


//        // for user_name
//        Pattern username_pattern = Pattern.compile(this.USERNAME_PATTERN);
//        System.out.println(username_pattern.matcher(user.getUsername()).matches());
//        if(!username_pattern.matcher(user.getUsername()).matches()){
//            System.out.println("username not matching!");
//            errors.rejectValue("username", null, "Username must between 3-16!!!");
//        }
    }
}
