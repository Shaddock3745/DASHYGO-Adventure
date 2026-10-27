package enemyattack;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import panels.GamePanel;
import panels.MainFrame;

public final class Warning {
	private int state = 0;
	private Rectangle2D bounds;
	private boolean available = true;

	private static final int borderWidth = MainFrame.Zoom(8), z10 = MainFrame.Zoom(10);
	private static BufferedImage staticImage = null;
	private static GamePanel gamePanel;

	private int min;

	public final static void Load(GamePanel gamePanel) {
		Warning.gamePanel = gamePanel;
		try {
			staticImage = ImageIO.read(Warning.class.getResourceAsStream("/Images/EnemyAttack/Warning.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Warning(Rectangle2D bounds) {
		this.bounds = bounds;
		min = (int) (Math.min(bounds.getHeight(), bounds.getWidth())) - z10;
		gamePanel.PlaySound("Warning");
	}

	public final void next() {
		if (++state == 100) {
			this.available = false;
		}
	}

	public final void Paint(Graphics g) {
		if (available) {
			g.drawImage(staticImage, (int) (Math.round(bounds.getCenterX() - min / 2)),
					(int) (Math.round(bounds.getCenterY() - min / 2)), min, min, null);
			g.fillRect((int) (Math.round(bounds.getMinX())), (int) (Math.round(bounds.getMinY())),
					(int) (Math.round(bounds.getWidth())), borderWidth);
			g.fillRect((int) (Math.round(bounds.getMinX())), (int) (Math.round(bounds.getMaxY() - borderWidth)),
					(int) (Math.round(bounds.getWidth())), borderWidth);
			g.fillRect((int) (Math.round(bounds.getMinX())), (int) (Math.round(bounds.getMinY())), borderWidth,
					(int) (Math.round(bounds.getHeight())));
			g.fillRect((int) (Math.round(bounds.getMaxX() - borderWidth)), (int) (Math.round(bounds.getMinY())),
					borderWidth, (int) (Math.round(bounds.getHeight())));
		}
	}

	public final Rectangle2D getBounds() {
		return bounds;
	}

	public final void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}

	public final boolean isAvailable() {
		return available;
	}

	public final void setAvailable(boolean available) {
		this.available = available;
	}
}
