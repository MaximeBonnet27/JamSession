
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
		AUDIO_ACK("AUDIO_ACK");

	private String nom;
	private Commande(String nom) {
		this.nom = nom;
	}

	public static Commande getCommande(String nom){
		for(Commande c : Commande.values()){
			if(c.nom.equals(nom))
				return c;
		}
		return UNKNOWN;
	}

	public void handler(Client client,String... args){
		switch(this){
			case CONNECT : handlerConnect(args[0], client); break;
			case WELCOME : handlerWelcome(client); break;
			case ACK_OPTS: handlerAckOpts(args, client); break;
			case AUDIO_ACK: handlerAudioAck(args, client); break;
			case AUDIO_CHUNK: handlerAudioChunk(args, client); break;
			case AUDIO_KO: handlerAudioKo(args, client); break;
			case AUDIO_MIX: handlerAudioMix(args, client); break;
			case AUDIO_OK: handlerAudioOk(args, client); break;
			case AUDIO_PORT: handlerAudioPort(client); break;
			case CONNECTED: handlerConnected(args, client); break;
			case CURRENT_SESSION: handlerCurrentSession(args, client); break;
			case EMPTY_SESSION: handlerEmptySession(args, client); break;
			case EXIT: handlerExit(args, client); break;
			case EXITED: handlerExited(args, client); break;
			case FULL_SESSION: handlerFullSession(args, client); break;
			case SET_OPTIONS: handlerSetOptions(args, client); break;
			default : System.out.println("Handler Commandes : Commande inconnue" + this);

		}
	}
	/**
	 * Envoi de CONNECT
	 */ 
	private void handlerConnect(String username, Client client) {

		client.send("CONNECT/"+username+"/");
	}
	/**
	 * Reception de WELCOME
	 */
	private void handlerWelcome(Client client) {
		String val;
		if((val = client.receive()) != null){
			System.out.println(val);
		}
	}



	private void handlerAckOpts(String[] args2, Client client) {
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
	private void handlerAudioPort(Client client) {
		String val;
		if((val = client.receive()) != null){
			String[] args = argumentsFromCommande(val);
			client.setSocketAudio(Integer.parseInt(args[0]));
			
		}
	}

	private void handlerConnected(String[] args2, Client client) {
	}
	private void handlerCurrentSession(String[] args2, Client client) {
	}
	private void handlerEmptySession(String[] args2, Client client) {
	}
	private void handlerExit(String[] args2, Client client) {
	}
	private void handlerExited(String[] args2, Client client) {
	}
	private void handlerFullSession(String[] args2, Client client) {
	}
	private void handlerSetOptions(String[] args2, Client client) {
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
