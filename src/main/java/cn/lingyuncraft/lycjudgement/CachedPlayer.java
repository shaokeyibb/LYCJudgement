package cn.lingyuncraft.lycjudgement;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CachedPlayer extends ProxyCommandSender {
    public static final Cache<String, CachedPlayer> OFFLINE_PLAYERS = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final String name;
    private final UUID uniqueId;

    public CachedPlayer(String name, UUID uniqueId) {
        this.name = name;
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public void sendMessage(@NotNull String message) {
    }

    @Override
    public void kickPlayer(@NotNull String reason) {
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public boolean eq(ProxyCommandSender sender) {
        if (sender instanceof CachedPlayer) {
            return name.equals(((CachedPlayer) sender).name) && uniqueId.equals(((CachedPlayer) sender).uniqueId);
        }
        return false;
    }
}
