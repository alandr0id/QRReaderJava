package com.pro;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Main extends JFrame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("QR");
        frame.setTitle("QR");
        frame.setMinimumSize(new Dimension(640, 640));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        JLabel qrContentLabel = new JLabel("No se encontre QR");
        JLabel label = new JLabel();

        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(qrContentLabel);
        frame.add(panel);

        //Activar webcam
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        Runnable runnable = () -> {
            String qrContent = null;

            webcam.open();

            while (qrContent == null) {
                try {
                    ImageIO.write(webcam.getImage(), "PNG", new File("qr.png"));
                    BufferedImage image = ImageIO.read(new File("qr.png"));
                    ImageIcon icon = new ImageIcon(image);
                    label.setIcon(icon);

                    qrContent = readQRCode("qr.png");
                    qrContentLabel.setText(qrContent);
                } catch (NotFoundException | IOException e) {
                    e.printStackTrace();
                    qrContentLabel.setText("No se encontro QR");
                }
            }

            webcam.close();
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static String readQRCode(String filePath) throws IOException, NotFoundException {
        FileInputStream stream = new FileInputStream(filePath);
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(ImageIO.read(stream));
        HybridBinarizer hybridBinarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
        Result result = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }
}
