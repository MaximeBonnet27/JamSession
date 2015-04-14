package controller;

import interfaces.IClientInterface;
import interfaces.IClientInterfaceDelegate;
import interfaces.UIClient;
import interfaces.UIClientDebug;

import java.net.ConnectException;

import model.Client;

public class ControllerClient extends Thread implements IClientInterfaceDelegate{
  public static void main(String[] args) {
    ControllerClient controller=new ControllerClient(new UIClient().init(600, 600));
    controller.run();
  }




  private IClientInterface view;
  private UIClientDebug debugView;
  private Client modelClient;

  public ControllerClient(IClientInterface view) {
    this.setView(view);
    debugView=new UIClientDebug();
  }

  public void setView(IClientInterface view){
    this.view=view;
    this.view.setDelegate(this);
  }


  @Override
  public void run() {
    super.run();
    //this.show_communication();
    this.view.showLauncher();
  }



  @Override
  public void connexion(String pseudo, String addr_serveur,String port_serveur) {
    modelClient=new Client(addr_serveur, Integer.parseInt(port_serveur), pseudo);

    modelClient.setController(this);
    modelClient.setOutPutStreamDebug(debugView.getOutputStream());

    if(modelClient.connect()){
      modelClient.mainLoop();
      view.showProfil();
    }else{
      //view.errorConnection()
    }
  }


  /*model to controller methods*/





  /*view to controller methods*/
  @Override
  public void deconnexion() {
    view.showLauncher();
    // Si on est encore sur le launcher
    if(modelClient != null){
      modelClient.exit();
    }
  }

  @Override
  public void show_communication() {
    debugView.setVisible(true);
  }

  @Override
  public void sendMessage(String message) {
    modelClient.sendMessage(message);
    //view.receiveMessage(message, "moi");
    //view.addContact(message);
  }

  public void receiveMessage(String message, String from){
    view.receiveMessage(message, from);
  }

  public void removeContact(String nom){
    view.removeContact(nom);

  }

  public void addContact(String nom) {
    view.addContact(nom);
  }
}
