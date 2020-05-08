package cn.lingyuncraft.lycjudgement.bukkit;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class BukkitConsoleSender extends BukkitProxyCommandSender {
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    public static final BukkitConsoleSender INSTANCE = new BukkitConsoleSender();

    @Override
    public @NotNull CommandSender getSender() {
        return console;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        console.sendMessage(message);
    }

    @Override
    public void kickPlayer(@NotNull String reason) {
    }

    @Override
    public @NotNull String getName() {
        return console.getName();
    }

    @Override
    public boolean eq(ProxyCommandSender sender) {
        return sender instanceof BukkitConsoleSender;
    }
}
