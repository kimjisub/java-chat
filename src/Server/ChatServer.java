package Server;

import Protocol.ChatServerInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	private final ServerSocket serverSocket;
	private final List<ClientHandler> clients;
	private final List<String> chatLog;

	private int nextUserId = 0;

	public ChatServer(int port) throws IOException {
		clients = new ArrayList<>();
		chatLog = new ArrayList<>();
		serverSocket = new ServerSocket(port);
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

		public ClientHandler(Socket socket, ChatServer server, int userId) throws IOException {
			this.socket = socket;
			this.server = server;
			this.userId = userId;
			this.chatServerInterface = new ChatServerInterface(socket.getInputStream(), socket.getOutputStream());
			chatServerInterface.setClientHandler(new ChatServerInterface.ClientHandler() {
				@Override
				public void onMessageReceived(String message) {
					server.broadcastMessage(message, userId); // 메시지를 모든 클라이언트에게 전송
				}

				@Override
				public void onMessageEditRequest(int messageId, String newMessage) {

				}

				@Override
				public void onMessageDeleteRequest(int messageId) {

				}

				@Override
				public void onInvalidRequest(String[] messages) {

				}

				// 다른 이벤트 핸들러 구현 (메시지 수정, 삭제 등)
			});
		}

		public void run() {
			try {
				while (chatServerInterface.readCommand()) ;
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
