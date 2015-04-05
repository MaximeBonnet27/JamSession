
public class ClientLoop extends Thread {

	private Client client;

	public ClientLoop(Client client){
		this.client = client;
	}

	public void run(){
		String commandeRecue;
		Commande commande;
		while(client.isRunning()){
			commandeRecue = client.receive();
			if(commandeRecue == null || commandeRecue.isEmpty()) break;
			commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(commandeRecue));
			commande.handler(client, Commande.argumentsFromCommande(commandeRecue));
		}
		System.out.println("Gestion commandes entrantes finie.");
	}

}
