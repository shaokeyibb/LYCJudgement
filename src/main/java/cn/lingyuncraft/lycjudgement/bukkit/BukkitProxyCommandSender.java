package cn.lingyuncraft.lycjudgement.bukkit;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitProxyCommandSender extends ProxyCommandSender {
    public abstract @NotNull CommandSender getSender();
}
