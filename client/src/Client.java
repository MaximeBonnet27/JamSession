/**
 * 
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author 3100381
 *
 */
public class Client {

	private Socket socket;
	private Socket socketAudio;
	private DataInputStream input;
	private PrintWriter output;
	private String nom;
	public Client(String adresse, int port, String nom) {
		/* Mise en place de la socket et des I/O */
		try{
			this.socket = new Socket(adresse, port);
			this.input = new DataInputStream(socket.getInputStream());
			this.output = new PrintWriter(socket.getOutputStream());
		}catch(Exception e){
			e.printStackTrace();
		}

		this.nom = nom;
	}
	public Client(String nom){
		this("localhost",2015, nom);
	}

	public boolean connect(){
		Commande.CONNECT.handler(this,nom);
		Commande.WELCOME.handler(this);
		Commande.AUDIO_PORT.handler(this);
		return true;
	}

	public boolean exit(){
		try{
			socket.close();
			input.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public void send(String commande){
		output.write(commande);
		output.flush();
	}
	public String receive(){
		String val = null;
		try{
			val = input.readLine();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return val;
	}

	public void setSocketAudio(int port){
		System.out.println("SETSOCKETAUDIO " + port);
		try{
			String addr = socket.getRemoteSocketAddress().toString().split("/")[0];
			System.out.println("ADDR : " + addr);
			this.socketAudio = new Socket(addr, port);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String... args) throws UnknownHostException, IOException {
		Client client = new Client("maxime");
		client.connect();
		client.exit();
	}

}
