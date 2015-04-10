package client.controller;

import client.interfaces.IClientInterface;
import client.interfaces.UIClient;
import client.interfaces.IClientInterfaceDelegate;
import client.interfaces.UIClientDebug;

public class ControllerClient extends Thread implements IClientInterfaceDelegate{
	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient(new UIClient().init(600, 600));
		controller.run();
	}
	
	
	
	
	private IClientInterface view;
	private UIClientDebug debugView;

	public ControllerClient(IClientInterface view) {
		this.setView(view);
	}

	public void setView(IClientInterface view){
		this.view=view;
		this.view.setDelegate(this);
	}


	@Override
	public void run() {
		super.run();
		this.view.showLauncher();
	}

	

	@Override
	public void connexion(String pseudo, String addr_serveur,String port_serveur) {
		view.showProfil();
	}

	@Override
	public void deconnexion() {
		view.showLauncher();
		System.out.println("deconnexion");
	}

	@Override
	public void show_communication() {
		debugView=new UIClientDebug();
	}

	@Override
	public void sendMessage(String message) {
		view.receiveMessage(message, "moi");
		view.addContact(message);
	}
}
