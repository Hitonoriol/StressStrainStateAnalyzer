package hitonoriol.stressstrain;

import hitonoriol.stressstrain.gui.calcscreen.MainScreenController;
import hitonoriol.stressstrain.resources.Locale;
import hitonoriol.stressstrain.resources.Prefs;
import hitonoriol.stressstrain.resources.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Analyzer extends Application {
	private static Analyzer application;

	private Stage mainStage;
	private MainScreenController mainController;

	public Analyzer() {
		application = this;
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		this.mainStage = mainStage;
		FXMLLoader primaryLoader = Resources.newFXMLLoader();
		Scene scene = new Scene(Locale.loadFXML(Resources.MAIN_SCREEN));
		mainController = primaryLoader.getController();
		loadMainScreen(scene);

		mainStage.setTitle("Stress Strain State Analyzer");
		mainStage.show();
		mainStage.setMinWidth(mainStage.getWidth());
		mainStage.setMinHeight(mainStage.getHeight());
	}

	@Override
	public void stop() throws Exception {
		mainController.getCalculationExecutor().shutdown();
		super.stop();
	}

	public void loadMainScreen(Scene scene) {
		Resources.loadStyles(scene);
		mainStage.setScene(scene);
	}

	public Stage mainStage() {
		return mainStage;
	}

	public MainScreenController mainController() {
		return mainController;
	}

	public static void run(String args[]) {
		Prefs.init();
		launch(args);
	}

	public static Analyzer app() {
		return application;
	}
}
