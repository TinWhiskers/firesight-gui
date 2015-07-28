/*
 	Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 	
 	This file is part of OpenPnP.
 	
	OpenPnP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenPnP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with OpenPnP.  If not, see <http://www.gnu.org/licenses/>.
 	
 	For more information about OpenPnP visit http://openpnp.org
 */

package io.tinwhiskers.firesight.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class CameraView extends JComponent {
	/**
	 * The last frame received, reported by the Camera.
	 */
	private BufferedImage lastFrame;

	/**
	 * The last width and height of the component that we painted for. If the
	 * width or height is different from these values at the start of paint
	 * we'll recalculate all the scaling data.
	 */
	private double lastWidth, lastHeight;

	/**
	 * The last width and height of the image that we painted for. If the width
	 * or height is different from these values at the start of paint we'll
	 * recalculate all the scaling data.
	 */
	private double lastSourceWidth, lastSourceHeight;

	/**
	 * The width and height of the image after it has been scaled to fit the
	 * bounds of the component.
	 */
	private int scaledWidth, scaledHeight;

	/**
	 * The ratio of scaled width and height to unscaled width and height.
	 * scaledWidth * scaleRatioX = sourceWidth. scaleRatioX = sourceWidth /
	 * scaledWidth
	 */
	private double scaleRatioX, scaleRatioY;

	/**
	 * The top left position within the component at which the scaled image can
	 * be drawn for it to be centered.
	 */
	private int imageX, imageY;

	public CameraView() {
		setBackground(Color.black);
		setOpaque(true);

		addComponentListener(componentListener);
	}

	public void frameReceived(BufferedImage img) {
		BufferedImage oldFrame = lastFrame;
		lastFrame = img;
		if (oldFrame == null
				|| (oldFrame.getWidth() != img.getWidth() || oldFrame
						.getHeight() != img.getHeight())) {
			calculateScalingData();
		}
		repaint();
	}

	/**
	 * Calculates a bunch of scaling data that we cache to speed up painting.
	 * This is recalculated when the size of the component or the size of the
	 * source changes. This method is synchronized, along with paintComponent()
	 * so that the updates to the cached data are atomic.
	 * TODO: Also need to update if the camera's units per pixels changes.
	 */
	private synchronized void calculateScalingData() {
		BufferedImage image = lastFrame;

		if (image == null) {
			return;
		}

		Insets ins = getInsets();
		int width = getWidth() - ins.left - ins.right;
		int height = getHeight() - ins.top - ins.bottom;

		double destWidth = width, destHeight = height;

		lastWidth = width;
		lastHeight = height;

		lastSourceWidth = image.getWidth();
		lastSourceHeight = image.getHeight();

		double heightRatio = lastSourceHeight / destHeight;
		double widthRatio = lastSourceWidth / destWidth;

		if (heightRatio > widthRatio) {
			double aspectRatio = lastSourceWidth / lastSourceHeight;
			scaledHeight = (int) destHeight;
			scaledWidth = (int) (scaledHeight * aspectRatio);
		}
		else {
			double aspectRatio = lastSourceHeight / lastSourceWidth;
			scaledWidth = (int) destWidth;
			scaledHeight = (int) (scaledWidth * aspectRatio);
		}

		imageX = ins.left + (width / 2) - (scaledWidth / 2);
		imageY = ins.top + (height / 2) - (scaledHeight / 2);

		scaleRatioX = lastSourceWidth / (double) scaledWidth;
		scaleRatioY = lastSourceHeight / (double) scaledHeight;
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage image = lastFrame;
		Insets ins = getInsets();
		int width = getWidth() - ins.left - ins.right;
		int height = getHeight() - ins.top - ins.bottom;
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(getBackground());
		g2d.fillRect(ins.left, ins.top, width, height);
		if (image != null) {
			// Only render if there is a valid image.
			g2d.drawImage(lastFrame, imageX, imageY, scaledWidth, scaledHeight,
					null);
		}
	}

	private ComponentListener componentListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			calculateScalingData();
		}
	};
}
