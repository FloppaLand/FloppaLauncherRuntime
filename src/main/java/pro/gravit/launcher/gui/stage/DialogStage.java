package pro.gravit.launcher.gui.stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.DesignConstants;
import pro.gravit.launcher.gui.dialogs.AbstractDialog;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.core.impl.FxStage;

import java.io.IOException;

public class DialogStage extends FxStage {

    private static final Logger logger =
            LoggerFactory.getLogger(DialogStage.class);

    public DialogStage(JavaFXApplication application, String title, AbstractDialog dialog) throws Exception {
        super(application, application.newStage());
        stage.setTitle(title);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setOnCloseRequest(Event::consume);
        scene.setFill(Color.TRANSPARENT);
        // Icons
        try {
            Image icon = new Image(JavaFXApplication.getResourceURL("favicon.png").toString());
            stage.getIcons().add(icon);
        } catch (IOException e) {
            logger.error("", e);
        }
        setClipRadius(DesignConstants.SCENE_CLIP_RADIUS, DesignConstants.SCENE_CLIP_RADIUS);
        setScene(dialog, true);
        enableMouseDrag(dialog.getLayout());
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        if (bounds.getMaxX() == 0 || bounds.getMaxY() == 0) {
            bounds = screen.getBounds();
        }
        logger.info("Bounds: X: {} Y: {}", bounds.getMaxX(), bounds.getMaxY());
        LookupHelper.Point2D coords = dialog.getOutSceneCoords(bounds);
        stage.setX(coords.x);
        stage.setY(coords.y);
    }
}