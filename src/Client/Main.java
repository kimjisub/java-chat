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
import javax.swing.border.LineBorder;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.UnsupportedEncodingException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
			// Nimbus LookAndFeel을 설정하는데 실패하면 기본 시스템 LookAndFeel을 사용합니다.
		}

		connectFrame = new JFrame("채팅 프로그램 - 접속");
		connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		connectFrame.setLayout(null);

		JLabel greeting = new JLabel("로그인 화면");
		greeting.setLocation(180,5);
		greeting.setSize(300,60);
		greeting.setFont(new Font(greeting.getFont().getName(), Font.BOLD, 40));
		connectFrame.add(greeting);

		JTextField hostField = new JTextField("localhost",20);
		hostField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (hostField.getText().equals("localhost")) {
					hostField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (hostField.getText().isEmpty()) {
					hostField.setText("localhost");
				}
			}
		});
		hostField.setLocation(60,75);
		hostField.setSize(270,25);
		hostField.setBorder(new LineBorder(new Color(0x45aaf2), 1));
		hostField.setHorizontalAlignment(JTextField.CENTER);
		hostField.setToolTipText("주소를 입력하세요.");


		JTextField portField = new JTextField("8080",20);
		portField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (portField.getText().equals("8080")) {
					portField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (portField.getText().isEmpty()) {
					portField.setText("8080");
				}
			}
		});
		portField.setLocation(380,75);
		portField.setSize(180,25);
		portField.setBorder(new LineBorder(new Color(0x45aaf2), 1));
		portField.setHorizontalAlignment(JTextField.CENTER);
		portField.setToolTipText("포트번호를 입력하세요.");

		JTextField nameField = new JTextField(20);
		nameField.setLocation(60,110);
		nameField.setSize(150,30);
		nameField.setBorder(new LineBorder(new Color(0x45aaf2), 1));
		nameField.setHorizontalAlignment(JTextField.CENTER);
		nameField.setToolTipText("이름을 입력하세요.");

		JLabel hos = new JLabel("Host : ");
		connectFrame.add(hos);
		hos.setLocation(15,70);
		hos.setSize(50,30);
		hos.setFont(new Font("Dialog", Font.BOLD, 16));

		JLabel por = new JLabel("Port : ");
		connectFrame.add(por);
		por.setLocation(340,70);
		por.setSize(50,30);
		por.setFont(new Font("Dialog", Font.BOLD, 16));

		JLabel nam = new JLabel("Name : ");
		connectFrame.add(nam);
		nam.setLocation(8,105);
		nam.setSize(60,40);
		nam.setFont(new Font("Dialog", Font.BOLD, 16));


		connectFrame.add(hostField);
		connectFrame.add(portField);
		connectFrame.add(nameField);


		JButton connectButton = new JButton("접속");
		connectFrame.add(connectButton);
		connectButton.setLocation(380,110);
		connectButton.setSize(70,40);
		connectButton.setFont(new Font("Serif", Font.BOLD, 14));
		connectButton.setForeground(new Color(0x45aaf2));
		connectButton.setBorder(new LineBorder(new Color(0xd1d8e0), 1));

		connectButton.setEnabled(false);
		nameField.getDocument().addDocumentListener(new DocumentListener(){
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

				client = new ChatClient(host, port,
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

		connectFrame.setSize(600, 220);
		connectFrame.getContentPane().setBackground(new Color(0xdfe6e9));
		connectFrame.setVisible(true);
		connectFrame.setLocationRelativeTo(null);
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
			try {
				byte[] messageBytes = message.getBytes("UTF-8");
				client.sendMessage(new String(messageBytes, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace(); // 적절한 예외 처리를 해야 합니다.
			}
			messageField.setText("");
		}
	}

	public static void main(String[] args) {createConnectFrame();}
}

