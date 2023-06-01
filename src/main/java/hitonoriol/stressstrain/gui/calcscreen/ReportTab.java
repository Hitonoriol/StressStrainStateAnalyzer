package hitonoriol.stressstrain.gui.calcscreen;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.gillius.jfxutils.chart.ChartPanManager;
import org.gillius.jfxutils.chart.JFXChartUtil;

import hitonoriol.stressstrain.Analyzer;
import hitonoriol.stressstrain.analyzer.StressStrainAnalyzer;
import hitonoriol.stressstrain.gui.calcscreen.MainScreenController.PlateDescriptor;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Resources;
import hitonoriol.stressstrain.util.Pair;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

class ReportTab extends Tab {
	private VBox contents = new VBox();
	ScrollPane scroll = new ScrollPane(contents);

	private StackPane plotContainer;
	private NumberAxis xAxis, yAxis;
	private LineChart<Number, Number> plot;
	private Range zoRange, dzoRange;

	private StressStrainAnalyzer analyzer;
	private PlateDescriptor plateDescriptor;

	private Button saveReportBtn = new Button();

	ReportTab(PlateDescriptor input, StressStrainAnalyzer analyzer) {
		this.analyzer = analyzer;
		this.plateDescriptor = input;
		scroll.setFitToWidth(true);
		scroll.getContent().setOnScroll(scrollEvent -> {
			double deltaY = scrollEvent.getDeltaY();
			double contentHeight = scroll.getContent().getBoundsInLocal().getHeight();
			double scrollPaneHeight = scroll.getHeight();
			double diff = (contentHeight - scrollPaneHeight) * 1.5f;
			if (diff < 1)
				diff = 1;
			double vvalue = scroll.getVvalue();
			scroll.setVvalue(vvalue + -deltaY / diff);
		});
		setContent(scroll);

		saveReportBtn.setOnAction(event -> {
			Report report = new Report(analyzer, plateDescriptor);
			FileChooser chooser = Resources.createFileChooser();
			chooser.setInitialFileName(getText());
			File reportFile = chooser.showSaveDialog(Analyzer.app().mainStage());
			if (reportFile != null) {
				report.save(reportFile);
				report.checkTab(this);
			}
		});
	}

	void calculateAndRefresh(PlateDescriptor input) {
		analyzer.calcStressStrainState(input.n, input.q1, input.q2, input.kpi, input.usl1, input.usl2);
		refreshContents(input);
	}

	void refreshContents(PlateDescriptor input) {
		this.plateDescriptor = input;
		refreshContents();
	}

	void refreshContents() {
		contents.getChildren().clear();
		createPlot();

		// addTextEntry(Locale.get("CHAR"),
		// Resources.readExternal("stress-strain.dat"));
		addEntry(Locale.get("CHAR"),
				new FloatTable(
						analyzer.getStressStrainTable(),
						StressStrainAnalyzer.OUT_TABLE_HEADER));
		// addTextEntry(Locale.get("BIAS"),
		// Resources.readExternal("stress-strain.bias"));
		addEntry(Locale.get("BIAS"),
				new FloatTable(
						analyzer.getCouplingErrorTable(),
						StressStrainAnalyzer.BIAS_TABLE_HEADER));

		addPlot();
		addTextEntry(Locale.get("ZO_TEXT"),
				Locale.localize(zoRange.toString()), Resources.readExternal("zo")).setExpanded(false);
		addTextEntry(Locale.get("DZO_TEXT"),
				Locale.localize(dzoRange.toString()), Resources.readExternal("dzo")).setExpanded(false);
		saveReportBtn.setMaxWidth(Double.MAX_VALUE);
		saveReportBtn.setText(Locale.get("SAVE_TEXT"));
		saveReportBtn.setDisable(false);
		VBox btnBox = new VBox(saveReportBtn);
		btnBox.setAlignment(Pos.CENTER);
		contents.getChildren().add(btnBox);
	}

	private TitledPane addTextEntry(String title, String... text) {
		TextFlow textEntry = new TextFlow();
		for (String str : text) {
			Text textNode = new Text(str);
			textNode.setFont(Resources.FNT_MONOSPACE);
			textNode.setFontSmoothingType(FontSmoothingType.LCD);
			textEntry.getChildren().add(textNode);
		}
		return addEntry(title, textEntry);
	}

