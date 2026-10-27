package character;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import animation.Animation;
import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import panels.GamePanel;

public final class Chain extends Character {

	public static BufferedImage[] images;
	private int lockFrames;
	private static GamePanel gamePanel;

	private static enum State {
		Locked, Locking, Unlocking, Invisible
	}

	private State state = State.Invisible;

	public static void Load(GamePanel gamePanel) {
		Chain.gamePanel = gamePanel;
		images = new BufferedImage[8];
		try {
			for (int i = 0; i < 8; i++)
				images[i] = ImageIO
						.read(Character.class.getResourceAsStream(String.format("/Images/Character/Chain_%d.png", i)));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int z200 = Zoom(200), z100 = Zoom(100), z60 = Zoom(60);

	public Chain() {
		imageIndex = 0;
		position = new DoublePoint(0, 0);
		size = new DoubleDimension(z200, z100);
	}

	@Override
	public void Paint(Graphics g) {
		// TODO Auto-generated method stub
		if (state != State.Invisible && !Animation.isGameEnd())
			g.drawImage(images[imageIndex], (int) Math.round(position.getX()), (int) Math.round(position.getY()), z200,
					z100, null);
	}

	public void Lock(DoublePoint playerPosition, int lockFrames) {
		position.setX(playerPosition.getX() - z100);
		position.setY(playerPosition.getY() - z60);
		gamePanel.setPlayerLocking(true);
		state = State.Locking;
		imageIndex = 0;
		this.lockFrames = lockFrames;
		gamePanel.PlaySound("Chain");
	}

	public void Next() {
		if (state != State.Invisible) {
			if (++imageIndexTemp == 10) {
				imageIndexTemp = 0;
				switch (state) {
				case Locking:
					if (++imageIndex == 7)
						state = State.Locked;
					break;
				case Locked:
					if (--lockFrames == 0) {
						state = State.Unlocking;
						gamePanel.PlaySound("Chain");
					}
					break;
				case Unlocking:
					if (--imageIndex == -1) {
						state = State.Invisible;
						gamePanel.setPlayerLocking(false);
					}
					break;
				case Invisible:
					break;
				}
			}
		}
	}
}
