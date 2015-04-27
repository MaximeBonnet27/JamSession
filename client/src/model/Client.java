package model;


import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;

import model.audio.CaptureAudio;
import model.audio.LectureAudio;
import controller.ControllerClient;


public class Client extends Observable{

	private ControllerClient controller;

	private Socket socket;
	private Socket socketAudio;
	private BufferedReader input;
	private PrintWriter output;
	private BufferedReader inputAudio;
	private PrintWriter outputAudio;
	private String nom;

	private Boolean connected;
	private Boolean running;
	private Boolean recording;
	private Boolean playing;

	private boolean ready_for_connexion;


	/**
	 * message du dernier problem rencontré 
	 */
	private String errorMessage;

	private LectureAudio lectureAudio;

	/**
	 * nombre de buffer audio envoie en attente de reception
	 */
	private int current_window_audio;


	/**
	 * nombre maximum de buffer audio pouvant etre en attente de reception 
	 */
	private static final int window_audio=4;


	/**
	 * Permet l'envoie des buffer audio dans l'audre 
	 */
	private ArrayList<Object> tickets;

	public Client(String adresse, int port, String nom){
		ready_for_connexion=true;
		setRunning(false);
		setConnected(false);
		setRecording(false);
		setPlaying(false);
		tickets=new ArrayList<Object>();

		if(nom.isEmpty()){
			ready_for_connexion=false;
			setErrorMessage("pseudo ne doit pas etre vide");
		}
		if(nom.contains(" ")){
			ready_for_connexion=false;
			setErrorMessage("pseudone doit pas contenir d'espaces");
		}
		if(adresse.isEmpty()){
			ready_for_connexion=false;
			setErrorMessage("addresse serveur ne doit pas etre vide");
		}

		/* Mise en place de la socket et des I/O */
		if(isReady()){
			try {

				this.socket = new Socket(adresse, port);
				this.input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				this.output = new PrintWriter(socket.getOutputStream());
				this.nom = nom;

			} catch (UnknownHostException e) {
				ready_for_connexion=false;
				setErrorMessage("Addresse serveur inconnue: "+e.getMessage());
			} catch (IOException e) {
				ready_for_connexion=false;
				setErrorMessage(e.getMessage());
			}
		}
	}

	public Client(String nom) throws Exception{
		this("localhost",2015, nom);
	}

	
	/**GETTERS**/
	boolean isTicket(Object ticket){
		boolean val;
		synchronized (this) {
			val=(tickets.get(0)==ticket);
		}
		return val;
	}
	
	Object nextTicket(){
		Object obj=new Object();
		synchronized (this) {
			tickets.add(obj);
		}
		return obj;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}


	public boolean isReady() {
		return ready_for_connexion;
	}
	
	public String [] getOptionsSouhaitees(){
		String [] res = {"Blues", "90"};
		return res;
	}

	public int getCurrent_window_audio() {
		int val;
		synchronized (this) {
			val=current_window_audio;
		}

		return val;
	}

	public Boolean isConnected() {
		boolean val;

		synchronized (this) {
			val=connected;
		}

		return val;
	}

	public boolean isRunning(){
		boolean val;

		synchronized (this) {
			val=running;
		}

		return val;
	}

	public boolean isRecording() {
		boolean val;

		synchronized (this) {
			val=recording;
		}

		return val;
	}

	public boolean isPlaying(){
		boolean val;

		synchronized (this) {
			val=playing;
		}

		return val;
	}
	/**SETTER**/
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void setCurrent_window_audio(int current_window_audio) {
		synchronized (this) {
			this.current_window_audio = Math.max(0,current_window_audio);	
			notifyAll();
		}

	}
	
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public void setConnected(boolean b) {
		synchronized (this) {
			connected=b;
			notifyAll();
		}
	}

	public void setRunning(Boolean running) {
		synchronized (this) {
			this.running = running;	
		}
	}

	public void setRecording(Boolean recording) {
		synchronized (this) {
			this.recording = recording;
		}
	}

	public void setPlaying(Boolean playing) {
		synchronized (this) {
			this.playing = playing;
		}
	}
	
