/**
 * 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author 3100381
 *
 */
public class Client {

  private Socket socket;
  private BufferedReader input;
  private OutputStreamWriter output;
  private String nom;
	public Client(String adresse, int port, String nom) {
	  /* Mise en place de la socket et des I/O */
	  try{
	  this.socket = new Socket(adresse, port);
	  this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	  this.output = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
	  }catch(Exception e){
	    e.printStackTrace();
	  }
	  
	  this.nom = nom;
	}
	public Client(String nom){
	  this("localhost",2015, nom);
	}
	
	public boolean connect(){
	  send("CONNECT/"+nom+"/");
	  return true;
	}
	
	public boolean exit(){
	  try{
	  socket.close();
	  input.close();
	  }
	  catch(Exception e){
	    e.printStackTrace();
	  }
	  return true;
	}
	
	public void send(String commande){
	  try {
      output.write(commande, 0, commande.length());
    } catch (IOException e) {
      e.printStackTrace();
    }
	}
	
	
	public static void main(String... args) throws UnknownHostException, IOException {
	  Client client = new Client("maxime");
	  client.connect();
	  client.exit();
	}
	
}
