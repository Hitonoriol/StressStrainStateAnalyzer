package hitonoriol.stressstrain;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import hitonoriol.stressstrain.resources.Resources;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class Util {
	private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public static void out(String str) {
		System.out.println(str);
	}

	public static void out(String str, Object... args) {
		System.out.println(String.format(str, args));
	}

	public static String timestamp() {
		return dateFormat.format(new Date());
	}

	private final static String extPattern = "(?<!^)[.][^.]*$";

	public static String removeFileExtension(String filename) {
		if (filename == null || filename.isEmpty())
			return filename;
		return filename.replaceAll(extPattern, "");
	}

	public static StringBuilder line(StringBuilder sb, String str) {
		return sb.append(str).append(System.lineSeparator());
	}
	
	public static String repeat(String str, int times) {
		return new String(new char[times]).replace("\0", str);
	}

	public static void saveScreenshot(final Node node, final String file, final int aWidth, final int aHeight) {

		final AnchorPane anchorPane = new AnchorPane();
		anchorPane.setMinSize(aWidth, aHeight);
		anchorPane.setMaxSize(aWidth, aHeight);
		anchorPane.setPrefSize(aWidth, aHeight);

		final ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(anchorPane);

		final JFXPanel fxPanel = new JFXPanel();
		Scene scene = new Scene(scrollPane);
		Resources.loadStyles(scene);
		fxPanel.setScene(scene);

		final JFrame frame = new JFrame();

		final Pane previousParentPane = (Pane) node.getParent();

		frame.setSize(new Dimension(128, 128));
		frame.setVisible(false);
		frame.add(fxPanel);

		anchorPane.getChildren().clear();
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		anchorPane.getChildren().add(node);

		anchorPane.layout();

		try {
			final SnapshotParameters snapshotParameters = new SnapshotParameters();
			snapshotParameters.setViewport(new Rectangle2D(0.0, 0.0, aWidth, aHeight));
			ImageIO.write(SwingFXUtils.fromFXImage(
					anchorPane.snapshot(snapshotParameters, new WritableImage(aWidth, aHeight)),
					new BufferedImage(aWidth, aHeight, BufferedImage.TYPE_INT_ARGB)), "png", new File(file));

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// Return the node back into it's previous parent
					previousParentPane.getChildren().clear();
					AnchorPane.setLeftAnchor(node, 0.0);
					AnchorPane.setRightAnchor(node, 0.0);
					AnchorPane.setTopAnchor(node, 0.0);
					AnchorPane.setBottomAnchor(node, 0.0);
					previousParentPane.getChildren().add(node);

					frame.dispose();
				}
			});
		}
	}
}
