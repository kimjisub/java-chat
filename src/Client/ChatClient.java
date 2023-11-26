package Client;

import Protocol.ChatClientInterface;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.BiConsumer;

public class ChatClient extends Thread {
	private final Socket socket;
	private final ChatClientInterface chatClientInterface;
	private List<String> chatLog;

	public void setOnNewMessage(BiConsumer<String, String> callback) {
		chatClientInterface.setOnNewMessage(callback);
	}



	public ChatClient(String host, int port) throws IOException {
		socket = new Socket(host, port);
		chatClientInterface = new ChatClientInterface(socket.getInputStream(), socket.getOutputStream());

		chatClientInterface.setOnNewMessage((sender, message) -> {
			chatLog.add(sender + ": " + message);
			System.out.println(sender + ": " + message);
		});
		chatLog = new ArrayList<>();
	}

	@Override
	public void run() {
		while (true) {
			try {
				chatClientInterface.processMessages();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	public void sendMessage(String message) {
		try {
			chatClientInterface.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
