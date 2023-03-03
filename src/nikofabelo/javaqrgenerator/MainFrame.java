package nikofabelo.javaqrgenerator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.commons.lang3.SystemUtils;

public class MainFrame extends Frame {
    static Panel panel;
    static String qrCodePath;

    public MainFrame() {
        super("javaQR-generator v1.1");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    public static void loadQRCode(String text)
        throws IOException, WriterException {

        BitMatrix matrix = new MultiFormatWriter().encode(
            text, BarcodeFormat.QR_CODE, 350, 350);
        MatrixToImageWriter.writeToFile(matrix, "PNG", new File(qrCodePath));

        if(panel.getComponentCount() != 1)
            panel.remove(1);

        BufferedImage qrCodeImage = ImageIO.read(new File(qrCodePath));
        ImageIcon qrCodeIcon = new ImageIcon(qrCodeImage);
        JLabel qrCodeLabel = new JLabel("", SwingConstants.CENTER);
        qrCodeLabel.setIcon(qrCodeIcon);

        panel.add(qrCodeLabel);
        panel.validate();
    }

    public static void main(String[] argv) throws FontFormatException, IOException,  WriterException {
        if(System.getProperty("java.io.tmpdir").endsWith(FileSystems.getDefault().getSeparator()))
            qrCodePath = System.getProperty("java.io.tmpdir")+"7ef9bc548e469b19b59f321add02d4c0";
        else
            qrCodePath = System.getProperty("java.io.tmpdir")
                + FileSystems.getDefault().getSeparator()+"7ef9bc548e469b19b59f321add02d4c0";

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                new File(qrCodePath).delete();
            }
        });

        MainFrame mainFrame = new MainFrame();
        mainFrame.setSize(750, 400);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setBackground(new Color(240, 240, 240));

        InputStream imgStream = MainFrame.class.getResourceAsStream("res/icon.png");
        BufferedImage appIcon = ImageIO.read(imgStream);
        mainFrame.setIconImage(appIcon);

        panel = new Panel();
        panel.setLayout(new GridLayout(1, 2));

        JTextArea textArea = new JTextArea();
        if(!SystemUtils.IS_OS_LINUX)
            textArea.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        else
            textArea.setFont(Font.createFont(Font.TRUETYPE_FONT,
                MainFrame.class.getResourceAsStream("res/font.ttf")).deriveFont(14f));
        textArea.setText("Write text here...");
        textArea.setCaretPosition(18);
        textArea.setFocusable(true);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try { loadQRCode(textArea.getText()); }
                catch(IOException | IllegalArgumentException | WriterException ex) {
                    JOptionPane.showMessageDialog(new JFrame(),
                        "Something happened while generating QR-code.\nERROR: "+ex.getMessage(),
                        "Runtime Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try { loadQRCode(textArea.getText()); }
                catch(IOException | IllegalArgumentException | WriterException ex) {
                    if(!ex.getMessage().contains("empty contents"))
                        JOptionPane.showMessageDialog(new JFrame(),
                            "Something happened while generating QR-code.\nERROR: "+ex.getMessage(),
                            "Runtime Error", JOptionPane.ERROR_MESSAGE);
                    else
                        Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        panel.add(new JScrollPane(textArea));
        loadQRCode("javaQR-generator v1.1, made in Cuba with Linux-GNU.\n"
            + "A simple QR-code generator.\n\n"
            + "Copyright © 2021- Yoel N. Fabelo.\n"
            + "License GPLv3+: GNU GPL version 3 or later\n"
            + "<https://gnu.org/licenses/gpl.html>.\n"
            + "This is free software: you are free to change and redistribute it.\n"
            + "There is NO WARRANTY, to the extent permitted by law.\n\n"
            + "Written by Yoel N. Fabelo <human.x7e6@gmail.com>.");

        mainFrame.add(panel);
        mainFrame.setVisible(true);
    }
}
