package model;

/**
 * 
 */

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

import model.audio.CaptureAudio;
import model.audio.LectureAudio;
import controller.ControllerClient;

/**
 * @author 3100381
 *
 */

//TODO
/*
 * Dans commande : remplacer les noms de commandes par THIS
 * Systeme de notify pour le connected
 * 
 */
public class Client extends Observable{

  private ControllerClient controller;

  private Socket socket;
  private Socket socketAudio;
  private BufferedReader input;
  private PrintWriter output;
  private BufferedReader inputAudio;
  private PrintWriter outputAudio;
  private String nom;
  private Boolean connected;
  private boolean running;
  private boolean recording;
  private boolean playing;
  private boolean ready;

  private String errorMessage;

  private LectureAudio lectureAudio;
  

  public Client(String adresse, int port, String nom){
    ready=true;
    running = false;
    connected=false;

    if(nom.isEmpty()){
      ready=false;
      setErrorMessage("pseudo ne doit pas etre vide");
    }
    if(nom.contains(" ")){
      ready=false;
      setErrorMessage("pseudone doit pas contenir d'espaces");
    }
    if(adresse.isEmpty()){
      ready=false;
      setErrorMessage("addresse serveur ne doit pas etre vide");
    }
    /* Mise en place de la socket et des I/O */
    if(isReady()){
      try {

        this.socket = new Socket(adresse, port);
        this.input = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(socket.getOutputStream());
        this.nom = nom;

      } catch (UnknownHostException e) {
        ready=false;
        setErrorMessage("Addresse serveur inconnue: "+e.getMessage());
      } catch (IOException e) {
        ready=false;
        setErrorMessage(e.getMessage());
      }
    }
  }

  /**
   * @param errorMessage the errorMessage to set
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * @return the errorMessage
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  public boolean isReady() {
    return ready;
  }

  public Client(String nom) throws Exception{
    this("localhost",2015, nom);
  }

  public void setConnected(boolean b) {
    synchronized (this) {
      connected=b;
      notifyAll();
    }
    if(b)
      captureAudio();
  }

  public Boolean isConnected() {
    return connected;
  }

  public void setOutPutStreamDebug(OutputStream stream){
    System.setOut(new PrintStream(stream));
  }

  public void setController(ControllerClient controller) {
    this.controller = controller;
  }

  public void sendChatMessage(String message) {
    System.out.println(message);

    message=message.replace("\\", "\\\\");
    System.out.println(message);

    message=message.replace("\n", "\\n");
    System.out.println(message);

    message=message.replace("/", "\\/");
    System.out.println(message);

    Commande.TALK.handler(this, message);
  }

  public void receiveChatMessage(String message, String nomUtil) {
    System.out.println(message);

    message=message.replace("\\/", "/");
    System.out.println(message);

    message=message.replace("\\n", "\n");
    System.out.println(message);

    message=message.replace("\\\\", "\\");
    System.out.println(message);

    message=message.replace("\\\n", "\\n");
    System.out.println(message);

    controller.receiveMessage(message, nomUtil);
  }


  public boolean connect(){
    if(ready){
      Commande.CONNECT.handler(this,nom);
      return waitConnexion();
    }
    return false;
  }

  public boolean isRunning(){
    return running;
  }
  
  public boolean isPlaying(){
    return playing;
  }
  
  public String getNom(){
    return nom;
  }

  public boolean exit(){
    if(connected){
      Commande.EXIT.handler(this, nom);
    }
    setConnected(false);
    return true;
  }

  public void cleanUp(){
    if(isRunning()){
      running = false;
      try{
        if(socket!=null)
          socket.close();

        if(input!=null)
          input.close();

        if(inputAudio!=null)
          inputAudio.close();

        if(socketAudio!=null)
          socketAudio.close();

      }catch(IOException e){
        e.printStackTrace();
      }
    }

    if(isConnected()){
      setConnected(false);
      setErrorMessage("connexion perdu");
      controller.connexion_perdu();
    }

  }

  public void send(String commande){
    System.out.println("CANAL CTRL : " + commande + " -> ");
    output.write(commande);
    output.flush();
  }

  public void sendAudio(String commande){
    System.out.println("CANAL AUDIO : " + commande.length() + " -> ");
    outputAudio.write(commande);
    outputAudio.flush();
  }

  public void sendRecording(int tick, String buffer){
    Commande.AUDIO_CHUNK.handler(this, tick +"", buffer);
  }

  public String receive() throws SocketException{
    String val = null;
    try{
      val = input.readLine();
      System.out.println("CANAL CTRL : <- " + val);
    }
    catch(IOException e){
    }
    return val;
  }

  public String [] getOptionsSouhaitees(){
    String [] res = {"Blues", "90"};
    return res;
  }


  public String receiveAudio(){
    String val = null;
    try{
      System.out.println("Avant readline audio");
      val = inputAudio.readLine();
      System.out.println("CANAL AUDIO : <- " + val);
    }
    catch(IOException e){
      e.printStackTrace();
    }
    return val;
  }

  public void setSocketAudio(int port){
    try{
      String addr = socket.getRemoteSocketAddress().toString().split("/")[0];
      if(addr.isEmpty()){
        addr = socket.getRemoteSocketAddress().toString().split("/")[1];
        addr = addr.split(":")[0];
      }
      this.socketAudio = new Socket(addr, port);
      this.inputAudio = new BufferedReader(
          new InputStreamReader(socketAudio.getInputStream()));
      this.outputAudio = new PrintWriter(socketAudio.getOutputStream());
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }


  public void removeContact(String nomUser) {
    controller.removeContact(nomUser);
  }

  public void addContact(String nomUser) {
    controller.addContact(nomUser);
  }

  public void mainLoop(){
    if(ready){
      this.running = true;
      ClientLoop loop = new ClientLoop(this);
      loop.start();
    }
  }

  public void captureAudio(){
    this.recording = true;
    CaptureAudio capture = new CaptureAudio(this);
    capture.start();
  }

  public void lectureAudio(){
    this.playing = true;
    lectureAudio = new LectureAudio(this);
    lectureAudio.start();
  }

  public boolean inscription(String password) throws Exception{
    if(password.isEmpty()){
      ready=false;
      setErrorMessage("Le mot de passe de doit pas être vide");
    }
    if(password.contains(" ")){
      ready=false;
      setErrorMessage("Le mot de passe de doit pas contenir d'espace");
    }

    if(ready){
      String[] args={nom,password};
      Commande.REGISTER.handler(this, args);
      return waitConnexion();
    }
    return false;
  }

  public boolean login(String password){
    if(password.isEmpty()){
      ready=false;
      setErrorMessage("Le mot de passe de doit pas être vide");
    }
    if(password.contains(" ")){
      ready=false;
      setErrorMessage("Le mot de passe de doit pas contenir d'espace");
    }

    if(ready){
      Commande.LOGIN.handler(this, password);
      return waitConnexion();
    }
    return false;
  }

  public boolean waitConnexion(){
    try {
      synchronized (this) {
        if(!connected)
          wait();
      }
    } catch (InterruptedException e) {
      setErrorMessage(e.getMessage());
    }
    return isConnected();
  }
  /**

   * @param nom the nom to set
   */
  public void setNom(String nom) {
    this.nom = nom;
  }

  public void playSoundFromBuffer(String buffer) {
    lectureAudio.playSoundFromBuffer(buffer);
  }

}
