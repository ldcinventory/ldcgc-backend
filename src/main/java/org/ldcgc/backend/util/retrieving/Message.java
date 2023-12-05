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
        RECOVERY_TOKEN_VALID("Recovery token valid"),

        USER_CREATED("User created successfully!"),
        USER_LISTED("Found %s user/s"),
        USER_UPDATED("User details updated"),
        USER_CREDENTIALS_UPDATED("User credentials updated"),
        USER_DELETED("User deleted"),

        VOLUNTEER_CREATED("Volunteer created"),
        VOLUNTEER_LISTED("Found %s volunteer/s"),
        VOLUNTEER_UPDATED("Volunteer details updated"),
        VOLUNTEER_DELETED("Volunteer deleted"),

        CREDENTIALS_EMAIL_SENT("Credentials email sent"),
        EMAIL_SENT("Email sent"),

        LOGOUT_SUCCESSFUL("Logout successful"),

        TOOL_CREATED("Tool created successfully!"),
        TOOL_LISTED("Found %s tool/s"),
        TOOL_UPDATED("Tool details updated"),
        TOOL_UPLOADED("Tools uploaded from Excel file successfully. Imported %s tools"),
        TOOL_DELETED("Tool deleted")

        ;

        private final String message;

    }

    @Getter
    @RequiredArgsConstructor
    public enum AppMessage implements EnumMethods {

        CREDENTIALS_EMAIL_TEMPLATE("credentialsTemplate.html"),
        CREDENTIALS_RECOVERY_SUBJECT("GC8Inventory : [Recuperación de credenciales] 🙃"),

        EMAIL_IMAGE_PARAMETER("image"),
        EMAIL_IMAGE_PNG("image/png"),

        COPYRIGHT("© gc8inventory %d")

        ;

        private final String message;

    }

    @Getter
    @RequiredArgsConstructor
    public enum ErrorMessage implements EnumMethods {
        TOKEN_NOT_PARSEABLE("This token is not parseable"),
        TOKEN_NOT_VALID("This token is not valid"),
        TOKEN_NOT_FOUND("This token doesn't exist in DB"),
        JWT_NOT_FOR_RECOVERY("This token is not for recover the account. Sorry, mate!"),
        RECOVERY_TOKEN_NOT_VALID_NOT_FOUND("This recovery token is not valid or is not found"),

        USER_ALREADY_EXIST("There's already a user with this id or email"),
        USER_NOT_FOUND("User not found"),
        USER_NOT_FOUND_TOKEN("User id or user from token not found, or token is not valid"),
        USER_PASSWORD_DOESNT_MATCH("Password provided for this email doesn't match our records"),

        EMAIL_NOT_SENT("Email not sent!"),
        EMAIL_SENDING_ERROR("There was an error sending email with 'subject' {} to: {}. The error was {}"),
        EMAIL_CREDENTIALS_SENDING_ERROR("There was an error sending email to: {}. The error was {}"),

        VOLUNTEER_ALREADY_EXIST("There's a volunteer with this id"),
        VOLUNTEER_NOT_FOUND("The volunteer you're searching for with this id couldn't be found"),

        TOOL_NOT_FOUND("Tool with id %s not found"),

        STATUS_NOT_FOUND("Status not found"),


        LOCATION_NOT_FOUND("Location with id %s not found."),

        GROUP_NOT_FOUND("Group with id %s not found."),

        CATEGORY_PARENT_NOT_FOUND("%s with id %s not found."),

        LOCATION_NOT_FOUND_EXCEL("Location %s not found. Please, fix the excel sheet or add it. Valid locations: %s"),
        GROUP_NOT_FOUND_EXCEL("Group %s not found. Please, fix the excel sheet or add it. Valid groups: %s"),
        CATEGORY_SON_NOT_FOUND("%s %s not found. Please, use a valid one or add it. Valid %ss: %s"),
        RUNTIME_EXCEPTION("Error processing data, check your request"),
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