	public void setSocketAudio(int port){
		try{
			String addr = socket.getRemoteSocketAddress().toString().split("/")[0];
			if(addr.isEmpty()){
				addr = socket.getRemoteSocketAddress().toString().split("/")[1];
				addr = addr.split(":")[0];
			}
			this.socketAudio = new Socket(addr, port);
			this.inputAudio = new BufferedReader(
					new InputStreamReader(socketAudio.getInputStream()));
			this.outputAudio = new PrintWriter(socketAudio.getOutputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	/**COMMUNICATION**/

	/**
	 * Envoi commande sur le canal de CTRL
	 */
	public void send(String commande){
		System.out.println("CANAL CTRL : " + commande + " -> ");
		synchronized (output) {
			output.write(commande);
			output.flush();
		}
	}


	/**
	 * Envoi commande sur le canal AUDIO
	 */
	public void sendAudio(String commande){
		Object obj=nextTicket();

		waitServeurForSendAudio(obj);
		System.out.println("CANAL AUDIO : " + commande.length() + " -> ");

		synchronized (outputAudio) {
			pass();
			outputAudio.write(commande);
			outputAudio.flush();
		}
	}
	
	/**
	 * Reception d'une commande sur le canal de CTRL
	 */
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
	
	/**
	 * Reception d'une commande sur le canal AUDIO
	 */
	public String receiveAudio(){
		String val = null;
		try{
			System.out.println("Avant readline audio");
			val = inputAudio.readLine();
			System.out.println("CANAL AUDIO : <- " + val);
		}
		catch(IOException e){
		}
		return val;
	}
	/**OPERATION**/
	
	/**
	 * connexion anonyme
	 */
	public boolean connect(){
		if(ready_for_connexion){
			Commande.CONNECT.handler(this,nom);
			return waitConnexion();
		}
		return false;
	}
	
	/**
	 * Creation nouveau compte et connexion
	 */
	public boolean inscription(String password) throws Exception{
		if(password.isEmpty()){
			ready_for_connexion=false;
			setErrorMessage("Le mot de passe de doit pas être vide");
		}
		if(password.contains(" ")){
			ready_for_connexion=false;
			setErrorMessage("Le mot de passe de doit pas contenir d'espace");
		}

		if(ready_for_connexion){
			String[] args={nom,password};
			Commande.REGISTER.handler(this, args);
			return waitConnexion();
		}
		return false;
	}
	
	/**
	 * Connexion avec compte
	 */
	public boolean login(String password){
		if(password.isEmpty()){
			ready_for_connexion=false;
			setErrorMessage("Le mot de passe de doit pas être vide");
		}
		if(password.contains(" ")){
			ready_for_connexion=false;
			setErrorMessage("Le mot de passe de doit pas contenir d'espace");
		}

		if(ready_for_connexion){
			Commande.LOGIN.handler(this, password);
			return waitConnexion();
		}
		return false;
	}
	
	public void playSoundFromBuffer(String buffer) {
		lectureAudio.playSoundFromBuffer(buffer);
	}
	
	public void waitServeurForSendAudio(Object ticket){
		synchronized (this) {
			while(getCurrent_window_audio()>=window_audio || isTicket(ticket)){
				try {
					System.out.println("wait serveur:"+tickets.size());
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("pass:"+getCurrent_window_audio());
			setCurrent_window_audio(getCurrent_window_audio()+1);
		}
	}
	
	public boolean waitConnexion(){
		try {
			synchronized (this) {
				if(!connected)
					wait();
			}
		} catch (InterruptedException e) {
			setErrorMessage(e.getMessage());
		}
		return isConnected();
	}
	
	void pass(){
		synchronized (this) {
			tickets.remove(0);
		}
	}
	
	public void removeContact(String nomUser) {
		controller.removeContact(nomUser);
	}

	public void addContact(String nomUser) {
		controller.addContact(nomUser);
	}

	public void mainLoop(){
		if(ready_for_connexion){
			this.running = true;
			ClientLoop loop = new ClientLoop(this);
			loop.start();
		}
	}

	public void captureAudio(){
		this.recording = true;
		CaptureAudio capture = new CaptureAudio(this);
		this.current_window_audio=0;
		capture.start();
	}

	public void lectureAudio(){
		this.playing = true;
		lectureAudio = new LectureAudio(this);
		lectureAudio.start();
	}
	
	/**
	 * Redefinie stdout
	 */
	public void setOutPutStreamDebug(OutputStream stream){
		System.setOut(new PrintStream(stream));
	}

	public void setController(ControllerClient controller) {
		this.controller = controller;
	}

	public void sendChatMessage(String message) {
		System.out.println(message);

		message=message.replace("\\", "\\\\");
		System.out.println(message);

		message=message.replace("\n", "\\n");
		System.out.println(message);

		message=message.replace("/", "\\/");
		System.out.println(message);

		Commande.TALK.handler(this, message);
	}

	public void receiveChatMessage(String message, String nomUtil) {
		System.out.println(message);

		message=message.replace("\\/", "/");
		System.out.println(message);

		message=message.replace("\\n", "\n");
		System.out.println(message);

		message=message.replace("\\\\", "\\");
		System.out.println(message);

		message=message.replace("\\\n", "\\n");
		System.out.println(message);

		controller.receiveMessage(message, nomUtil);
	}

	public String getNom(){
		return nom;
	}

	/**
	 * deconnexion
	 */
	public boolean exit(){
		if(connected){
			Commande.EXIT.handler(this, nom);
		}
		setConnected(false);
		return true;
	}


	/**
	 * fermeture du client
	 */
	public void cleanUp(){
		synchronized (this) {

			if(isRunning()){
				running = false;
				recording=false;
				playing=false;

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

			if(isConnected()){
				setConnected(false);
				setErrorMessage("connexion perdu");
				connexion_perdu();
			}

		}
	}

	public void connexion_perdu(){
		controller.connexion_perdu();
	}

	public void sendRecording(int tick, String buffer){
		Commande.AUDIO_CHUNK.handler(this, tick +"", buffer);
	}
}
