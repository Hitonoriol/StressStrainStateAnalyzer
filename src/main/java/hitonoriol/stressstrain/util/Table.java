package hitonoriol.stressstrain.util;

import java.util.ArrayList;
import java.util.List;

import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_Grid;
import de.vandermeer.asciithemes.TA_GridConfig;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

/* Allows to form AsciiTable rows cell by cell instead of adding the whole row at once */
public class Table extends AsciiTable {
	private final static TA_Grid DEFAULT_GRID = TA_Grid.create("Default")
			.addCharacterMap(TA_GridConfig.RULESET_NORMAL, '*', '-', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+')
			.addCharacterMap(TA_GridConfig.RULESET_HEAVY, '*', '=', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+');

	private List<String> currentRow;
	private int maxCellWidth = 0;

	public Table() {
		row(false);
		super.ctx.setGrid(DEFAULT_GRID);
	}

	public Table(Array2d contents, String... colNames) {
		this();
		addHeavyRule();
		for (String colName : colNames)
			add(colName);
		row(false).setTextAlignment(TextAlignment.CENTER);
		addHeavyRule();
		populate(contents);
	}

	/* Add cell to the current row */
	public Table add(Object obj) {
		if (obj == null) {
			currentRow.add(null);
			return this;
		}
		String str = obj.toString();
		currentRow.add(str);
		maxCellWidth = Math.max(maxCellWidth, str.length());
		return this;
	}

	/* Make remaining cells in the current row empty */
	public void expand() {
		int colDiff = getColNumber() - currentRow.size();
		if (colDiff > 0)
			for (int i = 0; i < colDiff; ++i)
				currentRow.add(null);
	}

	/* Begin a new row */
	public AT_Row row(boolean addRule) {
		AT_Row row = null;
		if (currentRow != null) {
			expand();
			row = super.addRow(currentRow);
			super.ctx.setWidth(maxCellWidth * currentRow.size() + currentRow.size() + 2);
		}
		if (addRule)
			addRule();
		currentRow = new ArrayList<>();
		return row;
	}

	public AT_Row row() {
		return row(true);
	}

	/* Add all elemets from 2d array `arr` to this table */
	public void populate(Array2d arr) {
		for (int i = 0; i < arr.getHeight(); ++i) {
			for (int j = 0; j < arr.getWidth(); ++j) {
				add(arr.get(i, j));
			}
			row();
		}
	}

	public void setWidth(int width) {
		ctx.setWidth(width);
	}
	
	public int getWidth() {
		return ctx.getWidth();
	}

	public static String frame(String str, int width) {
		Table tbl = new Table();
		tbl.addHeavyRule();
		tbl.add(str).row(false).setTextAlignment(TextAlignment.CENTER);
		tbl.addHeavyRule();
		if (width > 0)
			tbl.setWidth(width);
		return tbl.render();
	}
	
	public static String frame(String str) {
		return frame(str, 0);
	}
}