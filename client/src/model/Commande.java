package model;


import java.util.Arrays;

public enum Commande {
	UNKNOWN("UNKNOWN"),
	CONNECT("CONNECT"),
	WELCOME("WELCOME"),
	AUDIO_PORT("AUDIO_PORT"),
	AUDIO_OK("AUDIO_OK"),
	CONNECTED("CONNECTED"),
	EXIT("EXIT"),
	EXITED("EXITED"),
	EMPTY_SESSION("EMPTY_SESSION"),
	CURRENT_SESSION("CURRENT_SESSION"),
	SET_OPTIONS("SET_OPTIONS"),
	ACK_OPTS("ACK_OPTS"),
	FULL_SESSION("FULL_SESSION"),
	AUDIO_CHUNK("AUDIO_CHUNK"),
	AUDIO_KO("AUDIO_KO"),
	AUDIO_MIX("AUDIO_MIX"),
	AUDIO_ACK("AUDIO_ACK"),
	TALK("TALK"),
	LISTEN("LISTEN"),
	LS("LS");

	private String nom;
	private Commande(String nom) {
		this.nom = nom;
	}

	public static Commande getCommande(String nom){
		for(Commande c : Commande.values()){
			// Black Magic ?!
			// Equals ne marchait pas avec AUDIO_PORT
			// (Taille recue était 11, alors que commande de 10...)
			if(nom.endsWith(c.nom))
				return c;
		}
		return UNKNOWN;
	}

	public void handler(Client client,String... args){
		switch(this){
			case CONNECT : handlerConnect(client,args[0]); break;
			case WELCOME : handlerWelcome(client); break;
			case ACK_OPTS: handlerAckOpts(client); break;
			case AUDIO_ACK: handlerAudioAck(args, client); break;
			case AUDIO_CHUNK: handlerAudioChunk(args, client); break;
			case AUDIO_KO: handlerAudioKo(args, client); break;
			case AUDIO_MIX: handlerAudioMix(args, client); break;
			case AUDIO_OK: handlerAudioOk(args, client); break;
			case AUDIO_PORT: handlerAudioPort(client, args[0]); break;
			case CONNECTED: handlerConnected(client, args[0]); break;
			case CURRENT_SESSION: handlerCurrentSession(args, client); break;
			case EMPTY_SESSION: handlerEmptySession(client); break;
			case EXIT: handlerExit(client); break;
			case EXITED: handlerExited(client, args[0]); break;
			case FULL_SESSION: handlerFullSession(client); break;
			case SET_OPTIONS: handlerSetOptions(args, client); break;
			case TALK : handlerTalk(client, args[0]); break;
			case LISTEN : handlerListen(client, args[0], args[1]); break;
			case LS : handlerLS(client);
			default : System.out.println("Handler Commandes : Commande inconnue : " + this);

		}
	}
	/**
	 * Envoi de CONNECT
	 */ 
	private void handlerConnect(Client client, String username) {
		client.send("CONNECT/"+username+"/");
	}
	/**
	 * Reception de WELCOME
	 */
	private void handlerWelcome(Client client) {
		/*
		 * Rien a faire
		 */
		client.setConnected(true);
	}


	/**
	 * Reception de ACK_OPTS
	 */
	private void handlerAckOpts(Client client) {
		/*
		 * Rien a faire
		 */
	}
	private void handlerAudioAck(String[] args2, Client client) {
	}
	private void handlerAudioChunk(String[] args2, Client client) {
	}
	private void handlerAudioKo(String[] args2, Client client) {
	}
	private void handlerAudioMix(String[] args2, Client client) {
	}
	private void handlerAudioOk(String[] args2, Client client) {
	}
	/**
	 * Reception de AUDIO_PORT
	 */
	private void handlerAudioPort(Client client, String port) {
		client.setSocketAudio(Integer.parseInt(port));
	}
	/**
	 * Reception de CONNECTED 
	 */
	private void handlerConnected(Client client, String nomUser) {
	  client.addContact(nomUser);
	}
	private void handlerCurrentSession(String[] args2, Client client) {
	}
	/**
	 * Reception de EMPTY_SESSION
	 */
	private void handlerEmptySession(Client client) {
		String [] options = client.getOptionsSouhaitees();
		handlerSetOptions(options, client);
	}
	/**
	 * Envoi de EXIT 
	 */
	private void handlerExit(Client client) {
		client.send("EXIT/"+client.getNom()+"/");
		client.cleanUp();
	}
	/**
	 * Reception de EXITED 
	 */
	private void handlerExited(Client client, String nomUser) {
	  client.removeContact(nomUser);
	}
	/**
	 * Reception de FULL_SESSION
	 */
	private void handlerFullSession(Client client) {
		handlerExit(client);
	}
	/**
	 * Envoi de SET_OPTIONS
	 */
	private void handlerSetOptions(String[] options, Client client) {
		client.send("SET_OPTIONS/"+options[0]+"/"+options[1]+"/");
	}
	private void handlerLS(Client client){
		client.send("LS/");
	}
	/**
	 * Envoi de TALK
	 */
	private void handlerTalk(Client client, String texte){
		client.send("TALK/"+texte+"/");
	}
	/**
	 * Reception de LISTEN
	 */
	private void handlerListen(Client client, String nomUtil, String texte){
		client.receiveMessage(texte, nomUtil);
	}

	public static String commandeNameFromCommandeReceived(String commande){
		String [] res = commande.split("/");
		return res[0];
	}

	public static String[] argumentsFromCommande(String commande){
		String [] res = commande.split("/");
		return Arrays.copyOfRange(res, 1, res.length);
	}

}
