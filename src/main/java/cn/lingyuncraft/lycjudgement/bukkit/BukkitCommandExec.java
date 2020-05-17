package cn.lingyuncraft.lycjudgement.bukkit;

import cn.lingyuncraft.lycjudgement.CommandExec;
import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BukkitCommandExec extends CommandExec implements TabExecutor {
    @Override
    protected @NotNull Collection<String> getOnlineModifiablePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    protected @Nullable ProxyCommandSender getPlayer(@NotNull String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) return new BukkitPlayerCommandSender(player);
        return null;
    }

    @Override
    protected void dispatchCommand(@NotNull ProxyCommandSender sender, @NotNull String command) {

    }

    @Override
    protected @NotNull ProxyCommandSender getConsoleSender() {
        return BukkitConsoleSender.INSTANCE;
    }

    @Override
    protected int getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    protected void broadcastMessage(@NotNull String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        if (!(commandSender instanceof ConsoleCommandSender || commandSender instanceof Player)) {
            commandSender.sendMessage("§cOnly player/console can use this command.");
            return true;
        }
        ProxyCommandSender sender = commandSender instanceof ConsoleCommandSender
                ? BukkitConsoleSender.INSTANCE
                : new BukkitPlayerCommandSender((Player) commandSender);
        return onCommand(sender, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            @NotNull String[] args) {
        return onTabComplete(args);
    }
}
