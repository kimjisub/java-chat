package Client;

import Protocol.ChatClientInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

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

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {

		}

		connectFrame = new JFrame("Java Chat - 접속");
		connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectFrame.setLayout(new BoxLayout(connectFrame.getContentPane(), BoxLayout.Y_AXIS));
		connectFrame.getContentPane().setBackground(new Color(0x1d2127));
		((JComponent) connectFrame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel greeting = new JLabel("Java Chat");
		greeting.setAlignmentX(Component.CENTER_ALIGNMENT);
		greeting.setForeground(new Color(0xFFFFFF));
		greeting.setFont(new Font(greeting.getFont().getName(), Font.BOLD, 24));
		connectFrame.add(greeting);

		connectFrame.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel serverPanel = new JPanel();
		serverPanel.setLayout(new BoxLayout(serverPanel, BoxLayout.X_AXIS));
		serverPanel.setBackground(new Color(0x1d2127));
		serverPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));


		JLabel serverLabel = new JLabel("서버 주소:");
		serverLabel.setForeground(new Color(0xFFFFFF));
		connectFrame.add(serverLabel);

		JTextField hostField = new JTextField("localhost", 20);
		hostField.setBorder(new LineBorder(new Color(0x555555), 1));
		hostField.setBackground(new Color(0x333333));
		hostField.setForeground(new Color(0xFFFFFF));
		hostField.setCaretColor(new Color(0xFFFFFF));
		hostField.setToolTipText("호스트 주소를 입력하세요.");
		serverPanel.add(hostField);

		JLabel colonLabel = new JLabel(":");
		colonLabel.setForeground(new Color(0xFFFFFF));
		serverPanel.add(colonLabel);

		JTextField portField = new JTextField("8080", 4);
		portField.setBorder(new LineBorder(new Color(0x555555), 1));
		portField.setBackground(new Color(0x333333));
		portField.setForeground(new Color(0xFFFFFF));
		portField.setCaretColor(new Color(0xFFFFFF));
		portField.setToolTipText("포트번호를 입력하세요.");
		portField.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				String text = ((JTextField) input).getText();
				try {
					int port = Integer.parseInt(text);
					return port > 0 && port <= 65535;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		});
		serverPanel.add(portField);

		connectFrame.add(serverPanel);

		connectFrame.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel nameLabel = new JLabel("이름:");
		nameLabel.setForeground(new Color(0xFFFFFF));
		connectFrame.add(nameLabel);

		JTextField nameField = new JTextField(20);
		nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		nameField.setBorder(new LineBorder(new Color(0x555555), 1));
		nameField.setBackground(new Color(0x333333));
		nameField.setForeground(new Color(0xFFFFFF));
		nameField.setCaretColor(new Color(0xFFFFFF));
		nameField.setToolTipText("이름을 입력하세요.");
		connectFrame.add(nameField);

		connectFrame.add(Box.createRigidArea(new Dimension(0, 20)));

		JButton connectButton = new JButton("접속");
		connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		connectButton.setFont(new Font("Dialog", Font.BOLD, 14));
		connectButton.setForeground(new Color(0xFFFFFF));
		connectButton.setBackground(new Color(0x45aaf2));
		connectButton.setBorder(new EmptyBorder(10, 0, 10, 0));
		connectButton.setFocusPainted(false);
		connectButton.setEnabled(false);
		connectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectButton.getPreferredSize().height + 20));

		nameField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateButton();
			}

			public void removeUpdate(DocumentEvent e) {
				updateButton();
			}

			public void insertUpdate(DocumentEvent e) {
				updateButton();
			}

			private void updateButton() {
				connectButton.setEnabled(!nameField.getText().trim().isEmpty());
			}
		});

		connectFrame.add(connectButton);

		connectButton.addActionListener(e -> {
			try {
				String host = hostField.getText();
				int port;
				String portText = portField.getText().trim();
				String name = nameField.getText();

				if (portText.isEmpty()) {
					port = 8080;
				} else {
					port = Integer.parseInt(portText);
				}

				if (host.isEmpty()) {
					host = "localhost";
				}

				client = new ChatClient(host, port);

				client.setMessageHandler(
						new ChatClientInterface.MessageHandler() {


							@Override
							public void onMessageNew(int messageId, int userId, String message) {
								try {
									doc.insertString(doc.getLength(), userId + ": " + message + "\n", leftAlign);
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
						});

				client.setClientHandler(new ChatClient.ClientHandler() {
					@Override
					public void onConnect() {
						client.setMyName(name);
						createChatFrame(name);
						connectFrame.setVisible(false);
						chatFrame.setVisible(true);
					}

					@Override
					public void onDisconnect() {
						JOptionPane.showMessageDialog(connectFrame, "연결이 끊겼습니다.");
					}

					@Override
					public void onError(Exception e) {
						JOptionPane.showMessageDialog(connectFrame, "서버에 연결할 수 없습니다.");
					}
				});
				client.start();

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(connectFrame, "Error: " + ex.getMessage());
			}
		});

		connectFrame.setSize(400, 600);
		connectFrame.setLocationRelativeTo(null);
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

		chatFrame.setSize(400, 600);
		chatFrame.setLocationRelativeTo(null);
		chatFrame.setVisible(true);
	}

	private static void sendMessage(JTextField messageField) {
		String message = messageField.getText();
		if (!message.isEmpty() && client != null) {
			try {
				byte[] messageBytes = message.getBytes("UTF-8");
				// client.sendMessage(new String(messageBytes, "UTF-8"));
				client.sendMessage(message);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			messageField.setText("");
		}
	}

	public static void main(String[] args) {
		createConnectFrame();
	}
}

