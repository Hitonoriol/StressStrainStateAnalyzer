package hitonoriol.stressstrain.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import hitonoriol.stressstrain.Util;

public class Array2d {
	private final float[] arr;
	private final int width, height;

	public Array2d(float[] arr, int width) {
		this.arr = arr;
		this.width = width;
		height = arr.length / width;
		Util.out("Creating a new Array2d [%dx%d]: %s", height, width, Arrays.toString(arr));
	}

	public void forEach(Consumer<Float> action) {
		for (int i = 0; i < arr.length; ++i)
			action.accept(arr[i]);
	}

	public void forEachRow(Consumer<List<Float>> action) {
		Util.out("forEachRow on table [%dx%d]...", height, width);
		for (int i = 0; i < height; ++i) {
			List<Float> row = new ArrayList<>();
			for (int j = 0; j < width; ++j)
				row.add(get(i, j));
			action.accept(row);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float get(int i, int j) {
		return arr[j + i * width];
	}
}
