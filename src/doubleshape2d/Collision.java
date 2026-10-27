package doubleshape2d;

import java.util.ArrayList;
import java.util.List;

public final class Collision {

	private static final double E = Math.sqrt(calE());

	private static final double calE() {
		double e = 0.5;
		while (1.0 + e > 1.0)
			e *= 0.5;
		return e;
	}

	public static final double getE() {
		return E;
	}

	private static final void update1(DoubleVector vector, DoublePoint point) {
		vector.set(-point.getX(), -point.getY());
	}

	private static final void update2(DoubleVector vector, DoublePoint pointA, DoublePoint pointB) {
		if (pointA.getX() == pointB.getX()) {
			if (pointA.getX() == 0)
				vector.set(1, 0);
			else
				vector.set(-pointA.getX(), 0);
		} else if (pointA.getY() == pointB.getY()) {
			if (pointA.getY() == 0)
				vector.set(0, 1);
			else
				vector.set(0, -pointA.getY());
		} else {
			double x, Z = (pointB.getY() - pointA.getY()) / (pointB.getX() - pointA.getX());
			x = -(pointA.getX() * Z - pointA.getY()) / (1 / Z + Z);
			vector.set(x, -x / Z);
		}
	}

	private static final void update(List<DoublePoint> sim, DoubleVector vector) {
		switch (sim.size()) {
		case 2:
			DoublePoint pointA = sim.get(0), pointB = sim.get(1);
			DoubleVector E = new DoubleVector(pointA, pointB);
			double d1 = DoubleVector.dot(pointB, E), d2 = DoubleVector.dot(pointA, E.negativeVector());
			if (d1 < 0) {
				sim.remove(0);
				update1(vector, pointB);
			} else if (d2 < 0) {
				sim.remove(1);
				update1(vector, pointA);
			} else
				update2(vector, pointA, pointB);
			return;
		case 3:
			DoublePoint point1 = sim.get(0), point2 = sim.get(1), point3 = sim.get(2);
			DoubleVector E12 = new DoubleVector(point1, point2), E23 = new DoubleVector(point2, point3),
					E13 = new DoubleVector(point1, point3);
			double P1E12 = DoubleVector.dot(point1, E12), P2E12 = DoubleVector.dot(point2, E12),
					P1E13 = DoubleVector.dot(point1, E13), P3E13 = DoubleVector.dot(point3, E13),
					P2E23 = DoubleVector.dot(point2, E23), P3E23 = DoubleVector.dot(point3, E23),
					n123 = DoubleVector.cross(E12, E13);
			if (P1E12 >= 0 && P1E13 >= 0) {
				sim.remove(2);
				sim.remove(1);
				update1(vector, point1);
			} else if (P2E12 <= 0 && P2E23 >= 0) {
				sim.remove(2);
				sim.remove(0);
				update1(vector, point2);
			} else if (P3E13 <= 0 && P3E23 <= 0) {
				sim.remove(1);
				sim.remove(0);
				update1(vector, point3);
			} else if (P2E12 > 0 && P1E12 < 0 && n123 * DoubleVector.cross(point1, point2) <= 0) {
				sim.remove(2);
				update2(vector, point1, point2);
			} else if (P3E13 > 0 && P1E13 < 0 && n123 * DoubleVector.cross(point3, point1) <= 0) {
				sim.remove(1);
				update2(vector, point1, point3);
			} else if (P3E23 > 0 && P2E23 < 0 && n123 * DoubleVector.cross(point2, point3) <= 0) {
				sim.remove(0);
				update2(vector, point2, point3);
			} else
				vector.set(Double.NaN, Double.NaN);
			return;
		}
	}

	public static final boolean isColliding(DoublePolygon polygon1, DoublePolygon polygon2) {
		List<DoublePoint> sim = new ArrayList<>(3);
		DoubleVector vector = new DoubleVector(0, 1);
		sim.add(getMinkowskiDiscrepancy(polygon1, polygon2, vector));
		vector.negate();
		DoublePoint newPoint;
		for (short i = 0; i < 30; i++) {
			newPoint = getMinkowskiDiscrepancy(polygon1, polygon2, vector);
			if (DoubleVector.dot(newPoint, vector) <= 0)
				return false;
			sim.add(newPoint.clone());
			update(sim, vector);
			if (vector.isNaN())
				return true;
		}
		return false;
	}

