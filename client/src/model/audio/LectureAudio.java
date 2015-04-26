package model.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import model.Client;
import model.Commande;

public class LectureAudio extends Thread {

  SourceDataLine line;

  private Client client;

  public LectureAudio(Client client) {
    this.client = client;
  }

  @Override
  public void run() {

    String commandeRecue = "";
    Commande commande;
    try{
      while(client.isRunning()){
        commandeRecue = client.receiveAudio();
        if(commandeRecue == null || commandeRecue.isEmpty()) break;
        commande = Commande.getCommande(Commande.commandeNameFromCommandeReceived(commandeRecue));
        commande.handler(client, Commande.argumentsFromCommande(commandeRecue));
      }
    }
    catch(Exception e){
      if(client.isRunning()){
        e.printStackTrace();
        client.cleanUp();
        client.setConnected(false);
      }
    }
    //client.exit();
    client.cleanUp();
    client.setConnected(false);

  }

  public void playSoundFromBuffer(String buffer){
    String[] tokens = buffer.split(",");
    byte[] audio = new byte[tokens.length];
    for(int i = 0; i < tokens.length; i++){
      try{
        audio[i] = Byte.parseByte(tokens[i]);
      }catch(NumberFormatException e){
        System.err.println("erroor format " + i);
        i--;
      }
    }
    System.out.println("buffer : ");
    System.out.println(Arrays.toString(audio));
    ByteArrayInputStream bais = new ByteArrayInputStream(audio);
    AudioInputStream audioInputStream = new AudioInputStream(bais, CaptureAudio.format, audio.length / CaptureAudio.format.getFrameSize());

    try {
      audioInputStream.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
    AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(CaptureAudio.format,
        audioInputStream);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, CaptureAudio.format);
    if (!AudioSystem.isLineSupported(info)) {
      return;
    }
    try {
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(CaptureAudio.format, 16384); // 16384 = 16kB, taille du packet tcp par défaut ?
    } catch (LineUnavailableException ex) {
      return;
    }

    int frameSizeInBytes = CaptureAudio.format.getFrameSize();
    int bufferLengthInFrames = line.getBufferSize() / 8;
    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
    byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead = 0;
    System.out.println("Lecture commence");
    line.start();
    try {
      numBytesRead = playbackInputStream.read(data);
    } catch (IOException e) {
      e.printStackTrace();
    }
    int numBytesRemaining = numBytesRead;
    while (numBytesRemaining > 0) {
      numBytesRemaining -= line.write(data, 0, numBytesRemaining);
      System.out.println(numBytesRemaining);
    }
    line.stop();
    line.close();
    line = null;
    System.out.println("Lecture finie");
  }

}
