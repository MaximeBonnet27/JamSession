package interfaces;

import interfaces.profil.IProfil;

public interface IClientInterface extends IProfil {
	public IClientInterface init(int width,int height);
	public void setDelegate(IClientInterfaceDelegate delegate);
	public void showLauncher();
	public void showProfil();
}
