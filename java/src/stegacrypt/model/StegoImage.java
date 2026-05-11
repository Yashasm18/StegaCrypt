package stegacrypt.model;

/**
 * StegoImage - Model class representing an image used for steganography.
 * 
 * Demonstrates:
 * - ENCAPSULATION (private fields, public getters/setters)
 * - CONSTRUCTOR OVERLOADING
 * - COMPOSITION (has-a relationship with SecretMessage)
 * - toString() OVERRIDE (polymorphism)
 */
public class StegoImage {
    
    // Private fields — Encapsulation
    private String filePath;
    private int width;
    private int height;
    private boolean isEncoded;
    private SecretMessage hiddenMessage;  // Composition: has-a relationship
    
    /**
     * Constructor — initializes with file path.
     */
    public StegoImage(String filePath) {
        this.filePath = filePath;
        this.isEncoded = false;
        this.hiddenMessage = null;
    }
    
    /**
     * Overloaded constructor — demonstrates CONSTRUCTOR OVERLOADING.
     */
    public StegoImage(String filePath, int width, int height) {
        this(filePath);  // Constructor chaining with this()
        this.width = width;
        this.height = height;
    }
    
    /**
     * Calculates total pixel count.
     */
    public int getTotalPixels() {
        return width * height;
    }
    
    /**
     * Calculates maximum character capacity for steganography.
     * Each pixel stores 3 bits (RGB channels), minus 32-bit header.
     */
    public int getMaxCapacity() {
        return (getTotalPixels() * 3 - 32) / 8;
    }
    
    /**
     * Returns a formatted summary of the image.
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Image: ").append(filePath).append("\n");
        sb.append("Dimensions: ").append(width).append("x").append(height).append("\n");
        sb.append("Total Pixels: ").append(getTotalPixels()).append("\n");
        sb.append("Max Capacity: ").append(getMaxCapacity()).append(" characters\n");
        sb.append("Encoded: ").append(isEncoded ? "Yes" : "No").append("\n");
        if (isEncoded && hiddenMessage != null) {
            sb.append("Hidden Message: ").append(hiddenMessage.getLength()).append(" chars\n");
        }
        return sb.toString();
    }
    
    // Getters and Setters — Encapsulation
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public boolean isEncoded() { return isEncoded; }
    public void setEncoded(boolean encoded) { this.isEncoded = encoded; }
    
    public SecretMessage getHiddenMessage() { return hiddenMessage; }
    public void setHiddenMessage(SecretMessage hiddenMessage) { this.hiddenMessage = hiddenMessage; }
    
    /**
     * Overriding toString — demonstrates POLYMORPHISM.
     */
    @Override
    public String toString() {
        return String.format("StegoImage[%dx%d, encoded=%b, path=%s]", width, height, isEncoded, filePath);
    }
}