	private TitledPane addEntry(String title, Node node) {
		TitledPane container = new TitledPane(title, node);
		container.maxWidth(Double.MAX_VALUE);
		contents.getChildren().add(container);
		return container;
	}

	private void addPlot() {
		VBox plotSection = new VBox(plotContainer);
		HBox btnBox = new HBox();
		addEntry(Locale.get("PLOT"), plotSection);
		Button zoomZoBtn = new Button(Locale.get("ZOOM_ZO")), zoomDzoBtn = new Button(Locale.get("ZOOM_DZO"));
		btnBox.getChildren().addAll(zoomZoBtn, zoomDzoBtn);
		btnBox.setSpacing(10);
		btnBox.setAlignment(Pos.CENTER);
		plotSection.getChildren().add(btnBox);
		zoomZoBtn.setOnAction(event -> setAxisRange(zoRange));
		zoomDzoBtn.setOnAction(event -> setAxisRange(dzoRange));
	}

	/* Additional % of plot range padding when zooming in */
	private final static float PAD_PERCENT = 1.15f;

	private void createPlot() {
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		plot = new LineChart<Number, Number>(xAxis, yAxis);
		plot.setTitle("");
		plot.prefHeightProperty().bind(scroll.heightProperty().divide(1.5));

		plotContainer = new StackPane(plot);

		zoRange = plotAll("Zo", xySupplier -> analyzer.forEachZOPoint(xySupplier));
		dzoRange = plotAll("dZo", xySupplier -> analyzer.forEachDZOPoint(xySupplier));

		/* Pan with LMB / RMB */
		ChartPanManager panner = new ChartPanManager(plot);
		panner.setMouseFilter(mouseEvent -> {
			MouseButton btn = mouseEvent.getButton();
			if (btn != MouseButton.PRIMARY && btn != MouseButton.SECONDARY)
				mouseEvent.consume();
		});
		panner.start();
		/* Zoom with mouse wheel */
		JFXChartUtil.setupZooming(plot, mouseEvent -> mouseEvent.consume());
		/* Stop the scroll event from propagating back to tab root when zooming */
		plotContainer.addEventHandler(ScrollEvent.ANY, event -> event.consume());
		/* Double click to reset zoom & position */
		JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(plot);
	}

	private void setAxisRange(Range range) {
		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);
		xAxis.setLowerBound(range.min.x * PAD_PERCENT);
		xAxis.setUpperBound(range.max.x * PAD_PERCENT);
		yAxis.setLowerBound(range.min.y * PAD_PERCENT);
		yAxis.setUpperBound(range.max.y * PAD_PERCENT);
	}

	/* Plot all points provided by `populator` and return plotted data range (min / max coordinates) */
	private Range plotAll(String name, Consumer<BiConsumer<Float, Float>> populator) {
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		ObservableList<Data<Number, Number>> data = series.getData();
		Range range = new Range();
		populator.accept((x, y) -> {
			range.minMax(x, y);
			data.add(new Data<Number, Number>(x, y));
		});
		series.setName(name);
		plot.getData().add(series);
		return range;
	}

	PlateDescriptor getPlateDescriptor() {
		return plateDescriptor;
	}

	Button getSaveReportBtn() {
		return saveReportBtn;
	}

	LineChart<Number, Number> getPlot() {
		return plot;
	}

	Range getZORange() {
		return zoRange;
	}
	
	Range getDZORange() {
		return dzoRange;
	}
	
	static class Range {
		private int n = 0;
		private Pair min = new Pair(Float.MAX_VALUE, Float.MAX_VALUE);
		private Pair max = new Pair(Float.MIN_VALUE, Float.MIN_VALUE);

		void minMax(float x, float y) {
			++n;
			min.setIfLess(x, y);
			max.setIfGreater(x, y);
		}

		int getPointCount() {
			return n;
		}
		
		@Override
		public String toString() {
			return String.format("%d {POINTS}, {XRANGE} {FROM} %f {TO} %f / {YRANGE} {FROM} %f {TO} %f",
					n, min.x, max.x, min.y, max.x);
		}
	}
}
