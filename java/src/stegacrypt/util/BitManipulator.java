package stegacrypt.util;

/**
 * BitManipulator - Utility class for bit-level operations.
 * Demonstrates BIT MANIPULATION operators: AND, OR, SHIFT.
 */
public class BitManipulator {

    /** Extract a color channel from ARGB pixel. */
    public int extractChannel(int pixel, int position) {
        return (pixel >> position) & 0xFF;
    }

    /** Set the LSB of a value. (value & 11111110) | bit */
    public int setLSB(int value, int bit) {
        return (value & 0xFE) | (bit & 1);
    }

    /** Get the LSB of a value. */
    public int getLSB(int value) {
        return value & 1;
    }

    /** Construct ARGB pixel from channels. */
    public int constructPixel(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /** Convert int to binary string with padding. */
    public String intToBinary(int value, int bits) {
        String binary = Integer.toBinaryString(value);
        while (binary.length() < bits) binary = "0" + binary;
        return binary;
    }

    /** Convert binary string to int. */
    public int binaryToInt(String binary) {
        return Integer.parseInt(binary, 2);
    }
}
