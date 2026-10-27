package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import bullet.Bullet;
import bullet.Seed;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import enemyattack.Thorns;
import panels.MainFrame;

public final class FlyTrap extends Enemy {

	private static BufferedImage[] leftImages, rightImages, leftDeadImages, rightDeadImages, leftAppearImages,
			rightAppearImages, leftDisapperImages, rightDisapperImages, leftPuffImages, rightPuffImages;

	private static enum ActionState {
		Normal, Appear, Disappear, Puff
	}

	private static final double bulletRadinsDiscrepancy = Math.PI / 18;

	private boolean left = true;

	private ActionState actionState = ActionState.Normal;

	private static final int z120 = Zoom(120), z240 = Zoom(240), z74 = Zoom(74), z99 = Zoom(99), z110 = Zoom(110),
			z1660 = Zoom(1660), z260 = Zoom(260), z960 = Zoom(960), z60 = Zoom(60), z130 = Zoom(130), z175 = Zoom(175);

	public FlyTrap(double x, double y) {
		this.position = new DoublePoint(x - z120, y - z120);
		this.size = new DoubleDimension(z240, z240);
		hitbox = new DoublePolygon(4, new double[] { x - z74, x + z74, x - z74, x + z74 },
				new double[] { y - z99, y - z99, y + z110, y + z110 });
		left = x > z960;
		gamePanel.PlaySound("Flytrap");
	}

