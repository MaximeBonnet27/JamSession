package interfaces.profil;

import interfaces.profil.tchat.ITchat;

public interface IProfil extends ITchat{
	public IProfil init(int width,int height);
	public void addContact(String name);
	public void removeContact(String name);
}
