package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import bullet.Bullet;
import bullet.FireBall;
import bullet.IceMallet;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import enemyattack.Lightning;
import enemyattack.Thorns;
import panels.MainFrame;

public final class Wizard extends Enemy {

	private static BufferedImage[] images, bornImages, deadImages, liftImages, appearImages, disapperImages,
			ballOutImages, ballBackImages, realImages, fakeDeadImages;

	private static enum ActionState {
		Normal, Appear, Disappear, Lift, FakeDeath, BallOut, BallBack, Real
	}

	private ActionState actionState = ActionState.Lift;

	private static final int z150 = Zoom(150), z300 = Zoom(300), z54 = Zoom(54), z47 = Zoom(47), z107 = Zoom(107),
			z96 = Zoom(96), z80 = Zoom(80), z56 = Zoom(56), z120 = Zoom(120), z60 = Zoom(60), z160 = Zoom(160),
			z260 = Zoom(260), z360 = Zoom(360), z560 = Zoom(560), z760 = Zoom(760), z960 = Zoom(960),
			z1160 = Zoom(1160), z1360 = Zoom(1360), z1560 = Zoom(1560), z1660 = Zoom(1660), z1760 = Zoom(1760);

	public Wizard(double x, double y) {
		this.position = new DoublePoint(x - z150, y - z150);
		this.size = new DoubleDimension(z300, z300);
		hitbox = new DoublePolygon(4, new double[] { x - z54, x - z54, x + z47, x + z47 },
				new double[] { y + z107, y - z96, y + z107, y - z96 });
	}

