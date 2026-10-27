package doubleshape2d;

public final class DoubleCircle implements Cloneable {
	private double centerX, centerY, radius;

	public DoubleCircle(double centerX, double centerY, double radius) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
	}

	public final double getCenterX() {
		return centerX;
	}

	public final double getCenterY() {
		return centerY;
	}

	public final DoublePoint getCenterPoint() {
		return new DoublePoint(centerX, centerY);
	}

	public final double getRadius() {
		return radius;
	}

	public final void offset(double x, double y) {
		centerX += x;
		centerY += y;
	}

	public final double getDistance(DoubleCircle circle) {
		return Math.sqrt(Math.pow(this.centerX - circle.centerX, 2) + Math.pow(this.centerY - circle.centerY, 2));
	}

	public final boolean isColliding(DoublePolygon polygon) {
		return Collision.isCollidingCircle(polygon, this);
	}

	public final boolean isCollidingCircle(DoubleCircle circle) {
		return Collision.isCollidingTwoCircle(this, circle);
	}

	@Override
	public final DoubleCircle clone() {
		// TODO Auto-generated method stub
		return new DoubleCircle(centerX, centerY, radius);
	}
}
