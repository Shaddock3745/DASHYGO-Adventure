package doubleshape2d;

public final class DoubleVector {

	public static final double dot(double x1, double y1, double x2, double y2) {
		return x1 * x2 + y1 * y2;
	}

	public static final double dot(DoublePoint point, DoubleVector vector) {
		return vector.getX() * point.getX() + vector.getY() * point.getY();
	}

	public static final double cross(DoublePoint point1, DoublePoint point2) {
		return point1.getX() * point2.getY() - point1.getY() * point2.getX();
	}

	public static final double cross(DoubleVector vector1, DoubleVector vector2) {
		return vector1.getX() * vector2.getY() - vector1.getY() * vector2.getX();
	}

	private double x, y;

	public DoubleVector(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public DoubleVector(DoublePoint point1, DoublePoint point2) {
		this(point2.getX() - point1.getX(), point2.getY() - point1.getY());
	}

	public final double getX() {
		return x;
	}

	public final double getY() {
		return y;
	}

	public final double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	public final boolean isNaN() {
		return Double.isNaN(x) || Double.isNaN(y);
	}

	public final void negate() {
		x *= -1;
		y *= -1;
	}

	public final void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public final DoubleVector negativeVector() {
		return new DoubleVector(-x, -y);
	}

	public final DoubleVector normalVector1() {
		return new DoubleVector(x, -y);
	}

	public final DoubleVector normalVector2() {
		return new DoubleVector(-x, y);
	}

	public final DoubleVector getBasicVector() {
		double length = getLength();
		return new DoubleVector(x / length, y / length);
	}
}
