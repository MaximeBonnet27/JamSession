package controller;

import interfaces.IClientInterface;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClient;
import interfaces.UIClientDebug;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.swing.SwingWorker;

import sun.org.mozilla.javascript.internal.ast.ThrowStatement;
import model.Client;
import model.Commande;

public class ControllerClient extends Thread implements IClientInterfaceDelegate{

	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient(new UIClient().init(600, 600));
		controller.run();
	}




	private IClientInterface view;
	private UIClientDebug debugView;
	private Client modelClient;

	public ControllerClient(IClientInterface view) {
		this.setView(view);
		debugView=new UIClientDebug();
	}

	public void setView(IClientInterface view){
		this.view=view;
		this.view.setDelegate(this);
	}

	@Override
	public void run() {
		super.run();
		this.show_communication();
		this.view.showLauncher();
	}



	public void initClient(String pseudo, String addr_serveur,String port_serveur) throws Exception{
		modelClient=new Client(addr_serveur, Integer.parseInt(port_serveur), pseudo);
		modelClient.setController(this);
		modelClient.setOutPutStreamDebug(debugView.getOutputStream());
		modelClient.mainLoop();
	}


	/**
	 * Connexion mode anonyme
	 */
	@Override
	public void connexion(String pseudo, String addr_serveur,String port_serveur) throws  Exception {
		initClient(pseudo, addr_serveur, port_serveur);
		
		if(modelClient.connect()){
			view.showProfil(modelClient.getNom());
		}else{
			throw new Exception(modelClient.getErrorMessage());
		}
	}

	/**
	 * Creeation compte et login
	 */
	@Override
	public void register(String pseudo, String password,String addr_serveur,String port_serveur) throws Exception {
		initClient(pseudo, addr_serveur,port_serveur);

		if(modelClient.inscription(password)){
			view.showProfil(modelClient.getNom());
		}else{
			throw new Exception(modelClient.getErrorMessage());
		}
	}


	/**
	 * Connexion privilégié
	 */
	@Override
	public void login(String pseudo, String addr_serveur, String port_serveur,String password) throws Exception {
		initClient(pseudo, addr_serveur,port_serveur);

		if(modelClient.login(password))
			view.showProfil(modelClient.getNom());
		else{
			throw new Exception(modelClient.getErrorMessage());
		}
	}
	
	@Override
	public void sendMessage(String message) {
		modelClient.sendChatMessage(message);
	}

	

	/**************controller to view methods*******************/
	
	public void connexionEnCours(){
		view.showConnexionEnCours();
	}
	
	public void connexion_perdu(){
		view.showLauncher(modelClient.getErrorMessage());
	}
	
	@Override
	public void deconnexion() {
		view.showLauncher();
		// Si on est encore sur le launcher
		if(modelClient != null){
			modelClient.exit();
		}
	}

	@Override
	public void show_communication() {
		debugView.setVisible(true);
	}



	public void receiveMessage(String message, String from){
		view.receiveMessage(message, from);
	}

	public void removeContact(String nom){
		view.removeContact(nom);
	}

	public void addContact(String nom) {
		view.addContact(nom);
	}

	@Override
	public void creerCompte() {
		view.showInscription();
	}

	@Override
	public void annulerRegister() {
		view.showLauncher();
	}



}
