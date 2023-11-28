package Client;

import Protocol.ChatClientInterface;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {

	private static ChatClient client;
	private static JFrame connectFrame;
	private static JFrame chatFrame;
	private static JTextPane chatArea;
	private static JScrollPane chatScrollPane;
	private static StyledDocument doc;
	private static SimpleAttributeSet leftAlign;
	private static SimpleAttributeSet rightAlign;

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

				client = new ChatClient(host, port,
						new ChatClientInterface.MessageHandler() {
							@Override
							public void onMessageNew(int messageId, int userId, String message) {
								try {
									doc.insertString(doc.getLength(), message + "\n", leftAlign);
								} catch (BadLocationException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onMessageEdit(int messageId, String newMessage) {
								try {
									doc.insertString(doc.getLength(), newMessage + "\n", leftAlign);
								} catch (BadLocationException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onMessageDelete(int messageId) {
								try {
									doc.insertString(doc.getLength(), "[메시지 삭제됨]\n", leftAlign);
								} catch (BadLocationException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onUnknown(String[] messages) {
								JOptionPane.showMessageDialog(connectFrame, "Unknown command: " + String.join(" ", messages));
							}

							@Override
							public void onProtocolError() {
								JOptionPane.showMessageDialog(connectFrame, "Protocol error");
							}
						}
				);
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

		chatArea = new JTextPane();
		chatArea.setEditable(false);
		doc = chatArea.getStyledDocument();
		leftAlign = new SimpleAttributeSet();
		StyleConstants.setAlignment(leftAlign, StyleConstants.ALIGN_LEFT);
		rightAlign = new SimpleAttributeSet();
		StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);

		chatScrollPane = new JScrollPane(chatArea);
		chatFrame.add(chatScrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		JTextField messageField = new JTextField();
		JButton sendButton = new JButton("보내기");
		bottomPanel.add(messageField, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);
		chatFrame.add(bottomPanel, BorderLayout.SOUTH);

		sendButton.addActionListener(e -> sendMessage(messageField));
		messageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage(messageField);
				}
			}
		});

		chatFrame.setSize(500, 400);
	}

	private static void sendMessage(JTextField messageField) {
		String message = messageField.getText();
		if (!message.isEmpty() && client != null) {
			client.sendMessage(message);
			messageField.setText("");
		}
	}

	public static void main(String[] args) {
		createConnectFrame();
	}
}
