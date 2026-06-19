package pro.gravit.launcher.gui.core.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.utils.command.Command;

public class VersionCommand extends Command {

    private static final Logger logger =
            LoggerFactory.getLogger(VersionCommand.class);

    @Override
    public String getArgsDescription() {
        return "print version information";
    }

    @Override
    public String getUsageDescription() {
        return "[]";
    }

    @Override
    public void invoke(String... args) {
        logger.info("{}", JavaRuntimeModule.getLauncherInfo());
        logger.info("JDK Path: {}", System.getProperty("java.home", "UNKNOWN"));
    }
}