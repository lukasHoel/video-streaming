package de.hoel.video.client;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;

public class VideoClient {

	public static void main(String[] args) throws InvocationTargetException, InterruptedException {
		new NativeDiscovery().discover();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("Mat: " + mat.dump());
		
		JFrame player = new JFrame("Video Player");
		
		//1 - Direct Test Player
//		DirectTestPlayer directTestPlayer = new DirectTestPlayer(640, 480, args);
//		
//		MediaPlayer mediaPlayer = directTestPlayer.getMediaPlayer();
//		JPanel imagePane = directTestPlayer.getImagePane();
		
		//2 - EmbeddedMediaPlayer
		EmbeddedMediaPlayerComponent embeddedMediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		MediaPlayer mediaPlayer = embeddedMediaPlayerComponent.getMediaPlayer();
		
		
		player.setSize(640, 480);
		player.setVisible(true);
		player.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		player.setContentPane(imagePane);
		player.setContentPane(embeddedMediaPlayerComponent);
		
		mediaPlayer.playMedia("videos/bbb.mp4");
	}
}


