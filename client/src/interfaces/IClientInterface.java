package interfaces;

import interfaces.profil.IProfil;

public interface IClientInterface extends IProfil {
	public IClientInterface init(int width,int height);
	public void setDelegate(IClientInterfaceDelegate delegate);
	public void showProfil(String name);
	public void showLauncher();
	public void showLauncher(String message); /*pour retoure sur Launcher avec erreur message*/
	public void showInscription();
	public void showInscription(String message);
	public void showConnexionEnCours();
	public Boolean isInitialized();
}
