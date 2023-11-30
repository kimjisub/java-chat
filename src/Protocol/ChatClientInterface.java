package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class ChatClientInterface extends MessageProtocol {

	// 모든 이벤트 타입에 대한 콜백 인터페이스를 묶어 정의
	public interface MessageHandler {

		void onMessageNew(int messageId, int userId, String message);

		void onMessageEdit(int messageId, String newMessage);

		void onMessageDelete(int messageId);

		void onUnknown(String[] messages);

		void onProtocolError();
	}

	private MessageHandler messageHandler;

	public ChatClientInterface(InputStream in, OutputStream out) {
		super(in, out);
	}

	// 콜백 인터페이스 설정 메서드
	public void setMessageHandler(MessageHandler handler) {
		this.messageHandler = handler;
	}

	public boolean readCommand() throws IOException {
		try {
			String[] commands = super.read();
			if (commands.length > 0 && messageHandler != null) {
				switch (commands[0]) {
					case "message.new" -> {
						if (commands.length >= 4) {
							int messageId = Integer.parseInt(commands[1]);
							int userId = Integer.parseInt(commands[2]);
							String message = commands[3];
							messageHandler.onMessageNew(messageId, userId, message);
						}
					}
					case "message.edit" -> {
						if (commands.length >= 3) {
							int messageId = Integer.parseInt(commands[1]);
							String newMessage = commands[2];
							messageHandler.onMessageEdit(messageId, newMessage);
						}
					}
					case "message.delete" -> {
						if (commands.length >= 2) {
							int messageId = Integer.parseInt(commands[1]);
							messageHandler.onMessageDelete(messageId);
						}
					}
					default -> messageHandler.onUnknown(commands);
				}
			}
		} catch (Exception e) {
			messageHandler.onProtocolError();
			return false;
		}
		return true;
	}

	public void sendMessage(String message) throws IOException {
		super.send(new String[]{"message.new", message});
	}

	public void editMessage(int messageId, String newMessage) throws IOException {
		super.send(new String[]{"message.edit", String.valueOf(messageId), newMessage});
	}

	public void deleteMessage(int messageId) throws IOException {
		super.send(new String[]{"message.delete", String.valueOf(messageId)});
	}
}
