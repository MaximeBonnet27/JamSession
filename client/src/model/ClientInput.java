package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

@Deprecated
public class ClientInput extends Thread{

	private Client client;
	private InputStreamReader input;
	public ClientInput(Client client, InputStreamReader input){
		this.input = input;
		this.client = client;
	}

	public void run(){
		Scanner in = new Scanner(System.in);
		Commande commande;
		try{
			BufferedReader br=new BufferedReader(input);
			String ligne="";
			if(client.isRunning())

			while(client.isRunning ()){
				while(!br.ready()){
					ligne = br.readLine();
				}
				commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(ligne));
				commande.handler(client, Commande.argumentsFromCommande(ligne));
			}
		}
		catch(Exception e){
			e.printStackTrace();
			in.close();
			return;
		}
		client.exit();
		in.close();
		System.out.println("Gestion commandes sortantes finie.");
	}
}
