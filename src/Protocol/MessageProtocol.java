package Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 직접 정의한 메시지 프로토콜 클래스입니다.
 * 0x17: 요청 구분자
 * 0x1E: 요청 내 항목 구분자
 * <p>
 * 이를 통해서 메시지의 의미를 구분할 수 있습니다.
 */
public class MessageProtocol {
	private InputStream in;
	private OutputStream out;

	public MessageProtocol(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public String[] read() throws IOException {
		List<String> messages = new ArrayList<>();
		StringBuilder currentMessage = new StringBuilder();
		int byteRead;

		while ((byteRead = in.read()) != -1) {
			if (byteRead == 0x17) { // 요청 구분자
				break;
			} else if (byteRead == 0x1E) { // 요청 내 항목 구분자
				messages.add(currentMessage.toString());
				currentMessage = new StringBuilder();
			} else {
				currentMessage.append((char) byteRead);
			}
		}

		if(byteRead == -1) throw new IOException("Connection closed");

		if (!currentMessage.isEmpty()) {
			messages.add(currentMessage.toString());
		}

		log("Recv", messages.toArray(new String[0]));
		return messages.toArray(new String[0]);
	}

	public void send(String[] messages) throws IOException {
		for (String message : messages) {
			out.write(message.getBytes());
			out.write(0x1E); // 요청 내 항목 구분자
		}
		out.write(0x17); // 요청 구분자
		out.flush();

		log("Send", messages);
	}

	private static void log(String prefix, String[] messages) {
		System.out.println(prefix + ": " + Arrays.toString(messages));
	}
}
