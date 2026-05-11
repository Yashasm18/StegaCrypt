package stegacrypt.gui;

import stegacrypt.core.LSBSteganography;
import stegacrypt.core.SteganographyEngine;
import stegacrypt.core.StegoException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * StegaCryptGUI - Swing-based graphical user interface.
 * 
 * Demonstrates: SWING GUI, EVENT HANDLING, MULTITHREADING (SwingWorker),
 * INHERITANCE (extends JFrame), ANONYMOUS CLASSES (ActionListener).
 */
public class StegaCryptGUI extends JFrame {

    // UI Components
    private JTabbedPane tabbedPane;
    private JLabel encodeImageLabel, decodeImageLabel;
    private JTextArea messageArea, decodeResultArea;
    private JButton encodeLoadBtn, encodeSaveBtn, decodeLoadBtn, decodeBtn;
    private JLabel statusLabel;

    // Engine — uses INTERFACE type (polymorphism)
    private final SteganographyEngine engine;
    private String loadedEncodePath;
    private String loadedDecodePath;

    public StegaCryptGUI() {
        this.engine = new LSBSteganography(); // Polymorphism
        initializeUI();
    }

    private void initializeUI() {
        setTitle("StegaCrypt — Steganography Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel with dark theme
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(20, 20, 35));

        // Title
        JLabel titleLabel = new JLabel("🔐 StegaCrypt", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 212, 255));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.addTab("🔒 Encode", createEncodePanel());
        tabbedPane.addTab("🔓 Decode", createDecodePanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(0, 245, 160));
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createEncodePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Image preview
        encodeImageLabel = new JLabel("No image loaded", SwingConstants.CENTER);
        encodeImageLabel.setForeground(Color.GRAY);
        encodeImageLabel.setPreferredSize(new Dimension(300, 200));
        encodeImageLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));

        encodeLoadBtn = new JButton("📂 Load Image");
        styleButton(encodeLoadBtn);
        encodeLoadBtn.addActionListener(e -> loadImageForEncode());

        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setOpaque(false);
        imagePanel.add(encodeImageLabel, BorderLayout.CENTER);
        imagePanel.add(encodeLoadBtn, BorderLayout.SOUTH);
        panel.add(imagePanel, BorderLayout.WEST);

        // Message input
        JPanel msgPanel = new JPanel(new BorderLayout(5, 5));
        msgPanel.setOpaque(false);

        JLabel msgLabel = new JLabel("Secret Message:");
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        msgPanel.add(msgLabel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        messageArea.setBackground(new Color(15, 15, 30));
        messageArea.setForeground(Color.WHITE);
        messageArea.setCaretColor(new Color(0, 212, 255));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        msgPanel.add(scrollPane, BorderLayout.CENTER);

        encodeSaveBtn = new JButton("🔐 Encode & Save");
        styleButton(encodeSaveBtn);
        encodeSaveBtn.addActionListener(e -> encodeMessage());
        msgPanel.add(encodeSaveBtn, BorderLayout.SOUTH);

        panel.add(msgPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDecodePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Image preview
        decodeImageLabel = new JLabel("No image loaded", SwingConstants.CENTER);
        decodeImageLabel.setForeground(Color.GRAY);
        decodeImageLabel.setPreferredSize(new Dimension(300, 200));
        decodeImageLabel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));

        decodeLoadBtn = new JButton("📂 Load Encoded Image");
        styleButton(decodeLoadBtn);
        decodeLoadBtn.addActionListener(e -> loadImageForDecode());

        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setOpaque(false);
        imagePanel.add(decodeImageLabel, BorderLayout.CENTER);
        imagePanel.add(decodeLoadBtn, BorderLayout.SOUTH);
        panel.add(imagePanel, BorderLayout.WEST);

        // Result area
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));
        resultPanel.setOpaque(false);

        JLabel resultLabel = new JLabel("Decoded Message:");
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        decodeResultArea = new JTextArea();
        decodeResultArea.setEditable(false);
        decodeResultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        decodeResultArea.setBackground(new Color(15, 15, 30));
        decodeResultArea.setForeground(new Color(0, 245, 160));
        decodeResultArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(decodeResultArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        decodeBtn = new JButton("🔓 Decode Message");
        styleButton(decodeBtn);
        decodeBtn.addActionListener(e -> decodeMessage());
        resultPanel.add(decodeBtn, BorderLayout.SOUTH);

        panel.add(resultPanel, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(new Color(0, 180, 220));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadImageForEncode() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "bmp"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadedEncodePath = file.getAbsolutePath();
            ImageIcon icon = new ImageIcon(
                new ImageIcon(loadedEncodePath).getImage().getScaledInstance(280, 180, Image.SCALE_SMOOTH)
            );
            encodeImageLabel.setIcon(icon);
            encodeImageLabel.setText("");
            try {
                int capacity = engine.getCapacity(loadedEncodePath);
                statusLabel.setText("Image loaded. Capacity: " + capacity + " characters");
            } catch (StegoException e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        }
    }

    private void loadImageForDecode() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadedDecodePath = file.getAbsolutePath();
            ImageIcon icon = new ImageIcon(
                new ImageIcon(loadedDecodePath).getImage().getScaledInstance(280, 180, Image.SCALE_SMOOTH)
            );
            decodeImageLabel.setIcon(icon);
            decodeImageLabel.setText("");
            statusLabel.setText("Encoded image loaded. Ready to decode.");
        }
    }

    /** Encode using SwingWorker — demonstrates MULTITHREADING */
    private void encodeMessage() {
        if (loadedEncodePath == null || messageArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Load an image and enter a message first!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("stegacrypt_encoded.png"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String outputPath = chooser.getSelectedFile().getAbsolutePath();
        if (!outputPath.endsWith(".png")) outputPath += ".png";
        final String finalOutput = outputPath;

        statusLabel.setText("Encoding...");
        encodeSaveBtn.setEnabled(false);

        // SwingWorker — runs encoding in background thread
        final String message = messageArea.getText();
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return engine.encode(loadedEncodePath, finalOutput, message);
            }

            @Override
            protected void done() {
                try {
                    String stats = get();
                    statusLabel.setText("✅ Encoded successfully! Saved to: " + finalOutput);
                    JOptionPane.showMessageDialog(
                        StegaCryptGUI.this, stats, "Encoding Complete", JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (Exception e) {
                    statusLabel.setText("❌ Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        StegaCryptGUI.this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    encodeSaveBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    /** Decode using SwingWorker — demonstrates MULTITHREADING */
    private void decodeMessage() {
        if (loadedDecodePath == null) {
            JOptionPane.showMessageDialog(this, "Load an encoded image first!");
            return;
        }

        statusLabel.setText("Decoding...");
        decodeBtn.setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return engine.decode(loadedDecodePath);
            }

            @Override
            protected void done() {
                try {
                    String message = get();
                    decodeResultArea.setText(message);
                    statusLabel.setText("✅ Message decoded! Length: " + message.length() + " chars");
                } catch (Exception e) {
                    decodeResultArea.setText("");
                    statusLabel.setText("❌ " + e.getMessage());
                    JOptionPane.showMessageDialog(
                        StegaCryptGUI.this, e.getMessage(), "Decode Error", JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    decodeBtn.setEnabled(true);
                }
            }
        }.execute();
    }
}
