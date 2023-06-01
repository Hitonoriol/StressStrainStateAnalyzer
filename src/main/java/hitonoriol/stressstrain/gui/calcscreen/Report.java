package hitonoriol.stressstrain.gui.calcscreen;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import hitonoriol.stressstrain.Analyzer;
import hitonoriol.stressstrain.Util;
import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.gui.calcscreen.ReportTab.Range;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Resources;
import hitonoriol.stressstrain.util.Table;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Report {
	@JsonIgnore
	private String name;
	@JsonIgnore
	private File path;
	@JsonIgnore
	StressStrainAnalyzer analyzer;
	private PlateDescriptor plateParameters;

	public final static String fileExtension = ".ssr";
	private final static String outTableName = "characteristics.txt", biasTableName = "coupling-errors.txt";

	private final static String zoPtsFile = "Zo-points.txt", dzoPtsFile = "dZo-points.txt";
	private final static String plotFile = "transverse-section.png";
	private final static int plotWidth = 1920, plotHeight = 1080;

	public Report() {
	}

	public Report(StressStrainAnalyzer analyzer, PlateDescriptor plateParameters) {
		this.plateParameters = plateParameters;
		this.analyzer = analyzer;
	}

	public ReportTab restore(MainScreenController mainScreen) {
		ReportTab tab = mainScreen.newTab();
		tab.calculateAndRefresh(plateParameters);
		tab.setText(name);
		checkTab(tab);
		mainScreen.addTab(tab);
		return tab;
	}

	public boolean save(File reportFile) {
		File reportParent = reportFile.getParentFile();
		String reportName = reportFile.getName(), reportNoExt = Util.removeFileExtension(reportName);
		String newDir = reportParent.getAbsolutePath() + "/" + reportNoExt;

		Util.out("parent: [%s] name: [%s] noExt: [%s]", reportParent, reportName, reportNoExt);

		/* Create a new directory named the same as the report before saving */
		if (!reportParent.getName().equals(reportNoExt)) {
			new File(newDir).mkdirs();
			reportFile = new File(newDir + "/" + reportName);
		}

		if (Resources.serialize(reportFile, this)) {
			this.path = reportFile;
			saveTables();
			ReportTab tab = Analyzer.app().mainController().getCurrentTab();
			savePlotPoints(zoPtsFile, tab.getZORange(), ptConsumer -> analyzer.forEachZOPoint(ptConsumer));
			savePlotPoints(dzoPtsFile, tab.getDZORange(), ptConsumer -> analyzer.forEachDZOPoint(ptConsumer));
			savePlot();

			return true;
		}
		return false;
	}

	private void savePlot() {
		LineChart<Number, Number> plot = Analyzer.app().mainController().getCurrentTab().getPlot();
		plot.setAnimated(false);
		Util.saveScreenshot(plot, path.getParent() + "/" + plotFile, plotWidth, plotHeight);
		plot.setAnimated(true);
	}

	private void savePlotPoints(String path, Range range, Consumer<BiConsumer<Float, Float>> ptIterator) {
		StringBuilder sb = new StringBuilder();
		Util.line(sb, Locale.localize(range.toString()));
		Util.line(sb, "");
		Util.line(sb, String.format("%15s%15s", "X", "Y"));
		ptIterator.accept((x, y) -> sb
				.append(String.format("%15.5f %15.5f", x, y))
				.append(System.lineSeparator()));
		Resources.write(this.path.getParent() + "/" + path, sb.toString());
	}

	private final static String BR = Util.repeat(System.lineSeparator(), 3);

	private void saveTables() {
		Table outTable = new Table(analyzer.getStressStrainTable(), StressStrainAnalyzer.OUT_TABLE_HEADER);
		Table biasTable = new Table(analyzer.getCouplingErrorTable(), StressStrainAnalyzer.BIAS_TABLE_HEADER);
		int width = outTable.getWidth();
		String prefix = path.getParent() + "/";
		String inputParams = plateParameters.toString() + BR;
		Resources.write(prefix + outTableName,
				Table.frame(Locale.get("CHAR"), width) + BR + inputParams + outTable.render());
		Resources.write(prefix + biasTableName,
				Table.frame(Locale.get("BIAS"), width) + BR + inputParams + biasTable.render());
	}

	@JsonIgnore
	public boolean isSaved() {
		return path != null && path.exists();
	}

	@JsonIgnore
	public File getPath() {
		return path;
	}

	void checkTab(ReportTab tab) {
		if (isSaved()) {
			Button saveBtn = tab.getSaveReportBtn();
			saveBtn.setDisable(true);
			saveBtn.setText(Locale.get("SAVED_TO") + " \"" + getPath().getAbsolutePath() + "\"");
		}
	}

	public static void restore(MainScreenController mainScreen, File file) {
		Report report = Resources.deserialize(file, Report.class);
		report.name = file.getName();
		report.path = file;
		report.restore(mainScreen);
	}
}
