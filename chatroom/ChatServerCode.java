package lab4.chatroom;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServerCode {

	static class ClientRequest implements Runnable {
		
		Socket connectionSocket;
		
		public ClientRequest(Socket s) {
			this.connectionSocket = s;
		}
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				String clientMessage = inFromClient.readLine();
				System.out.println("RECEIVED: " + clientMessage);

				String modifiedMessage = clientMessage.toUpperCase() + "\r\n";
				outToClient.writeBytes(modifiedMessage);
				this.connectionSocket.close();
			}
			catch (Exception ex) {
				
			}
			
			
		}
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(1234);
			System.out.println("This server is ready to receive");

			while (true) {
				Socket connectionSocket = serverSocket.accept();
				ClientRequest clientRequest = new ClientRequest(connectionSocket);
				Thread thread = new Thread(clientRequest);
				thread.start();
				
				
				
			
			}
		}
			catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Error!\r\n");
		}

	
		}

	}


