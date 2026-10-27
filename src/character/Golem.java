package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import bullet.Bullet;
import bullet.Rock;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import enemyattack.Rockfall;
import panels.MainFrame;

public final class Golem extends Enemy {

	private static BufferedImage[] bornImages, images, dropImages, deadImages, throwLeftImages, throwRightImages;
	private static final double bulletRadinsDiscrepancy = Math.PI / 12, firstX = 210 * MainFrame.getZoomRatio();

	private static enum ActionState {
		Normal, Drop, ThrowLeft, ThrowRight
	}

	private ActionState actionState = ActionState.Normal;

	public static final void Load() {
		bornImages = new BufferedImage[6];
		images = new BufferedImage[7];
		deadImages = new BufferedImage[7];
		throwLeftImages = new BufferedImage[5];
		throwRightImages = new BufferedImage[5];
		dropImages = new BufferedImage[18];
		try {
			for (int i = 0; i < 6; i++) {
				images[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/Golem_%d.png", i)));
				bornImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/GolemBirth_%d.png", i)));
				deadImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/GolemDead_%d.png", i)));
			}
			images[6] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/Golem_6.png"));
			deadImages[6] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDead_6.png"));
			throwLeftImages[0] = throwLeftImages[3] = ImageIO
					.read(Enemy.class.getResourceAsStream("/Images/Character/GolemThrow_0.png"));
			throwLeftImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemThrow_1.png"));
			throwLeftImages[2] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemThrow_2.png"));
			throwLeftImages[4] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemThrow_4.png"));
			for (int i = 1; i < 5; i++)
				throwRightImages[i] = getFlipImage(throwLeftImages[i]);
			throwRightImages[0] = throwRightImages[3];
			dropImages[0] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDrop_00.png"));
			dropImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDrop_01.png"));
			dropImages[2] = dropImages[3] = ImageIO
					.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDrop_02.png"));
			dropImages[4] = dropImages[5] = dropImages[6] = ImageIO
					.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDrop_04.png"));
			for (int i = 7; i < 13; i++)
				dropImages[i] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/GolemDrop_07.png"));
			for (int i = 13; i < 18; i++)
				dropImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/GolemDrop_%d.png", i)));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int z150 = Zoom(150), z300 = Zoom(300), z78 = Zoom(78), z81 = Zoom(81), z600 = Zoom(600),
			z30 = Zoom(30), z270 = Zoom(270), z200 = Zoom(200), z60 = Zoom(60), z360 = Zoom(360);

	public Golem(double x, double y) {
		this.position = new DoublePoint(x - z150, y - z150);
		this.size = new DoubleDimension(z300, z300);
		hitbox = new DoublePolygon(4, new double[] { x - z78, x - z78, x + z78, x + z78 },
				new double[] { y + z81, y - z81, y + z81, y - z81 });
	}

	private void DropRockfall(boolean left) {
		for (int i = 0; i < 3; i++)
			gamePanel.addAttack(new Rockfall(firstX + i * z600 + (left ? 0 : z300)));
	}

	private static final double z6 = 6 * MainFrame.getZoomRatio();

	private void ThrowRock(DoublePoint playerCenter, boolean left) {
		double x = position.getX() + (left ? z30 : z270);
		double y = position.getY() + z200;
		double radins = Bullet.getLockPlayerRadin(playerCenter, x, y) - bulletRadinsDiscrepancy * 3;
		gamePanel.addBullet(new Rock(x, y, radins, z6));
		for (int i = 0; i < 6; i++)
			gamePanel.addBullet(new Rock(x, y, radins += bulletRadinsDiscrepancy, z6));
		gamePanel.PlaySound("Rock_Throw");
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
			if (imageIndex == 6) {
				imageIndex = 0;
				lifeState = LifeState.Alive;
			}
			break;
		case Alive:
			switch (actionState) {
			case ThrowLeft:
			case ThrowRight:
				if (imageIndex == 5)
					imageIndex = 0;
				break;
			case Drop:
				if (imageIndex == 18)
					imageIndex = 0;
				break;
			default:
				if (imageIndex == 7)
					imageIndex = 0;
				break;
			}
			switch (++actionTick) {
			case 210:
				actionState = ActionState.ThrowLeft;
				break;
			case 285:
				ThrowRock(playerCenter, true);
				actionState = ActionState.Normal;
				break;
			case 390:
				actionState = ActionState.ThrowRight;
				break;
			case 465:
				ThrowRock(playerCenter, false);
				actionState = ActionState.Normal;
				break;
			case 675:
				actionState = ActionState.Drop;
				break;
			case 870:
				gamePanel.PlaySound("Rock_Fall");
				DropRockfall(true);
				break;
			case 945:
				actionState = ActionState.Normal;
				break;
			case 995:
				DropRockfall(false);
				break;
			case 1575:
				actionTick = 0;
				break;
			}
			break;
		case Dying:
			if (imageIndex == 4)
				gamePanel.PlaySound("Enemy_Dead");
			if (imageIndex == 7)
				lifeState = LifeState.Removeable;
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
			g.drawImage(bornImages[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()),
					z300, z300, null);
			break;
		case Alive:
			switch (actionState) {
			case ThrowLeft:
				g.drawImage(throwLeftImages[imageIndex], (int) Math.round(position.getX()) - z60,
						(int) Math.round(position.getY()), z360, z300, null);
				break;
			case ThrowRight:
				g.drawImage(throwRightImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z360, z300, null);
				break;
			case Drop:
				g.drawImage(dropImages[imageIndex], (int) Math.round(position.getX() - z30),
						(int) Math.round(position.getY() - z60), z360, z360, null);
				break;
			default:
				g.drawImage(images[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()),
						z300, z300, null);
				break;
			}
			break;
		case Dying:
			g.drawImage(deadImages[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()),
					z300, z300, null);
			break;
		default:
			break;
		}
	}

	@Override
	protected synchronized void AfterBeenAttacked() {
		// TODO Auto-generated method stub
		if (lifeState == LifeState.Alive && actionTick > 765 && actionTick < 915) {
			imageIndex = 0;
			lifeState = LifeState.Dying;
			imageIndexTemp = -10;
			gamePanel.Clear();
		}
	}

}
