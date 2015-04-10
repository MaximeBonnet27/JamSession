import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ClientInput extends Thread{

	private Client client;

	public ClientInput(Client client){
		this.client = client;
	}

	public void run(){
		BufferedReader buffer = null;
		Commande commande;
		try{
			buffer = new BufferedReader(new InputStreamReader(System.in));
			String ligne;
			while(client.isRunning () && (ligne = buffer.readLine()) != null){
				commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(ligne));
				commande.handler(client, Commande.argumentsFromCommande(ligne));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		client.exit();
		System.out.println("Gestion commandes sortantes finie.");
	}
}
