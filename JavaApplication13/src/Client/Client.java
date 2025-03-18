package Client;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;
    private JFrame frame;
    private JTextArea textArea;
    private JTextField textField, serverAddressField, serverPortField;
    private JButton sendButton, connectButton, exitButton;
    
    public Client() {
        frame = new JFrame("Client Chat");
        textArea = new JTextArea();
        textArea.setEditable(false);
        textField = new JTextField(30);
        serverAddressField = new JTextField("127.0.0.1", 10);
        serverPortField = new JTextField("2000", 5);
        sendButton = new JButton("Send");
        connectButton = new JButton("Connect");
        exitButton = new JButton("Exit");
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Server Address:"));
        topPanel.add(serverAddressField);
        topPanel.add(new JLabel("Server Port:"));
        topPanel.add(serverPortField);
        topPanel.add(connectButton);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(textField);
        bottomPanel.add(sendButton);
        bottomPanel.add(exitButton);
        
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        sendButton.setEnabled(false);
        
        connectButton.addActionListener(e -> connectToServer());
        sendButton.addActionListener(e -> sendMessage());
        exitButton.addActionListener(e -> System.exit(0));
    }
    
    private void connectToServer() {
        try {
            String serverAddress = serverAddressField.getText().trim();
            int serverPort = Integer.parseInt(serverPortField.getText().trim());
            
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            userName = JOptionPane.showInputDialog(frame, "Enter your name:");
            if (userName == null || userName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            out.println(userName);
            
            textArea.append("Connected to server at " + serverAddress + "\n");
            sendButton.setEnabled(true);
            
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        textArea.append(message + "\n");
                    }
                } catch (IOException e) {
                    textArea.append("Disconnected from server\n");
                }
            }).start();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Could not connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void sendMessage() {
        if (out != null) {
            String message = textField.getText().trim();
            if (!message.isEmpty()) {
                out.println(message); // Sửa lỗi: Không gửi username ở đây nữa
                textField.setText("");
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
