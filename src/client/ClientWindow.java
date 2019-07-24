package client;

import javax.accessibility.AccessibleContext;
import javax.swing.*;

import org.omg.CORBA.PRIVATE_MEMBER;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import server.Constant;
import server.ServerOnSockets;

public class ClientWindow extends JFrame {

	// клиентский сокет
	protected static Socket clientSocket;
	protected static Scanner inMessage;
	protected static PrintWriter outMessage;

	// следующие поля отвечают за элементы формы
	private JTextField jtfMessage;
	private JTextField jtfName;
	private JTextArea jtaTextAreaMessage;
	
	private JScrollPane jsp;
	private JLabel jlNumberOfClients;
	private JButton jbSendMessage;
	private JButton btnLogIn;
	private JButton btnRegist;
	
	private LoginWindow loginWindow;
	private LoginWindow registWindow;
	
	// имя клиента
	private String clientName = "";

	private final int X = 500, Y = 250, W = 700, H = 500;
	public static JPanel chatPanel;
	ClientCommand clientCommand;

	public ClientWindow() {
		clientCommand = new ClientCommand();
		clientCommand.connectedToServer();

		// Задаём настройки элементов на форме
		setBounds(X, Y, W, H);
		setTitle("Client");
		setLayout(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		//инициализируем все компоненты
		initAllComponents();
		
		//добавляем слушателей на компоненты
		addAllListeners();
		
		// Добавили поток
		clientCommand.createThread(jtaTextAreaMessage, jlNumberOfClients, inMessage);

		//добавляем компоненты на JPanel
		addAllComponentsInPanel();

		setVisible(true);
	}

	private void initAllComponents() {
		jtaTextAreaMessage = new JTextArea();
		jtaTextAreaMessage.setEditable(false);
		jtaTextAreaMessage.setLineWrap(true);
		jtfMessage = new JTextField("Enter your message: ");
		jtfName = new JTextField("Enter your name: ");

		 jsp = new JScrollPane(jtaTextAreaMessage);
		 jlNumberOfClients = new JLabel("Number of customers in chat: ");
		chatPanel = new JPanel();
		chatPanel.setLayout(null);
		chatPanel.setBackground(new Color(23, 34, 56));
		jbSendMessage = new JButton("Send");
		btnLogIn = new JButton("LogIn");
		btnRegist = new JButton("Regist");

		loginWindow = new LoginWindow("LogIn", 0, 0, W, H);
		loginWindow.setVisible(false);

		registWindow = new LoginWindow("Regist", 0, 0, W, H);
		registWindow.setVisible(false);

		btnRegist.setBounds(X + 70, Y-35, 100, 25);
		btnLogIn.setBounds(X + 70, Y, 100, 25);

		chatPanel.setBounds(0, 0, 450, 500);
		jtaTextAreaMessage.setBounds(20, 50, 450, 400);
		jtfName.setBounds(0, 430, 120, 30);
		jtfMessage.setBounds(125, 430, 220, 30);
		jbSendMessage.setBounds(350, 430, 100, 30);
	}
	
	private void addAllListeners() {
		btnLogIn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loginWindow.setVisible(true);
			}
		});

		btnRegist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				registWindow.setVisible(true);
			}
		});
		
		
		jbSendMessage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// если имя клиента, и сообщение непустые, то отправляем сообщение
				if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
					clientName = jtfName.getText();
					sendMsg();
					// фокус на текстовое поле с сообщением
					jtfMessage.grabFocus();
				}
			}
		});

		jtfMessage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtfMessage.setText("");
			}
		});

		jtfName.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jtfName.setText("");
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					// здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
					if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
						outMessage.println(clientName + " вышел из чата!");
					} else {
						outMessage.println("Участник вышел из чата, так и не представившись!");
					}
					// отправляем служебное сообщение, которое является признаком того, что клиент
					// вышел из чата
					outMessage.println("##session##end##");
					outMessage.flush();
					outMessage.close();
					inMessage.close();
					clientSocket.close();
				} catch (IOException exc) {

				}
			}
		});

	}
	
	private void addAllComponentsInPanel() {
		add(btnLogIn);
		add(btnRegist);
		add(loginWindow);
		add(registWindow);
		add(jsp);
		add(jlNumberOfClients);

		chatPanel.add(jtfName);
		chatPanel.add(jtaTextAreaMessage);
		chatPanel.add(jtfMessage);
		chatPanel.add(jbSendMessage);
		chatPanel.setVisible(false);
		add(chatPanel);
	}
	
	// отправка сообщения
	public void sendMsg() {
		// формируем сообщение для отправки на сервер
		String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
		// отправляем сообщение
		outMessage.println(messageStr);
		outMessage.flush();
		jtfMessage.setText("");
	}

	public static Socket getClientSocket() {
		return clientSocket;
	}

	public JTextField getJtfMessage() {
		return jtfMessage;
	}

	public void setJtfMessage(JTextField jtfMessage) {
		this.jtfMessage = jtfMessage;
	}

	public JTextField getJtfName() {
		return jtfName;
	}

	public void setJtfName(JTextField jtfName) {
		this.jtfName = jtfName;
	}

	public void setJtaTextAreaMessage(JTextArea jtaTextAreaMessage) {
		this.jtaTextAreaMessage = jtaTextAreaMessage;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	static Scanner getInMessage() {
		return inMessage;
	}

	public static void setInMessage(Scanner inMess) {
		inMessage = inMess;
	}

	
	public static PrintWriter getOutMessage() {
		return outMessage;
	}

	public static void setOutMessage(PrintWriter outMess) {
		outMessage = outMess;
	}

	
	public static void setClientSocket(Socket clientSoc) {
		clientSocket = clientSoc;
	}

	public String getClientName() {
		return this.clientName;
	}
}
