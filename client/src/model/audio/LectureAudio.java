package model.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import model.Client;
import model.Commande;

public class LectureAudio extends Thread implements LineListener {

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
      }
    }
    client.cleanUp();

  }

  public void playSoundFromBuffer(String buffer){
    String[] tokens = buffer.split(",");
    byte[] audio = new byte[tokens.length];
    for(int i = 0; i < tokens.length; i++){
      try{
        audio[i] = Byte.parseByte(tokens[i]);
      }catch(NumberFormatException e){
      }
    }
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
      line.addLineListener(this);
      line.open(CaptureAudio.format, 16384); // 16384 = 16kB, taille du packet tcp par défaut ?
    } catch (LineUnavailableException ex) {
      return;
    }catch(IllegalStateException e){
      e.printStackTrace();
    }

    int frameSizeInBytes = CaptureAudio.format.getFrameSize();
    int bufferLengthInFrames = line.getBufferSize() / 8;
    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
    byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead = 0;
 
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
    line.drain();
    line.stop();
    line.close();
    line = null;
  }

  @Override
  public void update(LineEvent arg0) {
    System.out.println("EVENT : " + arg0.getType());
  }

}
