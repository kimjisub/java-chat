import Socket.ChatClient;
import Socket.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	public static void main(String[] args) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print("Enter server address ('host:port' or press Enter to start server): ");
			String input = reader.readLine();

			if (input.isEmpty()) {
				// 서버 모드
				System.out.print("Enter port number for the server: ");
				int port = Integer.parseInt(reader.readLine());
				ChatServer server = new ChatServer(port);
				server.start();
			} else {
				// 클라이언트 모드
				String[] parts = input.split(":");
				if (parts.length != 2) {
					System.out.println("Invalid input format. Please use 'host:port'.");
					return;
				}
				String host = parts[0];
				int port = Integer.parseInt(parts[1]);

				ChatClient client = new ChatClient(host, port);

				System.out.println("Connected to the server. Type 'exit' to quit.");
				String message;
				while (!(message = reader.readLine()).equalsIgnoreCase("exit")) {
					client.sendMessage(message);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}