package model;

/**
 * 
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Scanner;

import controller.ControllerClient;

/**
 * @author 3100381
 *
 */
public class Client{

	private ControllerClient controller;

	private Socket socket;
	private Socket socketAudio;
	private BufferedReader input;
	private PrintWriter output;
	private BufferedReader inputAudio;
	private PrintWriter outputAudio;
	private String nom;
	private boolean connected;
	private boolean running;



	public Client(String adresse, int port, String nom) {
		/* Mise en place de la socket et des I/O */
		try{
			this.socket = new Socket(adresse, port);
			this.input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			this.output = new PrintWriter(socket.getOutputStream());
		}catch(Exception e){
			e.printStackTrace();
		}

		this.nom = nom;
		this.running = false;
		this.connected=false;
	}

	public Client(String nom){
		this("localhost",2015, nom);
	}

	public void setConnected(boolean b) {
		connected=b;
	}

	public void setOutPutStreamDebug(OutputStream stream){
		System.setOut(new PrintStream(stream));
	}

	public void setController(ControllerClient controller) {
		this.controller = controller;
	}

	public void sendMessage(String message) {
		Commande.TALK.handler(this, message);
	}

	public void receiveMessage(String texte, String nomUtil) {
		controller.receiveMessage(texte, nomUtil);
	}

	
	public boolean connect(){
		Commande.CONNECT.handler(this,nom);
		return true;
	}

	public boolean isRunning(){
		return running;
	}

	public String getNom(){
		return nom;
	}

	public boolean exit(){
		Commande.EXIT.handler(this, nom);
		return true;
	}
	
	public void cleanUp(){
		if(isRunning()){
			running = false;
			try{
			socket.close();
			input.close();
			inputAudio.close();
			socketAudio.close();
			}catch(IOException e){
				e.printStackTrace();
			}

		}
	}

	public void send(String commande){
		System.out.println("CANAL CTRL : " + commande + " -> ");
		output.write(commande);
		output.flush();
	}

	public void sendAudio(String commande){
		System.out.println("CANAL AUDIO : " + commande + " -> ");
		outputAudio.write(commande);
		outputAudio.flush();
	}

	public String receive() throws SocketException{
		String val = null;
		try{
			val = input.readLine();
			System.out.println("CANAL CTRL : <- " + val);
		}
		catch(IOException e){
		}
		return val;
	}

	public String [] getOptionsSouhaitees(){
		String [] res = {"Blues", "90"};
		return res;
	}


	public String receiveAudio(){
		String val = null;
		try{
			val = inputAudio.readLine();
			System.out.println("CANAL AUDIO : <- " + val);
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return val;
	}

	public void setSocketAudio(int port){
		System.out.println("Mise en place de la socket audio");
		try{
			String addr = socket.getRemoteSocketAddress().toString().split("/")[0];
			this.socketAudio = new Socket(addr, port);
			this.inputAudio = new BufferedReader(
					new InputStreamReader(socketAudio.getInputStream()));
			this.outputAudio = new PrintWriter(socketAudio.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public void mainLoop(){
		this.running = true;
		ClientLoop loop = new ClientLoop(this);
		//ClientInput in = new ClientInput(this, new InputStreamReader(System.in));
		loop.start();
		//in.start();
	}

	public static void main(String... args) throws UnknownHostException, IOException {
		Client client = new Client(args[0]);
		client.connect();
		client.mainLoop();
	}




}
