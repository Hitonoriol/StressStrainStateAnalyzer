package hitonoriol.stressstrain.resources;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import hitonoriol.stressstrain.gui.calcscreen.Report;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Resources {
	public final static String MAIN_SCREEN = "/MainWindow.fxml";

	private static FXMLLoader fxmlLoader;

	private final static ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	private final static TypeFactory typeFactory = mapper.getTypeFactory();

	static {
		mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator());
	}

	public final static File reportDir = new File("reports");

	static {
		if (!reportDir.exists())
			reportDir.mkdirs();
	}

	public final static Font FNT_MONOSPACE = Font
			.loadFont(Resources.class.getResource("/RobotoMono.ttf").toExternalForm(), 14);

	public final static Charset UTF8 = Charset.forName("utf8");

	private Resources() {
	}

	private final static ExtensionFilter reportExtFilter = new ExtensionFilter(
			"Stress strain analysis reports", "*" + Report.fileExtension);

	public static FileChooser createFileChooser() {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(reportDir);
		chooser.getExtensionFilters().add(reportExtFilter);
		return chooser;
	}

	public static MapType getMapType(Class<?> key, Class<?> value) {
		return typeFactory.constructMapType(HashMap.class, key, value);
	}

	public static ObjectWriter getMapWriter(Class<?> key, Class<?> value) {
		return mapper.writerFor(getMapType(key, value));
	}

	public static ObjectReader getMapReader(Class<?> key, Class<?> value) {
		return mapper.readerFor(getMapType(key, value));
	}

	public static <K, V> HashMap<K, V> loadMap(String file, Class<K> keyType, Class<V> valueType) {
		try {
			return mapper.readValue(Resources.class.getResourceAsStream(file),
					getMapType(keyType, valueType));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> boolean serialize(String path, T obj) {
		return serialize(new File(path), obj);
	}

	public static <T> boolean serialize(File file, T obj) {
		try {
			mapper.writeValue(file, obj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static <T> T deserialize(File file, Class<T> clazz) {
		try {
			return mapper.readerFor(clazz).readValue(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T deserialize(String path, Class<T> clazz) {
		return deserialize(new File(path), clazz);
	}

	public static String readExternal(String path) {
		try {
			return Files.readString(Paths.get(path));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean write(String path, String contents) {
		try {
			Files.write(Paths.get(path), contents.getBytes(UTF8));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void loadStyles(Scene scene) {
		scene.getStylesheets().add(Resources.class.getResource("/styles.css").toExternalForm());
	}

	private final static String controllerRegex = "fx:controller=\\\"(.*)\\\"";

	public static String removeFXMLController(String fxml) {
		return fxml.replaceFirst(controllerRegex, "");
	}

	public static FXMLLoader fxmlLoader() {
		return fxmlLoader;
	}

	public static FXMLLoader newFXMLLoader() {
		return fxmlLoader = new FXMLLoader();
	}

	public static ObjectMapper mapper() {
		return mapper;
	}
}
