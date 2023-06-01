package hitonoriol.stressstrain.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;

import hitonoriol.stressstrain.Analyzer;
import hitonoriol.stressstrain.Util;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class Locale {
	private final static Locale instance = new Locale();
	private final static String PREFIX = "{", SUFFIX = "}";
	private Map<String, String> locMap = new HashMap<>();
	private StringSubstitutor substitutor = new StringSubstitutor(locMap, PREFIX, SUFFIX);

	private Locale() {
	}

	public static void loadLanguage(String lang) {
		Util.out("Loading language: %s", lang);
		boolean langUpdated;
		if (langUpdated = !instance.locMap.isEmpty())
			Prefs.values().updateLanguage(lang);

		instance.locMap.clear();
		instance.locMap.putAll(Resources.loadMap("/language/" + lang + ".json", String.class, String.class));
		
		if (langUpdated) {
			Analyzer app = Analyzer.app();
			Resources.newFXMLLoader();
			app.loadMainScreen(new Scene(loadFXML(Resources.MAIN_SCREEN, app.mainController())));
		}
	}

	/* Read a localized file from classpath, replacing localization placeholders with values from currently loaded locale */
	public static String readFile(String file) {
		String src;
		try {
			src = new String(instance.getClass().getResourceAsStream(file).readAllBytes(), Resources.UTF8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return localize(src);
	}

	/* (Re)Load a localized FXML file from classpath */
	public static <T> T loadFXML(String file, Object controller) {
		try {
			FXMLLoader loader = Resources.fxmlLoader();
			String fxml = readFile(file);
			if (controller != null) {
				loader.setController(controller);
				fxml = Resources.removeFXMLController(fxml);
			}
			return loader.load(new ByteArrayInputStream(fxml.getBytes(Resources.UTF8)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T loadFXML(String file) {
		return loadFXML(file, null);
	}
	
	public static String localize(String str) {
		return instance.substitutor.replace(str);
	}

	/* Get localization variable value by name without substitution */
	public static String get(String str) {
		return instance.locMap.getOrDefault(str, str);
	}

	public static enum Language {
		EN, RU, UA
	}
}
