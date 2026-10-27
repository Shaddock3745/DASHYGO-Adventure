package enemyattack;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Rockfall extends EnemyAttack {

	private static BufferedImage image;
	private static final double dropLength = 12.6 * MainFrame.getZoomRatio();
	private static final double maxY = 890 * MainFrame.getZoomRatio();

	public static void Load() {
		try {
			image = ImageIO.read(EnemyAttack.class.getResourceAsStream("/Images/EnemyAttack/Rockfall.png"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int z120 = Zoom(120), z68 = Zoom(68), z240 = Zoom(240), z944 = Zoom(944), z150 = Zoom(150),
			z72 = Zoom(72), z83 = Zoom(83), z3 = Zoom(3), z60 = Zoom(60), z116 = Zoom(116), z146 = Zoom(146),
			z181 = Zoom(181), z217 = Zoom(217), z178 = Zoom(178);

	public Rockfall(double x) {
		super(new Warning(new Rectangle2D.Double(x - z120, z68, z240, z944)));
		position = new DoublePoint(x - z120, z68);
		size = new DoubleDimension(z240, z150);
		hitbox = new DoublePolygon(8,
				new double[] { x - z72, x + z83, x + z120, x + z120, x + z3, x - z60, x - z120, x - z120 },
				new double[] { z68, z116, z146, z181, z217, z217, z178, z116 });
	}

	@Override
	public void paintAttack(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable && !warning.isAvailable()) {
			g.drawImage(image, (int) Math.round(position.getX()), (int) Math.round(position.getY()), z240, z150, null);
		}
	}

	@Override
	public void Next() {
		// TODO Auto-generated method stub
		if (!warning.isAvailable()) {
			position.offsetY(dropLength);
			hitbox.offestY(dropLength);
			if (position.getY() >= maxY)
				removeable = true;
		}
	}

}
