package bg.sofia.uni.fmi.mjt.crypto.factory.exception;

public class UnsupportedCommandArgumentsException extends IllegalArgumentException {
    public UnsupportedCommandArgumentsException() {
    }

    public UnsupportedCommandArgumentsException(String s) {
        super(s);
    }

    public UnsupportedCommandArgumentsException(Throwable cause) {
        super(cause);
    }
}
