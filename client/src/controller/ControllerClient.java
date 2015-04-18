package controller;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import interfaces.IClientInterface;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClient;
import interfaces.UIClientDebug;
import model.Client;

public class ControllerClient implements IClientInterfaceDelegate{

	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient(new UIClient().init(600, 600));
		controller.show_communication();
		controller.view.showLauncher();
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
	public void sendMessage(final String message) {

		SwingWorker sw= new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				modelClient.sendChatMessage(message);
				return null;
			}

		};
		sw.execute();
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



	public void receiveMessage(final String message, final String from){
		SwingWorker sw=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				waitViewInitialisation();
				return null;
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						view.receiveMessage(message, from);
					}
				});
			}
		};
		sw.execute();
	}

	public void removeContact(final String nom){
		SwingWorker sw=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				waitViewInitialisation();
				return null;
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						view.removeContact(nom);
					}
				});
			}
		};
		sw.execute();
	}

	public void addContact(final String nom) {
		SwingWorker sw=new SwingWorker(){

			@Override
			protected Object doInBackground() throws Exception {
				waitViewInitialisation();
				return null;
			}

			@Override
			protected void done() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						view.addContact(nom);
					}
				});
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

	private void waitViewInitialisation(){
		try{
			synchronized (view) {
				if(!view.isInitialized())
					view.wait();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}
