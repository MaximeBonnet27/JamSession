package model;


public class ClientLoop extends Thread {

	private Client client;

	public ClientLoop(Client client){
		this.client = client;
	}

	public void run(){
		String commandeRecue = "";
		Commande commande;
		try{
			while(client.isRunning()){
				commandeRecue = client.receive();
				if(commandeRecue == null || commandeRecue.isEmpty()) break;
				commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(commandeRecue));
				commande.handler(client, Commande.argumentsFromCommande(commandeRecue));

			}
		}
		catch(Exception e){
			if(client.isRunning()){
				e.printStackTrace();
				client.cleanUp();
				client.setConnected(false);
			}
		}
		//client.exit();
		client.cleanUp();
		client.setConnected(false);
	}

}
