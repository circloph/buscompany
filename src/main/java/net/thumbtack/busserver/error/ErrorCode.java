package net.thumbtack.busserver.error;

public enum ErrorCode {

    INVALID_ANTHROPONYM_VALUES("anthroponym must contain only Russian letters, numbers, space, dash and not be empty", "lastname, firstname, patronymic"),
    FIELD_TOO_LONG("length of fields should be less", "password"),
    INVALID_LOGIN_VALUE("login must contain only Russian, Latin letters, numbers and not be empty", "login"),
    SHORT_PASSWORD("password length must be longer", "password"),
    INVALID_EMAIL_VALUE("invalid email value", "email"),
    INVALID_NUMBER_PHONE_VALUE("invalid number phone value", "numberPhone"),
    USER_NOT_EXIST("invalid login or password", "login, password"),
    USER_EXIST("user with current login exists", "login"),
    ACCESS_DENIED("access denied", ""),
    INVALID_OLD_PASSWORD("password do not match", "oldPassword"),
    LAST_ADMINISTRATION("you are the last administration", "administration"),
    INVALID_DATE_VALUE("there is no such date on the trip", "date"),
    INVALID_PASSENGER_DATA("there is no such passenger in the order", "passenger"),
    NO_PLACE("no place", "passengers"),
    BUSY_PLACE("place is busy", "place"),
    INVALID_ORDER_ID("invalid order id value", "orderId"),
    IMPOSSIBLE_ACTION("impossible change/delete the approved trip", "trip"),
    INVALID_SCHEDULE_VALUE("schedule does not contain days of dispatch", "schedule"),
    SERVER_ERROR("unknown database problem", ""),
    INVALID_FORMAT_DATE("invalid format date", "dates"),
    INVALID_FORMAT_START("invalid format start", "start"),
    INVALID_FORMAT_DURATION("invalid format duration", "duration"),
    MUTUALLY_EXCLUSIVE_FIELDS("can be either a schedule or dates", ""),
    EMPTY_FIELD_SCHEDULE("schedule fields must not be empty", ""),
    NOT_FOUND("", ""),
    INVALID_JSON("", ""),
    MISSING_PARAMETER("", ""),
    MEDIA_TYPE_NOT_SUPPORTED("", "");

    private String message;
    private String field;

    ErrorCode(String message, String field) {
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
