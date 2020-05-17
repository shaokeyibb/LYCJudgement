package cn.lingyuncraft.lycjudgement.bungee;

import cn.lingyuncraft.lycjudgement.CachedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import static cn.lingyuncraft.lycjudgement.LYCJudgementConfig.*;

@SuppressWarnings("unused")
public class LYCJudgement extends Plugin {
    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    public boolean reloadConfig() {
        File configFile = saveDefaultConfig();
        Configuration configuration;
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            configuration = provider.load(reader);
        } catch (Exception ioException) {
            getLogger().log(Level.SEVERE, "Failed to load config.yml");
            return false;
        }

        ban_command = configuration.getString("ban-command");
        vote_up_player_percent = configuration.getDouble("vote-up-player-percent");
        player_is_invalid = configuration.getString("lang.player-is-invalid");
        broadcast = configuration.getString("lang.broadcast");
        ban_success = configuration.getString("lang.ban-success");
        vote_success = configuration.getString("lang.vote-success");
        player_is_not_being_voted = configuration.getString("lang.player-is-not-being-voted");
        player_is_already_being_voted = configuration.getString("lang.player-is-already-being-voted");
        already_voted = configuration.getString("lang.already-voted");
        vote_timed_out = configuration.getLong("vote-timed-out");

        return true;
    }

    public File saveDefaultConfig() {
        final File folder = getDataFolder();
        final File config = new File(folder, "config.yml");
        if (!config.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
            try (FileOutputStream output = new FileOutputStream(config)) {
                try (InputStream stream = getResourceAsStream("config.yml")) {
                    byte[] buffer = new byte[2048];
                    while (true) {
                        int length = stream.read(buffer);
                        if (length == -1) break;
                        output.write(buffer, 0, length);
                    }
                }
            } catch (IOException ioException) {
                getLogger().log(Level.SEVERE, "Failed to save config file.", ioException);
            }
        }
        return config;
    }

    @Override
    public void onEnable() {
        if (!reloadConfig()) {
            getLogger().severe("Failed to load config. This plugin will not work.");
            return;
        }
        getProxy().getPluginManager().registerCommand(this, new BungeeCordCommandExec());
        // see #4
        getProxy().getPluginManager().registerListener(this, new Listener() {
            @EventHandler
            public void on(@NotNull PlayerDisconnectEvent event) {
                CachedPlayer.OFFLINE_PLAYERS.put(
                        event.getPlayer().getName(),
                        new CachedPlayer(event.getPlayer().getName(), event.getPlayer().getUniqueId())
                );
            }
        });
    }
}
