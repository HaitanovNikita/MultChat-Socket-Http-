package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerOnSockets {
		// порт, который будет прослушивать наш сервер
    
		// список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    

    public ServerOnSockets() {
				// сокет клиента, это некий поток, который будет подключаться к серверу
				// по адресу и порту
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        try {
						// создаём серверный сокет на определенном порту
            serverSocket = new ServerSocket(Constant.SOCKET_SERVER_PORT);
            System.out.println("Сервер запущен!");
            
						// запускаем бесконечный цикл
            while (true) {
								// таким образом ждём подключений от сервера
                clientSocket = serverSocket.accept();
                
								// создаём обработчик клиента, который подключился к серверу
								// this - это наш сервер
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
								// каждое подключение клиента обрабатываем в новом потоке
                new Thread(client).start();
            }
            
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
				// закрываем подключение
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            }
            catch (IOException ex) {
            	System.out.println("Сервер не остановлен!");
                ex.printStackTrace();
            }
        }
    }
		
		// отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }

    }

		// удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
