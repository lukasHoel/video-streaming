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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

/**
 * This simple test player shows how to get direct access to the video frame data.
 * <p>
 * This implementation uses the new (1.1.1) libvlc video call-backs function.
 * <p>
 * Since the video frame data is made available, the Java call-back may modify the contents of the
 * frame if required.
 * <p>
 * The frame data may also be rendered into components such as an OpenGL texture.
 */
public class DirectTestPlayer {

    // The size does NOT need to match the mediaPlayer size - it's the size that
    // the media will be scaled to
    // Matching the native size will be faster of course
    private final int width;

    private final int height;

    // private final int width = 1280;
    // private final int height = 720;

    /**
     * Image to render the video frame data.
     */
    private final BufferedImage image;

    private final MediaPlayerFactory factory;

    private final DirectMediaPlayer mediaPlayer;

    public JPanel getImagePane() {
		return imagePane;
	}

	public DirectMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	private final ImagePane imagePane;

    public DirectTestPlayer(int width, int height, String[] args) throws InterruptedException, InvocationTargetException {
        image = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
        image.setAccelerationPriority(1.0f);
        
        this.width = width;
        this.height = height;

        imagePane = new ImagePane(image);
        imagePane.setSize(width, height);
        imagePane.setMinimumSize(new Dimension(width, height));
        imagePane.setPreferredSize(new Dimension(width, height));
        
        factory = new MediaPlayerFactory(args);
        mediaPlayer = factory.newDirectMediaPlayer(new TestBufferFormatCallback(), new TestRenderCallback());
    }

    @SuppressWarnings("serial")
    private final class ImagePane extends JPanel {

        private final BufferedImage image;

        private final Font font = new Font("Sansserif", Font.BOLD, 36);

        public ImagePane(BufferedImage image) {
            this.image = image;
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.drawImage(image, null, 0, 0);
            // You could draw on top of the image here...
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//            g2.setColor(Color.red);
//            g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
//            g2.fillRoundRect(100, 100, 100, 80, 32, 32);
//            g2.setComposite(AlphaComposite.SrcOver);
//            g2.setColor(Color.white);
//            g2.setFont(font);
//            g2.drawString("vlcj direct media player", 130, 150);
        }
    }

    private final class TestRenderCallback extends RenderCallbackAdapter {

        public TestRenderCallback() {
            super(((DataBufferInt) image.getRaster().getDataBuffer()).getData());
        }

        @Override
        public void onDisplay(DirectMediaPlayer mediaPlayer, int[] data) {
            // The image data could be manipulated here...
//        	Mat frame = new Mat(height, width, CvType.CV_8UC3);
//        	frame.put(0, 0, data);
//        	
//        	Mat blurredImage = new Mat();
//        	Mat hsvImage = new Mat();
//        	Mat mask = new Mat();
//        	Mat morphOutput = new Mat();
//
//        	// remove some noise
//        	Imgproc.blur(frame, blurredImage, new Size(7, 7));
//
//        	// convert the frame to HSV
//        	Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);
//        	
//        	// get thresholding values from the UI
//        	// remember: H ranges 0-180, S and V range 0-255
//        	Scalar minValues = new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(),
//        	this.valueStart.getValue());
//        	Scalar maxValues = new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(),
//        	this.valueStop.getValue());
//
//        	// show the current selected HSV range
//        	String valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
//        	+ "\tSaturation range: " + minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: "
//        	+ minValues.val[2] + "-" + maxValues.val[2];
//        	this.onFXThread(this.hsvValuesProp, valuesToPrint);
//
//        	// threshold HSV image to select tennis balls
//        	Core.inRange(hsvImage, minValues, maxValues, mask);
//        	// show the partial output
//        	this.onFXThread(maskProp, this.mat2Image(mask));
//        	
//        	// init
//        	List<MatOfPoint> contours = new ArrayList<>();
//        	Mat hierarchy = new Mat();
//
//        	// find contours
//        	Imgproc.findContours(maskedImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        	// if any contour exist...
//        	if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
//        	{
//        	        // for each contour, display it in blue
//        	        for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
//        	        {
//        	                Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
//        	        }
//        	}
        	
            /* RGB to GRAYScale conversion example */
            for(int i=0; i < data.length; i++){
                int argb = data[i];
                int b = (argb & 0xFF);
                int g = ((argb >> 8 ) & 0xFF);
                int r = ((argb >> 16 ) & 0xFF);
                int grey = (r + g + b + g) >> 2 ; //performance optimized - not real grey!
                data[i] = (grey << 16) + (grey << 8) + grey;
            }
            imagePane.repaint();
        }
    }

    private final class TestBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            return new RV32BufferFormat(width, height);
        }

    }
}