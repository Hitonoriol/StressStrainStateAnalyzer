package hitonoriol.stressstrain.analyzer;

import java.util.Arrays;
import java.util.function.BiConsumer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import hitonoriol.stressstrain.Util;
import hitonoriol.stressstrain.util.Array2d;

/*
 * Wrapper for `StressStrainLibrary`.
 * 		the interface itself can't be implemented directly because it's bound to native library calls.
 */
public class StressStrainAnalyzer {
	/* Will search for:
	 * 		`stress-strain.dll` on Windows
	 * 		`libstress-strain.so` on Linux
	 */
	private final StressStrainLibrary library = Native.load("stress-strain", StressStrainLibrary.class);

	private float zx[], zy[], dzx[], dzy[];

	public final static String[] OUT_TABLE_HEADER = { "x", "U(x)", "W(x)", "W^(X)", "N(x)", "M(x)", "Q(x)" };
	public final static String[] BIAS_TABLE_HEADER = { "U - U", "W - W", "W^ - W^", "N - N", "M - M", "Q - Q" };

	public Array2d getStressStrainTable() {
		return getTable(library.getOutTablePtr(), library.outTableSize(), library.outTableWidth());
	}

	public Array2d getCouplingErrorTable() {
		return getTable(library.getBiasTablePtr(), library.biasTableSize(), library.biasTableWidth());
	}

	private Array2d getTable(Pointer arrPtr, int size, int width) {
		float[] tableArr = arrPtr.getFloatArray(0, size);
		return new Array2d(tableArr, width);
	}

	public void forEachZOPoint(BiConsumer<Float, Float> pointConsumer) {
		forEachPlotPoint(zx, zy, pointConsumer);
	}

	public void forEachDZOPoint(BiConsumer<Float, Float> pointConsumer) {
		forEachPlotPoint(dzx, dzy, pointConsumer);
	}

	private void forEachPlotPoint(float[] x, float[] y, BiConsumer<Float, Float> pointConsumer) {
		final int ptCount = x.length;
		for (int i = 0; i < ptCount; ++i)
			pointConsumer.accept(x[i], y[i]);
	}

	public void calcStressStrainState(int n, float q1, float q2, float kpi, boolean usl1, boolean usl2) {
		library.calcStressStrainState(n, q1, q2, kpi, toInt(usl1), toInt(usl2));
		final int ptCount = library.getPlotPoints();
		float zx[] = array(library.getZOXPtr(), ptCount);
		float zy[] = array(library.getZOYPtr(), ptCount);
		float dzx[] = array(library.getDZOXPtr(), ptCount);
		float dzy[] = array(library.getDZOYPtr(), ptCount);
		if (n % 2 == 0)
			setPlotPoints(zx, zy, dzx, dzy);
		/* Truncate the extra section when `n` is odd */
		else {
			int i = 0;
			for (int intersections = 0; i < zx.length && intersections <= n; ++i) {
				if (i > 0 && zx[i] == 0 && zy[i] == 0)
					continue;

				if (zy[i] == 0) {
					Util.out("Intersected Ox at idx: %d [%f, %f], segments: %d", i, zx[i], zy[i], intersections);
					++intersections;
				}
			}
			setPlotPoints(
					Arrays.copyOfRange(zx, 0, i),
					Arrays.copyOfRange(zy, 0, i),
					Arrays.copyOfRange(dzx, 0, i),
					Arrays.copyOfRange(dzy, 0, i));
		}
	}

	private void setPlotPoints(float zx[], float zy[], float dzx[], float dzy[]) {
		this.zx = zx;
		this.zy = zy;
		this.dzx = dzx;
		this.dzy = dzy;
	}

	public float[] getZOX() {
		return zx;
	}

	public float[] getZOY() {
		return zy;
	}

	public float[] getDZOX() {
		return dzx;
	}

	public float[] getDZOY() {
		return dzy;
	}

	public void dispose() {
		library.dispose();
	}

	public StressStrainLibrary getLibrary() {
		return library;
	}

	private static float[] array(Pointer ptr, int size) {
		return ptr.getFloatArray(0, size);
	}

	private static int toInt(boolean b) {
		return b ? 1 : 0;
	}
}
