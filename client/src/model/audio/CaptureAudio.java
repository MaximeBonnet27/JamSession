package model.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import model.Client;

public class CaptureAudio extends Thread{

	TargetDataLine line;

	Client client;
	public CaptureAudio(Client client2) {
		this.client = client2;
	}


	@Override
	public void run() {
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 44100.0f;
		int channels = 2;
		int frameSize = 4;
		int sampleSize = 16;
		boolean bigEndian = true;

		AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8)
				* channels, rate, bigEndian);

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("Line matching " + info + " not supported.");
			return;
		}

		// get and open the target data line for capture.

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, 44100);
		} catch (LineUnavailableException ex) {
			System.out.println("Unable to open the line: " + ex);
			return;
		} catch (SecurityException ex) {
			System.out.println(ex.toString());
			//JavaSound.showInfoDialog();
			return;
		} catch (Exception ex) {
			System.out.println(ex.toString());
			return;
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 32;//16;//8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;

		line.start();

		numBytesRead = line.read(data, 0, bufferLengthInBytes);
		out.write(data, 0, numBytesRead);

		line.stop();
		line.close();
		line = null;
		String bufferString = Arrays.toString(data).substring(1, data.length - 1);
		client.sendRecording(0,bufferString);
		System.out.println("bufferString.length():"+bufferString.length()+"\n**"+bufferString);
		// stop and close the output stream
		try {
			out.flush();
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}



	}



}
