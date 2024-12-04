package org.gregoire;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DualSocketServer {

    private static final int PORT = 8080;

    private static final int BUFFER_SIZE = 1024;

    private volatile boolean running = true;

    public void start() throws IOException {
        // Create TCP server socket
        ServerSocket tcpSocket = new ServerSocket(PORT);
        
        // Create UDP socket
        DatagramSocket udpSocket = new DatagramSocket(PORT);
        
        System.out.println("Server listening on port " + PORT + " (TCP & UDP)");

        // Start UDP listener thread
        Thread udpThread = new Thread(() -> {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println("UDP received: " + message);

                    // Send response
                    String response = "UDP Server received: " + message;
                    byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseData, 
                        responseData.length, 
                        packet.getAddress(), 
                        packet.getPort()
                    );
                    udpSocket.send(responsePacket);
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        });
        udpThread.start();

        // Handle TCP connections
        while (running) {
            try {
                Socket clientSocket = tcpSocket.accept();
                Thread tcpThread = new Thread(() -> handleTcpClient(clientSocket));
                tcpThread.start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
        tcpSocket.close();
        udpSocket.close();
    }

    private void handleTcpClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("TCP received: " + message);
                out.println("TCP Server received: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new DualSocketServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
