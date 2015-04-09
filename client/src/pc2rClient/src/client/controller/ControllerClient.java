package client.controller;

import client.interfaces.IClientInterface;
import client.interfaces.UIClient;
import client.interfaces.IClientInterfaceDelegate;

public class ControllerClient extends Thread implements IClientInterfaceDelegate{

	private IClientInterface view;


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

	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient(new UIClient().init(800, 800));
		controller.run();
	}

	@Override
	public void connexion(String pseudo, String addr_serveur,
			String port_serveur) {
		System.out.println("connexion traitement");	
		view.showProfil();
	}

	@Override
	public void deconnexion() {
		System.out.println("deconnexion");
		view.showLauncher();
	}

	@Override
	public void show_communication() {
		// TODO Auto-generated method stub
		
	}
}
