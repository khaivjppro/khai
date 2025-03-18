package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 2000;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<PrintWriter, String> clientNames = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                userName = in.readLine();
                synchronized (clientWriters) {
                    clientWriters.add(out);
                    clientNames.put(out, userName);
                }
                
                broadcast("[SERVER] " + userName + " has joined the chat.");
                
                String message;
                while ((message = in.readLine()) != null) {
                    broadcast("[" + userName + "] " + message);
                }
            } catch (IOException e) {
                System.out.println(userName + " disconnected.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                    clientNames.remove(out);
                }
                broadcast("[SERVER] " + userName + " has left the chat.");
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }
}
