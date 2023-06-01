package hitonoriol.stressstrain.gui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class DoubleFormatter extends TextFormatter<Double> {
	private final static Pattern dblAllowedPattern = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?E?-?([0-9]*)");
	private final static NumberFormat decimalFormat = new DecimalFormat("0.####################");
	private final static NumberFormat scientificFormat = new DecimalFormat("0.######E0");

	public DoubleFormatter(Format format, double defaultValue) {
		super(
				/* Double converter - to and from String */
				new StringConverter<Double>() {
					@Override
					public Double fromString(String s) {
						if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s))
							return 0.0;
						else
							return Double.valueOf(s);
					}

					@Override
					public String toString(Double d) {
						return (format == Format.DECIMAL ? decimalFormat : scientificFormat).format(d);
					}
				},
				/* Default value */
				defaultValue,
				/* Input filter */
				c -> dblAllowedPattern.matcher(c.getControlNewText()).matches() ? c : null);
	}

	public static enum Format {
		DECIMAL, SCIENTIFIC
	}
}
