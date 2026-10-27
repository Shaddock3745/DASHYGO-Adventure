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

public final class IceMallet extends Bullet {

	private static BufferedImage image = null;
	private DoublePolygon hitbox, oldHitbox;
	private short generateTick = 120;
	private double moveX, moveY;

	public static final void Load() {
		try {
			image = ImageIO.read(Bullet.class.getResourceAsStream("/Images/Bullet/IceMallet.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int z50 = Zoom(50), z25 = Zoom(25), z11 = Zoom(11), z20 = Zoom(20), z14 = Zoom(14);

	public IceMallet(double x, double y, double radins, double speed) {
		this.speed = speed;
		size = new DoubleDimension(z50, z50);
		position = new DoublePoint(x - z25, y - z25);
		hitbox = new DoublePolygon(3, new double[] { x - z11, x + z11, x + z20 }, new double[] { y - z14, y - z14, y });
		Rotate(radins);
		oldHitbox = hitbox.clone();
		gamePanel.PlaySound("IceMallet");
	}

	@Override
	public void PaintBullet(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable)
			g.drawImage(getRotateImage(image, Radins), (int) Math.round(position.getX()),
					(int) Math.round(position.getY()), z50, z50, null);
	}

	@Override
	public boolean HitCheck(DoublePolygon playerHitbox) {
		// TODO Auto-generated method stub
		return !removeable && (hitbox.isColliding(playerHitbox) || oldHitbox.isColliding(playerHitbox)
				|| Collision.getSweptPolygon(oldHitbox, hitbox).isColliding(playerHitbox));
	}

	private static final int z65 = Zoom(65), z1805 = Zoom(1805), z965 = Zoom(965);

	@Override
	public void Move(DoublePoint playerCenterPoint) {
		// TODO Auto-generated method stub
		if (generateTick-- > 0)
			Rotate(getLockPlayerRadin(playerCenterPoint, hitbox.getGravityCenterPoint()) - Radins);
		else {
			position.offsetX(moveX);
			position.offsetY(moveY);
			oldHitbox = hitbox.clone();
			hitbox.offest(moveX, moveY);
			if (position.getX() < z65 || position.getX() > z1805 || position.getY() < z65 || position.getY() > z965)
				this.removeable = true;
		}
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
		if (radins == 0)
			return;
		Radins += radins;
		if (Radins >= Math.PI * 2)
			Radins -= Math.PI * 2;
		hitbox.Rotate(radins);
		moveX = Math.cos(Radins) * speed;
		moveY = Math.sin(Radins) * speed;
	}

}
