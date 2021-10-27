import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientCode {
	static class Chatroom implements Runnable {
		DataOutputStream outToServer;
		BufferedReader inFromServer;
		
		Chatroom(DataOutputStream outToServer,BufferedReader inFromServer){
			this.outToServer = outToServer;
			this.inFromServer = inFromServer;
		}
		
		@Override
		public void run() {
		while(true) {
			try {
				String modifiedMessage;
				modifiedMessage = inFromServer.readLine();
				System.out.println(modifiedMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				break;
			}
		}
	}
}
	public static void main(String argv[]) throws Exception {
		Socket connectionSocket = new Socket("localhost", 1234);

		DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
		Chatroom chatRoom = new Chatroom(outToServer,inFromServer);
		Thread thread = new Thread(chatRoom);
		thread.start();
		System.out.println("Welcome to the chat room! before you can begin please tell us your name.");
		Scanner s = new Scanner(System.in);
		String message = s.nextLine();
		outToServer.writeBytes(message + "\r\n");
		System.out.println("You are now in the chat room! If you wish to leave please say \"quit\".");
		while(!message.toUpperCase().equals("QUIT")) {
			message = s.nextLine();
			outToServer.writeBytes(message + "\r\n");
		}
		s.close();
		outToServer.close();
		connectionSocket.close();
	}
}
