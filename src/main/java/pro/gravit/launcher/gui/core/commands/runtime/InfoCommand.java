package pro.gravit.launcher.gui.core.commands.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import pro.gravit.launcher.gui.core.JavaFXApplication;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.JVMHelper;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends Command {

    private static final Logger logger =
            LoggerFactory.getLogger(InfoCommand.class);

    private final JavaFXApplication application;

    public InfoCommand(JavaFXApplication application) {
        this.application = application;
    }

    @Override
    public String getArgsDescription() {
        return "[]";
    }

    @Override
    public String getUsageDescription() {
        return "show javafx info";
    }

    @Override
    public void invoke(String... args) {
        Platform.runLater(() -> {
            logger.info("OS {} ARCH {} Java {}", JVMHelper.OS_TYPE.name(), JVMHelper.ARCH_TYPE.name(), JVMHelper.JVM_VERSION);
            logger.info("JavaFX version: {}", System.getProperty( "javafx.runtime.version"));
            {
                List<String> supportedFeatures = new ArrayList<>();
                List<String> unsupportedFeatures = new ArrayList<>();
                for (var e : ConditionalFeature.values()) {
                    if (Platform.isSupported(e)) {
                        supportedFeatures.add(e.name());
                    } else {
                        unsupportedFeatures.add(e.name());
                    }
                }
                logger.info("JavaFX supported features: [{}]", String.join(",", supportedFeatures));
                logger.info("JavaFX unsupported features: [{}]", String.join(",", unsupportedFeatures));
            }
            logger.info("Is accessibility active {}", Platform.isAccessibilityActive() ? "true" : "false");
        });
    }
}