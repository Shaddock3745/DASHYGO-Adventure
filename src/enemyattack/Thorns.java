package enemyattack;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import doubleshape2d.DoublePolygon;
import panels.MainFrame;

public final class Thorns extends EnemyAttack {

	private static BufferedImage image = null;

	public static final void Load() {
		try {
			image = ImageIO.read(Thorns.class.getResourceAsStream("/Images/EnemyAttack/Thorns.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final int z30 = Zoom(30), z890 = Zoom(890), z60 = Zoom(60), z120 = Zoom(120), z1010 = Zoom(1010);

	private static final double zN0_5 = -0.5 * MainFrame.getZoomRatio(), zN2 = -2 * MainFrame.getZoomRatio();

	public Thorns(double x) {
		super(new Warning(new Rectangle2D.Double(x - z30, z890, z60, z120)));
		position = new DoublePoint(x, z1010);
		size = new DoubleDimension(0, 0);
		hitbox = new DoublePolygon(3, new double[] { x, x, x }, new double[] { z1010, z1010, z1010 });
	}

	@Override
	public final void Next() {
		// TODO Auto-generated method stub
		if (!warning.isAvailable()) {
			if (++state <= 60) {
				size.setWidth(Double.valueOf(state) * MainFrame.getZoomRatio());
				size.setHeight(size.getWidth() * 2);
				position.offsetX(zN0_5);
				position.offsetY(zN2);
				hitbox.forciblySet(0, position.getX() + Double.valueOf(size.getWidth()) / 2, position.getY());
				hitbox.forciblySet(1, position.getX(), z1010);
				hitbox.forciblySet(2, position.getX() + state, z1010);
			} else if (state == 120)
				removeable = true;
		}
	}

	@Override
	public final void paintAttack(Graphics g) {
		// TODO Auto-generated method stub
		if (!removeable && !warning.isAvailable())
			g.drawImage(image, (int) Math.round(position.getX()), (int) Math.round(position.getY()),
					(int) size.getWidth(), (int) size.getHeight(), null);
	}
}
