package cn.lingyuncraft.lycjudgement.bungee;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

public class BungeeCordProxySender extends BungeeCordProxyCommandSender {
    private final CommandSender sender;

    public BungeeCordProxySender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void kickPlayer(@NotNull String reason) {
        if (sender instanceof ProxiedPlayer) {
            ((ProxiedPlayer) sender).disconnect(TextComponent.fromLegacyText(reason));
        }
    }

    @Override
    public @NotNull String getName() {
        return sender.getName();
    }

    @Override
    public boolean eq(ProxyCommandSender sender) {
        if (sender instanceof BungeeCordProxyCommandSender) {
            return ((BungeeCordProxyCommandSender) sender).getSender().equals(this.sender);
        }
        return false;
    }
}
