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

public final class SlimeBullet extends Bullet {

	private static BufferedImage image = null;
	private DoubleCircle oldhitbox, hitbox;
	private double moveX, moveY;

	private static final int z40 = Zoom(40), z20 = Zoom(20), z70 = Zoom(70), z1830 = Zoom(1830), z990 = Zoom(990);

	public SlimeBullet(double x, double y, double radins, double speed) {
		// TODO Auto-generated constructor stub
		this.speed = speed;
		size = new DoubleDimension(z40, z40);
		position = new DoublePoint(x - z20, y - z20);
		hitbox = new DoubleCircle(x, y, z20);
		oldhitbox = hitbox.clone();
		Rotate(radins);
		gamePanel.PlaySound("Slime_Puff");
	}

	public static final void Load() {
		try {
			image = ImageIO.read(MainFrame.class.getResourceAsStream("/Images/Bullet/SlimeBullet.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public final void PaintBullet(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable)
			g.drawImage(image, (int) Math.round(position.getX()), (int) Math.round(position.getY()), z40, z40, null);
	}

	@Override
	public final void Move(DoublePoint playerCenterPoint) {
		// TODO Auto-generated method stub
		position.offsetX(moveX);
		position.offsetY(moveY);
		oldhitbox = hitbox.clone();
		hitbox.offset(moveX, moveY);
		if (position.getX() < z70 || position.getX() > z1830 || position.getY() < z70 || position.getY() > z990)
			this.removeable = true;
	}

	@Override
	public final void Countered(DoubleCircle playerCounterCircle) {
		// TODO Auto-generated method stub
		if (!isReverse && hitbox.isCollidingCircle(playerCounterCircle)) {
			Radins = Math.atan2(this.position.getY() - playerCounterCircle.getCenterY(),
					this.position.getX() - playerCounterCircle.getCenterX());
			moveX = Math.cos(Radins) * speed;
			moveY = Math.sin(Radins) * speed;
			isReverse = true;
			gamePanel.PlaySound("Slime_Puff");
		}
	}

	@Override
	public final boolean HitCheck(DoublePolygon playerHitbox) {
		return !removeable && (hitbox.isColliding(playerHitbox) || oldhitbox.isColliding(playerHitbox)
				|| Collision.getSweptPolygon(oldhitbox, hitbox).isColliding(playerHitbox));
	}

	@Override
	public final void Rotate(double radins) {
		// TODO Auto-generated method stub
		if (radins == 0)
			return;
		Radins += radins;
		if (Radins >= Math.PI * 2)
			Radins -= Math.PI * 2;
		moveX = Math.cos(Radins) * speed;
		moveY = Math.sin(Radins) * speed;
	}
}
