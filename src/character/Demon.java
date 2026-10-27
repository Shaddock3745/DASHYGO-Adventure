package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import bullet.Bullet;
import bullet.FireBall;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Demon extends Enemy {

	private static BufferedImage[] images, bornImages, DeadImages;
	private static final double bulletRadinsDiscrepancy = Math.PI / 6;

	private static final int z210 = Zoom(210), z30 = Zoom(30), z105 = Zoom(105), z165 = Zoom(165), z270 = Zoom(270);

	public Demon(double x, double y) {
		this.position = new DoublePoint(x - z105, y - z105);
		this.size = new DoubleDimension(z210, z210);
		hitbox = new DoublePolygon(4, new double[] { x - z30, x - z30, x + z30, x + z30 },
				new double[] { y + z30, y - z30, y + z30, y - z30 });
	}

	public static void Load() {
		images = new BufferedImage[4];
		bornImages = new BufferedImage[7];
		DeadImages = new BufferedImage[4];
		try {
			for (int i = 0; i < 3; i++) {
				images[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/Demon_%d.png", i)));
				DeadImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/DemonDead_%d.png", i)));
			}
			images[3] = images[1];
			DeadImages[3] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/DemonDead_3.png"));
			for (int i = 0; i < 6; i++) {
				bornImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/DemonBorn_%d.png", i)));
			}
			bornImages[6] = images[0];
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public synchronized void Next(DoublePoint playerCenter) {
		// TODO Auto-generated method stub
		if (++imageIndexTemp == 15) {
			imageIndex++;
			imageIndexTemp = 0;
		}
		switch (lifeState) {
		case Borning:
			if (imageIndex == 7) {
				imageIndex = 0;
				lifeState = LifeState.Alive;
			}
			break;
		case Alive:
			if (imageIndex == 4)
				imageIndex = 0;
			switch (++actionTick) {
			case 60:
				throwFireBall(playerCenter);
				break;
			case 600:
				actionTick = 0;
				break;
			}
			break;
		case Dying:
			if (imageIndex == 1)
				gamePanel.PlaySound("Enemy_Dead");
			if (imageIndex == 4)
				lifeState = LifeState.Removeable;
			break;
		default:
			break;
		}

	}

	private static final double z3_75 = 3.75 * MainFrame.getZoomRatio();

	private void throwFireBall(DoublePoint playerCenter) {
		double x = position.getX() + z105;
		double y = position.getY() + z165;
		double radins = Bullet.getLockPlayerRadin(playerCenter, x, y) - bulletRadinsDiscrepancy;
		gamePanel.addBullet(new FireBall(x, y, radins, z3_75));
		for (int i = 0; i < 2; i++)
			gamePanel.addBullet(new FireBall(x, y, radins += bulletRadinsDiscrepancy, z3_75));
	}

	@Override
	protected synchronized void AfterBeenAttacked() {
		// TODO Auto-generated method stub
		if (lifeState == LifeState.Alive) {
			imageIndex = 0;
			lifeState = LifeState.Dying;
			imageIndexTemp = -10;
		}
	}

	@Override
	public void Paint(Graphics g) {
		// TODO Auto-generated method stub
		switch (lifeState) {
		case Borning:
			g.drawImage(bornImages[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()),
					z210, z210, null);
			break;
		case Alive:
			g.drawImage(images[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()), z210,
					z210, null);
			break;
		case Dying:
			g.drawImage(DeadImages[imageIndex], (int) Math.round(position.getX()) - z30,
					(int) Math.round(position.getY()) - z30, z270, z270, null);
			break;
		default:
			break;
		}
	}
}
