package stegacrypt.core;

/**
 * SteganographyEngine Interface - Demonstrates ABSTRACTION
 * 
 * This interface defines the contract for any steganography implementation.
 * Using an interface allows polymorphism — we can swap LSB encoding with
 * any other method (e.g., DCT-based) without changing the rest of the code.
 */
public interface SteganographyEngine {
    
    /**
     * Encodes a secret message into an image file.
     * 
     * @param inputImagePath  Path to the original image
     * @param outputImagePath Path to save the encoded image
     * @param message         The secret message to hide
     * @return Encoding statistics as a formatted string
     * @throws StegoException if encoding fails
     */
    String encode(String inputImagePath, String outputImagePath, String message) throws StegoException;
    
    /**
     * Decodes a hidden message from an encoded image file.
     * 
     * @param imagePath Path to the encoded image
     * @return The hidden message
     * @throws StegoException if decoding fails
     */
    String decode(String imagePath) throws StegoException;
    
    /**
     * Returns the maximum number of characters that can be hidden in an image.
     * 
     * @param imagePath Path to the image
     * @return Maximum character capacity
     * @throws StegoException if image cannot be read
     */
    int getCapacity(String imagePath) throws StegoException;
}
