package interfaces.launcher;

/*methodes d'affichage*/
public interface ILauncher {
	public ILauncher init(int width,int height);
	public void setDelegate(ILaucherDelegate delegate);
	public void reset();


}