	public static void Load() {
		images = new BufferedImage[8];
		bornImages = new BufferedImage[23];
		liftImages = new BufferedImage[13];
		appearImages = new BufferedImage[8];
		disapperImages = new BufferedImage[8];
		fakeDeadImages = new BufferedImage[13];
		deadImages = new BufferedImage[12];
		ballOutImages = new BufferedImage[8];
		ballBackImages = new BufferedImage[8];
		realImages = new BufferedImage[8];
		try {
			for (int i = 0; i < 8; i++)
				images[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/Wizard_%d.png", i)));
			liftImages[0] = images[0];
			for (int i = 1; i < 7; i++)
				liftImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardLift_%d.png", i)));
			liftImages[7] = liftImages[3];
			liftImages[8] = liftImages[4];
			liftImages[9] = liftImages[5];
			liftImages[10] = liftImages[6];
			liftImages[11] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardLift_11.png"));
			liftImages[12] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardLift_12.png"));
			fakeDeadImages[0] = fakeDeadImages[1] = fakeDeadImages[2] = fakeDeadImages[3] = ImageIO
					.read(Enemy.class.getResourceAsStream("/Images/Character/WizardFakeDead_0.png"));
			fakeDeadImages[4] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardFakeDead_4.png"));
			fakeDeadImages[5] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardFakeDead_5.png"));
			fakeDeadImages[6] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardFakeDead_6.png"));
			fakeDeadImages[7] = fakeDeadImages[8] = fakeDeadImages[9] = fakeDeadImages[10] = fakeDeadImages[11] = fakeDeadImages[12] = null;
			bornImages[0] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_00.png"));
			bornImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_01.png"));
			bornImages[2] = bornImages[3] = ImageIO
					.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_02.png"));
			for (int i = 4; i < 9; i++)
				bornImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardBirth_%02d.png", i)));
			bornImages[9] = bornImages[10] = bornImages[11] = bornImages[12] = bornImages[8];
			for (int i = 13; i < 17; i++)
				bornImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardBirth_%02d.png", i)));
			bornImages[17] = bornImages[18] = bornImages[16];
			for (int i = 19; i < 23; i++)
				bornImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardBirth_%02d.png", i)));
			for (int i = 0; i < 5; i++)
				deadImages[i] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardDead_00.png"));
			for (int i = 5; i < 12; i++)
				deadImages[i] = ImageIO.read(
						Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardDead_%02d.png", i)));
			appearImages[0] = null;
			appearImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardAppear_1.png"));
			appearImages[2] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_14.png"));
			appearImages[3] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_15.png"));
			appearImages[4] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_16.png"));
			appearImages[5] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_19.png"));
			appearImages[6] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_20.png"));
			appearImages[7] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/WizardBirth_21.png"));
			for (int i = 0; i < 8; i++)
				disapperImages[i] = appearImages[7 - i];
			for (int i = 0; i < 8; i++)
				ballOutImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardBall_%d.png", i)));
			for (int i = 0; i < 8; i++)
				ballBackImages[i] = ballOutImages[7 - i];
			for (int i = 0; i < 8; i++)
				realImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/WizardReal_%d.png", i)));

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void CallLightning(double x) {
		gamePanel.addAttack(new Lightning(x));
	}

	private static final double z4_3 = 4.3 * MainFrame.getZoomRatio(), z5_5 = 5.5 * MainFrame.getZoomRatio();

	private void ThrowFireBall(DoublePoint playerCenter) {
		double x = position.getX() + z80;
		double y = position.getY() + z56;
		gamePanel.addBullet(new FireBall(x, y, Bullet.getLockPlayerRadin(playerCenter, x, y), z4_3));
	}

	private void SummonThorns(DoublePoint playerCenter) {
		double x = playerCenter.getX() - z120;
		gamePanel.addAttack(new Thorns(x));
		for (int i = 0; i < 4; i++)
			gamePanel.addAttack(new Thorns(x += z60));
	}

	private void SummonIceMallet(double x, double y, DoublePoint playerCenter) {
		gamePanel.addBullet(new IceMallet(x, y, Bullet.getLockPlayerRadin(playerCenter, x, y), z5_5));
	}

	private void Move(double x) {
		position.setX(x - z150);
		hitbox.setX(x - z54);
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
			if (imageIndex == 23) {
				imageIndex = 0;
				lifeState = LifeState.Alive;
			}
			break;
		case Alive:
			switch (actionState) {
			case FakeDeath:
				if (imageIndex == 10)
					gamePanel.PlaySound("Enemy_Dead");
				if (imageIndex == 13) {
					imageIndex = 0;
					actionState = ActionState.Appear;
					double d = Math.random() * 3;
					if (d < 1)
						actionTick = 629;
					else if (d < 2)
						actionTick = 1184;
					else
						actionTick = 1739;
				}
				break;
			case Lift:
				if (imageIndex == 13)
					imageIndex = 0;
				break;
			default:
				if (imageIndex == 8)
					imageIndex = 0;
				break;
			}
			if (actionState != ActionState.FakeDeath)
				switch (++actionTick) {
				case 15:
				case 4200:
					CallLightning(z160);
					CallLightning(z560);
					CallLightning(z960);
					CallLightning(z1360);
					CallLightning(z1760);
					break;
				case 210:
				case 4320:
					CallLightning(z360);
					CallLightning(z760);
					CallLightning(z1160);
					CallLightning(z1560);
					break;
				case 390:
				case 750:
				case 1305:
				case 1860:
				case 2370:
				case 2685:
				case 3195:
				case 3825:
				case 5940:
					actionState = ActionState.Normal;
					break;
				case 510:
				case 1065:
				case 1620:
					actionState = ActionState.Disappear;
					break;
				case 630:
					Move(z1660);
					actionState = ActionState.Appear;
					break;
				case 870:
				case 1425:
				case 1980:
				case 2490:
				case 2805:
				case 3435:
				case 4185:
					actionState = ActionState.Lift;
					break;
				case 970:
				case 990:
				case 1010:
				case 1525:
				case 1545:
				case 1565:
				case 3045:
					ThrowFireBall(playerCenter);
					break;
				case 1185:
					Move(z260);
					actionState = ActionState.Appear;
					break;
				case 1740:
					Move(z960);
					actionState = ActionState.Appear;
					break;
				case 1995:
				case 2200:
				case 3480:
					SummonThorns(playerCenter);
					break;
				case 2535:
				case 2580:
				case 2625:
					SummonIceMallet(playerCenter.getX(), z360, playerCenter);
					break;
				case 2850:
					gamePanel.getChain().Lock(playerCenter, 32);
					break;
				case 3675:
					gamePanel.getChain().Lock(playerCenter, 38);
					break;
				case 3710:
					SummonIceMallet(playerCenter.getX() - z30, z390, playerCenter);
					break;
				case 3755:
					SummonIceMallet(playerCenter.getX(), z360, playerCenter);
					break;
				case 3815:
					SummonIceMallet(playerCenter.getX() + z30, z390, playerCenter);
					break;
				case 4380:
					actionState = ActionState.BallOut;
					break;
				case 4500:
					actionState = ActionState.Real;
					hitbox.forciblySet(0, position.getX() + z126, position.getY() + z330);
					hitbox.forciblySet(1, position.getX() + z126, position.getY() + z372);
					hitbox.forciblySet(2, position.getX() + z167, position.getY() + z372);
					hitbox.forciblySet(3, position.getX() + z167, position.getY() + z330);
					break;
				case 4530:
					CallLightning(z160);
					break;
				case 4590:
					CallLightning(z360);
					break;
				case 4650:
					CallLightning(z560);
					break;
				case 4710:
					CallLightning(z760);
					break;
				case 4770:
					CallLightning(z960);
					break;
				case 4830:
					CallLightning(z1160);
					break;
				case 4890:
					CallLightning(z1360);
					break;
				case 4950:
					CallLightning(z1560);
					break;
				case 5070:
					CallLightning(z1760);
					break;
				case 5190:
					CallLightning(z1560);
					break;
				case 5250:
					CallLightning(z1360);
					break;
				case 5310:
					CallLightning(z1160);
					break;
				case 5370:
					CallLightning(z960);
					break;
				case 5430:
					CallLightning(z760);
					break;
				case 5490:
					CallLightning(z560);
					break;
				case 5550:
					CallLightning(z360);
					break;
				case 5730:
					CallLightning(z160);
					CallLightning(z360);
					CallLightning(z560);
					CallLightning(z1360);
					CallLightning(z1560);
					CallLightning(z1760);
					break;
				case 5820:
					actionState = ActionState.BallBack;
					hitbox.forciblySet(0, position.getX() + z96, position.getY() + z57);
					hitbox.forciblySet(1, position.getX() + z96, position.getY() + z54);
					hitbox.forciblySet(2, position.getX() + z197, position.getY() + z54);
					hitbox.forciblySet(3, position.getX() + z197, position.getY() + z257);
					break;
				case 6060:
					actionTick = 0;
					actionState = ActionState.Lift;
					break;
				}
			break;
		case Dying:
			if (imageIndex == 9)
				gamePanel.PlaySound("Enemy_Dead");
			if (imageIndex == 12)
				lifeState = LifeState.Removeable;
			break;
		default:
			break;
		}
	}

	private static final int z30 = Zoom(30), z390 = Zoom(390), z420 = Zoom(420), z126 = Zoom(126), z167 = Zoom(167),
			z330 = Zoom(330), z372 = Zoom(372), z57 = Zoom(57), z257 = Zoom(257), z197 = Zoom(197);

	@Override
	protected synchronized void AfterBeenAttacked() {
		// TODO Auto-generated method stub
		if (lifeState == LifeState.Alive) {
			if (actionState == ActionState.Real) {
				lifeState = LifeState.Dying;
				imageIndex = 0;
				imageIndexTemp = 0;
				gamePanel.Clear();
			} else if (actionState == ActionState.Normal || actionState == ActionState.Lift) {// FakeDeath
				actionState = ActionState.FakeDeath;
				gamePanel.Clear();
				imageIndexTemp = 0;
				imageIndex = 0;
			}
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
			case Appear:
				g.drawImage(appearImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z300, null);
				break;
			case Disappear:
				g.drawImage(disapperImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z300, null);
				break;
			case Lift:
				g.drawImage(liftImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z300, null);
				break;
			case FakeDeath:
				g.drawImage(fakeDeadImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z300, null);
				break;
			case BallOut:
				g.drawImage(ballOutImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z420, null);
				break;
			case BallBack:
				g.drawImage(ballBackImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z420, null);
				break;
			case Real:
				g.drawImage(realImages[imageIndex], (int) Math.round(position.getX()),
						(int) Math.round(position.getY()), z300, z420, null);
				break;
			case Normal:
				g.drawImage(images[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()),
						z300, z300, null);
				break;
			}
			break;
		case Dying:
			g.drawImage(deadImages[imageIndex], (int) Math.round(position.getX() - z60),
					(int) Math.round(position.getY()), z420, z420, null);
			break;
		default:
			break;
		}
	}
}
