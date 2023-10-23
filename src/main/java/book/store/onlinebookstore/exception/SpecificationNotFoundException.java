package book.store.onlinebookstore.exception;

public class SpecificationNotFoundException extends RuntimeException {
    public SpecificationNotFoundException(String message) {
        super(message);
    }

    public SpecificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
