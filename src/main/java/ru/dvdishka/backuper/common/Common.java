package ru.dvdishka.backuper.common;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Common {

    public static Plugin plugin;
    public static Properties properties = new Properties();
    public static boolean isBackupRunning = false;
    static {
        try {
            properties.load(Common.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (Exception e) {
            Logger.getLogger().devWarn("Common", "Failed to load properties!");
            Logger.getLogger().devWarn("Common", e);        }
    }

    public static final int bStatsId = 17735;
    public static final List<String> downloadLinks = List.of("https://modrinth.com/plugin/backuper/versions#all-versions",
            "https://hangar.papermc.io/Collagen/Backuper");
    public static final List<String> downloadLinksName = List.of("Modrinth", "Hangar");
    public static URL getLatestVersionURL = null;
    public static boolean isUpdatedToLatest = true;
    static {
        try {
            getLatestVersionURL = new URL("https://hangar.papermc.io/api/v1/projects/Collagen/Backuper/latestrelease");
        } catch (MalformedURLException e) {
            Logger.getLogger().warn("Failed to check Backuper updates!");
            Logger.getLogger().devWarn("Common", e);
        }
    }

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    public static boolean isFolia = false;

    public static String getProperty(String property) {
        return properties.getProperty(property);
    }

    public static long getPathOrFileByteSize(File path) {

        if (!path.isDirectory()) {
            try {
                return Files.size(path.toPath());
            } catch (Exception e) {
                Logger.getLogger().warn("Something went wrong while trying to calculate backup size!");
                Logger.getLogger().devWarn("Common", e);
            }
        }

        long size = 0;

        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                size += getPathOrFileByteSize(file);
            }
        }

        return size;
    }

    public static void returnFailure(String message, CommandSender sender) {
        try {
            sender.sendMessage(ChatColor.RED + message);
        } catch (Exception ignored) {}
    }

    public static void returnSuccess(String message, CommandSender sender) {
        try {
            sender.sendMessage(ChatColor.GREEN + message);
        } catch (Exception ignored) {}
    }

    public static void returnSuccess(String message, @NotNull CommandSender sender, ChatColor color) {
        try {
            sender.sendMessage(color + message);
        } catch (Exception ignored) {}
    }

    public static void returnFailure(String message, @NotNull CommandSender sender, ChatColor color) {
        try {
            sender.sendMessage(color + message);
        } catch (Exception ignored) {}
    }

    public static void cancelButtonSound(CommandSender sender) {
        sender.playSound(Sound.sound(Sound.sound(Key.key("block.anvil.place"), Sound.Source.NEUTRAL, 50, 1)).build());
    }

    public static void normalButtonSound(CommandSender sender) {
        sender.playSound(Sound.sound(Sound.sound(Key.key("ui.button.click"), Sound.Source.NEUTRAL, 50, 1)).build());
    }
}
