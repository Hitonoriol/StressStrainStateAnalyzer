package hitonoriol.stressstrain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.BiConsumer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;

public class LibraryTest {
	@Disabled
	@RepeatedTest(5)
	public void testPlotPoints() {
		StressStrainAnalyzer analyzer = new StressStrainAnalyzer();
		BiConsumer<Float, Float> printPts = (x, y) -> Util.out(x + ", " + y);

		Util.out("Calculating SSS on sample data...");
		analyzer.calcStressStrainState(6, 0.0315f, 0.0125f, 1.25f, false, true);

		Util.out("Zo:");
		analyzer.forEachZOPoint(printPts);

		Util.out("DZo:");
		analyzer.forEachDZOPoint(printPts);

		assertEquals(analyzer.getLibrary().getPlotPoints(), 157);
	}

	@Test
	public void testAnalyzer() {
		StressStrainAnalyzer analyzer = new StressStrainAnalyzer();
		analyzer.calcStressStrainState(6, 0.0315f, 0.0125f, 1.25f, false, true);
	}
}
