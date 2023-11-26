package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ServerProtocol extends MessageProtocol{
	public ServerProtocol(InputStream in, OutputStream out) {
		super(in, out);
	}

	public void sendMessage(String message) throws IOException {
		super.send(new String[]{message});
	}
	
	public String[] readMessage() throws IOException {
		return super.read();
	}

}
