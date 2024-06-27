package ru.dvdishka.backuper.handlers.commands.menu.copyToSftp;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import ru.dvdishka.backuper.Backuper;
import ru.dvdishka.backuper.backend.classes.LocalBackup;
import ru.dvdishka.backuper.backend.classes.SftpBackup;
import ru.dvdishka.backuper.backend.common.Scheduler;
import ru.dvdishka.backuper.backend.config.Config;
import ru.dvdishka.backuper.backend.tasks.sftp.SftpSendFileFolderTask;
import ru.dvdishka.backuper.backend.utils.SftpUtils;
import ru.dvdishka.backuper.backend.utils.Utils;
import ru.dvdishka.backuper.handlers.commands.Command;
import ru.dvdishka.backuper.handlers.commands.status.StatusCommand;

public class CopyToSftpCommand extends Command {

    public CopyToSftpCommand(CommandSender sender, CommandArguments arguments) {
        super(sender, arguments);
    }

    @Override
    public void execute() {

        if (!Config.getInstance().getLocalConfig().isEnabled()) {
            cancelSound();
            returnFailure("Local storage is disabled");
            return;
        }

        if (!Config.getInstance().getSftpConfig().isEnabled()) {
            cancelSound();
            returnFailure("SFTP storage is disabled");
            return;
        }

        LocalBackup localBackup = LocalBackup.getInstance((String) arguments.get("backupName"));

        if (localBackup == null) {
            cancelSound();
            returnFailure("Wrong backup name");
            return;
        }

        if (Backuper.isLocked()) {
            cancelSound();
            returnFailure("Blocked by another operation!");
            return;
        }

        for (SftpBackup sftpBackup : SftpBackup.getBackups()) {
            if (sftpBackup.getName().equals(localBackup.getName())) {
                cancelSound();
                returnFailure("Sftp storage already contains this backup");
                return;
            }
        }

        buttonSound();

        StatusCommand.sendTaskStartedMessage("CopyToSftp", sender);

        Scheduler.getScheduler().runAsync(Utils.plugin, () -> {

            String inProgressName = localBackup.getName() + " in progress";
            if (localBackup.getFileType().equals("(ZIP)")) {
                inProgressName += ".zip";
            }

            new SftpSendFileFolderTask(localBackup.getFile(),
                    SftpUtils.resolve(Config.getInstance().getSftpConfig().getBackupsFolder(), inProgressName),
                    false, true, true, sender).run();

            SftpUtils.renameFile(SftpUtils.resolve(Config.getInstance().getSftpConfig().getBackupsFolder(), inProgressName),
                    SftpUtils.resolve(Config.getInstance().getSftpConfig().getBackupsFolder(), localBackup.getFileName()),
                    sender);

            sendMessage("CopyToSftp task completed");
        });
    }
}
