package domain.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String number, boolean isNumber) {
        super("Category not found with Number: " + number);
    }
}
