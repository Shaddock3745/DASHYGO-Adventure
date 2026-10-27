package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import bullet.Bullet;
import bullet.SlimeBullet;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Slime extends Enemy {

	private static BufferedImage[] Limages, Rimages, bornImages, LDeadImages, RDeadImages, LPuffImages, RPuffImages;
	private double movelength = -0.4 * MainFrame.getZoomRatio();
	private static final double bulletRadinsDiscrepancy = Math.PI / 12;
	private boolean isPuffing = true;

	private static final int z75 = Zoom(75), z150 = Zoom(150), z105 = Zoom(105);
	private static final double z52_5 = (double) z75 / 2;

	public Slime(double x, double y) {
		this.position = new DoublePoint(x - z75, y - z52_5);
		this.size = new DoubleDimension(z150, z105);
		hitbox = new DoublePolygon(4, new double[] { x - z75, x - z75, x + z75, x + z75 },
				new double[] { y + z52_5, y - z52_5, y + z52_5, y - z52_5 });
	}

	public static final void Load() {
		Limages = new BufferedImage[7];
		Rimages = new BufferedImage[7];
		bornImages = new BufferedImage[7];
		LDeadImages = new BufferedImage[6];
		RDeadImages = new BufferedImage[6];
		LPuffImages = new BufferedImage[6];
		RPuffImages = new BufferedImage[6];
		try {
			for (int i = 0; i < 6; i++) {
				Limages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/Slime_%d.png", i)));
				Rimages[i] = getFlipImage(Limages[i]);
				bornImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/SlimeBirth_%d.png", i)));
				LDeadImages[i] = ImageIO
						.read(Enemy.class.getResourceAsStream(String.format("/Images/Character/SlimeDead_%d.png", i)));
				RDeadImages[i] = getFlipImage(LDeadImages[i]);
			}
			Limages[6] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/Slime_6.png"));
			Rimages[6] = getFlipImage(Limages[6]);
			bornImages[6] = Limages[0];
			LPuffImages[0] = LPuffImages[4] = Limages[0];
			RPuffImages[0] = RPuffImages[4] = Rimages[0];
			LPuffImages[1] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/SlimePuff_1.png"));
			RPuffImages[1] = getFlipImage(LPuffImages[1]);
			LPuffImages[3] = LPuffImages[1];
			RPuffImages[3] = RPuffImages[1];
			LPuffImages[2] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/SlimePuff_2.png"));
			RPuffImages[2] = getFlipImage(LPuffImages[2]);
			LPuffImages[5] = ImageIO.read(Enemy.class.getResourceAsStream("/Images/Character/SlimePuff_5.png"));
			RPuffImages[5] = getFlipImage(LPuffImages[5]);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int z60 = Zoom(60), z300 = Zoom(300), z1470 = Zoom(1470), z30 = Zoom(30), z210 = Zoom(210);
	private static final double z4 = 4 * MainFrame.getZoomRatio();

	public final void Puff(DoublePoint playerCenter) {
		double x = position.getX() + z75;
		double y = position.getY() + z60;
		double radins = Bullet.getLockPlayerRadin(playerCenter, x, y) - bulletRadinsDiscrepancy * 2;
		gamePanel.addBullet(new SlimeBullet(x, y, radins, z4));
		for (int i = 0; i < 4; i++)
			gamePanel.addBullet(new SlimeBullet(x, y, radins += bulletRadinsDiscrepancy, z4));
		gamePanel.PlaySound("Slime_Puff");
	}

	@Override
	public synchronized final void Next(DoublePoint playerCenter) {
		// TODO Auto-generated method stub
		if (++imageIndexTemp == 15) {
			imageIndexTemp = 0;
			imageIndex++;
		}
		switch (lifeState) {
		case Borning:
			if (imageIndex == 7) {
				imageIndex = 0;
				lifeState = LifeState.Alive;
			}
			break;
		case Alive:
			if (isPuffing) {
				if (imageIndex == 6)
					imageIndex = 0;
			} else {
				if (imageIndex == 7) {
					imageIndex = 0;
					if (this.position.getX() <= z300 || this.position.getX() >= z1470)
						movelength = -movelength;
				}
				this.position.offsetX(movelength);
				this.hitbox.offestX(movelength);
			}
			switch (++actionTick) {
			case 72:
			case 162:
			case 252:
				Puff(playerCenter);
				break;
			case 270:
				isPuffing = false;
				break;
			case 795:
				actionTick = 0;
				isPuffing = true;
				break;
			}
			break;
		case Dying:
			if (imageIndex == 3)
				gamePanel.PlaySound("Enemy_Dead");
			if (imageIndex == 6)
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
					z150, z105, null);
			break;
		case Alive:
			if (isPuffing)
				g.drawImage(movelength < 0 ? LPuffImages[imageIndex] : RPuffImages[imageIndex],
						(int) Math.round(position.getX()), (int) Math.round(position.getY()), z150, z105, null);
			else
				g.drawImage(movelength < 0 ? Limages[imageIndex] : Rimages[imageIndex],
						(int) Math.round(position.getX()), (int) Math.round(position.getY()), z150, z105, null);
			break;
		case Dying:
			g.drawImage(movelength < 0 ? LDeadImages[imageIndex] : RDeadImages[imageIndex],
					(int) Math.round(position.getX()) - z30, (int) Math.round(position.getY()), z210, z105, null);
			break;
		default:
			break;
		}
	}

	@Override
	protected final synchronized void AfterBeenAttacked() {
		// TODO Auto-generated method stub
		if (lifeState == LifeState.Alive) {
			imageIndex = 0;
			lifeState = LifeState.Dying;
			imageIndexTemp = -10;
		}
	}
}
