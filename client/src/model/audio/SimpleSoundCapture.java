package model.audio;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;


public class SimpleSoundCapture extends JPanel implements ActionListener {

  final int bufSize = 16384;

  Capture capture = new Capture();

  Playback playback = new Playback();

  AudioInputStream audioInputStream;

  JButton playB, captB;

  JTextField textField;

  String errStr;

  double duration, seconds;

  File file;

  
  
  public SimpleSoundCapture() {
    setLayout(new BorderLayout());
    EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
    SoftBevelBorder sbb = new SoftBevelBorder(SoftBevelBorder.LOWERED);
    setBorder(new EmptyBorder(5, 5, 5, 5));

    JPanel p1 = new JPanel();
    p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));

    JPanel p2 = new JPanel();
    p2.setBorder(sbb);
    p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));

    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setBorder(new EmptyBorder(10, 0, 5, 0));
    playB = addButton("Play", buttonsPanel, false);
    captB = addButton("Record", buttonsPanel, true);
    p2.add(buttonsPanel);

    p1.add(p2);
    add(p1);
  }

  public void open() {
  }

  public void close() {
    if (playback.thread != null) {
      playB.doClick(0);
    }
    if (capture.thread != null) {
      captB.doClick(0);
    }
  }

  private JButton addButton(String name, JPanel p, boolean state) {
    JButton b = new JButton(name);
    b.addActionListener(this);
    b.setEnabled(state);
    p.add(b);
    return b;
  }

  public void actionPerformed(ActionEvent e) {
    Object obj = e.getSource();
    if (obj.equals(playB)) {
      if (playB.getText().startsWith("Play")) {
        playback.start();
        captB.setEnabled(false);
        playB.setText("Stop");
      } else {
        playback.stop();
        captB.setEnabled(true);
        playB.setText("Play");
      }
    } else if (obj.equals(captB)) {
      if (captB.getText().startsWith("Record")) {
        capture.start();
        playB.setEnabled(false);
        captB.setText("Stop");
      } else {
        capture.stop();
        playB.setEnabled(true);
      }

    }
  }

  /**
   * Write data to the OutputChannel.
   */
  public class Playback implements Runnable {

    SourceDataLine line;

    Thread thread;

    public void start() {
      errStr = null;
      thread = new Thread(this);
      thread.setName("Playback");
      thread.start();
    }

    public void stop() {
      thread = null;
    }

    private void shutDown(String message) {
      if ((errStr = message) != null) {
        System.err.println(errStr);
      }
      if (thread != null) {
        thread = null;
        captB.setEnabled(true);
        playB.setText("Play");
      }
    }

    public void run() {

      if (audioInputStream == null) {
        shutDown("No loaded audio to play back");
        return;
      }

      
      try {
        audioInputStream.reset();
      } catch (Exception e) {
        shutDown("Unable to reset the stream\n" + e);
        return;
      }


      AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
      float rate = 44100.0f;
      int channels = 2;
      int frameSize = 4;
      int sampleSize = 16;
      boolean bigEndian = true;

      AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
          * channels, rate, bigEndian);

      AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format,
          audioInputStream);

      if (playbackInputStream == null) {
        shutDown("Unable to convert stream of format " + audioInputStream + " to format " + format);
        return;
      }


      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        shutDown("Line matching " + info + " not supported.");
        return;
      }


      try {
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format, bufSize);
      } catch (LineUnavailableException ex) {
        shutDown("Unable to open the line: " + ex);
        return;
      }


      int frameSizeInBytes = format.getFrameSize();
      int bufferLengthInFrames = line.getBufferSize() / 8;
      int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
      byte[] data = new byte[bufferLengthInBytes];
      int numBytesRead = 0;

      line.start();

      while (thread != null) {
        try {
          if ((numBytesRead = playbackInputStream.read(data)) == -1) {
            break;
          }
          int numBytesRemaining = numBytesRead;
          while (numBytesRemaining > 0) {
            numBytesRemaining -= line.write(data, 0, numBytesRemaining);
          }
        } catch (Exception e) {
          shutDown("Error during playback: " + e);
          break;
        }
      }


      if (thread != null) {
        line.drain();
      }
      line.stop();
      line.close();
      line = null;
      shutDown(null);
    }
  } 


  class Capture implements Runnable {

    private int nombreEnvoisParMesure;
    private int bpm;
    public static final float SAMPLE_RATE = 44100.0f;
    public static final int CHANNELS = 2;
    public static final int SAMPLE_SIZE = 16;

    public int getNombreEnvoisParMesure() {
      return nombreEnvoisParMesure;
    }

    public double getNombreEnvoisParMinute() {
      return ((nombreEnvoisParMesure / 4.0) * bpm);
    }

    public double getNombreEnvoisParSeconde() {
      return getNombreEnvoisParMinute() / 60;
    }

    public int getBufferSize() {
      return (int) (SAMPLE_RATE / getNombreEnvoisParSeconde());
    }
    
    TargetDataLine line;

    Thread thread;

    public void start() {
      errStr = null;
      thread = new Thread(this);
      thread.setName("Capture");
      thread.start();
    }

    public void stop() {
      thread = null;
    }

    private void shutDown(String message) {
      if ((errStr = message) != null && thread != null) {
        thread = null;
        playB.setEnabled(true);
        captB.setText("Record");
        System.err.println(errStr);
      }
    }

    public void run() {
      this.bpm = 120;
      this.nombreEnvoisParMesure = 4;
      duration = 0;
      audioInputStream = null;

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
      double tick = 0.0f;
      while (thread != null) {
        if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
          break;
        }
        tick = (tick + getNombreEnvoisParSeconde() / getNombreEnvoisParMesure()) % 4;
        out.write(data, 0, numBytesRead);
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


      byte audioBytes[] = out.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
      audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

      long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format
          .getFrameRate());
      duration = milliseconds / 1000.0;

      try {
        audioInputStream.reset();
      } catch (Exception ex) {
        ex.printStackTrace();
        return;
      }

    }
  }

  public static void main(String s[]) {
    SimpleSoundCapture ssc = new SimpleSoundCapture();
    ssc.open();
    JFrame f = new JFrame("Capture/Playback");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add("Center", ssc);
    f.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int w = 360;
    int h = 170;
    f.setLocation(screenSize.width / 2 - w / 2, screenSize.height / 2 - h / 2);
    f.setSize(w, h);
    f.show();
  }
}