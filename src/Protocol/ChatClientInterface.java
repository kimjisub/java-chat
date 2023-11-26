package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class ChatClientInterface extends MessageProtocol {
	private final Map<String, BiConsumer<String, String>> callbacks;

	public ChatClientInterface(InputStream in, OutputStream out) {
		super(in, out);
		this.callbacks = new HashMap<>();
	}

	public void setOnNewMessage(BiConsumer<String, String> callback) {
		callbacks.put("NewMessage", callback);
	}

	public void sendMessage(String message) throws IOException {
		super.send(new String[]{"NewMessage", message});
	}

	public void processMessages() throws IOException {
		String[] messages = super.read();
		if (messages.length > 0 && callbacks.containsKey(messages[0])) {
			switch (messages[0]) {
				case "NewMessage":
					callbacks.get("NewMessage").accept(messages[1], messages[2]);
					break;
			}
		}
	}
}
