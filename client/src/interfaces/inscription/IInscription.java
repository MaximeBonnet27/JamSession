package interfaces.inscription;


public interface IInscription {
	public IInscription init(int width,int height);
	public void setDelegate(IInscriptionDelegate delegate);
	public void reset();
}
