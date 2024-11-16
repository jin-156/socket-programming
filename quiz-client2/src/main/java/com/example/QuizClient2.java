package com.example;

import java.io.*;
import java.net.*;

public class QuizClient2 {
    //서버 Config 파일 경로 저장
    private static final String CONFIG_FILE = "/home/jin156/gachon/socket-programming/server_info.dat";
    
    public static void main(String[] args) {
        String serverAddress = "localhost"; //디폴트 주소
        int serverPort = 1234;  //디폴트 포트

        //configfile에서 서버 접속 정보 read
        File configFile = new File(CONFIG_FILE);   
        if (configFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                serverAddress = br.readLine();
                serverPort = Integer.parseInt(br.readLine());
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading config file. Using default settings.");
            }   //서버 config 파일이 존재할 경우, 파일에서 접속 정보를 읽어와서 접속 정보 저장
        } else {
            System.out.println("Config file not found. Using default settings.");
        }   //서버 config 파일이 존재하지 않을 경우, 디폴트 세팅으로 접속

        try (Socket socket = new Socket(serverAddress, serverPort); //서버 소캣 생성
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //서버로 전송을 위한 출력 스트림 생성
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));    //서버로부터 수신을 위한 입력 스트림 생성
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) { //사용자 입력 처리를 위한 스트림

            System.out.println("Connected to the quiz server!");

            String serverMessage;
            //서버로 메시지를 반복 순회
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.startsWith("QUESTION:")) {    //서버 메시지가 QUESTION 필드로 시작할 경우
                    System.out.println(serverMessage.substring(9)); // Display the question
                    System.out.print("Your answer: ");
                    String answer = userInput.readLine();
                    out.println(answer); // 질문으로 판단하여 사용자에 input을 입력받음
                } else if (serverMessage.startsWith("FEEDBACK:")) {
                    System.out.println(serverMessage.substring(9)); //서버 메시지가 FEEDBACK 필드로 시작할 경우
                } else if (serverMessage.startsWith("FINAL_SCORE:")) {  //서버 메시지가 FINAL_SCORE 필드로 시작할 경우
                    System.out.println("Your final score: " + serverMessage.substring(12));
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }   //에러 처리
    }
}
