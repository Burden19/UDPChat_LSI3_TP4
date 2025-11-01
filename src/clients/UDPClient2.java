package clients;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UDPClient2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            System.out.print("Entrez votre nom d'utilisateur : ");
            String username = sc.nextLine();

            InetAddress serverAddr = InetAddress.getByName("localhost");
            int serverPort = 1234;

            DatagramSocket socket = new DatagramSocket();
            System.out.println(" Connecté au chat UDP sur le port " + serverPort);

            // Thread pour recevoir les messages en continu
            Thread receiver = new Thread(() -> {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    while (true) {
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n " + message);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("Réception arrêtée.");
                }
            });

            receiver.setDaemon(true);
            receiver.start();

            // Thread principal pour envoyer les messages
            while (true) {
                System.out.print("> ");
                String msg = sc.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    System.out.println(" Vous quittez le chat...");
                    break;
                }

                String timestamp = LocalTime.now().format(timeFormat);
                String fullMsg = "[" + timestamp + "] [" + username + "] : " + msg;

                byte[] data = fullMsg.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, serverAddr, serverPort);
                socket.send(packet);
            }

            socket.close();
            sc.close();

        } catch (IOException e) {
            System.err.println(" Erreur client : " + e.getMessage());
        }
    }
}
