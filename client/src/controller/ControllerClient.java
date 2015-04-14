package controller;

import model.Client;
import interfaces.IClientInterface;
import interfaces.UIClient;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClientDebug;

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



	@Override
	public void connexion(String pseudo, String addr_serveur,String port_serveur) {
		modelClient=new Client(addr_serveur, Integer.parseInt(port_serveur), pseudo);
		modelClient.setOutPutStreamDebug(debugView.getOutputStream());
		
		if(modelClient.connect()){
			view.showProfil();
		}else{
			//view.errorConnection()
		}
	}

	@Override
	public void deconnexion() {
		view.showLauncher();
		System.out.println("deconnexion");
	}

	@Override
	public void show_communication() {
		debugView.setVisible(true);
	}

	@Override
	public void sendMessage(String message) {
		view.receiveMessage(message, "moi");
		view.addContact(message);
	}
}
