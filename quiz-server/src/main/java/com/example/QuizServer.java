package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234; // 서버가 동작할 포트 번호

    // 퀴즈 질문과 정답 배열
    private static final String[][] QUESTIONS = {
        {"What is the capital of France?", "Paris"}, 
        {"What is 5 + 7?", "12"},             
        {"What is the color of the sky?", "Blue"}  
    };

    // 각 클라이언트를 처리하기 위한 핸들러 클래스
    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        // 생성자: 클라이언트 소켓을 받아 처리
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                // 클라이언트와 데이터를 주고받기 위한 입출력 스트림 설정
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("Client connected: " + clientSocket.getInetAddress()); // 클라이언트 연결 확인 메시지

                int score = 0; // 클라이언트 점수 초기화

                // 질문을 순차적으로 클라이언트에게 전송
                for (String[] question : QUESTIONS) {
                    out.println("QUESTION:" + question[0]); // 질문 전송
                    String clientAnswer = in.readLine(); // 클라이언트의 답변 수신

                    // 클라이언트 답변 검증
                    if (clientAnswer != null) {
                        if (clientAnswer.equalsIgnoreCase(question[1])) { // 정답 확인
                            out.println("FEEDBACK:Correct!"); // 정답 메시지 전송
                            score++; // 점수 증가
                        } else {
                            out.println("FEEDBACK:Incorrect. The correct answer was: " + question[1]); // 오답 메시지 전송
                        }
                    } else {
                        out.println("FEEDBACK:Error receiving answer. Please try again."); // 오류 메시지 전송
                    }
                }

                // 최종 점수 클라이언트에게 전송
                out.println("FINAL_SCORE:" + score + "/" + QUESTIONS.length);
                System.out.println("Client disconnected: " + clientSocket.getInetAddress()); // 클라이언트 종료 메시지

            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage()); // 클라이언트 처리 중 오류 발생 시 출력
            } finally {
                try {
                    clientSocket.close(); // 클라이언트 소켓 종료
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage()); // 소켓 종료 중 오류 발생 시 출력
                }
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Quiz Server is running..."); // 서버 시작 메시지

        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // 서버 소켓 생성
            while (true) {
                try {
                    // 클라이언트 연결 대기
                    Socket clientSocket = serverSocket.accept();
                    // 연결된 클라이언트를 별도의 스레드에서 처리
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage()); // 연결 중 오류 발생 시 출력
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage()); // 서버 시작 실패 시 출력
        }
    }
}
