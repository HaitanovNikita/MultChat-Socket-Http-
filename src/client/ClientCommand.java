package client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import server.Constant;
import server.Server;

public class ClientCommand {
	
//	private Socket clientSocket;
//	private Scanner inMessage;
//	private PrintWriter outMessage;
	
	
	public ClientCommand() {
		/*this.clientSocket=clientSocket;
		this.inMessage=inMessage;
		this.outMessage=outMessage;
		*/
		
	}

	
	
	protected void connectedToServer() {
		 try {
	            ClientWindow.setClientSocket(new Socket(Constant.getServerHost(), Constant.getServerPort())) ;
	            ClientWindow.setOutMessage(new PrintWriter(ClientWindow.getClientSocket().getOutputStream()));
	            ClientWindow.setInMessage(new Scanner(ClientWindow.getClientSocket().getInputStream()));
	        	
	         } catch (IOException e) {
	        	 System.out.println("printStackTrace");
	        	
	         }
	}

	public static boolean loginCheckClientData(String login,String clientData) {
		boolean res = false;
		if(Server.mapLoginDetails.get(login)!=null)
		{	
			if(Server.mapLoginDetails.get(login).equals(clientData));{
				res = true;
			}
		}

		return res;
	}
	
	public static boolean registCheckClientData(String login,String clientData) {
		boolean res = false;
		if(Server.mapLoginDetails.get(login)==null)
		{	
			Server.mapLoginDetails.put(login, clientData);	
			res = true;
		}
		return res;
	}
	
	
	protected void connectedToServerLogin() {
		 try {
	            LoginWindow.setClientSocket(new Socket(Constant.getServerHost(), Constant.getServerPort())) ;
	            LoginWindow.setOutMessage(new PrintWriter(ClientWindow.getClientSocket().getOutputStream()));
	            LoginWindow.setInMessage(new Scanner(ClientWindow.getClientSocket().getInputStream()));
	            LoginWindow.setOutPutStream(ClientWindow.getClientSocket().getOutputStream());
	            LoginWindow.setInPutStream(ClientWindow.getClientSocket().getInputStream());
	         } catch (IOException e) {
	        	 System.out.println("printStackTrace");
	        	
	         }
	}
	
	protected void disconnectedServer(String clientName,Socket clientSocket,Scanner inMessage,PrintWriter outMessage) {
		  try {
              // здесь проверяем, что имя клиента непустое и не равно значению по умолчанию
              if (!clientName.isEmpty() && clientName != "Введите ваше имя: ") {
                  outMessage.println(clientName + " вышел из чата!");
              } else {
                  outMessage.println("Участник вышел из чата, так и не представившись!");
              }
              // отправляем служебное сообщение, которое является признаком того, что клиент вышел из чата
              outMessage.println("##session##end##");
              outMessage.flush();
              outMessage.close();
              inMessage.close();
              clientSocket.close();
          } catch (IOException exc) {

          }
	}
	
	protected void createThread(JTextArea jtaTextAreaMessage,JLabel jlNumberOfClients,Scanner inMessage) {
		
		 new Thread(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                    // бесконечный цикл
	                    while (true) {
	                        // если есть входящее сообщение
	                        if (inMessage.hasNext()) {
	                            // считываем его
	                            String inMes = inMessage.nextLine();
	                            String clientsInChat = "Клиентов в чате = ";
	                            if (inMes.indexOf(clientsInChat) == 0) {
	                                jlNumberOfClients.setText(inMes);
	                            } else {
	                                // выводим сообщение
	                                jtaTextAreaMessage.append(inMes.concat("\n"));
	                            }
	                        }
	                    }
	                } catch (Exception e) {
	                }
	            }
	        }).start();
	}


}
