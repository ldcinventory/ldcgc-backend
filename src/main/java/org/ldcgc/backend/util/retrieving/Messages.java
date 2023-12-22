package org.ldcgc.backend.util.retrieving;

public class Messages {

    public class Info {

        public static final String

            RECOVERY_TOKEN_VALID = "Recovery token valid",

            USER_CREATED = "User created successfully!",
            USER_LISTED = "Found %s user/s",
            USER_UPDATED = "User details updated",
            USER_CREDENTIALS_UPDATED = "User credentials updated",
            USER_DELETED = "User deleted",

            EULA_ALREADY_ACCEPTED = "EULA for %s already accepted",
            EULA_ACCEPTED = "EULA for %s accepted",
            EULA_REJECTED = "EULA for %s rejected. %s",
            EULA_PENDING = "EULA for %s pending",
            EULA_DELETE_USER = "The user is deleted",
            EULA_DOWNGRADE_USER = "The user in downgrade to standard user",

            VOLUNTEER_CREATED = "Volunteer created",
            VOLUNTEER_LISTED = "Found %s volunteer/s",
            VOLUNTEER_UPDATED = "Volunteer details updated",
            VOLUNTEER_DELETED = "Volunteer deleted",

            CREDENTIALS_EMAIL_SENT = "Credentials email sent",
            EMAIL_SENT = "Email sent",

            LOGOUT_SUCCESSFUL = "Logout successful",

            TOOL_CREATED = "Tool created successfully!",
            TOOL_LISTED = "Found %s tool/s",
            TOOL_UPDATED = "Tool details updated",
            TOOL_UPLOADED = "Tools uploaded from Excel file successfully. Imported %s tools",
            TOOL_DELETED = "Tool deleted"

            ;

    }

    public class App {

        public static final String

            EULA_SELECT_ACTION = "EULA for %s: Please select an action to do",
            EULA_ENDPOINT = "/api/eula",

            CREDENTIALS_EMAIL_TEMPLATE = "credentialsTemplate.html",
            CREDENTIALS_RECOVERY_SUBJECT = "GC8Inventory : [Recuperación de credenciales] 🙃",

            EMAIL_IMAGE_PARAMETER = "image",
            EMAIL_IMAGE_PNG = "image/png",

            COPYRIGHT = "© gc8inventory %d"

            ;

    }

    public class Error {

        public static final String
            TOKEN_NOT_PARSEABLE = "This token is not parseable",
            TOKEN_NOT_VALID = "This token is not valid",
            TOKEN_NOT_FOUND = "This token doesn't exist in DB",
            JWT_NOT_FOR_RECOVERY = "This token is not for recover the account. Sorry, mate!",
            RECOVERY_TOKEN_NOT_VALID_NOT_FOUND = "This recovery token is not valid or is not found",

            USER_ALREADY_EXIST = "There's already a user with this id or email",
            USER_NOT_FOUND = "User not found",
            USER_NOT_FOUND_TOKEN = "User id or user from token not found, or token is not valid",
            USER_PERMISSION_ROLE = "User doesn't have the permission to change its own role, even if it's admin",
            USER_PERMISSION_OTHER = "User doesn't have the permission to change other users",
            USER_PASSWORD_DONT_MATCH = "Password provided for this email doesn't match our records",

            EULA_ACTION_INVALID = "EULA action is not valid",
            EULA_STANDARD_NOT_ACCEPTED = "EULA for users not accepted yet",
            EULA_MANAGER_NOT_ACCEPTED = "EULA for managers not accepted yet",

            EMAIL_NOT_SENT = "Email not sent!",
            EMAIL_SENDING_ERROR = "There was an error sending email with 'subject' {} to: {}. The error was {}",
            EMAIL_CREDENTIALS_SENDING_ERROR = "There was an error sending email to: {}. The error was {}",

            VOLUNTEER_ALREADY_EXIST = "There's a volunteer with this builder assistant id: %s",
            VOLUNTEER_TOKEN_NOT_EXIST = "The volunteer from this token doesn't exist or is not found",
            VOLUNTEER_NOT_FOUND = "The volunteer you're searching for with this id couldn't be found",
            VOLUNTEER_ID_ALREADY_TAKEN = "Volunteer's Builder Assistant Id already assigned to another volunteer",

            TOOL_NOT_FOUND = "Tool with id %s not found",

            STATUS_NOT_FOUND = "Status not found",

            LOCATION_NOT_FOUND = "Location with id %s not found.",

            GROUP_NOT_FOUND = "Group with id %s not found.",

            CATEGORY_PARENT_NOT_FOUND = "%s with id %s not found.",

            LOCATION_NOT_FOUND_EXCEL = "Location %s not found. Please, fix the excel sheet or add it. Valid locations: %s",
            GROUP_NOT_FOUND_EXCEL = "Group %s not found. Please, fix the excel sheet or add it. Valid groups: %s",
            CATEGORY_SON_NOT_FOUND = "%s %s not found. Please, use a valid one or add it. Valid %ss: %s",
            RUNTIME_EXCEPTION = "Error processing data, check your request",
            ENDPOINT_NOT_IMPLEMENTED = "This endpoint is not implemented yet"

            ;

    }

}
