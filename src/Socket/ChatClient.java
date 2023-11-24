package Socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	private List<String> chatLog; // 로컬 채팅 로그

	public ChatClient(String host, int port) throws IOException {
		socket = new Socket(host, port);
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
		chatLog = new ArrayList<>();
		new Thread(this::receiveMessages).start();
	}

	private void receiveMessages() {
		try {
			String message;
			while ((message = input.readLine()) != null) {
				chatLog.add(message); // 새 메시지를 로컬 로그에 추가
				System.out.println(message); // 콘솔에 메시지 출력 (혹은 GUI에 표시)
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		output.println(message);
	}

	// 기타 메소드 생략
}
