package bullet;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import doubleshape2d.DoubleCircle;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.GamePanel;
import panels.MainFrame;

public abstract class Bullet {

	protected DoublePoint position;
	protected DoubleDimension size;

	protected int imageIndex = 0;
	protected double Radins, speed;
	protected boolean removeable = false, isReverse = false;
	protected double moveX, moveY;
	protected static GamePanel gamePanel;

	public static void Load(GamePanel gamePanel) {
		Bullet.gamePanel = gamePanel;
		SlimeBullet.Load();
		FireBall.Load();
		Rock.Load();
		Seed.Load();
		IceMallet.Load();
	}

	protected static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}

	public abstract void PaintBullet(Graphics g);

	public final boolean isRemoveable() {
		return removeable;
	}

	public abstract boolean HitCheck(DoublePolygon playerHitbox);

	public abstract void Move(DoublePoint playerCenterPoint);

	public abstract void Countered(DoubleCircle playerCounterCircle);

	public abstract void Rotate(double radins);

	protected static final BufferedImage getRotateImage(BufferedImage image, double radins) {
		if (radins == 0)
			return image;
		int width = image.getWidth(), height = image.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.rotate(radins, Double.valueOf(width) / 2, Double.valueOf(height) / 2);
		g2d.drawImage(image, 0, 0, width, height, 0, 0, width, height, null);
		g2d.dispose();
		return img;
	}

	public static final double getLockPlayerRadin(DoublePoint playerCenterPoint, double x, double y) {
		return Math.atan2(playerCenterPoint.getY() - y, playerCenterPoint.getX() - x);
	}

	public static final double getLockPlayerRadin(DoublePoint playerCenterPoint, DoublePoint center) {
		return Math.atan2(playerCenterPoint.getY() - center.getY(), playerCenterPoint.getX() - center.getX());
	}
}
