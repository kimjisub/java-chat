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

	private static ChatClient client;
	private static JFrame connectFrame;
	private static JFrame chatFrame;
	private static JTextArea chatArea;

	public static void createConnectFrame() {
		connectFrame = new JFrame("채팅 프로그램 - 접속");
		connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectFrame.setLayout(new FlowLayout());

		JTextField hostField = new JTextField(20);
		JTextField portField = new JTextField(5);
		JTextField nameField = new JTextField(20);

		connectFrame.add(new JLabel("Host: "));
		connectFrame.add(hostField);
		connectFrame.add(new JLabel("Port: "));
		connectFrame.add(portField);
		connectFrame.add(new JLabel("Name: "));
		connectFrame.add(nameField);

		JButton connectButton = new JButton("접속");
		connectFrame.add(connectButton);

		connectButton.addActionListener(e -> {
			try {
				String host = hostField.getText();
				int port = Integer.parseInt(portField.getText());
				String name = nameField.getText();

				ChatClientInterface.MessageHandler messageHandler = new ChatClientInterface.MessageHandler() {
					@Override
					public void onMessageNew(int messageId, int userId, String message) {
						chatArea.append(userId + ": " + message + "\n");
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
						// 프로토콜 오류 처리
					}
				};

				client = new ChatClient(host, port, messageHandler);
				client.start();
				createChatFrame(name);
				connectFrame.setVisible(false);
				chatFrame.setVisible(true);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(connectFrame, "Error: " + ex.getMessage());
			}
		});

		connectFrame.setSize(300, 200);
		connectFrame.setVisible(true);
	}

	public static void createChatFrame(String userName) {
		chatFrame = new JFrame("채팅 프로그램 - " + userName);
		chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		chatFrame.setLayout(new BorderLayout());

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(chatArea);
		chatFrame.add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		JTextField messageField = new JTextField();
		JButton sendButton = new JButton("보내기");
		bottomPanel.add(messageField, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);
		chatFrame.add(bottomPanel, BorderLayout.SOUTH);

		sendButton.addActionListener(e -> {
			String message = messageField.getText();
			if (!message.isEmpty() && client != null) {
				client.sendMessage(message);
				messageField.setText("");
			}
		});

		chatFrame.setSize(500, 400);
	}

	public static void main(String[] args) {
		createConnectFrame();
	}
}