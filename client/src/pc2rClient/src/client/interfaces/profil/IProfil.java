package client.interfaces.profil;


public interface IProfil {
	public IProfil init(int width,int height);
	public void setDelegate(IProfilDelegate delegate);
	public void reset();

}
