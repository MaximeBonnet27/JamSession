/**
 * 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author 3100381
 *
 */
public class Client {

	static int port = 2015;
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", port);
		BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());

		Scanner scan = new Scanner(System.in);
		String cmd;
		boolean run = true;
		while(run){
			cmd = scan.nextLine();
			System.out.println("COMMANDE -> " + cmd);
			if(cmd.equals("q")) break;
			output.writeBytes(cmd);
		}
	}
	
}
