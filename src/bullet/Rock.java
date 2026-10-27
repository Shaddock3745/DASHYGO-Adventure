package bullet;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import doubleshape2d.Collision;
import doubleshape2d.DoubleCircle;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Rock extends Bullet {

	private static BufferedImage image = null;
	private DoublePolygon hitbox, oldHitbox;
	private double moveX, moveY;

	public static void Load() {
		try {
			image = ImageIO.read(MainFrame.class.getResourceAsStream("/Images/Bullet/Rock.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int z60 = Zoom(60), z30 = Zoom(30), z27 = Zoom(27), z12 = Zoom(12), z15 = Zoom(15),
			z29 = Zoom(29), z17 = Zoom(17), z8 = Zoom(8), z23 = Zoom(23), z37 = Zoom(37);

	public Rock(double x, double y, double radins, double speed) {
		this.speed = speed;
		size = new DoubleDimension(z60, z60);
		position = new DoublePoint(x - z30, y - z30);
		hitbox = new DoublePolygon(8,
				new double[] { x - z30, x - z27, x - z12, x + z15, x + z29, x + z29, x + z17, x - z15 },
				new double[] { y + z8, y - z12, y - z27, y - z23, y + z37, y + z8, y + z17, y + z23 });
		Rotate(radins);
		oldHitbox = hitbox.clone();
	}

	@Override
	public void PaintBullet(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable)
			g.drawImage(getRotateImage(image, Radins), (int) Math.round(position.getX()),
					(int) Math.round(position.getY()), z60, z60, null);
	}

	@Override
	public boolean HitCheck(DoublePolygon playerHitbox) {
		// TODO Auto-generated method stub
		return !removeable && (hitbox.isColliding(playerHitbox) || oldHitbox.isColliding(playerHitbox)
				|| Collision.getSweptPolygon(oldHitbox, hitbox).isColliding(playerHitbox));
	}

	private static final int z70 = Zoom(70), z1790 = Zoom(1790), z950 = Zoom(950);

	@Override
	public void Move(DoublePoint playerCenterPoint) {
		// TODO Auto-generated method stub
		position.offsetX(moveX);
		position.offsetY(moveY);
		oldHitbox = hitbox.clone();
		hitbox.offest(moveX, moveY);
		if (position.getX() < z70 || position.getX() > z1790 || position.getY() < z70 || position.getY() > z950)
			this.removeable = true;
	}

	@Override
	public void Countered(DoubleCircle playerCounterCircle) {
		// TODO Auto-generated method stub
		if (!isReverse && hitbox.isCollidingCircle(playerCounterCircle)) {
			Radins = getLockPlayerRadin(hitbox.getGravityCenterPoint(), playerCounterCircle.getCenterPoint());
			moveX = Math.cos(Radins) * speed;
			moveY = Math.sin(Radins) * speed;
			isReverse = true;
			gamePanel.PlaySound("Counter");
		}
	}

	@Override
	public void Rotate(double radins) {
		// TODO Auto-generated method stub
		Radins = radins;
		if (Radins >= Math.PI * 2)
			Radins -= Math.PI * 2;
		hitbox.Rotate(radins);
		moveX = Math.cos(Radins) * speed;
		moveY = Math.sin(Radins) * speed;
	}

}
