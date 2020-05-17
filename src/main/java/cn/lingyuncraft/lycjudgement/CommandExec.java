package cn.lingyuncraft.lycjudgement;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CommandExec {
    private static class VoteStatus {
        final long create = System.currentTimeMillis();
        String reason;
        final Set<String> voted = new HashSet<>();

        final int counter() {
            return voted.size();
        }
    }

    final Map<String, VoteStatus> voteStatus = new HashMap<>();

    public static @NotNull List<String> filter(
            String[] args,
            Collection<String> completes
    ) {
        if (completes == null || completes.isEmpty()) return Collections.emptyList();
        if (args == null || args.length == 0) {
            if (completes instanceof List) {
                return (List<String>) completes;
            }
            return new LinkedList<>(completes);
        }
        String last = args[args.length - 1].toLowerCase();
        return completes.stream().filter(it -> it.toLowerCase().startsWith(last))
                .sorted(String::compareTo)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public @Nullable List<String> onTabComplete(@NotNull String[] args) {
        switch (args.length) {
            case 0:
            case 1: {
                return filter(args, Arrays.asList("kick", "vote"));
            }
            case 2: {
                String first = args[0];
                if (first.equals("kick")) {
                    final Collection<String> names = getOnlineModifiablePlayerNames();
                    names.addAll(CachedPlayer.OFFLINE_PLAYERS.asMap().keySet());
                    return filter(args, names);
                } else if (first.equals("vote")) {
                    return filter(args, voteStatus.keySet());
                }
            }
            default:
                return Collections.emptyList();
        }
    }

    protected abstract @NotNull Collection<String> getOnlineModifiablePlayerNames();

    protected abstract @Nullable ProxyCommandSender getPlayer(@NotNull String name);

    public boolean onCommand(
            @NotNull ProxyCommandSender sender,
            @NotNull String[] args
    ) {
        if (args.length >= 2) {
            {
                long current = System.currentTimeMillis();
                voteStatus.entrySet().removeIf(entry -> current - entry.getValue().create > LYCJudgementConfig.vote_timed_out);
            }
            if (args[0].equalsIgnoreCase("kick")) {
                if (args.length == 3) {
                    ProxyCommandSender player = findPlayerOrOffline(args[1]);
                    if (player != null) {
                        final VoteStatus status = this.voteStatus.get(player.getName());
                        if (status != null) {
                            sender.sendMessage(LYCJudgementConfig.player_is_already_being_voted);
                        } else {
                            if (player.eq(sender)) {
                                player.kickPlayer(LYCJudgementConfig.ban_success.replace("{player}", args[1]));
                                return true;
                            }
                            broadcastMessage(
                                    LYCJudgementConfig.broadcast
                                            .replace("{player}", args[1])
                                            .replace("{reason}", args[2])
                            );
                            sender.sendMessage(
                                    LYCJudgementConfig.vote_success
                                            .replace("{current}", "1")
                                            .replace("{max}", String.valueOf(
                                                    getOnlinePlayers()
                                            ))
                            );
                            VoteStatus status0 = new VoteStatus();
                            status0.voted.add(sender.getName());
                            status0.reason = args[2];
                            voteStatus.put(player.getName(), status0);
                        }
                        return true;
                    } else {
                        sender.sendMessage(LYCJudgementConfig.player_is_invalid);
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
                                    LYCJudgementConfig.vote_success
                                            .replace("{current}", String.valueOf(status.counter()))
                                            .replace("{max}", String.valueOf(
                                                    getOnlinePlayers()
                                            ))
                            );
                            if (status.counter() >= getOnlinePlayers() * LYCJudgementConfig.vote_up_player_percent) {
                                dispatchCommand(getConsoleSender(),
                                        LYCJudgementConfig.ban_command
                                                .replace("{player}", args[1])
                                                .replace("{reason}", status.reason)
                                );
                                voteStatus.remove(args[1]);
                                broadcastMessage(LYCJudgementConfig.ban_success.replace("{player}", args[1]));
                            }
                        } else {
                            sender.sendMessage(LYCJudgementConfig.already_voted);
                        }
                    } else {
                        sender.sendMessage(LYCJudgementConfig.player_is_not_being_voted);
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

    private ProxyCommandSender findPlayerOrOffline(String name) {
        ProxyCommandSender result = getPlayer(name);
        if (result == null) {
            return CachedPlayer.OFFLINE_PLAYERS.getIfPresent(name);
        }
        return result;
    }

    protected abstract void dispatchCommand(
            @NotNull ProxyCommandSender sender,
            @NotNull String command
    );

    protected abstract @NotNull ProxyCommandSender getConsoleSender();

    protected abstract int getOnlinePlayers();

    protected abstract void broadcastMessage(@NotNull String message);
}
