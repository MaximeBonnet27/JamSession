package interfaces.profil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class UIContacts extends JPanel{

		private JLabel labelConnexion;
		private JScrollPane listContact_scroll;
		private JList<String> listContact;
		private DefaultListModel<String> contactModel;
		
		public UIContacts(){
			setLayout(new BorderLayout());
			
			/*label connexion*/
			labelConnexion=new JLabel("Connexion ");
			add(labelConnexion,BorderLayout.NORTH);
			
			/*listcontact*/
			contactModel=new DefaultListModel<String>();
			listContact=new JList<>(contactModel);
			listContact.setLayoutOrientation(JList.VERTICAL);
			
			listContact_scroll=new JScrollPane(listContact);
			
			add(listContact_scroll);
		}
		
		public void init(int width, int height) {
			setPreferredSize(new Dimension(width, height));
			listContact_scroll.setPreferredSize(new Dimension(width, height));
		}
		
		public void addContact(String name){
			if(!contactModel.contains(name))
			contactModel.addElement(name);
		}
		
		public void removeContact(String name){
		  contactModel.removeElement(name);
		}
}
