package doubleshape2d;

public final class DoublePoint implements Cloneable {
	private double x, y;

	public DoublePoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final void setX(double x) {
		this.x = x;
	}

	public final void setY(double y) {
		this.y = y;
	}

	public final void offsetX(double x) {
		this.x += x;
	}

	public final void offsetY(double y) {
		this.y += y;
	}

	@Override
	protected final DoublePoint clone() {
		// TODO Auto-generated method stub
		return new DoublePoint(x, y);
	}
}
