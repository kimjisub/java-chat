package Client;

import Protocol.ChatClientInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;

public class Main extends JFrame {
	public static void frame_chat() {
		try {
			JFrame a = new JFrame("채팅 프로그램");
			NumberFormat format = NumberFormat.getIntegerInstance();
			JFormattedTextField port_field = new JFormattedTextField(format);

			port_field.setColumns(10);
			a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			Container contentPane = a.getContentPane();
			contentPane.setBackground(Color.LIGHT_GRAY);

			contentPane.setLayout(new FlowLayout());

			JTextField ipField = new JTextField(30);
			JTextField name = new JTextField(30);

			contentPane.add(ipField);
			contentPane.add(port_field);
			contentPane.add(name);

			JButton submitButton = new JButton("접속");
			contentPane.add(submitButton);
			submitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String Input_ip = ipField.getText();
					String Input_name = name.getText();

					System.out.println(Input_name);
					System.out.println(Input_ip);

					Number number = (Number) port_field.getValue();
					int Input_port = number.intValue();
					System.out.println(Input_port);
				}
			});


			a.setSize(500, 500);
			a.setVisible(true);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static ChatClient client;


	public static void main(String[] args) {
//		frame_chat();
//		if (true)
//			return;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print("Enter server address ('host:port'): ");
			String input = reader.readLine();

			String[] parts = input.split(":");
			if (parts.length != 2) {
				System.out.println("Invalid input format. Please use 'host:port'.");
				return;
			}
			String host = parts[0];
			int port = Integer.parseInt(parts[1]);

			ChatClientInterface.MessageHandler messageHandler = new ChatClientInterface.MessageHandler() {
				@Override
				public void onMessageNew(int messageId, int userId, String message) {
					System.out.println("New Message from " + userId + ": " + message);
				}

				@Override
				public void onMessageEdit(int messageId, String newMessage) {
					// 메시지 수정 처리
				}

				@Override
				public void onMessageDelete(int messageId) {
					// 메시지 삭제 처리
				}

				@Override
				public void onUnknown(String[] messages) {
					// 알 수 없는 메시지 처리
				}

				@Override
				public void onProtocolError() {
					System.err.println("Protocol Error");
				}
			};

			client = new ChatClient(host, port, messageHandler);
			client.start();

			System.out.println("Connected to the server. Type 'exit' to quit.");
			String message;
			while (!(message = reader.readLine()).equalsIgnoreCase("exit")) {
				client.sendMessage(message);
			}

			client.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}