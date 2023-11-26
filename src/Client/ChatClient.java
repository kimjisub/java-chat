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
	private final ChatClientInterface.MessageHandler messageHandler;


	// 서버로 메시지를 보내기 위한 스레드 풀
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();


	public ChatClient(String host, int port, ChatClientInterface.MessageHandler messageHandler) {
		this.host = host;
		this.port = port;
		this.messageHandler = messageHandler;
	}

	@Override
	public void run() {
		try (Socket socket = new Socket(host, port)) {
			chatInterface = new ChatClientInterface(socket.getInputStream(), socket.getOutputStream());
			chatInterface.setMessageHandler(messageHandler);

			while (!interrupted()) {
				chatInterface.readCommand();
			}
		} catch (IOException e) {
			System.err.println("Error in ChatClient: " + e.getMessage());

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
