package client.interfaces.profil;

import client.interfaces.profil.tchat.ITchatDelegate;

public interface IProfilDelegate extends ITchatDelegate {
	public void deconnexion();
	public void show_communication();
}
