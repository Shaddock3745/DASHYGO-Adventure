package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import animation.Animation;
import doubleshape2d.DoubleCircle;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Player extends Character {

	/**
	 * 
	 */
	private static final double moveLength = 2 * MainFrame.getZoomRatio();
	private static final int z120 = Zoom(120);
	private short Stamina = 100, StaminaTemp = 0, stepTemp = 0;
	private boolean LeftMove = false, RightMove = false, Locking = false, fL = true;
	private static BufferedImage[] Limages, Rimages;
	protected DoublePolygon hitbox, attackingHitbox;

	public final void setLocking(boolean Locking) {
		this.Locking = Locking;
	}

	public final DoublePolygon getHitbox() {
		return Animation.isAttacking() ? attackingHitbox : hitbox;
	}

	public final DoubleCircle getCenterCircle() {
		return new DoubleCircle(position.getX() + size.getWidth() / 2, position.getY() + size.getHeight(), z120);
	}

	public final DoublePoint getCenterPoint() {
		return new DoublePoint(position.getX() + size.getWidth() / 2, position.getY() + size.getHeight() / 2);
	}

	public final void setLeftMove(boolean leftMove) {
		LeftMove = leftMove;
	}

	public final void setRightMove(boolean rightMove) {
		RightMove = rightMove;
	}

	public final short getStamina() {
		return Stamina;
	}

	@Override
	public final void setX(double x) {
		position.setX(x);
		hitbox.setX(x);
		attackingHitbox.setX(x);
	}

	@Override
	public final void setY(double y) {
		position.setY(y);
		hitbox.setY(y);
		attackingHitbox.setY(y);
	}

	@Override
	public final void setPosition(double x, double y) {
		position.setX(x);
		position.setY(y);
		hitbox.setPosition(x, y);
		attackingHitbox.setPosition(x, y);
	}

	@Override
	public final void OffsetX(double x) {
		this.position.offsetX(x);
		hitbox.offestX(x);
		attackingHitbox.offestX(x);
	}

	public final synchronized void StaminaUp() {
		if (Animation.isNone() && Stamina < 100)
			if (++StaminaTemp == 4) {
				Stamina++;
				StaminaTemp = 0;
			}
	}

	private static final int z64 = Zoom(64), z60 = Zoom(60), z4 = Zoom(4), z59 = Zoom(59), z51 = Zoom(51),
			z220 = Zoom(220);

	public Player() {
		position = new DoublePoint(0, 0);
		size = new DoubleDimension(z64, z60);
		hitbox = new DoublePolygon(4, new double[] { z4, z4, z59, z59 }, new double[] { z4, z51, z4, z51 });
		attackingHitbox = new DoublePolygon(4, new double[] { z4, z4, z59, z59 }, new double[] { z4, z220, z4, z220 });
	}

	public static final void Load() {
		Limages = new BufferedImage[2];
		Rimages = new BufferedImage[2];
		try {
			Limages[0] = ImageIO.read(Player.class.getResourceAsStream("/Images/Character/Player_0.png"));
			Limages[1] = ImageIO.read(Player.class.getResourceAsStream("/Images/Character/Player_1.png"));
			Rimages[0] = Enemy.getFlipImage(Limages[0]);
			Rimages[1] = Enemy.getFlipImage(Limages[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public final void Paint(Graphics g) {
		if (fL)
			g.drawImage(Limages[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()), z64,
					z60, null);
		else
			g.drawImage(Rimages[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()), z64,
					z60, null);
	}

	public final synchronized void Attack() {
		if (!Locking && Animation.isNone() && Stamina >= 60) {
			Animation.Attack();
			Stamina -= 60;
		}
	}

	public final synchronized void Counter(boolean isLeftCountering) {
		if (Animation.isNone() && Stamina >= 30) {
			if (isLeftCountering)
				Animation.LeftCounter();
			else
				Animation.RightCounter();
			Stamina -= 30;
		}
	}

	private static final int z70 = Zoom(70), z1786 = Zoom(1786), z230 = Zoom(230), z1626 = Zoom(1626), z160 = Zoom(160);

	public final synchronized void Move() {
		if (!Locking && !Animation.isAttacking() && !(LeftMove && RightMove)) {
			if (LeftMove) {
				OffsetX(-moveLength);
				stepTemp++;
				fL = true;
				if (position.getX() < z70)
					setX(z70);
			} else if (RightMove) {
				OffsetX(moveLength);
				stepTemp++;
				fL = false;
				if (position.getX() > z1786)
					setX(z1786);
			}
			if (stepTemp == 10) {
				imageIndex += 1;
				imageIndex %= 2;
				stepTemp = 0;
			}
		}
	}

	public final synchronized void Dash() {
		if (!Locking && Animation.isNone() && Stamina >= 30 && !(LeftMove && RightMove))
			if (LeftMove) {
				if (position.getX() < z230)
					setX(z70);
				else
					OffsetX(-z160);
				Animation.RightDash();
				Stamina -= 30;
			} else if (RightMove) {
				if (position.getX() > z1626)
					setX(z1786);
				else
					OffsetX(z160);
				Animation.LeftDash();
				Stamina -= 30;
			}
	}
}
