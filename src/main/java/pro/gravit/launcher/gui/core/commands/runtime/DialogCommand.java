package pro.gravit.launcher.gui.core.commands.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.gravit.launcher.gui.core.impl.MessageManager;
import pro.gravit.utils.command.Command;

public class DialogCommand extends Command {

    private static final Logger logger =
            LoggerFactory.getLogger(DialogCommand.class);

    private final MessageManager messageManager;

    public DialogCommand(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Override
    public String getArgsDescription() {
        return "[header] [message] (dialog/dialogApply/dialogTextInput) (launcher/native/default)";
    }

    @Override
    public String getUsageDescription() {
        return "show test dialog";
    }

    @Override
    public void invoke(String... args) throws Exception {
        verifyArgs(args, 3);
        boolean isLauncher = args.length <= 3 || args[3].equals("launcher");
        String header = args[0];
        String message = args[1];
        String dialogType = args[2];
        switch (dialogType) {
            case "dialog" -> messageManager.showDialog(header, message,
                                                       () -> logger.info("Dialog apply callback"), () -> logger.info("Dialog cancel callback"), isLauncher);
            case "dialogApply" -> messageManager.showApplyDialog(header, message,
                                                                 () -> logger.info("Dialog apply callback"), () -> logger.info("Dialog deny callback"), () -> logger.info("Dialog close callback"), isLauncher);
        }
    }
}