package com.example;

import java.io.*;
import java.net.*;

public class QuizClient1 {
    private static final String CONFIG_FILE = "/home/jin156/gachon/socket-programming/server_info.dat";

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 1234;

        // Load server information from configuration file
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                serverAddress = br.readLine();
                serverPort = Integer.parseInt(br.readLine());
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading config file. Using default settings.");
            }
        } else {
            System.out.println("Config file not found. Using default settings.");
        }

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the quiz server!");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION:")) {
                    System.out.println(serverMessage.substring(9)); // Display the question
                    System.out.print("Your answer: ");
                    String answer = userInput.readLine();
                    out.println(answer); // Send the answer to the server
                } else if (serverMessage.startsWith("FEEDBACK:")) {
                    System.out.println(serverMessage.substring(9)); // Display feedback
                } else if (serverMessage.startsWith("FINAL_SCORE:")) {
                    System.out.println("Your final score: " + serverMessage.substring(12));
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}
