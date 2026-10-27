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

public final class FireBall extends Bullet {

	private static BufferedImage images = null;
	private static final double rotateRadins = 0.004;
	private DoubleCircle oldhitbox, hitbox;
	private double moveX, moveY;
	private double playerRadins;

	private static int z24 = Zoom(24), z60 = Zoom(60), z120 = Zoom(120);

	public FireBall(double x, double y, double radins, double speed) {
		hitbox = new DoubleCircle(x, y, z24);
		oldhitbox = hitbox.clone();
		this.speed = speed;
		position = new DoublePoint(x - z60, y - z60);
		size = new DoubleDimension(z120, z120);
		Rotate(radins);
		gamePanel.PlaySound("Fireball");
	}

	public static final void Load() {
		try {
			images = ImageIO.read(FireBall.class.getResourceAsStream("/Images/Bullet/FireBall.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public final void Rotate(double radins) {
		// TODO Auto-generated method stub
		Radins += radins;
		if (Radins >= Math.PI * 2)
			Radins -= Math.PI * 2;
		moveX = Math.cos(Radins) * speed;
		moveY = Math.sin(Radins) * speed;
	}

	@Override
	public final void PaintBullet(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable)
			g.drawImage(getRotateImage(images, Radins), (int) Math.round(position.getX()),
					(int) Math.round(position.getY()), z120, z120, null);
	}

	@Override
	public final boolean HitCheck(DoublePolygon playerHitbox) {
		// TODO Auto-generated method stub
		return !removeable && (hitbox.isColliding(playerHitbox) || oldhitbox.isColliding(playerHitbox)
				|| Collision.getSweptPolygon(oldhitbox, hitbox).isColliding(playerHitbox));
	}

	private static final int z22 = Zoom(22), z1778 = Zoom(1778), z938 = Zoom(938);

	@Override
	public final void Move(DoublePoint playerCenterPoint) {
		// TODO Auto-generated method stub
		if (!isReverse) {
			playerRadins = getLockPlayerRadin(playerCenterPoint, hitbox.getCenterPoint());
			if (playerRadins < Radins)
				Rotate(-rotateRadins);
			else if (playerRadins > Radins)
				Rotate(rotateRadins);
		}
		position.offsetX(moveX);
		position.offsetY(moveY);
		oldhitbox = hitbox.clone();
		hitbox.offset(moveX, moveY);
		if (position.getX() < z22 || position.getX() > z1778 || position.getY() < z22 || position.getY() > z938)
			this.removeable = true;
	}

	@Override
	public final void Countered(DoubleCircle playerCounterCircle) {
		// TODO Auto-generated method stub
		if (!isReverse && hitbox.isCollidingCircle(playerCounterCircle)) {
			Radins = getLockPlayerRadin(hitbox.getCenterPoint(), playerCounterCircle.getCenterPoint());
			moveX = Math.cos(Radins) * speed;
			moveY = Math.sin(Radins) * speed;
			isReverse = true;
			gamePanel.PlaySound("Fireball");
		}
	}

}
