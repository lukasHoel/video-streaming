package de.hoel.video.client;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class VideoClient {

	public static void main(String[] args) {
		new NativeDiscovery().discover();
		
		JFrame player = new JFrame("Video Player");
		
		EmbeddedMediaPlayerComponent playerComp = new EmbeddedMediaPlayerComponent();
		
		player.setSize(1920, 1080);
		player.setVisible(true);
		player.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		player.setContentPane(playerComp);
		
		playerComp.getMediaPlayer().playMedia("http://localhost:8080/videos/test");
	}
}
