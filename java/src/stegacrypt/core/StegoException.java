package stegacrypt.core;

/**
 * StegoException - Demonstrates CUSTOM EXCEPTION HANDLING
 * 
 * Custom checked exception for steganography-related errors.
 * Extends Exception (not RuntimeException) to force callers to handle it.
 */
public class StegoException extends Exception {
    
    private final ErrorType errorType;
    
    /**
     * Enum for categorizing error types — demonstrates ENUM usage.
     */
    public enum ErrorType {
        IMAGE_NOT_FOUND("Image file not found"),
        IMAGE_READ_ERROR("Could not read image file"),
        IMAGE_WRITE_ERROR("Could not write image file"),
        MESSAGE_TOO_LONG("Message exceeds image capacity"),
        INVALID_FORMAT("Image format not supported (use PNG)"),
        DECODE_ERROR("No valid message found in image"),
        EMPTY_MESSAGE("Message cannot be empty");
        
        private final String description;
        
        ErrorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public StegoException(ErrorType errorType) {
        super(errorType.getDescription());
        this.errorType = errorType;
    }
    
    public StegoException(ErrorType errorType, String additionalInfo) {
        super(errorType.getDescription() + ": " + additionalInfo);
        this.errorType = errorType;
    }
    
    public StegoException(ErrorType errorType, Throwable cause) {
        super(errorType.getDescription(), cause);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
}
