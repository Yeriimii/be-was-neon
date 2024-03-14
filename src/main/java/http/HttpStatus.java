package http;

public enum HttpStatus {
    STATUS_OK(200, "OK"),
    STATUS_CREATED(201, "Created"),
    STATUS_MOVED_PERMANENTLY(301, "Moved Permanently"),
    STATUS_FORBIDDEN(403, "Forbidden"),
    STATUS_NOT_FOUND(404, "Not Found"),
    STATUS_NOT_ALLOWED(405, "Method Not Allowed"),
    ;

    public final int code;
    public final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
