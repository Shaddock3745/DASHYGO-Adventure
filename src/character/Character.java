package character;

import java.awt.Graphics;

import doubleshape2d.DoubleDimension;
import doubleshape2d.DoublePoint;
import panels.MainFrame;

public abstract class Character {

	protected DoublePoint position;
	protected int imageIndex = 0, imageIndexTemp = 0;
	protected DoubleDimension size;

	public abstract void Paint(Graphics g);

	public void setX(double x) {
		position.setX(x);
	}

	public void setY(double y) {
		position.setY(y);
	}

	public void setPosition(double x, double y) {
		position.setX(x);
		position.setY(y);
	}

	public final DoublePoint getPosition() {
		return position;
	}

	public void OffsetX(double x) {
		this.position.offsetX(x);
	}

	protected static final int Zoom(double input) {
		return MainFrame.Zoom(input);
	}
}
