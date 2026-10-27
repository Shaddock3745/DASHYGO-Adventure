package doubleshape2d;

public final class DoubleDimension {
	private double height, width;

	public DoubleDimension(double width, double height) {
		this.height = height;
		this.width = width;
	}

	public final double getHeight() {
		return height;
	}

	public final void setHeight(double height) {
		this.height = height;
	}

	public final double getWidth() {
		return width;
	}

	public final void setWidth(double width) {
		this.width = width;
	}
}
