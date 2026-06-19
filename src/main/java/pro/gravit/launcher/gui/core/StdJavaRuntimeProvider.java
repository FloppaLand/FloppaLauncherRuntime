package pro.gravit.launcher.gui.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.application.Application;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;
import pro.gravit.launcher.runtime.utils.LauncherUpdater;
import pro.gravit.utils.helper.IOHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class StdJavaRuntimeProvider implements RuntimeProvider {

    private static final Logger logger =
            LoggerFactory.getLogger(StdJavaRuntimeProvider.class);

    public static volatile Path updatePath;
    private static final AtomicReference<StdJavaRuntimeProvider> INSTANCE = new AtomicReference<>();

    public StdJavaRuntimeProvider() {
        INSTANCE.set(this);
    }

    public static StdJavaRuntimeProvider getInstance() {
        return INSTANCE.get();
    }

    public JavaFXApplication getApplication() {
        return JavaFXApplication.getInstance();
    }

    @Override
    public void run(String[] args) {
        logger.debug("Start JavaFX Application");
        Application.launch(JavaFXApplication.class, args);
        logger.debug("Post Application.launch method invoked");
        if (updatePath != null) {
            LauncherUpdater.nothing();
            LauncherEngine.beforeExit(0);
            Path target = IOHelper.getCodeSource(LauncherUpdater.class);
            try {
                try (InputStream input = IOHelper.newInput(updatePath)) {
                    try (OutputStream output = IOHelper.newOutput(target)) {
                        IOHelper.transfer(input, output);
                    }
                }
                Files.deleteIfExists(updatePath);
            } catch (IOException e) {
                logger.error("", e);
                LauncherEngine.forceExit(-109);
            }
            LauncherUpdater.restart();
        }
    }

    @Override
    public void preLoad() {
    }

    public void registerPrivateCommands() {
        JavaFXApplication application = JavaFXApplication.getInstance();
        if (application != null) {
            application.registerPrivateCommands();
        }
    }

    @Override
    public void init(boolean clientInstance) {
    }
}