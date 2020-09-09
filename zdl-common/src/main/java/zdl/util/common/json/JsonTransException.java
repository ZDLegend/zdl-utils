package zdl.util.common.json;

public class JsonTransException extends RuntimeException {
    public JsonTransException() {
        super();
    }

    public JsonTransException(String message) {
        super(message);
    }

    public JsonTransException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonTransException(Throwable cause) {
        super(cause);
    }

    protected JsonTransException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
