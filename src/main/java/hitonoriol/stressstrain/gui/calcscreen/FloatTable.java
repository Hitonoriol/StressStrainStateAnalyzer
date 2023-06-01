package hitonoriol.stressstrain.gui.calcscreen;

import java.util.List;

import hitonoriol.stressstrain.util.Array2d;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FloatTable extends TableView<List<Float>> {
	public FloatTable(Array2d table, String... colNames) {
		ObservableList<TableColumn<List<Float>, ?>> cols = getColumns();
		for (int i = 0; i < table.getWidth(); ++i) {
			final int idx = i;
			TableColumn<List<Float>, Float> col = new TableColumn<>(i < colNames.length ? colNames[i] : "");
			col.setCellValueFactory(data -> new SimpleFloatProperty(data.getValue().get(idx)).asObject());
			cols.add(col);
		}

		if (table != null)
			populate(table);
		
		/* Remove extra space at the bottom */
		setFixedCellSize(25);
		prefHeightProperty().bind(Bindings.size(getItems()).multiply(getFixedCellSize()).add(30));
	}

	public FloatTable(String... colNames) {
		this(null, colNames);
	}

	public void populate(Array2d table) {
		ObservableList<List<Float>> cells = getItems();
		table.forEachRow(entry -> cells.add(entry));
	}
}
