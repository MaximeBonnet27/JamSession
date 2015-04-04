
public class ClientLoop extends Thread {

	private Client client;

	public ClientLoop(Client client){
		this.client = client;
	}

	public void run(){
		String commandeRecue;
		Commande commande;
		while(true){
			commandeRecue = client.receive();
			commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(commandeRecue));
			commande.handler(client, Commande.argumentsFromCommande(commandeRecue));
		}
	}

}
