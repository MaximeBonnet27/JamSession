package interfaces.profil;

import interfaces.profil.tchat.ITchatDelegate;

public interface IProfilDelegate extends ITchatDelegate {
	public void deconnexion();
	public void show_communication();
}
