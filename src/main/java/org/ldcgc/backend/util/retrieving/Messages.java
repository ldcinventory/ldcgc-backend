package org.ldcgc.backend.util.retrieving;

public class Messages {

    public static class Info {

        public static final String

            RECOVERY_TOKEN_VALID = "Recovery token valid",
            TOKEN_REFRESHED = "Regular token recreated from refresh token",

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
            TOOL_UNTOUCHED = "Tool untouched",
            TOOL_IMAGES_UPDATED = "Tool details for attached images updated",
            TOOL_UPLOADED = "Tools uploaded from Excel file successfully. Imported %s tools",
            TOOL_DELETED = "Tool deleted",

            CONSUMABLE_CREATED = "Consumable created successfully!",
            CONSUMABLE_UPDATED = "Consumable details updated",
            CONSUMABLE_UNTOUCHED = "Consumable untouched",
            CONSUMABLE_IMAGES_UPDATED = "Consumable details for attached images updated",
            CONSUMABLE_LISTED = "Found %s consumable/s",
            CONSUMABLE_DELETED = "Consumable deleted",
            CONSUMABLES_UPLOADED = "Consumables uploaded from Excel file successfully. Imported %s consumables",

            CONSUMABLE_REGISTER_CREATED = "Consumable register created",
            CONSUMABLE_REGISTER_LISTED = "Found %s consumable registers",
            CONSUMABLE_REGISTER_UPDATED = "Consumable register updated",
            CONSUMABLE_REGISTER_DELETED = "Consumable register deleted",

            AVAILABILITY_UPDATED = "Availability updated",
            AVAILABILITY_CLEARED = "Availability cleared",

            ABSENCES_FOUND = "%s absences found",
            ABSENCE_CREATED = "Absence created",
            ABSENCE_UPDATED = "Absence updated",
            ABSENCE_DELETED = "Absence deleted",

            CSV_VOLUNTEERS_CREATED = "Volunteers created from CSV. There were imported %s volunteers."

            ;

    }

    public static class App {

        public static final String

        EULA_SELECT_ACTION = "EULA for %s: Please select an action to do",
        EULA_ENDPOINT = "/api/eula",

        CREDENTIALS_EMAIL_TEMPLATE = "credentialsTemplate.html",
        CREDENTIALS_RECOVERY_SUBJECT = "GC8Inventory : [Recuperación de credenciales] 🙃",

        EMAIL_IMAGE_PARAMETER = "image",
        EMAIL_IMAGE_PNG = "image/png",

        COPYRIGHT = "© gc8inventory %d";

    }

    public static class Error {

        public static final String
            TOKEN_NOT_FOUND_HEADERS = "The token is not found in headers",
            TOKEN_NOT_PARSEABLE = "This token is not parseable",
            TOKEN_NOT_VALID = "This token is not valid",
            TOKEN_NOT_FOUND = "This token doesn't exist in DB",
            TOKEN_EXPIRED = "This token is expired",
            JWT_NOT_FOR_RECOVERY_REFRESH = "This token is not for recover the account or refresh actual expired token. Sorry, mate!",
            RECOVERY_TOKEN_NOT_VALID_NOT_FOUND = "This recovery token is not valid or is not found",
            REFRESH_TOKEN_NOT_VALID = "This refresh token is not valid",

            USER_ALREADY_EXIST = "There's already a user with this id or email",
            USER_NOT_FOUND = "User not found",
            USER_NOT_FOUND_TOKEN = "User id or user from token not found, or token is not valid",
            USER_PERMISSION_ROLE = "User doesn't have the permission to change its own role, even if it's admin",
            USER_PERMISSION_ROLE_OTHER = "User doesn't have the permission to change other's roles, or elevate them",
            USER_PERMISSION_OTHER = "User doesn't have the permission to change other users",
            USER_PASSWORD_DONT_MATCH = "Password provided for this email doesn't match our records",
            USER_VOLUNTEER_ALREADY_ASSIGNED = "This volunteer is already assigned to another user",
            USER_DOESNT_HAVE_VOLUNTEER = "This user doesn't have any volunteer associated",

            EULA_ACTION_INVALID = "EULA action is not valid",
            EULA_STANDARD_NOT_ACCEPTED = "EULA for users not accepted yet",
            EULA_MANAGER_NOT_ACCEPTED = "EULA for managers not accepted yet",

            EMAIL_NOT_SENT = "Email not sent!",
            EMAIL_SENDING_ERROR = "There was an error sending email with 'subject' {} to: {}. The error was {}",
            EMAIL_CREDENTIALS_SENDING_ERROR = "There was an error sending email to: {}. The error was {}",

            VOLUNTEER_ALREADY_EXIST = "There's a volunteer with this builder assistant id: %s",
            VOLUNTEER_NOT_INFORMED = "Volunter is not informed in the payload",
            VOLUNTEER_TOKEN_NOT_EXIST = "The volunteer from this token doesn't exist or is not found",
            VOLUNTEER_NOT_FOUND = "The volunteer you're searching for with this id couldn't be found",
            VOLUNTEER_BARCODE_NOT_FOUND = "The volunteer you're searching for with builder assistant id '%s' couldn't be found",
            VOLUNTEER_ID_ALREADY_TAKEN = "Volunteer's Builder Assistant Id already assigned to another volunteer",
            VOLUNTEER_WITHOUT_BA_ID = "Volunteer hasn't Builder Assistant Id assigned",
            VOLUNTEER_ABSENCES_EMPTY = "Volunter hasn't informed any absence",

