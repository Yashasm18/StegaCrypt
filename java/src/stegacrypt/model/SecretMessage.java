package stegacrypt.model;

/**
 * SecretMessage - Model class for the secret message.
 * 
 * Demonstrates:
 * - ENCAPSULATION (private fields, getters/setters)
 * - STRING MANIPULATION
 * - METHOD DESIGN (toBinary, fromBinary)
 */
public class SecretMessage {
    
    // Private fields — Encapsulation
    private String content;
    private int length;
    private String binaryRepresentation;
    
    /**
     * Constructor with validation.
     */
    public SecretMessage(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Message content cannot be null");
        }
        this.content = content;
        this.length = content.length();
        this.binaryRepresentation = convertToBinary(content);
    }
    
    /**
     * Converts the message to its binary representation.
     * Each character is converted to 8-bit ASCII binary.
     * 
     * Example: "Hi" → "0100100001101001"
     */
    public String toBinary() {
        return binaryRepresentation;
    }
    
    /**
     * Static factory method to create a SecretMessage from binary data.
     * Demonstrates static method and factory pattern.
     */
    public static SecretMessage fromBinary(String binary) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            if (i + 8 > binary.length()) break;
            String byteStr = binary.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            message.append((char) charCode);
        }
        return new SecretMessage(message.toString());
    }
    
    /**
     * Private helper method — Encapsulation.
     */
    private String convertToBinary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            String charBinary = Integer.toBinaryString(c);
            // Pad to 8 bits
            while (charBinary.length() < 8) {
                charBinary = "0" + charBinary;
            }
            binary.append(charBinary);
        }
        return binary.toString();
    }
    
    /**
     * Returns the number of bits needed to encode this message.
     */
    public int getBitCount() {
        return binaryRepresentation.length();
    }
    
    // Getters — Encapsulation (read-only access)
    public String getContent() { return content; }
    public int getLength() { return length; }
    
    /**
     * Overriding toString — demonstrates polymorphism (Object class method).
     */
    @Override
    public String toString() {
        return String.format("SecretMessage[length=%d, bits=%d]", length, getBitCount());
    }
}
