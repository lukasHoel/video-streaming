/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009-2018 Caprica Software Limited.
 */

package de.hoel.video.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.sun.awt.AWTUtilities;
import com.sun.jna.platform.WindowUtils;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 * A test player demonstrating how to achieve a transparent overlay and translucent painting.
 * <p>
 * Press SPACE to pause the video play-back.
 * <p>
 * Press F11 to toggle the overlay.
 * <p>
 * If the video looks darker with the overlay enabled, then most likely you are using a compositing
 * window manager that is doing some fancy blending of the overlay window and the main application
 * window. You have to turn off those window effects.
 * <p>
 * Note that it is not possible to use this approach if you also want to use Full-Screen Exclusive
 * Mode. If you want to use an overlay and you need full- screen, then you have to emulate
 * full-screen by changing your window bounds rather than using FSEM.
 * <p>
 * This approach <em>does</em> work in full-screen mode if you use your desktop window manager to
 * put your application into full-screen rather than using the Java FSEM.
 * <p>
 * If you want to provide an overlay that dynamically updates, e.g. if you want some animation, then
 * your overlay should sub-class <code>JWindow</code> rather than <code>Window</code> since you will
 * get double-buffering and eliminate flickering. Since the overlay is transparent you must take
 * care to erase the overlay background properly.
 * <p>
 * Specify a single MRL to play on the command-line.
 */
public class OverlayTest extends VlcjTest {

    public static void main(final String[] args) throws Exception {
//        if(args.length != 1) {
//            System.out.println("Specify a single MRL");
//            System.exit(1);
//        }
    	
    	new NativeDiscovery().discover();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new OverlayTest("videos/bbb.mp4");
            }
        });
    }

    public OverlayTest(String mrl) {
        Frame f = new Frame("Test Player");
//        f.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());
        f.setSize(800, 600);
        f.setBackground(Color.black);

        f.setLayout(new BorderLayout());
        Canvas vs = new Canvas();
        f.add(vs, BorderLayout.CENTER);
        f.setVisible(true);

        final MediaPlayerFactory factory = new MediaPlayerFactory();

        final EmbeddedMediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();
        
        CanvasVideoSurface videoSurface = factory.newVideoSurface(vs);
        mediaPlayer.setVideoSurface(videoSurface);

        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_F11:
                        mediaPlayer.enableOverlay(!mediaPlayer.overlayEnabled());
                        break;

                    case KeyEvent.VK_SPACE:
                        mediaPlayer.pause();
                        break;
                }
            }
        });

        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mediaPlayer.release();
                factory.release();
                System.exit(0);
            }
        });
        
        AnnotationWindow aw = new AnnotationWindow(f, videoSurface.canvas(), mediaPlayer);
        
        mediaPlayer.setOverlay(aw);
        mediaPlayer.enableOverlay(true);

        mediaPlayer.playMedia(mrl);
        mediaPlayer.pause();
    }

    private class Overlay extends Window {

        private static final long serialVersionUID = 1L;

        public Overlay(Window owner) {
            super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());

            AWTUtilities.setWindowOpaque(this, false);

            setLayout(null);

            JButton b = new JButton("JButton");
            b.setBounds(150, 150, 100, 24);
            add(b);

            TranslucentComponent c = new TranslucentComponent();
            c.setBounds(150, 200, 300, 40);
            add(c);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(180.0f, 280.0f, new Color(255, 255, 255, 255), 250.0f, 380.0f, new Color(255, 255, 0, 0));
            g2.setPaint(gp);
            for(int i = 0; i < 3; i ++ ) {
                g2.drawOval(150, 280, 100, 100);
                g2.fillOval(150, 280, 100, 100);
                g2.translate(120, 20);
            }
        }
    }

    private class TranslucentComponent extends JComponent {

        private static final long serialVersionUID = 1L;

        public TranslucentComponent() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            g2.setPaint(new Color(255, 128, 128, 64));

            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setPaint(new Color(0, 0, 0, 128));
            g2.setFont(new Font("Sansserif", Font.BOLD, 18));
            g2.drawString("Translucent", 16, 26);
        }
    }
    
    private class AnnotationWindow extends JWindow{

		private static final long serialVersionUID = 8498200660685726854L;
		
		private Dimension videoDimension;
		private Canvas videoSurface;
		private MediaPlayer mediaPlayer;
		
		public AnnotationWindow(Window owner, Canvas videoSurface, MediaPlayer mediaPlayer) {
			
			super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
			
			videoDimension = mediaPlayer.getVideoDimension();
			
			owner.addComponentListener(new ComponentAdapter() {
	        	@Override
	        	public void componentResized(ComponentEvent e) {
	        		repaint();
	        	}
			});
			
			videoSurface.addComponentListener(new ComponentAdapter() {
	        	@Override
	        	public void componentResized(ComponentEvent e) {
	        		repaint();
	        	}
			});
			
			mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
				@Override
				public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
					repaint();
				}
			});
			
			this.videoSurface = videoSurface;
			this.mediaPlayer = mediaPlayer;
			
			setOpacity(0.5f);
			setBackground(new Color(1.0f, 1.0f, 1.0f, 0.0f));
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			Graphics2D g2 = (Graphics2D)g;
			
			if(videoDimension == null) {
				videoDimension = mediaPlayer.getVideoDimension();
			}
			
			if(videoDimension != null) {
				
				//Input in Video-Dimension: 1920 x 1080 ... this would be saved in an AnnotationFormat File
				int annoInput_X = 1000;
				int annoInput_Y = 500;
				int annoInput_W = 200;
				int annoInput_H = 100;
				
				int w = videoSurface.getWidth();
				int h = videoSurface.getHeight();
				
				//linear Interpolation from 1920 x 1080 to VideoSurface (e.g. 1000 x 500)
				int interpolated_x = (int) (1.0f * annoInput_X * w / videoDimension.width);
				int interpolated_y = (int) (1.0f * annoInput_Y * h / videoDimension.height);
				int interpolated_w = (int) (1.0f * annoInput_W * w / videoDimension.width);
				int interpolated_h = (int) (1.0f * annoInput_H * h / videoDimension.height);
				
				float aspectRatio = 1.0f * videoDimension.width / videoDimension.height;
				float surfaceRatio = 1.0f * w / h;
				
				//Determine black borders
				if(surfaceRatio > aspectRatio) {
					//border left/right -> change x / width
					
					int actualWidth = (int) (aspectRatio * h);				
					int borderSize = w - actualWidth; //left and right
					
					//recalculate values with actual width and add half of border size
					interpolated_x = (int) (1.0f * annoInput_X * actualWidth / videoDimension.width) + borderSize/2;
					interpolated_w = (int) (1.0f * annoInput_W * actualWidth / videoDimension.width);
					
				} else {
					//border up/down -> change y / height
					
					int actualHeight = (int) (w / aspectRatio);		
					int borderSize = h - actualHeight; //top and down
					
					//recalculate values with actual height and add half of border size
					interpolated_y = (int) (1.0f * annoInput_Y * actualHeight / videoDimension.height) + borderSize/2;
					interpolated_h = (int) (1.0f * annoInput_H * actualHeight / videoDimension.height);
				}
				
				
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				g.setColor(Color.GREEN);
				g2.drawRect(interpolated_x, interpolated_y, interpolated_w, interpolated_h);
	            g2.fillRect(interpolated_x, interpolated_y, interpolated_w, interpolated_h);
			}
			
			
		}
	}
}

