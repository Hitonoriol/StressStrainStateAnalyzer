package hitonoriol.stressstrain.resources;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Prefs {
	private static Prefs instance;
	private static final File CONFIG = new File("stress-strain.cfg");

	private String language = "en";

	private Prefs() {}

	public static void init() {
		if (instance != null)
			return;
		
		if (CONFIG.exists()) {
			try {
				instance = Resources.mapper().readerFor(Prefs.class).readValue(CONFIG);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			instance = new Prefs();
		Locale.loadLanguage(instance.language);
	}
	
	public void updateLanguage(String language) {
		this.language = language;
		save();
	}
	
	private void save() {
		try {
			Resources.mapper().writerFor(Prefs.class).writeValue(CONFIG, instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Prefs values() {
		return instance;
	}
}
