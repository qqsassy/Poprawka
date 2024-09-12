package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Client {
    public static void main(String[] args) {

        JFrame jFrame = new JFrame("Client");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(500, 550);
        JLabel jLabelPic = new JLabel();
        JButton jButtonSelectImage = new JButton("Select Image");
        JButton jButtonSendImage = new JButton("Send Image...");

        jFrame.add(jLabelPic, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JPanel panel2 = new JPanel();

        panel.add(jButtonSelectImage);
        panel.add(jButtonSendImage);
        jFrame.add(panel, BorderLayout.SOUTH);
        jFrame.add(panel2, BorderLayout.NORTH);

        jFrame.setVisible(true);

        // Slider
        JSlider slider = new JSlider(JSlider.CENTER, 1, 15, 2);
        slider.setPaintTrack(true);
        slider.setMajorTickSpacing(2);
        slider.setPaintLabels(true);
        panel2.add(slider);

        final BufferedImage[] selectedImage = new BufferedImage[1];

        jButtonSelectImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(jFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        selectedImage[0] = ImageIO.read(selectedFile);
                        jLabelPic.setIcon(new ImageIcon(selectedImage[0]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        jButtonSendImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (selectedImage[0] == null) {
                    JOptionPane.showMessageDialog(jFrame, "No image selected!");
                    return;
                }

                int valueOfBlur = slider.getValue();
                BufferedImage blurredImage = applyBoxBlurParallel(selectedImage[0], valueOfBlur);

                try(Socket socket = new Socket("localhost", 1234);
                    OutputStream outputStream = socket.getOutputStream();
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(dataOutputStream)){

                    //Wysyłanie rozmiaru filtra do serwera
                    dataOutputStream.writeInt(valueOfBlur);
                    dataOutputStream.flush();

                    //Wysyłanie obrazu do serwera
                    ImageIO.write(blurredImage,"png",bufferedOutputStream);
                    bufferedOutputStream.flush();

                    JOptionPane.showMessageDialog(jFrame,"Image sent successfully!!!.");

                } catch (IOException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(jFrame,"Failed to send image: " + e.getMessage());
                }


            }
        });
    }


        private static BufferedImage applyBoxBlurParallel(BufferedImage image, int radius) {
            int width = image.getWidth();
            int height = image.getHeight();
            BufferedImage blurredImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            int cores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(cores);

            int chunkHeight = height / cores;

            for (int i = 0; i < cores; i++) {
                int startY = i * chunkHeight;
                int endY = (i == cores - 1) ? height : startY + chunkHeight;

                executor.submit(new BlurTask(image, blurredImage, radius, startY, endY));
            }

            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return blurredImage;
        }

        static class BlurTask implements Runnable {
            private BufferedImage sourceImage;
            private BufferedImage targetImage;
            private int radius;
            private int startY;
            private int endY;

            public BlurTask(BufferedImage sourceImage, BufferedImage targetImage, int radius, int startY, int endY) {
                this.sourceImage = sourceImage;
                this.targetImage = targetImage;
                this.radius = radius;
                this.startY = startY;
                this.endY = endY;
            }

            @Override
            public void run() {
                for (int y = startY; y < endY; y++) {
                    for (int x = 0; x < sourceImage.getWidth(); x++) {
                        int r = 0, g = 0, b = 0;
                        int count = 0;

                        for (int ky = -radius; ky <= radius; ky++) {
                            for (int kx = -radius; kx <= radius; kx++) {
                                int px = x + kx;
                                int py = y + ky;

                                if (px >= 0 && px < sourceImage.getWidth() && py >= 0 && py < sourceImage.getHeight()) {
                                    Color color = new Color(sourceImage.getRGB(px, py));
                                    r += color.getRed();
                                    g += color.getGreen();
                                    b += color.getBlue();
                                    count++;
                                }
                            }
                        }

                        r /= count;
                        g /= count;
                        b /= count;

                        targetImage.setRGB(x, y, new Color(r, g, b).getRGB());
                    }
                }
            }



    }
}