package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234;

    // Questions and answers
    private static final String[][] QUESTIONS = {
        {"What is the capital of France?", "Paris"},
        {"What is 5 + 7?", "12"},
        {"What is the color of the sky?", "Blue"}
    };

    // Client handler for each connection
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("Client connected: " + clientSocket.getInetAddress());

                int score = 0;
                // Loop through each question
                for (String[] question : QUESTIONS) {
                    out.println("QUESTION:" + question[0]); // Send the question
                    String clientAnswer = in.readLine(); // Receive the answer from client

                    // Validate the client's answer
                    if (clientAnswer != null) {
                        if (clientAnswer.equalsIgnoreCase(question[1])) {
                            out.println("FEEDBACK:Correct!");
                            score++;
                        } else {
                            out.println("FEEDBACK:Incorrect. The correct answer was: " + question[1]);
                        }
                    } else {
                        out.println("FEEDBACK:Error receiving answer. Please try again.");
                    }
                }

                // Send the final score to the client
                out.println("FINAL_SCORE:" + score + "/" + QUESTIONS.length);
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Quiz Server is running...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    // Wait for a client connection
                    Socket clientSocket = serverSocket.accept();
                    // Handle the client in a new thread
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }
}
