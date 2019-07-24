package server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import client.ClientCommand;

public class ClientHandler implements Runnable {

	private ServerOnSockets server;

	private PrintWriter outMessage;
	private Scanner inMessage;

	private FileOutputStream fos;
	private Socket clientSocket = null;
	private static int clients_count = 0;

	// конструктор, который принимает клиентский сокет и сервер
	public ClientHandler(Socket socket, ServerOnSockets server) {
		try {
			clients_count++;
			this.server = server;
			this.clientSocket = socket;
			fos = new FileOutputStream(Constant.LINK_TO_FILE_STRING);
			this.inMessage = new Scanner(socket.getInputStream());

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// Переопределяем метод run(), который вызывается когда
	// мы вызываем new Thread(client).start();
	@Override
	public void run() {
		try {
			while (true) {
				// сервер отправляет сообщение
				outMessage = new PrintWriter(clientSocket.getOutputStream(), true);
				server.sendMessageToAllClients("Новый участник вошёл в чат!");
				server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
				break;
			}
			int indexOfResult;
			boolean firstRun = false;
			while (true) {

				// Если от клиента пришло сообщение
				if (inMessage.hasNext()) {
					String clientMessage = inMessage.nextLine();
					System.out.println("ClientHandler. @run()\n" + " Пришло сообщение от клиента:\n" + clientMessage);

					//если клиент совершает регистрацию
					if (clientMessage.indexOf(Constant.OPERATION_REGIST) != -1) {
						
						clientAutorization(Constant.SUCCESSFULL_REGIST, Constant.WRONG_REGIST,
								Constant.OPERATION_REGIST, clientMessage);

						//если клиент совершает вход в систему	
					} else if (clientMessage.indexOf(Constant.OPERATION_LOGIN) != -1) {
						clientAutorization(Constant.SUCCESSFULL_LOGIN, Constant.WRONG_LOGIN, Constant.OPERATION_LOGIN,
								clientMessage);

					}
					// если клиент отправляет данное сообщение, то цикл прерывается и
					// клиент выходит из чата
					if (clientMessage.equalsIgnoreCase("##session##end##")) {
						break;
					} else if(firstRun==true) {
						firstRun=false;
						outMessage = new PrintWriter(clientSocket.getOutputStream(), true);
						server.sendMessageToAllClients(clientMessage);
					}
//					System.out.println(clientMessage);

				}
				Thread.sleep(100);
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.close();
		}
	}

	// отправляем сообщение
	public void sendMsg(String msg) {
		try {
			outMessage.println(msg);
			outMessage.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void clientAutorization(String succesfullOper, String wrongOper, String messageOper, String clientMessage) {
		
		outMessage.println(" ");
		outMessage = new PrintWriter(fos, true);
		System.out.println(messageOper);
		String[] arrClientData = clientMessage.split("login=|&password=| ");

		String key = arrClientData[2];
		String clientData = arrClientData[2].concat("&").concat(arrClientData[3]);

		boolean check = messageOper == Constant.OPERATION_LOGIN ? ClientCommand.loginCheckClientData(key, clientData)
				: ClientCommand.registCheckClientData(key, clientData);

		if (check == true) {
			outMessage.println(succesfullOper);
			outMessage.flush();
		} else {
			outMessage.println(wrongOper);
			outMessage.flush();
		}
		
	}

	// клиент выходит из чата
	public void close() {
		// удаляем клиента из списка
		server.removeClient(this);
		clients_count--;
		server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
	}

}
