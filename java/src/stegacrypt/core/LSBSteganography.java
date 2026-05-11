package stegacrypt.core;

import stegacrypt.model.StegoImage;
import stegacrypt.model.SecretMessage;
import stegacrypt.util.BitManipulator;
import stegacrypt.util.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LSBSteganography - Implements SteganographyEngine using LSB Encoding
 * 
 * Demonstrates:
 * - INHERITANCE (implements interface)
 * - POLYMORPHISM (can be used wherever SteganographyEngine is expected)
 * - ENCAPSULATION (private helper methods, data hiding)
 * - COLLECTIONS (ArrayList, HashMap)
 * - BIT MANIPULATION (core algorithm)
 * 
 * Algorithm:
 * 1. Convert message to binary string
 * 2. Prepend 32-bit message length header
 * 3. For each bit, replace the LSB of the image's RGB channels
 * 4. Save the modified image as PNG (lossless format)
 */
public class LSBSteganography implements SteganographyEngine {
    
    // Encapsulated fields — private access
    private final BitManipulator bitManipulator;
    private final ImageProcessor imageProcessor;
    private Map<String, Object> lastEncodingStats;  // HashMap usage
    
    /**
     * Constructor — demonstrates dependency injection pattern.
     */
    public LSBSteganography() {
        this.bitManipulator = new BitManipulator();
        this.imageProcessor = new ImageProcessor();
        this.lastEncodingStats = new HashMap<>();
    }
    
    /**
     * Encodes a secret message into an image using LSB substitution.
     * 
     * @param inputImagePath  Path to the original image
     * @param outputImagePath Path where encoded image will be saved
     * @param message         The secret message to hide
     * @return Encoding statistics as formatted string
     * @throws StegoException if any error occurs
     */
    @Override
    public String encode(String inputImagePath, String outputImagePath, String message) throws StegoException {
        // Validate inputs
        if (message == null || message.isEmpty()) {
            throw new StegoException(StegoException.ErrorType.EMPTY_MESSAGE);
        }
        
        // Create model objects — demonstrates OOP
        StegoImage stegoImage = new StegoImage(inputImagePath);
        SecretMessage secretMessage = new SecretMessage(message);
        
        // Read the image
        BufferedImage image = imageProcessor.readImage(inputImagePath);
        stegoImage.setWidth(image.getWidth());
        stegoImage.setHeight(image.getHeight());
        
        // Check capacity
        int capacity = calculateCapacity(image);
        if (message.length() > capacity) {
            throw new StegoException(
                StegoException.ErrorType.MESSAGE_TOO_LONG,
                "Max " + capacity + " characters, got " + message.length()
            );
        }
        
        // Convert message to binary with length header
        String binaryMessage = secretMessage.toBinary();
        String lengthHeader = bitManipulator.intToBinary(message.length(), 32);
        String fullBinary = lengthHeader + binaryMessage;
        
        // === CORE LSB ENCODING ALGORITHM ===
        int bitIndex = 0;
        int pixelsModified = 0;
        List<int[]> modifiedPixels = new ArrayList<>();  // ArrayList usage
        
        for (int y = 0; y < image.getHeight() && bitIndex < fullBinary.length(); y++) {
            for (int x = 0; x < image.getWidth() && bitIndex < fullBinary.length(); x++) {
                int pixel = image.getRGB(x, y);
                
                // Extract RGB channels using bit manipulation
                int alpha = bitManipulator.extractChannel(pixel, 24);
                int red   = bitManipulator.extractChannel(pixel, 16);
                int green = bitManipulator.extractChannel(pixel, 8);
                int blue  = bitManipulator.extractChannel(pixel, 0);
                
                // Replace LSB of each channel with message bits
                if (bitIndex < fullBinary.length()) {
                    red = bitManipulator.setLSB(red, fullBinary.charAt(bitIndex) - '0');
                    bitIndex++;
                }
                if (bitIndex < fullBinary.length()) {
                    green = bitManipulator.setLSB(green, fullBinary.charAt(bitIndex) - '0');
                    bitIndex++;
                }
                if (bitIndex < fullBinary.length()) {
                    blue = bitManipulator.setLSB(blue, fullBinary.charAt(bitIndex) - '0');
                    bitIndex++;
                }
                
                // Reconstruct pixel using bit manipulation
                int newPixel = bitManipulator.constructPixel(alpha, red, green, blue);
                image.setRGB(x, y, newPixel);
                
                pixelsModified++;
                modifiedPixels.add(new int[]{x, y});
            }
        }
        
        // Save encoded image
        imageProcessor.writeImage(image, outputImagePath);
        
        // Store statistics in HashMap — demonstrates Collections
        lastEncodingStats.clear();
        lastEncodingStats.put("messageLength", message.length());
        lastEncodingStats.put("bitsEncoded", fullBinary.length());
        lastEncodingStats.put("pixelsModified", pixelsModified);
        lastEncodingStats.put("totalPixels", image.getWidth() * image.getHeight());
        lastEncodingStats.put("capacity", capacity);
        lastEncodingStats.put("modifiedPixelsList", modifiedPixels);
        
        // Update model objects
        stegoImage.setEncoded(true);
        stegoImage.setHiddenMessage(secretMessage);
        
        return formatStats();
    }
    
