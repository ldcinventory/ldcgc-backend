package org.ldcgc.backend.util.retrieving;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.util.compare.EnumMethods;

@Getter
@RequiredArgsConstructor
public class Message implements EnumMethods {

    @Getter
    @RequiredArgsConstructor
    public enum InfoMessage implements EnumMethods {

        USER_CREATED("User created successfully!"),
        USER_LISTED("Found %s user/s"),
        USER_UPDATED("User details updated"),
        USER_DELETED("User deleted"),
        VOLUNTEER_CREATED("Volunteer created"),
        VOLUNTEER_LISTED("Found %s volunteer/s"),
        VOLUNTEER_UPDATED("Volunteer details updated"),
        VOLUNTEER_DELETED("Volunteer deleted"),
        LOGOUT_SUCCESSFUL("Logout successful");

        private final String message;

    }

    @Getter
    @RequiredArgsConstructor
    public enum AppMessage implements EnumMethods {

        CREDENTIALS_EMAIL_TEMPLATE("credentialsTemplate.html");

        private final String message;

    }

    @Getter
    @RequiredArgsConstructor
    public enum ErrorMessage implements EnumMethods {
        USER_ALREADY_EXIST("There's a user with this id or email"),
        USER_NOT_FOUND("User not found"),

        VOLUNTEER_ALREADY_EXIST("There's a volunteer with this id"),
        VOLUNTEER_NOT_FOUND("The volunteer you're searching for with this id couldn't be found"),

        ENDPOINT_NOT_IMPLEMENTED("This endpoint is not implemented yet");

        private final String message;

    }

    public static String getInfoMessage(InfoMessage infoMessage) {
        return infoMessage.getMessage();
    }

    public static String getAppMessage(AppMessage appMessage) {
        return appMessage.getMessage();
    }

    public static String getErrorMessage(ErrorMessage errorMessage) {
        return errorMessage.getMessage();
    }

}