            TOOL_NOT_FOUND = "Tool with id '%s' not found",
            TOOL_BARCODE_ALREADY_EXISTS = "There is another tool with the same barcode '%s' in the database. Please make sure that the barcode is unique",
            TOOL_ID_SHOULDNT_BE_PRESENT = "The request shouldn't include an id. Please, make sure that the tool id is null",
            TOOL_IMAGE_INFORMED_NOT_FOUND = "The image with id '%s' is not registered for this tool",

            CONSUMABLE_NOT_FOUND = "Consumable with id '%s' not found",
            CONSUMABLE_BARCODE_NOT_FOUND = "Consumable with barcode '%s' not found",
            CONSUMABLE_BARCODE_ALREADY_EXISTS = "There is another consumable with the same barcode '%s' in the database. Please make sure that the barcode is unique",
            CONSUMABLE_BARCODE_USED_MANY_TIMES = "The barcode '%s' is used by more than one consumable. Please report to admin",
            CONSUMABLE_ID_SHOULDNT_BE_PRESENT = "The request shouldn't include an id. Please, make sure that the consumable id is null",
            CONSUMABLE_IMAGE_INFORMED_NOT_FOUND = "The image with id '%s' is not registered for this consumable",

            CONSUMABLE_REGISTER_NOT_FOUND = "Consumable register with id '%s' not found",
            CONSUMABLE_REGISTER_VOLUNTEER_DUPLICATED = "This consumable has been registered to this volunteer",
            CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_ALLOCATE = "There is no enough amount to allocate for this consumable to this volunteer",
            CONSUMABLE_REGISTER_CLOSED_FOR_MODIFICATIONS = "Consumable register only allows modify stock amount returned and volunteer assigned",
            CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_RETURN = "The amount to be returned to stock for this consumable is higher than the amount allocated",
            CONSUMABLE_REGISTER_ALLOCATE_DATE_BEFORE_TODAY = "The date of allocation the consumable is before the actual date and time",
            CONSUMABLE_REGISTER_RETURN_DATE_BEFORE_ALLOCATE = "The date of returning the consumable is before the date and time of allocations",
            CONSUMABLE_REGISTER_RETURN_DATE_AFTER_TODAY = "The date of returning the consumable is after the actual date and time",
            CONSUMABLE_REGISTER_DATA_OUT_NOT_COMPLETE = "The data of returning the consumible lacks of the date or the amount",
            CONSUMABLE_REGISTER_DATA_CLOSING_NOT_COMPLETE = "The data for closing the consumible lacks any of the required fields: registrationOut, stockAmountOut",

            UPLOAD_IMAGES_TOO_FEW_ARGUMENTS = "There are few arguments defined for this operation. Inform a tool or a consumable at least",

            UPLOAD_IMAGES_TOO_MANY_ARGUMENTS = "There are many arguments defined for this operation. Inform a tool or a consumable only",
            CLEAN_IMAGES_ENTITY_CANT_BE_CASTABLE = "Couldn't cast the entity to Tool or Consumable",

            STATUS_NOT_FOUND = "Status '%s' not found",

            LOCATION_NOT_FOUND = "Location with id/name '%s' not found.",
            LOCATION_NOT_FOUND_EXCEL = "Location '%s' not found. Please, fix the excel sheet or add it. Valid locations: %s",

            GROUP_NOT_FOUND = "Group with id/name '%s' not found.",
            GROUP_NOT_FOUND_EXCEL = "Group '%s' not found. Please, fix the excel sheet or add it. Valid groups: %s",

            CATEGORY_PARENT_NOT_FOUND = "%s with id '%s' not found.",
            CATEGORY_SON_NOT_FOUND = "%s %s not found. Please, use a valid one or add it. Valid %ss: %s",
            CATEGORY_NOT_FOUND = "This category '%s' couldn't be found",
            BRAND_NOT_FOUND = "This brand '%s' couldn't be found",

            STOCK_TYPE_NOT_FOUND = "Stock type '%s' not found",
            TIME_UNIT_NOT_FOUND = "Time unit '%s' not found",

            RUNTIME_EXCEPTION = "Error processing data, check your request",
            ENDPOINT_NOT_IMPLEMENTED = "This endpoint is not implemented yet",

            ABSENCE_VOLUNTEER_NOT_FOUND = "This absence was not found for this volunteer",
            VOLUNTEER_FROM_ABSENCE_NOT_FOUND = "The volunteer informed in the absence is not found (maybe not linked?)",
            ABSENCE_NOT_FOUND = "Absence not found",

            CSV_NAME_ERROR = "The name provided has some invalid characters",
            CSV_LAST_NAME_ERROR = "The last name provided has some invalid characters",
            CSV_BA_IDENTIFIER_ERROR = "The builder assistant ID is not valid",
            CSV_VOLUNTEER_DUPLICATED = "The user with email '%s' is duplicated in the shift",
            CSV_PROCESS_ERROR = "Validation error when trying to process CSV with delimiter",

            EXCEL_VALUE_INCORRECT = "The value '%s' is incorrect on excel row %s, column %s",
            EXCEL_CELL_TYPE_INCORRECT = "The type of cell on row %s, column %s is incorrect. Valid type: %s",
            EXCEL_PARSE_ERROR = "There has been an error while parsing Excel file, please check that the template is correct and all the values are filled out and have a correct type.",
            EXCEL_EMPTY_CELL = "An empty value is not allowed on excel row %s, column %s",

            PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL = "The index requested is out of bounds of the total pages available.",

            IMAGE_QUALITY_DEFINITION_OUT_OF_RANGE = "The value of image quality to compress is between 0.0 and 1.0",
            UNEXPECTED_ERROR = "Unexpected error ocurred, or uncontrolled exception had been thrown."

        ;

    }

}
