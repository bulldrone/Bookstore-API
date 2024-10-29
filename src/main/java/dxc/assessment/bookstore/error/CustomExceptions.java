package dxc.assessment.bookstore.error;

public class CustomExceptions {

    public static class UnauthorizedError extends RuntimeException {
        public UnauthorizedError(String message) {
            super(message);
        }
    }

    public static class AuthenticationError extends RuntimeException {
        public AuthenticationError(String message) {
            super(message);
        }
    }

    public static class BookNotFoundError extends RuntimeException {
        public BookNotFoundError(String message) {
            super(message);
        }
    }

    public static class MissingParamQueryError extends RuntimeException {
        public MissingParamQueryError(String message) {
            super(message);
        }
    }

    public static class MissingJsonContentError extends RuntimeException {
        public MissingJsonContentError(String message) {
            super(message);
        }
    }

    public static class InternalServerError extends RuntimeException {
        public InternalServerError(String message) {
            super(message);
        }
    }
}