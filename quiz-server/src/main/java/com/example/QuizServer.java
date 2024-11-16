package com.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class QuizServer {
    private static final int PORT = 1234; // 서버가 수신 대기할 포트 번호

    // 퀴즈 질문과 정답을 저장
    private static final String[][] QUESTIONS = {
        {"What is the capital of France?", "Paris"}, 
        {"What is 5 + 7?", "12"}, 
        {"What is the color of the sky?", "Blue"} 
    };

    public static void main(String[] args) {
        System.out.println("Quiz Server is running..."); // 서버가 실행 중임을 알림

        // 서버 소켓을 생성하고 지정된 포트에서 대기
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) { // 무한 루프를 돌며 클라이언트 연결 대기
                try (
                    // 클라이언트 연결 수락
                    Socket clientSocket = serverSocket.accept();
                    // 클라이언트로 데이터를 전송하기 위한 출력 스트림
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    // 클라이언트로부터 데이터를 수신하기 위한 입력 스트림
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {

                    System.out.println("Client connected."); // 클라이언트가 연결되었음을 알림

                    int score = 0; // 클라이언트의 점수를 초기화
                    // 모든 질문을 처리
                    for (String[] question : QUESTIONS) {
                        out.println("QUESTION:" + question[0]); // 클라이언트에게 질문 전송
                        String clientAnswer = in.readLine(); // 클라이언트로부터 답변 수신

                        // 답변이 올바른지 검증
                        if (clientAnswer != null && clientAnswer.equalsIgnoreCase(question[1])) {
                            out.println("FEEDBACK:Correct!"); // 정답임을 알림
                            score++; // 점수 증가
                        } else {
                            out.println("FEEDBACK:Incorrect. The correct answer was: " + question[1]); // 오답 메시지 전송
                        }
                    }
                    // 최종 점수를 클라이언트에 전송
                    out.println("FINAL_SCORE:" + score + "/" + QUESTIONS.length);
                    System.out.println("Client disconnected."); // 클라이언트 연결 종료 알림
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage()); // 클라이언트 처리 중 오류 알림
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage()); // 서버 시작 실패 시 오류 메시지 출력
        }
    }
}
