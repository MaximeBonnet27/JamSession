package client.interfaces;

public interface IClientInterface {
	public IClientInterface init(int width,int height);
	public void setDelegate(IClientInterfaceDelegate delegate);
	public void showLauncher();
	public void showProfil();
}
