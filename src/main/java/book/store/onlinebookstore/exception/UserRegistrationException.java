package book.store.onlinebookstore.exception;

public class UserRegistrationException extends Exception {
    public UserRegistrationException(String message) {
        super(message);
    }

    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
