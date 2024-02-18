package ru.dvdishka.backuper.handlers.commands.menu.delete;

import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;
import ru.dvdishka.backuper.handlers.commands.Command;
import ru.dvdishka.backuper.back.common.Scheduler;
import ru.dvdishka.backuper.back.common.Backup;
import ru.dvdishka.backuper.back.common.Common;
import ru.dvdishka.backuper.back.common.Logger;

import java.io.File;
import java.util.Objects;

public class Delete extends Command {

    private boolean isDeleteSuccessful = true;

    public Delete(CommandSender sender, CommandArguments arguments) {
        super(sender, arguments);
    }

    @Override
    public void execute() {

        String backupName = (String) arguments.get("backupName");

        if (!Backup.checkBackupExistenceByName(backupName)) {
            cancelButtonSound();
            returnFailure("Backup does not exist!");
            return;
        }

        normalButtonSound();

        Backup backup = new Backup(backupName);

        if (backup.isLocked() || Backup.isBackupBusy) {
            cancelButtonSound();
            returnFailure("Backup is blocked by another operation!");
            return;
        }

        File backupFile = backup.getFile();

        backup.lock();

        if (backup.zipOrFolder().equals("(ZIP)")) {

            Scheduler.getScheduler().runAsync(Common.plugin, () -> {
                if (backupFile.delete()) {
                    returnSuccess("Backup has been deleted successfully");
                } else {
                    returnFailure("Backup " + backupName + " can not be deleted!");
                }
                backup.unlock();
            });

        } else {

            Scheduler.getScheduler().runAsync(Common.plugin, () -> {
                deleteDir(backupFile);
                if (!isDeleteSuccessful) {
                    returnFailure("Delete task has been finished with an exception!");
                } else {
                    returnSuccess("Backup has been deleted successfully");
                }
                backup.unlock();
            });
        }
    }

    public void deleteDir(File dir) {

        if (dir != null && dir.listFiles() != null) {

            for (File file : Objects.requireNonNull(dir.listFiles())) {

                if (file.isDirectory()) {

                    deleteDir(file);

                } else {

                    if (!file.delete()) {

                        isDeleteSuccessful = false;
                        Logger.getLogger().devWarn(this, "Can not delete file " + file.getName());
                    }
                }
            }
            if (!dir.delete()) {

                isDeleteSuccessful = false;
                Logger.getLogger().devWarn(this, "Can not delete directory " + dir.getName());
            }
        }
    }
}