    /**
     * Decodes a hidden message from an encoded image.
     * 
     * @param imagePath Path to the encoded image
     * @return The decoded secret message
     * @throws StegoException if decoding fails
     */
    @Override
    public String decode(String imagePath) throws StegoException {
        BufferedImage image = imageProcessor.readImage(imagePath);
        
        // === CORE LSB DECODING ALGORITHM ===
        StringBuilder binaryData = new StringBuilder();
        
        // Read LSBs from all pixels
        outerLoop:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);
                
                int red   = bitManipulator.extractChannel(pixel, 16);
                int green = bitManipulator.extractChannel(pixel, 8);
                int blue  = bitManipulator.extractChannel(pixel, 0);
                
                // Extract LSB from each channel
                binaryData.append(bitManipulator.getLSB(red));
                binaryData.append(bitManipulator.getLSB(green));
                binaryData.append(bitManipulator.getLSB(blue));
                
                // Once we have enough bits for the header + potential message, we can stop
                if (binaryData.length() > 32) {
                    int msgLength = bitManipulator.binaryToInt(binaryData.substring(0, 32));
                    if (msgLength <= 0 || msgLength > 100000) {
                        throw new StegoException(StegoException.ErrorType.DECODE_ERROR);
                    }
                    int totalBitsNeeded = 32 + (msgLength * 8);
                    if (binaryData.length() >= totalBitsNeeded) {
                        break outerLoop;
                    }
                }
            }
        }
        
        // Extract message length from first 32 bits
        if (binaryData.length() < 32) {
            throw new StegoException(StegoException.ErrorType.DECODE_ERROR);
        }
        
        int messageLength = bitManipulator.binaryToInt(binaryData.substring(0, 32));
        
        if (messageLength <= 0 || messageLength > 100000) {
            throw new StegoException(StegoException.ErrorType.DECODE_ERROR);
        }
        
        // Extract message characters
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < messageLength; i++) {
            int start = 32 + (i * 8);
            int end = start + 8;
            if (end > binaryData.length()) {
                throw new StegoException(StegoException.ErrorType.DECODE_ERROR);
            }
            String charBinary = binaryData.substring(start, end);
            int charCode = bitManipulator.binaryToInt(charBinary);
            message.append((char) charCode);
        }
        
        return message.toString();
    }
    
    /**
     * Calculates how many characters can be hidden in an image.
     */
    @Override
    public int getCapacity(String imagePath) throws StegoException {
        BufferedImage image = imageProcessor.readImage(imagePath);
        return calculateCapacity(image);
    }
    
    /**
     * Private helper — demonstrates ENCAPSULATION.
     */
    private int calculateCapacity(BufferedImage image) {
        int totalPixels = image.getWidth() * image.getHeight();
        int totalBits = totalPixels * 3;  // 3 channels (RGB)
        return (totalBits - 32) / 8;      // Subtract 32-bit header, divide by 8 bits per char
    }
    
    /**
     * Formats encoding statistics from the HashMap.
     */
    private String formatStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║     ENCODING STATISTICS              ║\n");
        sb.append("╠══════════════════════════════════════╣\n");
        sb.append(String.format("║  Message Length  : %d chars%n", lastEncodingStats.get("messageLength")));
        sb.append(String.format("║  Bits Encoded    : %d%n", lastEncodingStats.get("bitsEncoded")));
        sb.append(String.format("║  Pixels Modified : %d / %d%n", 
            lastEncodingStats.get("pixelsModified"), lastEncodingStats.get("totalPixels")));
        sb.append(String.format("║  Image Capacity  : %d chars%n", lastEncodingStats.get("capacity")));
        sb.append("╚══════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Getter for encoding statistics — demonstrates ENCAPSULATION.
     */
    public Map<String, Object> getLastEncodingStats() {
        return new HashMap<>(lastEncodingStats);  // Return copy to protect internal state
    }
}
