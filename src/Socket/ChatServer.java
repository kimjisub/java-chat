package Socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	private ServerSocket serverSocket;
	private List<ClientHandler> clients;
	private List<String> chatLog; // 채팅 로그를 저장하는 리스트

	public ChatServer(int port) throws IOException {
		clients = new ArrayList<>();
		chatLog = new ArrayList<>();
		serverSocket = new ServerSocket(port);
	}

	public void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			ClientHandler clientHandler = new ClientHandler(socket, this);
			clients.add(clientHandler);
			clientHandler.start();
		}
	}

	public synchronized void broadcastMessage(String message) {
		chatLog.add(message); // 채팅 로그에 메시지 추가
		System.out.println(message);
		for (ClientHandler client : clients) {
			client.sendMessage(message);
		}
	}

	public List<String> getChatLog() {
		return chatLog;
	}

	public class ClientHandler extends Thread {
		private Socket socket;
		private ChatServer server;
		private PrintWriter out;

		public ClientHandler(Socket socket, ChatServer server) {
			this.socket = socket;
			this.server = server;
		}

		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// 클라이언트로부터 메시지를 받아 처리
				String message;
				while ((message = in.readLine()) != null) {
					server.broadcastMessage(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}

		private void closeConnection() {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message) {
			out.println(message);
		}
	}
}
