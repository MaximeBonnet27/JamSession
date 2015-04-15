package interfaces.inscription;

public interface IInscriptionDelegate {
	public void inscription(String pseudo,String password,String addr_serveur,String port_serveur) throws Exception;
	public void annulerInscription();
}
