package hitonoriol.stressstrain.util;

public class Pair {
	public float x, y;

	public Pair(float x, float y) {
		set(x, y);
	}
	
	public Pair(Pair rhs) {
		set(rhs.x, rhs.y);
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Pair mul(float n) {
		x *= n;
		y *= n;
		return this;
	}

	public void setIfLess(float x, float y) {
		if (x < this.x)
			this.x = x;

		if (y < this.y)
			this.y = y;
	}

	public void setIfGreater(float x, float y) {
		if (x > this.x)
			this.x = x;

		if (y > this.y)
			this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("[%f; %f]", x, y);
	}
}
