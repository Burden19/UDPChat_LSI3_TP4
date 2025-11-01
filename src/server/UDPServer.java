package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class UDPServer {
    public static void main(String[] args) {
        final int PORT = 1234;
        Set<SocketAddress> clients = new HashSet<>();

        try (DatagramSocket serverSocket = new DatagramSocket(new InetSocketAddress(PORT))) {
            System.out.println("Serveur de chat UDP en cours d'exécution sur le port " + PORT);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                SocketAddress clientAddress = packet.getSocketAddress();

                // Enregistrer automatiquement les nouveaux clients
                clients.add(clientAddress);

                System.out.println("📩 De " + clientAddress + " -> " + received);

                // Diffuser à tous sauf à l'expéditeur
                for (SocketAddress addr : clients) {
                    if (!addr.equals(clientAddress)) {
                        byte[] msg = received.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, addr);
                        serverSocket.send(sendPacket);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur serveur : " + e.getMessage());
        }
    }
}
