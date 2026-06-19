package pro.gravit.launcher.gui.core.commands.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.launcher.gui.core.impl.FxStage;
import pro.gravit.utils.command.Command;

public class GetSizeCommand extends Command {

    private static final Logger logger =
            LoggerFactory.getLogger(GetSizeCommand.class);

    private final JavaFXApplication application;

    public GetSizeCommand(JavaFXApplication application) {
        this.application = application;
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return null;
    }

    @Override
    public void invoke(String... args) throws Exception {
        FxStage fxStage = application.getMainStage();
        var stage = fxStage.getStage();
        logger.info("Stage: H: {} W: {}", stage.getHeight(), stage.getWidth());
        var scene = stage.getScene();
        logger.info("Scene: H: {} W: {}", scene.getHeight(), scene.getWidth());
        var stackPane = (Pane)scene.getRoot();
        logger.info("StackPane: H: {} W: {}", stackPane.getHeight(), stackPane.getWidth());
        var layout = (Pane)stackPane.getChildren().get(0);
        logger.info("Layout: H: {} W: {}", layout.getHeight(), layout.getWidth());
    }
}