package enemyattack;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;

public final class Lightning extends EnemyAttack {

	private static BufferedImage[] images;

	public static void Load() {
		images = new BufferedImage[3];
		try {
			for (int i = 0; i < 3; i++)
				images[i] = ImageIO.read(EnemyAttack.class
						.getResourceAsStream(String.format("/Images/EnemyAttack/Lightning_%d.png", i)));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int z100 = Zoom(100), z68 = Zoom(68), z200 = Zoom(200), z944 = Zoom(944), z255 = Zoom(255),
			z1012 = Zoom(1012);

	public Lightning(double x) {
		super(new Warning(new Rectangle2D.Double(x - z100, z68, z200, z944)));
		// TODO Auto-generated constructor stub
		position = new DoublePoint(x - z100, z68);
		size = new DoubleDimension(z200, z944);
		hitbox = new DoublePolygon(4, new double[] { x - z100, x - z100, x + z100, x + z100 },
				new double[] { z68, z255, z68, z255 });
	}

	@Override
	public void paintAttack(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable && !warning.isAvailable())
			g.drawImage(images[imageIndex], (int) Math.round(position.getX()), z68, z200, z944, null);
	}

	@Override
	public void Next() {
		// TODO Auto-generated method stub
		if (!warning.isAvailable()) {
			switch (++state) {
			case 12:
				imageIndex++;
				hitbox.forciblySet(0, position.getX(), z68);
				hitbox.forciblySet(1, position.getX(), z1012);
				hitbox.forciblySet(2, position.getX() + z200, z1012);
				hitbox.forciblySet(3, position.getX() + z200, z68);
				gamePanel.PlaySound("Lightning");
				break;
			case 48:
				imageIndex++;
				break;
			case 60:
				removeable = true;
				break;
			}
		}
	}

}
