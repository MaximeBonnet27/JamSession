package client.controller;

import client.interfaces.UIClient;
import client.interfaces.UIClientDelegateInterface;

public class ControllerClient extends Thread implements UIClientDelegateInterface{

	private UIClient view;
	
	
	public ControllerClient() {
	}

	public void setView(UIClient view){
		this.view=view;
		this.view.setDelegate(this);
	}
	

	@Override
	public void run() {
		super.run();
		this.view.initWithDimension(600, 600);
		this.view.setVisible(true);
	}

	public static void main(String[] args) {
		ControllerClient controller=new ControllerClient();
		controller.setView(new UIClient());
		controller.run();
	}
}
