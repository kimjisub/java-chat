package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ChatServerInterface extends MessageProtocol {

	public interface ClientHandler {
		void onMessageReceived(String message);

		void onMessageEditRequest(int messageId, String newMessage);

		void onMessageDeleteRequest(int messageId);

		void onInvalidRequest(String[] messages);
	}

	private ClientHandler clientHandler;

	public ChatServerInterface(InputStream in, OutputStream out) {
		super(in, out);
	}

	public void setClientHandler(ClientHandler handler) {
		this.clientHandler = handler;
	}

	public void readCommand() throws IOException {
		try {
			String[] commands = super.read();
			if (commands.length > 0 && clientHandler != null) {
				switch (commands[0]) {
					case "message.new" -> {
						if (commands.length >= 2) {
							String message = commands[1];
							clientHandler.onMessageReceived(message);
						}
					}
					case "message.edit" -> {
						if (commands.length >= 3) {
							int messageId = Integer.parseInt(commands[1]);
							String newMessage = commands[2];
							clientHandler.onMessageEditRequest(messageId, newMessage);
						}
					}
					case "message.delete" -> {
						if (commands.length >= 2) {
							int messageId = Integer.parseInt(commands[1]);
							clientHandler.onMessageDeleteRequest(messageId);
						}
					}
					default -> clientHandler.onInvalidRequest(commands);
				}
			}
		} catch (Exception e) {
			// Handle the exception appropriately
		}
	}

	public void sendMessageToClient(int messageId, int userId, String message) throws IOException {
		super.send(new String[]{"message.new", String.valueOf(messageId), String.valueOf(userId), message});
	}
}
