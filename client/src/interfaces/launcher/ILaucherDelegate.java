package interfaces.launcher;

import java.io.IOException;
import java.net.UnknownHostException;

import interfaces.inscription.IInscriptionDelegate;

/*methodes de traitement d'actions*/
public interface ILaucherDelegate{
	void connexion(String pseudo, String addr_serveur, String port_serveur)throws  UnknownHostException, IOException;
	void creerCompte();
	
}
