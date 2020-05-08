package cn.lingyuncraft.lycjudgement.bukkit;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitPlayerCommandSender extends BukkitProxyCommandSender {
    private final Player player;

    @Override
    public @NotNull CommandSender getSender() {
        return player;
    }

    public BukkitPlayerCommandSender(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        player.sendMessage(message);
    }

    @Override
    public void kickPlayer(@NotNull String reason) {
        player.kickPlayer(reason);
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public boolean eq(ProxyCommandSender sender) {
        if (sender instanceof BukkitPlayerCommandSender) {
            return ((BukkitPlayerCommandSender) sender).player.equals(player);
        }
        return false;
    }
}
