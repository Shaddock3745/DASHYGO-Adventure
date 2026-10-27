package animation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import character.Player;
import doubleshape2d.DoublePoint;
import panels.MainFrame;

public final class Animation {

	private static BufferedImage[] swordSpiritImageLtoR, swordSpiritImageRtoL, attackImages, gameEndImages;
	private static BufferedImage dashafterImageLtoR, dashafterImageRtoL;
	private static int gameEndImageIndex = 0, gameEndIndex = 0, imageIndex = 0, imageIndexTemp = 0;
	private static boolean gameEnd = false;

	private static enum State {
		LeftDash, RightDash, LeftCounter, RightCounter, Attack, None
	}

	private static State state = State.None;

	private static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}

	public static final boolean isNone() {
		return state == State.None;
	}

	public static final boolean isGameEnd() {
		return gameEnd;
	}

	public static final void setGameEnd(boolean gameEnd) {
		Animation.gameEnd = gameEnd;
	}

	public final static boolean isAttacking() {
		return state == State.Attack;
	}

	public final static void Attack() {
		state = State.Attack;
	}

	public final static boolean isCountering() {
		return state == State.LeftCounter || state == State.RightCounter;
	}

	public final static void LeftCounter() {
		state = State.LeftCounter;
	}

	public final static void RightCounter() {
		state = State.RightCounter;
	}

	public final static void LeftDash() {
		state = State.LeftDash;
	}

	public final static void RightDash() {
		state = State.RightDash;
	}

	public final static void Reset() {
		imageIndex = imageIndexTemp = gameEndIndex = gameEndImageIndex = 0;
		gameEnd = false;
		state = State.None;
	}

	public final static void Load() {
		swordSpiritImageLtoR = new BufferedImage[7];
		swordSpiritImageRtoL = new BufferedImage[7];
		attackImages = new BufferedImage[6];
		gameEndImages = new BufferedImage[5];
		try {
			dashafterImageLtoR = ImageIO
					.read(Animation.class.getResourceAsStream("/Images/AfterImage/DashAfterImageLtoR.png"));
			dashafterImageRtoL = getFlipImage(dashafterImageLtoR);
			for (int i = 0; i < 6; i++) {
				swordSpiritImageLtoR[i] = ImageIO.read(
						Animation.class.getResourceAsStream(String.format("/Images/Sword/SwordSpirit_%d.png", i)));
				swordSpiritImageRtoL[i] = getFlipImage(swordSpiritImageLtoR[i]);
				attackImages[i] = ImageIO.read(Animation.class
						.getResourceAsStream(String.format("/Images/AfterImage/PlayerAttack_%d.png", i)));
			}
			swordSpiritImageLtoR[6] = ImageIO
					.read(Animation.class.getResourceAsStream("/Images/Sword/SwordSpirit_6.png"));
			swordSpiritImageRtoL[6] = getFlipImage(swordSpiritImageLtoR[6]);
			for (int i = 0; i < 4; i++)
				gameEndImages[i] = ImageIO
						.read(Animation.class.getResourceAsStream(String.format("/Images/Character/Spirit_%d.png", i)));
			gameEndImages[4] = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public final static void GameEndNext(Player player) {
		if (++gameEndIndex == 15) {
			gameEndIndex = 0;
			if (++gameEndImageIndex == 4)
				MainFrame.getGamePanel().GameEnd();
		}
	}

	private static final int zoom950 = Zoom(950), zoom610 = Zoom(610), zoom430 = Zoom(430), zoom250 = Zoom(250),
			zoom70 = Zoom(70);

	public final static synchronized void Next(Player player) {
		switch (state) {
		case LeftDash:
			if (++imageIndex == 10) {
				state = State.None;
				imageIndex = 0;
			}
			break;
		case RightDash:
			if (++imageIndex == 10) {
				state = State.None;
				imageIndex = 0;
			}
			break;
		case LeftCounter:
			if (++imageIndexTemp == 2) {
				imageIndexTemp = 0;
				if (++imageIndex == 7) {
					state = State.None;
					imageIndex = 0;
				}
			}
			break;
		case RightCounter:
			if (++imageIndexTemp == 2) {
				imageIndexTemp = 0;
				if (++imageIndex == 7) {
					state = State.None;
					imageIndex = 0;
				}
			}
			break;
		case Attack:
			switch (++imageIndexTemp) {
			case 2:
				player.setY(zoom610);
				imageIndex++;
				break;
			case 4:
				player.setY(zoom430);
				imageIndex++;
				break;
			case 6:
				player.setY(zoom250);
				imageIndex++;
				break;
			case 8:
				player.setY(zoom70);
				imageIndex++;
				break;
			case 12:
				state = State.None;
				imageIndex = imageIndexTemp = 0;
				player.setY(zoom950);
				break;
			}
			break;
		case None:
			break;
		}
	}

	private final static int z110 = Zoom(110), z950 = Zoom(950), z120 = Zoom(120), z60 = Zoom(60), z54 = Zoom(54),
			z88 = Zoom(88), z875 = Zoom(875), z240 = Zoom(240), z135 = Zoom(135), z64 = Zoom(64), z900 = Zoom(900),
			z40 = Zoom(40), z80 = Zoom(80), z100 = Zoom(100), z8 = Zoom(8);

	public final static void paintPlayerAnimation(Graphics g, double playerX) {
		switch (state) {
		case LeftDash:
			g.drawImage(dashafterImageLtoR, (int) Math.round(playerX) - z110, z950, z120, z60, null);
			break;
		case RightDash:
			g.drawImage(dashafterImageRtoL, (int) Math.round(playerX) + z54, z950, z120, z60, null);
			break;
		case LeftCounter:
			g.drawImage(swordSpiritImageLtoR[imageIndex], (int) Math.round(playerX) - z88, z875, z240, z135, null);
			break;
		case RightCounter:
			g.drawImage(swordSpiritImageRtoL[imageIndex], (int) Math.round(playerX) - z88, z875, z240, z135, null);
			break;
		case Attack:
			g.drawImage(attackImages[imageIndex], (int) Math.round(playerX), z110, z64, z900, null);
			break;
		case None:
			break;
		}
	}

	public final static void paintGameEndAnimation(Graphics g, DoublePoint playerPosition) {
		g.drawImage(gameEndImages[gameEndImageIndex], (int) Math.round(playerPosition.getX()) - z8,
				(int) Math.round(playerPosition.getY()) - z40, z80, z100, null);
	}

	private static final BufferedImage getFlipImage(BufferedImage image) {
		int width = image.getWidth(), height = image.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
		g2d.dispose();
		return img;
	}
}
