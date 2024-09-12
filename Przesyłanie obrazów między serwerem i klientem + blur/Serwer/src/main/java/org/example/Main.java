package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(400,400);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jLabel = new JLabel("Waiting for image from client...");
        jFrame.add(jLabel, BorderLayout.SOUTH);
        jFrame.setVisible(true);

        createNewTable();

        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server is running...");

        while(true)
        {
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");

            new Thread(() -> {
                try {
                    handleClient(socket, jFrame, jLabel);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }
    }

    private static void handleClient(Socket socket, JFrame jFrame, JLabel jLabel) throws IOException {

        try (InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(dataInputStream)) {

            int size = dataInputStream.readInt(); // rozmiar filtra

            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);

            if(bufferedImage !=null) {
                File imagesDir = new File("images");
                if(!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }

                String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                File outputFile = new File(imagesDir, timeStamp + ".png");
                ImageIO.write(bufferedImage,"png",outputFile);

                // wpis do bazy danych

                long delay = System.currentTimeMillis();
                saveToDatabase(outputFile.getPath(), size, (int) delay);

                JLabel jLabelPic = new JLabel(new ImageIcon(bufferedImage));
                jLabel.setText("Image received and saved to 'images/'" + outputFile.getName() + ".");
                jFrame.add(jLabelPic, BorderLayout.CENTER);
                jFrame.revalidate();

            } else
            {
                jLabel.setText("Failed to receive image.");
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        }

        private static void saveToDatabase(String path, int size, int delay)
        {
            String jdbcURL = "jdbc:sqlite:images/index.db";
            String insertSQL = "INSERT INTO images (path, size, delay) VALUES (?, ?, ?)";

            try(Connection conn = DriverManager.getConnection(jdbcURL);
            PreparedStatement pstmt = conn.prepareStatement(insertSQL))
            {
                pstmt.setString(1, path);
                pstmt.setInt(2, size);
                pstmt.setInt(3, delay);

                int rowsInserted = pstmt.executeUpdate();
                if(rowsInserted > 0)
                {
                    System.out.println("A new row was inserted successfully!");
                }

            }   catch (SQLException e)
            {
                System.out.println("Błąd przy dodawaniu danych: " + e.getMessage());
            }

        }

        private static Connection connection()
        {
            String url = "jdbc:sqlite:images/index.db";
            Connection conn = null;
            try {
                conn = DriverManager.getConnection(url);
            } catch (SQLException e)
            {
                System.out.println("Connection to SQLite has failed: " + e.getMessage());
            }
            return conn;
        }

        private static void createNewTable()
        {
            String sql = "CREATE TABLE IF NOT EXISTS images("
                    + "id integer PRIMARY KEY AUTOINCREMENT,"
                    + " path text NOT NULL,"
                    + " size integer,"
                    + " delay integer"
                    + ");";

            try(Connection conn = connection();
                Statement stmt = conn.createStatement())
            {
                    if(conn != null)
                    {
                        stmt.execute(sql);
                    }
            }   catch (SQLException e)
            {
                System.out.println("Error creating table: " + e.getMessage());
            }
        }
}