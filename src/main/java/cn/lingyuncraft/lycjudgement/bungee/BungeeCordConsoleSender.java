package cn.lingyuncraft.lycjudgement.bungee;

import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class BungeeCordConsoleSender extends BungeeCordProxyCommandSender {
    public static final BungeeCordConsoleSender INSTANCE = new BungeeCordConsoleSender();
    private static final CommandSender console = ProxyServer.getInstance().getConsole();

    @Override
    public CommandSender getSender() {
        return console;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        console.sendMessage(TextComponent.fromLegacyText(message));
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
        return sender instanceof BungeeCordProxyCommandSender;
    }
}
