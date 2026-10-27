package enemyattack;

import java.awt.Graphics;

import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.GamePanel;
import panels.MainFrame;

public abstract class EnemyAttack {

	protected DoublePoint position;
	protected DoubleDimension size;
	protected DoublePolygon hitbox;
	protected int state = 0, imageIndex = 0;
	protected Warning warning;
	protected boolean removeable = false;
	protected static GamePanel gamePanel;

	public static void Load(GamePanel gamePanel) {
		EnemyAttack.gamePanel = gamePanel;
		Warning.Load(gamePanel);
		Rockfall.Load();
		Lightning.Load();
		Thorns.Load();
	}

	protected static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}

	public EnemyAttack(Warning warning) {
		this.warning = warning;
	}

	public final Warning getWarning() {
		return warning;
	}

	public final boolean isRemoveable() {
		return removeable;
	}

	public boolean HitCheck(DoublePolygon playerHitbox) {
		return hitbox.isColliding(playerHitbox) && !warning.isAvailable();
	}

	public abstract void paintAttack(Graphics g);

	public abstract void Next();
}
