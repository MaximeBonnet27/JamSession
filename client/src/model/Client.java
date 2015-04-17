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

import com.sun.org.apache.bcel.internal.generic.IfInstruction;

import controller.ControllerClient;

/**
 * @author 3100381
 *
 */

//TODO
/*
 * Dans commande : remplacer les noms de commandes par THIS
 * Systeme de notify pour le connected
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
	private Boolean connected;
	private boolean running;

	private String errorMessage;

	public Client(String adresse, int port, String nom) throws Exception{
		/* Mise en place de la socket et des I/O */
		try {

			this.socket = new Socket(adresse, port);
			this.input = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			this.output = new PrintWriter(socket.getOutputStream());

		} catch (UnknownHostException e) {
			throw new Exception("Addresse serveur inconnue: "+e.getMessage());

		} catch (IOException e) {
			throw e;
		}

		this.nom = nom;
		this.running = false;
		this.connected=false;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	public Client(String nom) throws Exception{
		this("localhost",2015, nom);
	}

	public void setConnected(boolean b) {
		synchronized (this) {
			connected=b;
			notifyAll();
		}
	}

	public Boolean isConnected() {
		return connected;
	}
	
	public void setOutPutStreamDebug(OutputStream stream){
		System.setOut(new PrintStream(stream));
	}

	public void setController(ControllerClient controller) {
		this.controller = controller;
	}

	public void sendChatMessage(String message) {
		Commande.TALK.handler(this, message);
	}

	public void receiveChatMessage(String texte, String nomUtil) {
		controller.receiveMessage(texte, nomUtil);
	}


	public boolean connect(){
		Commande.CONNECT.handler(this,nom);

		try {
			synchronized (this) {
				if(!connected)
					wait();
			}
		} catch (InterruptedException e) {
			setErrorMessage(e.getMessage());
			return false;
		}	

		return connected;
	}

	public boolean isRunning(){
		return running;
	}

	public String getNom(){
		return nom;
	}

	public boolean exit(){
		if(connected){
			Commande.EXIT.handler(this, nom);
		}
		connected=false;
		return true;
	}

	public void cleanUp(){
		if(isRunning()){
			running = false;
			try{
				if(socket!=null)
					socket.close();

				if(input!=null)
					input.close();

				if(inputAudio!=null)
					inputAudio.close();

				if(socketAudio!=null)
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


	public void removeContact(String nomUser) {
		controller.removeContact(nomUser);
	}

	public void addContact(String nomUser) {
		controller.addContact(nomUser);
	}

	public void mainLoop(){
		this.running = true;
		ClientLoop loop = new ClientLoop(this);
		loop.start();
	}

	public static void main(String... args) throws Exception {
		Client client = new Client(args[0]);
		client.connect();
		client.mainLoop();
	}


	public boolean inscription(String password){
		String[] args={nom,password};
		Commande.REGISTER.handler(this, args);

		try {

			synchronized (this) {
				if(!connected)
					wait();	
			}

		} catch (InterruptedException e) {
			setErrorMessage(e.getMessage());
			return false;
		}

		return connected;
	}

	public boolean login(String password){
		Commande.LOGIN.handler(this, password);
		try {

			synchronized (this) {
				if(!connected)
					wait();	
			}

		} catch (InterruptedException e) {
			setErrorMessage(e.getMessage());
			return false;
		}

		return connected;
	}

}