	public static final boolean isCollidingCircle(DoublePolygon polygon, DoubleCircle circle) {
		List<DoublePoint> sim = new ArrayList<>(3);
		DoubleVector vector = new DoubleVector(0, 1);
		sim.add(getMinkowskiDiscrepancyCircle(polygon, circle, vector));
		vector.negate();
		DoublePoint newPoint;
		for (short i = 0; i < 30; i++) {
			newPoint = getMinkowskiDiscrepancyCircle(polygon, circle, vector);
			if (DoubleVector.dot(newPoint, vector) <= 0)
				return false;
			sim.add(newPoint.clone());
			update(sim, vector);
			if (vector.isNaN())
				return true;
		}
		return false;
	}

	public static final boolean isCollidingTwoCircle(DoubleCircle circle1, DoubleCircle circle2) {
		return circle1.getDistance(circle2) <= circle1.getRadius() + circle2.getRadius();
	}

	private static final DoublePoint getFarthestPoint(DoublePolygon polygon, DoubleVector vector) {
		int size = polygon.getPointsNumber(), maxIndex = 0;
		double max = Double.NEGATIVE_INFINITY, op;
		for (int i = 0; i < size; i++) {
			op = DoubleVector.dot(polygon.getX(i), polygon.getY(i), vector.getX(), vector.getY());
			if (op > max) {
				max = op;
				maxIndex = i;
			}
		}
		return new DoublePoint(polygon.getX(maxIndex), polygon.getY(maxIndex));
	}

	private static final DoublePoint getFarthestPointCircle(DoubleCircle circle, DoubleVector vector) {
		DoubleVector basicVector = vector.getBasicVector();
		return new DoublePoint(circle.getCenterX() + basicVector.getX() * circle.getRadius(),
				circle.getCenterY() + basicVector.getY() * circle.getRadius());
	}

	private static final DoublePoint getMinkowskiDiscrepancy(DoublePolygon polygon1, DoublePolygon polygon2,
			DoubleVector vector) {
		DoublePoint point1 = getFarthestPoint(polygon1, vector),
				point2 = getFarthestPoint(polygon2, vector.negativeVector());
		return new DoublePoint(point1.getX() - point2.getX(), point1.getY() - point2.getY());
	}

	private static final DoublePoint getMinkowskiDiscrepancyCircle(DoublePolygon polygon, DoubleCircle circle,
			DoubleVector vector) {
		DoublePoint point1 = getFarthestPoint(polygon, vector),
				point2 = getFarthestPointCircle(circle, vector.negativeVector());
		return new DoublePoint(point1.getX() - point2.getX(), point1.getY() - point2.getY());
	}

	public static final DoublePolygon getSweptPolygon(DoublePolygon o, DoublePolygon n) {
		DoubleVector vector = new DoubleVector(n.getX(0) - o.getX(0), n.getY(0) - o.getY(0));
		DoubleVector v1 = vector.normalVector1(), v2 = vector.normalVector2();
		DoublePoint point1 = getFarthestPoint(o, v1), point2 = getFarthestPoint(n, v1),
				point3 = getFarthestPoint(n, v2), point4 = getFarthestPoint(o, v2);
		return new DoublePolygon(4, new double[] { point1.getX(), point2.getX(), point3.getX(), point4.getX() },
				new double[] { point1.getY(), point2.getY(), point3.getY(), point4.getY() });
	}

	public static final DoublePolygon getSweptPolygon(DoubleCircle o, DoubleCircle n) {
		DoubleVector vector = new DoubleVector(n.getCenterX() - o.getCenterX(), n.getCenterY() - o.getCenterY());
		DoubleVector v1 = vector.normalVector1(), v2 = vector.normalVector2();
		DoublePoint point1 = getFarthestPointCircle(o, v1), point2 = getFarthestPointCircle(n, v1),
				point3 = getFarthestPointCircle(n, v2), point4 = getFarthestPointCircle(o, v2);
		return new DoublePolygon(4, new double[] { point1.getX(), point2.getX(), point3.getX(), point4.getX() },
				new double[] { point1.getY(), point2.getY(), point3.getY(), point4.getY() });
	}
}
