package cn.lingyuncraft.lycjudgement.bungee;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import net.md_5.bungee.api.CommandSender;

public abstract class BungeeCordProxyCommandSender extends ProxyCommandSender {
    public abstract CommandSender getSender();
}
