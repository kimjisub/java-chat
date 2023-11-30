package Client;

import Protocol.ChatClientInterface;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient extends Thread {
	private final String host;
	private final int port;

	private ChatClientInterface chatInterface;
	private ChatClientInterface.MessageHandler messageHandler;


	private ClientHandler clientHandler;

	public interface ClientHandler {

		void onConnect();

		void onDisconnect();

		void onError(Exception e);
	}


	// 서버로 메시지를 보내기 위한 스레드 풀
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();


	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.messageHandler = messageHandler;
	}


	// 메시지 핸들러 설정
	public void setMessageHandler(ChatClientInterface.MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}


	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}


	@Override
	public void run() {
		try {
			Socket socket = new Socket(host, port);
			
			chatInterface = new ChatClientInterface(socket.getInputStream(), socket.getOutputStream());
			chatInterface.setMessageHandler(messageHandler);

			// 연결 성공 시 ClientHandler의 onConnect 호출
			if (clientHandler != null) {
				clientHandler.onConnect();
			}

			while (!interrupted() && chatInterface.readCommand()) ;

			// 스레드가 중단되면 onDisconnect 호출
			if (clientHandler != null) {
				clientHandler.onDisconnect();
			}
		} catch (IOException e) {
			System.err.println("Error in ChatClient: " + e.getMessage());
			if (clientHandler != null) {
				clientHandler.onError(e);
			}
			throw new RuntimeException(e);
		}
	}

	public void sendMessage(String message) {
		executorService.submit(() -> {
			try {
				chatInterface.sendMessage(message);
			} catch (IOException e) {
				System.err.println("Error in ChatClient: " + e.getMessage());
			}
		});
	}
}
