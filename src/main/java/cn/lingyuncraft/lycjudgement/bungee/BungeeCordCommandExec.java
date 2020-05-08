package cn.lingyuncraft.lycjudgement.bungee;

import cn.lingyuncraft.lycjudgement.CommandExec;
import cn.lingyuncraft.lycjudgement.ProxyCommandSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BungeeCordCommandExec extends Command implements TabExecutor {
    public BungeeCordCommandExec() {
        super("judgement", "judgement.use", "ju");
    }

    private static final CommandExec exec = new CommandExec() {
        @Override
        protected @Nullable ProxyCommandSender getPlayer(@NotNull String name) {
            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);
            if (proxiedPlayer != null) return new BungeeCordProxySender(proxiedPlayer);
            return null;
        }

        @Override
        protected void dispatchCommand(@NotNull ProxyCommandSender sender, @NotNull String command) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(
                    ((BungeeCordConsoleSender) sender).getSender(), command
            );
        }

        @Override
        protected @NotNull ProxyCommandSender getConsoleSender() {
            return BungeeCordConsoleSender.INSTANCE;
        }

        @Override
        protected int getOnlinePlayers() {
            return ProxyServer.getInstance().getPlayers().size();
        }

        @Override
        protected void broadcastMessage(@NotNull String message) {
            ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message));
        }
    };

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        BungeeCordProxyCommandSender sender;
        if (commandSender.equals(ProxyServer.getInstance().getConsole())) {
            sender = BungeeCordConsoleSender.INSTANCE;
        } else {
            sender = new BungeeCordProxySender(commandSender);
        }
        if (!exec.onCommand(sender, args)) {
            commandSender.sendMessage(new TextComponent("/judgement kick 玩家名 理由"));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        final List<String> list = exec.onTabComplete(args);
        if (list == null) {
            return CommandExec.filter(args,
                    ProxyServer.getInstance().getPlayers().stream()
                            .map(CommandSender::getName)
                            .collect(Collectors.toCollection(LinkedList::new))
            );
        }
        return list;
    }
}
