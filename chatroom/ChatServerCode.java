

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServerCode {

	static List<People> persons = new ArrayList<>();
	static List<String> prevMsg = new ArrayList<>();
	static class ClientRequest implements Runnable {
		Socket connectionSocket;
		
		ClientRequest(Socket c){
			connectionSocket = c;
			
		}
		
		@Override
		public void run() {
			String name;
			try{
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			String clientMessage ="";
			name = inFromClient.readLine();
			for(int i = 0; i < persons.size();i++) {
				if(persons.get(i).getConnection()==connectionSocket) {
					persons.get(i).setName(name);
				}
			}
			for(int i = 0; i < prevMsg.size();i++) {
				outToClient.writeBytes(prevMsg.get(i));
			}
			sendToAll(name+" has joined the chat!\n");
			
			while(true) {
				clientMessage = inFromClient.readLine();
				System.out.println("RECEIVED: " + clientMessage);
				if(clientMessage.toUpperCase().equals("QUIT")) {
					break;
				}
				String modifiedMessage = clientMessage + "\r\n";	
				sendToAll(modifiedMessage,name);
			}
			
			sendToAll(name + " has left the chat!\n");
			closeConnection(name);
			inFromClient.close();
			outToClient.close();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String argv[]) throws Exception {
		ServerSocket serverSocket = new ServerSocket(1234);

		System.out.println("This server is ready to receive");

		while (true) {
			Socket connectionSocket = serverSocket.accept();
			ClientRequest clientRequest = new ClientRequest(connectionSocket);
			Thread thread = new Thread(clientRequest);
			thread.start();
			persons.add(new People(connectionSocket));
		}
	}
	
	public static void sendToAll(String msg, String name) throws IOException {
		if(prevMsg.size() < 5) {
			prevMsg.add(name+":"+msg);
		}
		else {
			prevMsg.remove(0);
			prevMsg.add(name+":"+msg);
		}
		for(int i = 0; i < persons.size();i++) {
			if(name.equals(persons.get(i).getName())) {
				continue;
			}
			DataOutputStream outToClient = new DataOutputStream(persons.get(i).getConnection().getOutputStream());
			outToClient.writeBytes(name+":"+msg);
		}
	}
	
	public static void sendToAll(String msg) throws IOException {
		if(prevMsg.size() < 5) {
			prevMsg.add(msg);
		}
		else {
			prevMsg.remove(0);
			prevMsg.add(msg);
		}
		for(int i = 0; i < persons.size();i++) {
			DataOutputStream outToClient = new DataOutputStream(persons.get(i).getConnection().getOutputStream());
			outToClient.writeBytes(msg);
		}
	}

	public static void closeConnection(String name) {
		for(int i = 0; i < persons.size();i++) {
			if(name.equals(persons.get(i).getName())) {
				persons.remove(i);
			}
		}
	}
	
	static class People {
		String name;
		Socket connection;
		
		People(Socket c){
			connection = c;
		}
		
		public void setName(String n) {
			name = n;
		}
		
		public String getName() {
			return name;
		}
		
		public Socket getConnection() {
			return connection;
		}
	}
	
	}


