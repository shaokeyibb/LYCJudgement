package cn.lingyuncraft.lycjudgement;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommandExec implements CommandExecutor {
    private static class VoteStatus {
        final long create = System.currentTimeMillis();
        String reason;
        final Set<String> voted = new HashSet<>();

        final int counter() {
            return voted.size();
        }
    }

    final Map<String, VoteStatus> voteStatus = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            {
                long current = System.currentTimeMillis();
                voteStatus.entrySet().removeIf(entry -> current - entry.getValue().create > LYCJudgement.vote_timed_out);
            }
            if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 3) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        final VoteStatus status = this.voteStatus.get(player.getName());
                        if (status != null) {
                            sender.sendMessage(LYCJudgement.player_is_already_being_voted);
                        } else {
                            Bukkit.broadcastMessage(
                                    LYCJudgement.broadcast
                                            .replace("{player}", args[1])
                                            .replace("{reason}", args[2])
                            );
                            sender.sendMessage(
                                    LYCJudgement.vote_success
                                            .replace("{current}", "1")
                                            .replace("{max}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                            );
                            VoteStatus status0 = new VoteStatus();
                            status0.voted.add(sender.getName());
                            status0.reason = args[2];
                            voteStatus.put(player.getName(), status0);
                        }
                        return true;
                    } else {
                        sender.sendMessage(LYCJudgement.player_is_invalid);
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("vote")) {
                if (args.length == 2) {
                    final VoteStatus status = this.voteStatus.get(args[1]);
                    if (status != null) {
                        if (status.voted.add(sender.getName())) {
                            sender.sendMessage(
                                    LYCJudgement.vote_success
                                            .replace("{current}", String.valueOf(status.counter())).
                                            replace("{max}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                            );
                            if (status.counter() >= Bukkit.getOnlinePlayers().size() * LYCJudgement.vote_up_player_percent) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        LYCJudgement.ban_command
                                                .replace("{player}", args[1])
                                                .replace("{reason}", status.reason)
                                );
                                voteStatus.remove(args[1]);
                                Bukkit.broadcastMessage(LYCJudgement.ban_success.replace("{player}", args[1]));
                            }
                        } else {
                            sender.sendMessage(LYCJudgement.already_voted);
                        }
                    } else {
                        sender.sendMessage(LYCJudgement.player_is_not_being_voted);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
