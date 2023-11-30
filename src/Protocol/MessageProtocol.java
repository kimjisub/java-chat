package Protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
	private final InputStream in;
	private final OutputStream out;

	public MessageProtocol(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}

	public String[] read() throws IOException {
		List<String> messages = new ArrayList<>();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int byteRead;

		while ((byteRead = in.read()) != -1) {
			if (byteRead == 0x17) { // 요청 구분자
				break;
			} else if (byteRead == 0x1E) { // 요청 내 항목 구분자
				messages.add(buffer.toString(StandardCharsets.UTF_8));
				buffer.reset();
			} else {
				buffer.write(byteRead);
			}
		}

		if(byteRead == -1) throw new IOException("Connection closed");

		if (buffer.size() > 0) {
			messages.add(buffer.toString(StandardCharsets.UTF_8));
		}

		log("Recv", messages.toArray(new String[0]));
		return messages.toArray(new String[0]);
	}

	public void send(String[] messages) throws IOException {
		for (String message : messages) {
			out.write(message.getBytes(StandardCharsets.UTF_8));
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
