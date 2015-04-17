package controller;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingWorker;

import interfaces.IClientInterface;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClient;
import interfaces.UIClientDebug;
import model.Client;

public class ControllerClient implements IClientInterfaceDelegate,Observer{

	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient(new UIClient().init(600, 600));
		controller.show_communication();
		controller.view.showLauncher();
		//controller.run();
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



	public void initClient(String pseudo, String addr_serveur,String port_serveur) throws Exception{
		modelClient=new Client(addr_serveur, Integer.parseInt(port_serveur), pseudo);
		//modelClient.addObserver(this);
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
		
		SwingWorker sw= new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				modelClient.connect();
				return false;
			}

			@Override
			protected void done() {
				if(modelClient.isConnected()){
					view.showProfil(modelClient.getNom());
				}else{
					view.showLauncher(modelClient.getErrorMessage());
				}
			}
		};
		sw.execute();
	}

	/**
	 * Creeation compte et login
	 */
	@Override
	public void register(String pseudo, final String password,String addr_serveur,String port_serveur) throws Exception {
		initClient(pseudo, addr_serveur,port_serveur);
		SwingWorker sw= new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				modelClient.inscription(password);
				return false;
			}

			@Override
			protected void done() {
				if(modelClient.isConnected()){
					view.showProfil(modelClient.getNom());
				}else{
					view.showInscription(modelClient.getErrorMessage());
				}
			}
		};
		sw.execute();
	}


	/**
	 * Connexion privilégié
	 */
	@Override
	public void login(String pseudo, String addr_serveur, String port_serveur,final String password) throws Exception {
		initClient(pseudo, addr_serveur,port_serveur);
		SwingWorker sw= new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				modelClient.login(password);
				return null;
			}

			@Override
			protected void done() {
				if(modelClient.isConnected()){
					view.showProfil(modelClient.getNom());
				}else{
					view.showLauncher(modelClient.getErrorMessage());
				}
			}
		};
		sw.execute();
	
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

	public void addContact(final String nom) {
		SwingWorker sw=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				synchronized (view) {
					if(!view.isInitialized())
						view.wait();
				}
				return null;
			}

			@Override
			protected void done() {
				view.addContact(nom);
			}
			
		};
		sw.execute();
		
	}

	@Override
	public void creerCompte() {
		view.showInscription();
	}

	@Override
	public void annulerRegister() {
		view.showLauncher();
	}

	
	@Override
	public void update(Observable o, Object arg) {
		if(modelClient.isConnected()){
			view.showProfil(modelClient.getNom());
		}else{
			view.showLauncher(modelClient.getErrorMessage());
		}
		
	}



}
