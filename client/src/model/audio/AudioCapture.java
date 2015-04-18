package model.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioCapture implements Runnable {

  private int nombreEnvoisParMesure;
  private int bpm;
  public static final float SAMPLE_RATE = 44100.0f;
  public static final int CHANNELS = 2;
  public static final int SAMPLE_SIZE = 16;
  Thread thread;
  TargetDataLine line;
  public AudioCapture(int nombreEnvoisParMesure, int bpm) {
    this.nombreEnvoisParMesure = nombreEnvoisParMesure;
    this.bpm = bpm;
  }

  public int getNombreEnvoisParMesure() {
    return nombreEnvoisParMesure;
  }

  public double getNombreEnvoisParMinute() {
    return  ((nombreEnvoisParMesure / 4.0) * bpm);
  }

  public double getNombreEnvoisParSeconde() {
    return getNombreEnvoisParMinute() / 60;
  }

  public int getBufferSize() {
    return (int) (SAMPLE_RATE / getNombreEnvoisParSeconde());
  }

  public void start() {
    thread = new Thread(this);
    thread.setName("Capture Audio Client");
    thread.start();
  }

  public void stop() {
    thread = null;
  }

  public void run() {
    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
    float rate = SAMPLE_RATE;
    int channels = CHANNELS;
    int sampleSize = SAMPLE_SIZE;
    boolean bigEndian = true;
    AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
        * channels, rate, bigEndian);
    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

    if (!AudioSystem.isLineSupported(info)) {
      stop();
      return;
    }

    try {
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format, getBufferSize());
    } catch (Exception ex) {
      stop();
      return;
    }
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    int frameSizeInBytes = format.getFrameSize();
    int bufferLengthInFrames = getBufferSize() / 8;
    int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
    byte[] data = new byte[bufferLengthInBytes];
    int numBytesRead; 

    line.start();
    double tick = 0;
    while (thread != null) {
      if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
        break;
      }
      out.write(data, 0, numBytesRead);
      for(int i = 0; i < bufferLengthInBytes; i++) System.out.println(data[i]);
      tick = (tick + getNombreEnvoisParSeconde() / getNombreEnvoisParMesure()) % 4;
    }

    line.stop();
    line.close();
    line = null;
    try {
      out.flush();
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

  }

}
