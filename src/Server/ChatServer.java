package Server;

import Protocol.ChatServerInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServer {
	private final ServerSocket serverSocket;
	private final List<ClientHandler> clients;
	private final List<String> chatLog;

	private int nextUserId = 0;

	public ChatServer(int port) throws IOException {
		clients = new ArrayList<>();
		chatLog = new ArrayList<>();
		serverSocket = new ServerSocket(port);
		System.out.println("Server is now open on port " + port); // 서버가 열린 포트 번호를 로그로 찍는 코드 추가
	}

	public void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			ClientHandler clientHandler = new ClientHandler(socket, this, nextUserId++);
			clients.add(clientHandler);
			clientHandler.start();
		}
	}

	public synchronized void broadcastMessage(String message, int userId) {
		chatLog.add(message);

		System.out.println(message);
		for (ClientHandler client : clients) {
			client.sendMessage(message, userId);
		}
	}

	public List<String> getChatLog() {
		return chatLog;
	}

	public static class ClientHandler extends Thread {
		private final Socket socket;
		private final ChatServer server;
		private final int userId;

		private final ChatServerInterface chatServerInterface;

		private static final Map<Integer, String> nameMap = new HashMap<>();

		public ClientHandler(Socket socket, ChatServer server, int userId) throws IOException {
			this.socket = socket;
			this.server = server;
			this.userId = userId;
			this.chatServerInterface = new ChatServerInterface(socket.getInputStream(), socket.getOutputStream());
			chatServerInterface.setClientHandler(new ChatServerInterface.ClientHandler() {
				@Override
				public void onNameSet(String name) {
					nameMap.put(userId, name);
				}

				@Override
				public void onMessageReceived(String message) {
					String userName = nameMap.getOrDefault(userId, "Unknown");
					server.broadcastMessage(userName + ": " + message, userId); // 사용자 이름과 메시지를 모든 클라이언트에게 전송
				}

				@Override
				public void onMessageEditRequest(int messageId, String newMessage) {
					// 메시지 수정 요청 처리
				}

				@Override
				public void onMessageDeleteRequest(int messageId) {
					// 메시지 삭제 요청 처리
				}

				@Override
				public void onInvalidRequest(String[] messages) {
					// 잘못된 요청 처리
				}
			});
		}

		public void run() {
			try {
				while (!interrupted() && chatServerInterface.readCommand()) ;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeConnection();
			}
		}

		private void closeConnection() {
			try {
				System.out.println("Client " + userId + " disconnected.");
				socket.close();
				server.clients.remove(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void sendMessage(String message, int userId) {
			try {
				chatServerInterface.sendMessageToClient(server.getChatLog().size() - 1, userId, message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
