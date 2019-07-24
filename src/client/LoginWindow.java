package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import server.ClientHandler;
import server.Constant;

public class LoginWindow extends JPanel {

	private String operationStr = "";
	private JLabel labelOper;
	private JButton btnLogin;
	private JButton btnCancel;
	private JButton btnClear;
	private JTextField loginField, passwordField;

	protected static Socket clientSocket;
	protected static Scanner inMessage;
	protected static PrintWriter outMessage;
	protected static OutputStream outPutStream;
	protected static InputStream inPutStream;
	private FileOutputStream fos;
	
	ClientCommand clientCommand;

	public LoginWindow(String oper, int x, int y, int width, int height) {

		operationStr = oper;
		clientCommand = new ClientCommand();
		clientCommand.connectedToServerLogin();
		try {
			fos = new FileOutputStream(Constant.LINK_TO_FILE_STRING);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setBounds(x, y, width, height);
		setLayout(null);
		setBackground(new Color(23, 36, 77));

		//инициализируем все компоненты
		initAllComponents();

		//добавляем слушателей на компоненты
		addAllListeners();
		
		//добавляем компоненты на JPanel
		addAllComponentsInPanel();

		setVisible(true);
	}
	
	private void addAllComponentsInPanel() {
		add(passwordField);
		add(loginField);
		add(btnLogin);
		add(labelOper);
	}

	private void addAllListeners() {
		btnLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkOperation();
			}
		});

		passwordField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				passwordField.setText("");
			}
		});

		loginField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				loginField.setText("");
			}
		});
	}

	private void initAllComponents() {
		labelOper = new JLabel(operationStr);
		btnLogin = new JButton();
		btnCancel = new JButton("Cancel");
		btnClear = new JButton("Clear");
		createTextField(operationStr);

		labelOper.setBounds(230, 40, 200, 80);
		labelOper.setFont(labelOper.getFont().deriveFont(64f));
		btnLogin.setBounds(230, 250, 100, 25);
		loginField.setBounds(230, 140, 200, 30);
		passwordField.setBounds(230, 210, 200, 30);
	}

	private String answerServer() {
		String answerServer = "";
		try (FileReader reader = new FileReader(Constant.LINK_TO_FILE_STRING)) {
			Thread.sleep(7000);
			// читаем посимвольно
			int c;
			while ((c = reader.read()) != -1) {
				answerServer += (char) c;

			}
			outMessage = new PrintWriter(fos, true);
			outMessage.println("");
			outMessage.flush();
			System.out.println("answer server: " + answerServer);
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return answerServer;
	}

	private void checkOperation() {
		String requestToServer = "";
		String answerServer = "";

		switch (operationStr) {
		case "LogIn":
			System.out.println("Операция: Логин");

			requestToServer = Constant.OPERATION_LOGIN + " login=" + loginField.getText() + "&password="
					+ passwordField.getText();

			outMessage.println(requestToServer);
			outMessage.flush();

			answerServer = answerServer();
			if (answerServer.trim().equals(Constant.SUCCESSFULL_LOGIN)) {
				ClientWindow.chatPanel.setVisible(true);
				setVisible(false);

			} else {
				loginField.setText(Constant.WRONG_LOGIN);
				passwordField.setText(Constant.WRONG_LOGIN);
			}
			break;

		default:
			System.out.println("Операция: Регистрация");
			requestToServer = Constant.OPERATION_REGIST + " login=" + loginField.getText() + "&password="
					+ passwordField.getText();

			outMessage.println(requestToServer);
			outMessage.flush();

			answerServer = answerServer();
			if (answerServer.trim().equals(Constant.SUCCESSFULL_REGIST)) {
				ClientWindow.chatPanel.setVisible(true);
				setVisible(false);
			} else {
				loginField.setText(Constant.WRONG_REGIST);
				passwordField.setText(Constant.WRONG_REGIST);
			}
		}

	}

	private void createTextField(String operationStr) {
		switch (operationStr) {
		case "LogIn":
			btnLogin.setText("Сontinue");
			loginField = new JTextField("Enter your login:");
			passwordField = new JTextField("Enter your password:");
			break;

		default:
			System.out.println("switch(), default, operStr=" + operationStr);
			btnLogin.setText("Save data");
			loginField = new JTextField("Create your login:");
			passwordField = new JTextField("Create your password:");
			break;
		}
	}

	/**
	 * @return the clientSocket
	 */
	public static Socket getClientSocket() {
		return clientSocket;
	}

	/**
	 * @return the inMessage
	 */
	public static Scanner getInMessage() {
		return inMessage;
	}

	/**
	 * @return the outMessage
	 */
	public static PrintWriter getOutMessage() {
		return outMessage;
	}

	/**
	 * @param clientSocket the clientSocket to set
	 */
	public static void setClientSocket(Socket clientSocket) {
		LoginWindow.clientSocket = clientSocket;
	}

	/**
	 * @param inMessage the inMessage to set
	 */
	public static void setInMessage(Scanner inMessage) {
		LoginWindow.inMessage = inMessage;
	}

	/**
	 * @param outMessage the outMessage to set
	 */
	public static void setOutMessage(PrintWriter outMessage) {
		LoginWindow.outMessage = outMessage;
	}

	/**
	 * @return the outPutStream
	 */
	public static OutputStream getOutPutStream() {
		return outPutStream;
	}

	/**
	 * @param outPutStream the outPutStream to set
	 */
	public static void setOutPutStream(OutputStream outPutStream) {
		LoginWindow.outPutStream = outPutStream;
	}

	/**
	 * @return the inPutStream
	 */
	public static InputStream getInPutStream() {
		return inPutStream;
	}

	/**
	 * @param inPutStream the inPutStream to set
	 */
	public static void setInPutStream(InputStream inPutStream) {
		LoginWindow.inPutStream = inPutStream;
	}
}
