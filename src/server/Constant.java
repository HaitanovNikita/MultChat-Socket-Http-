package server;

public class Constant {

	 static final String SERVER_HOST = "localhost";
     static final int SOCKET_SERVER_PORT = 9999;
     public static final String OPERATION_LOGIN = "Operation:LogIn";
     public static final String OPERATION_REGIST = "Operation:Regist";
	 public static final String SUCCESSFULL_LOGIN= "Successful login";	
	 public static final String SUCCESSFULL_REGIST= "Successful registration";
	 public static final String WRONG_REGIST= "User with this login already exists!!!";	
	 public static final String WRONG_LOGIN= "Wrong login or password!!!";	
	 public static final String LINK_TO_FILE_STRING = "C:\\Users\\User\\IdeaProjects\\SocketChat2\\Files\\file.txt";
	 
	public static String getServerHost() {
		return SERVER_HOST;
	}

	  
	public static int getServerPort() {
		return SOCKET_SERVER_PORT;
	}
     
     
}
