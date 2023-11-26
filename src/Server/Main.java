package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

			// 서버 모드
			System.out.print("Enter port number for the server: ");
			int port = Integer.parseInt(reader.readLine());
			ChatServer server = new ChatServer(port);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}