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

public final class Seed extends Bullet {

	private static BufferedImage image = null;
	private DoublePolygon hitbox, oldHitbox;

	public static void Load() {
		try {
			image = ImageIO.read(MainFrame.class.getResourceAsStream("/Images/Bullet/Seed.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int z40 = Zoom(40), z20 = Zoom(20), z8 = Zoom(8), z12 = Zoom(12), z4 = Zoom(4), z62 = Zoom(62),
			z1838 = Zoom(1838), z998 = Zoom(998);

	public Seed(double x, double y, double radins, double speed) {
		this.speed = speed;
		size = new DoubleDimension(z40, z40);
		position = new DoublePoint(x - z20, y - z20);
		hitbox = new DoublePolygon(8,
				new double[] { x - z8, x - z12, x - z12, x - z8, x + z8, x + z12, x + z12, x + z8 },
				new double[] { y - z8, y - z4, y + z4, y + z8, y - z8, y - z4, y + z4, y + z8 });
		Rotate(radins);
		oldHitbox = hitbox.clone();
	}

	@Override
	public void PaintBullet(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable)
			g.drawImage(getRotateImage(image, Radins), (int) Math.round(position.getX()),
					(int) Math.round(position.getY()), z40, z40, null);
	}

	@Override
	public boolean HitCheck(DoublePolygon playerHitbox) {
		// TODO Auto-generated method stub
		return !removeable && (hitbox.isColliding(playerHitbox) || oldHitbox.isColliding(playerHitbox)
				|| Collision.getSweptPolygon(oldHitbox, hitbox).isColliding(playerHitbox));
	}

	@Override
	public void Move(DoublePoint playerCenterPoint) {
		// TODO Auto-generated method stub
		position.offsetX(moveX);
		position.offsetY(moveY);
		oldHitbox = hitbox.clone();
		hitbox.offest(moveX, moveY);
		if (position.getX() < z62 || position.getX() > z1838 || position.getY() < z62 || position.getY() > z998)
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
