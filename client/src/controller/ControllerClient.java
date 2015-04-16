package controller;

import interfaces.IClientInterface;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClient;
import interfaces.UIClientDebug;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

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



	public void initClient(String pseudo, String addr_serveur,String port_serveur) throws NumberFormatException, UnknownHostException, IOException{
		modelClient=new Client(addr_serveur, Integer.parseInt(port_serveur), pseudo);

		modelClient.setController(this);
		modelClient.setOutPutStreamDebug(debugView.getOutputStream());
	}

	/**************controller to model methods
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws  **********/

	@Override
	public void connexion(String pseudo, String addr_serveur,String port_serveur) throws  UnknownHostException, IOException {
		initClient(pseudo, addr_serveur, port_serveur);

		if(modelClient.connect()){
			modelClient.mainLoop();
			view.showProfil();
		}else{
			//view.errorConnection()
		}
	}
	
	@Override
	public void sendMessage(String message) {
		modelClient.sendMessage(message);
		//view.receiveMessage(message, "moi");
		//view.addContact(message);
	}

	@Override
	public void inscription(String pseudo, String password,String addr_serveur,String port_serveur) throws Exception {
		initClient(pseudo, addr_serveur,port_serveur);
		
		modelClient.inscription(password);
		throw new Exception("echec inscription");
	}



	/**************controller to view methods*******************/
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
	public void annulerInscription() {
		view.showLauncher();
	}

}
