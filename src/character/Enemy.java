package character;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import animation.Animation;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.GamePanel;

public abstract class Enemy extends Character {
	protected DoublePolygon hitbox;
	protected static GamePanel gamePanel = null;
	
	protected int actionTick = 0;
	protected LifeState lifeState = LifeState.Borning;

	protected static enum LifeState {
		Borning, Alive, Dying, Removeable;
	}

	public static void Load(GamePanel gamePanel) {
		Enemy.gamePanel = gamePanel;
		Slime.Load();
		Demon.Load();
		Golem.Load();
		FlyTrap.Load();
		Wizard.Load();
		Player.Load();
		Chain.Load(gamePanel);
	}

	public final boolean isRemoveable() {
		return lifeState == LifeState.Removeable;
	}

	public final void HitCheck(DoublePolygon playerHitbox) {
		if (Animation.isAttacking() && hitbox.isColliding(playerHitbox))
			AfterBeenAttacked();
	}

	public abstract void Next(DoublePoint playerCenter);

	protected abstract void AfterBeenAttacked();

	protected static final BufferedImage CloneImage(BufferedImage image) {
		return image.getSubimage(0, 0, image.getWidth(), image.getHeight());
	}

	protected static final BufferedImage getFlipImage(BufferedImage image) {
		int width = image.getWidth(), height = image.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
		g2d.dispose();
		return img;
	}
}
