package cn.lingyuncraft.lycjudgement.bukkit;

import cn.lingyuncraft.lycjudgement.CachedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static cn.lingyuncraft.lycjudgement.LYCJudgementConfig.*;

@SuppressWarnings("unused")
public final class LYCJudgement extends JavaPlugin {

    private static LYCJudgement instance;


    @Override
    public void reloadConfig() {
        super.reloadConfig();

        ban_command = getConfig().getString("ban-command");
        vote_up_player_percent = getConfig().getDouble("vote-up-player-percent");
        player_is_invalid = getConfig().getString("lang.player-is-invalid");
        broadcast = getConfig().getString("lang.broadcast");
        ban_success = getConfig().getString("lang.ban-success");
        vote_success = getConfig().getString("lang.vote-success");
        player_is_not_being_voted = getConfig().getString("lang.player-is-not-being-voted");
        player_is_already_being_voted = getConfig().getString("lang.player-is-already-being-voted");
        already_voted = getConfig().getString("lang.already-voted");
        vote_timed_out = getConfig().getLong("vote-timed-out");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        reloadConfig();
        final PluginCommand command = Bukkit.getPluginCommand("judgement");
        assert command != null;
        BukkitCommandExec exec = new BukkitCommandExec();
        command.setExecutor(exec);
        command.setTabCompleter(exec);
        // see #4
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void on(@NotNull PlayerQuitEvent event) {
                CachedPlayer.OFFLINE_PLAYERS.put(
                        event.getPlayer().getName(),
                        new CachedPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId())
                );
            }
        }, this);
    }

    public static LYCJudgement getInstance() {
        return instance;
    }
}