	public static void Load() {
		leftImages = new BufferedImage[8];
		rightImages = new BufferedImage[8];
		leftAppearImages = new BufferedImage[8];
		rightAppearImages = new BufferedImage[8];
		leftDisapperImages = new BufferedImage[8];
		rightDisapperImages = new BufferedImage[8];
		leftDeadImages = new BufferedImage[8];
		rightDeadImages = new BufferedImage[8];
		leftPuffImages = new BufferedImage[13];
		rightPuffImages = new BufferedImage[13];
		try {
			for (int i = 0; i < 8; i += 2) {
				leftImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/FlyTrap_%d.png", i)));
				rightImages[i] = getFlipImage(leftImages[i]);
				leftImages[i + 1] = leftImages[i];
				rightImages[i + 1] = rightImages[i];
			}
			leftDeadImages[0] = leftAppearImages[7] = leftDisapperImages[0] = leftPuffImages[0] = leftImages[0];
			rightDeadImages[0] = rightAppearImages[7] = rightDisapperImages[0] = rightPuffImages[0] = rightImages[0];
			leftAppearImages[0] = rightAppearImages[0] = leftDisapperImages[7] = rightDisapperImages[7] = null;
			for (int i = 1; i < 7; i++) {
				leftAppearImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/FlyTrapBirth_%d.png", i - 1)));
				rightAppearImages[i] = getFlipImage(leftAppearImages[i]);
				leftDisapperImages[7 - i] = leftAppearImages[i];
				rightDisapperImages[7 - i] = rightAppearImages[i];
				leftDeadImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/FlyTrapDead_%d.png", i)));
				rightDeadImages[i] = getFlipImage(leftDeadImages[i]);
				leftPuffImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/FlyTrapPuff_%02d.png", i)));
				rightPuffImages[i] = getFlipImage(leftPuffImages[i]);
			}
			leftDeadImages[7] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/FlyTrapDead_7.png"));
			rightDeadImages[7] = getFlipImage(leftDeadImages[7]);
			leftPuffImages[7] = leftPuffImages[6];
			rightPuffImages[7] = rightPuffImages[6];
			leftPuffImages[8] = leftPuffImages[5];
			rightPuffImages[8] = rightPuffImages[5];
			leftPuffImages[9] = leftPuffImages[4];
			rightPuffImages[9] = rightPuffImages[4];
			leftPuffImages[10] = leftPuffImages[3];
			rightPuffImages[10] = rightPuffImages[3];
			leftPuffImages[11] = leftPuffImages[2];
			rightPuffImages[11] = rightPuffImages[2];
			leftPuffImages[12] = leftPuffImages[1];
			rightPuffImages[12] = rightPuffImages[1];
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void Move(double PlayerX) {
		left = PlayerX < z960;
		double x = left ? z1660 : z260;
		position.setX(x - z120);
		hitbox.setX(x - z74);
	}

	private void SummonThorns(DoublePoint playerCenter) {
		double x = playerCenter.getX() - z120;
		gamePanel.addAttack(new Thorns(x));
		for (int i = 0; i < 4; i++)
			gamePanel.addAttack(new Thorns(x += z60));
	}

	private static final double z6_3 = 6.3 * MainFrame.getZoomRatio();

	private void PuffSeeds(DoublePoint playerCenter) {
		double x = position.getX() + (left ? z110 : z130);
		double y = position.getY() + z175;
		double radins = Bullet.getLockPlayerRadin(playerCenter, x, y) - bulletRadinsDiscrepancy * 6;
		gamePanel.addBullet(new Seed(x, y, radins, z6_3));
		for (int i = 0; i < 12; i++)
			gamePanel.addBullet(new Seed(x, y, radins += bulletRadinsDiscrepancy, z6_3));
		gamePanel.PlaySound("Seed_Puff");
	}

	@Override
	public synchronized void Next(DoublePoint playerCenter) {
		// TODO Auto-generated method stub
		if (++imageIndexTemp == 15) {
			imageIndexTemp = 0;
			imageIndex++;
		}
		switch (lifeState) {
		case Borning:
			if (imageIndex == 8) {
				imageIndex = 0;
				lifeState = LifeState.Alive;
			}
			break;
		case Alive:
			if (actionState == ActionState.Puff) {
				if (imageIndex == 13)
					imageIndex = 0;
			} else if (imageIndex == 8)
				imageIndex = 0;
			switch (++actionTick) {
			case 120:
			case 675:
				actionState = ActionState.Disappear;
				gamePanel.PlaySound("Flytrap");
				break;
			case 240:
			case 795:
				Move(playerCenter.getX());
				actionState = ActionState.Appear;
				gamePanel.PlaySound("Flytrap");
				break;
			case 360:
			case 915:
				actionState = ActionState.Puff;
				break;
			case 430:
			case 465:
				PuffSeeds(playerCenter);
				break;
			case 555:
				actionState = ActionState.Normal;
				break;
			case 1020:
				SummonThorns(playerCenter);
				break;
			case 1110:
				actionState = ActionState.Normal;
				actionTick = 0;
				break;
			}
			break;
		case Dying:
			if (imageIndex == 5)
				gamePanel.PlaySound("Enemy_Dead");
			if (imageIndex == 8)
				lifeState = LifeState.Removeable;
			break;
		default:
			break;
		}
	}

	@Override
	protected synchronized void AfterBeenAttacked() {
		// TODO Auto-generated method stub
		if (lifeState == LifeState.Alive)
			switch (actionState) {
			case Puff:
			case Normal:
				imageIndex = 0;
				lifeState = LifeState.Dying;
				imageIndexTemp = -10;
				gamePanel.Clear();
				break;
			default:
				break;
			}
	}

	@Override
	public void Paint(Graphics g) {
		// TODO Auto-generated method stub
		switch (lifeState) {
		case Borning:
			g.drawImage(left ? leftAppearImages[imageIndex] : rightAppearImages[imageIndex],
					(int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z240, null);
			break;
		case Alive:
			switch (actionState) {
			case Normal:
				g.drawImage(left ? leftImages[imageIndex] : rightImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z240, z240, null);
				break;
			case Appear:
				g.drawImage(left ? leftAppearImages[imageIndex] : rightAppearImages[imageIndex],
						(int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z240, null);
				break;
			case Disappear:
				g.drawImage(left ? leftDisapperImages[imageIndex] : rightDisapperImages[imageIndex],
						(int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z240, null);
				break;
			case Puff:
				g.drawImage(left ? leftPuffImages[imageIndex] : rightPuffImages[imageIndex],
						(int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z240, null);
				break;
			}
			break;
		case Dying:
			g.drawImage(left ? leftDeadImages[imageIndex] : rightDeadImages[imageIndex],
					(int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z240, null);
			break;
		default:
			break;
		}
	}

}
