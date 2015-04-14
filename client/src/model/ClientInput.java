package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;


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
		System.out.println("BLABLABLA");
		try{
			BufferedReader br=new BufferedReader(input);
			String ligne="";
			if(client.isRunning())
				System.out.println("JE RUNNNNNN");

			while(client.isRunning ()){
				System.out.println("je suis dedans");
				while(!br.ready()){
					System.out.println("ready");
					ligne = br.readLine();
					System.out.println("jai ecrit " + ligne);
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
