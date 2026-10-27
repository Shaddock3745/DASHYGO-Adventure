package doubleshape2d;

import java.util.Arrays;

public final class DoublePolygon implements Cloneable {

	private int length;
	private double[] Xarray, Yarray;

	public DoublePolygon(int length, double[] Xarray, double[] Yarray) {
		this.length = length;
		this.Xarray = Arrays.copyOf(Xarray, length);
		this.Yarray = Arrays.copyOf(Yarray, length);
	}

	public final int getPointsNumber() {
		return length;
	}

	public final void forciblySet(int index, double x, double y) {
		Xarray[index] = x;
		Yarray[index] = y;
	}

	public final double getX(int index) {
		return Xarray[index];
	}

	public final double getY(int index) {
		return Yarray[index];
	}

	public final void offest(double x, double y) {
		for (int i = 0; i < length; i++) {
			Xarray[i] += x;
			Yarray[i] += y;
		}
	}

	public final void offestX(double x) {
		for (int i = 0; i < length; i++)
			Xarray[i] += x;
	}

	public final void offestY(double y) {
		for (int i = 0; i < length; i++)
			Yarray[i] += y;
	}

	public final void setX(double x) {
		for (int i = length - 1; i > -1; i--) {
			Xarray[i] -= Xarray[0];
			Xarray[i] += x;
		}
	}

	public final void setY(double y) {
		for (int i = length - 1; i > -1; i--) {
			Yarray[i] -= Yarray[0];
			Yarray[i] += y;
		}
	}

	public final void setPosition(double x, double y) {
		for (int i = length - 1; i > -1; i--) {
			Xarray[i] -= Xarray[0];
			Xarray[i] += x;
			Yarray[i] -= Yarray[0];
			Yarray[i] += y;
		}
	}

	public final DoublePoint getGravityCenterPoint() {
		double x = 0, y = 0;
		for (int i = 0; i < length; i++) {
			x += Xarray[i];
			y += Yarray[i];
		}
		return new DoublePoint(x / length, y / length);
	}

	public final void Rotate(double radins) {
		double x = 0, y = 0, cx, cy;
		for (int i = 0; i < length; i++) {
			x += Xarray[i];
			y += Yarray[i];
		}
		cx = x / length;
		cy = y / length;
		for (int i = 0; i < length; i++) {
			x = Xarray[i] - cx;
			y = Yarray[i] - cy;
			Xarray[i] = x * Math.cos(radins) - y * Math.sin(radins) + cx;
			Yarray[i] = x * Math.sin(radins) + y * Math.cos(radins) + cy;
		}
	}

	public final boolean isColliding(DoublePolygon polygon) {
		return Collision.isColliding(this, polygon);
	}

	public final boolean isCollidingCircle(DoubleCircle circle) {
		return Collision.isCollidingCircle(this, circle);
	}

	@Override
	public final DoublePolygon clone() {
		// TODO Auto-generated method stub
		return new DoublePolygon(length, Xarray, Yarray);
	}
}
