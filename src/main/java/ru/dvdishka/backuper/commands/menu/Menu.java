package ru.dvdishka.backuper.commands.menu;

import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import ru.dvdishka.backuper.commands.common.CommandInterface;
import ru.dvdishka.backuper.common.Common;

import java.time.LocalDateTime;

public class Menu implements CommandInterface {

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        String backupName = ((String) args.get("backupName"));
        try {
            LocalDateTime.parse(backupName, Common.dateTimeFormatter);
        } catch (Exception e) {
            returnFailure("Wrong backup name!", sender);
            return;
        }

        long backupSize = Common.getBackupMBSizeByName(backupName);
        String zipOrFolder = Common.zipOrFolderBackupByName(backupName);

        Component message = Component.text("");

        message = message
                .append(Component.text("---------------")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(0xE3A013)))
                .appendNewline();

        message = message
                .append(Component.text(backupName)
                        .hoverEvent(HoverEvent.showText(Component.text(zipOrFolder + " " + backupSize + " MB"))))
                .appendNewline();

        message = message
                .append(Component.text("[SET]")
                        .hoverEvent(HoverEvent.showText(Component.text("SET IT UP ON-THE SERVER")))
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(0x5DB1)))
                .appendSpace();

        message = message
                .append(Component.text("[DELETE]")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(0xB02100)))
                .appendNewline();

        message = message
                .append(Component.text("---------------")
                        .decorate(TextDecoration.BOLD)
                        .color(TextColor.color(0xE3A013)));

        sender.sendMessage(message);
    }
}