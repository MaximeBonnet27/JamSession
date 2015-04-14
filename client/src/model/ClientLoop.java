package model;

import java.net.SocketException;


public class ClientLoop extends Thread {

	private Client client;

	public ClientLoop(Client client){
		this.client = client;
	}

	public void run(){
		String commandeRecue = "";
		Commande commande;
		while(client.isRunning()){
			try{
			commandeRecue = client.receive();
			if(commandeRecue == null || commandeRecue.isEmpty()) break;
			commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(commandeRecue));
			commande.handler(client, Commande.argumentsFromCommande(commandeRecue));
			
			}catch(SocketException e){
				if(client.isRunning()){
					System.err.println("JE RUNNNNNNNNNNNN");
					e.printStackTrace();
				}else{
					break;
				}
			}
		}
		System.err.println("Gestion commandes entrantes finie.");
		client.exit();
	}

}
