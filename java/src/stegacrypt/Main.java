package stegacrypt;

import stegacrypt.gui.StegaCryptGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main - Entry point of the StegaCrypt application.
 * 
 * Demonstrates:
 * - APPLICATION ENTRY POINT (main method)
 * - SWING THREAD SAFETY (SwingUtilities.invokeLater)
 * - LOOK AND FEEL configuration
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       StegaCrypt v1.0                ║");
        System.out.println("║  Steganography using LSB Encoding    ║");
        System.out.println("║  Core Java PBL Project               ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Using default look and feel.");
        }

        // Launch GUI on Event Dispatch Thread — thread safety
        SwingUtilities.invokeLater(() -> {
            StegaCryptGUI gui = new StegaCryptGUI();
            gui.setVisible(true);
            System.out.println("GUI launched successfully.");
        });
    }
}
