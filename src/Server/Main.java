package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
		// 포트 번호를 가져오는 코드를 수정합니다. null 값을 처리할 수 있도록 합니다.
		int port = 8080;
		try {
			port = args.length > 0 ? Integer.parseInt(args[0]) : Integer.parseInt(System.getenv("PORT"));
		} catch (NumberFormatException | NullPointerException e) {
		}

		try {
			ChatServer server = new ChatServer(port);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}