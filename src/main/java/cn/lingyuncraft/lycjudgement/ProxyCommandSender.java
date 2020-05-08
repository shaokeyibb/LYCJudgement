package cn.lingyuncraft.lycjudgement;

import org.jetbrains.annotations.NotNull;

public abstract class ProxyCommandSender {
    public abstract void sendMessage(@NotNull String message);

    public abstract void kickPlayer(@NotNull String reason);

    public abstract @NotNull String getName();

    public abstract boolean eq(ProxyCommandSender sender);
}
