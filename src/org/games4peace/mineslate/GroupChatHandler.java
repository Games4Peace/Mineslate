package org.games4peace.mineslate;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.games4peace.mineslate.translation.Translator;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupChatHandler implements Listener {
    private final String GLOBAL_LISTEN_PERMISSION = "MineSlate.globalListen";

    private Translator _translator;

    public GroupChatHandler(Translator translator) {
        _translator = translator;
    }

    // Return the chat group of "player"
    private int getChatGroup(Player player) {
        int chatGroup = 0;
        Objective objective = player.getScoreboard().getObjective("ms_chatGroup");

        // If the objective exists and is set for "player"
        if(objective != null) {
            Score subgroup = objective.getScore(player.getName());
            if(subgroup.isScoreSet()) {
                chatGroup = subgroup.getScore();
            }
        }

        return chatGroup;
    }

    // Return a subset of "possibleRecipients"
    //  filtering only the players in the same chat groups as "indexPlayer"
    //  or players who have the "GLOBAL_LISTEN_PERMISSION" permission
    private Set<Player> filterChatGroup(Player indexPlayer, Set<Player> possibleRecipients) {
        int indexSubgroup = getChatGroup(indexPlayer);

        return possibleRecipients.stream()
                .filter(player ->
                    (player.hasPermission(GLOBAL_LISTEN_PERMISSION) || getChatGroup(player) == indexSubgroup))
                .collect(Collectors.toSet());
    }

    private void sendMessage(String msg, String initiatingPlayerName, Player receivingPlayer) {
        receivingPlayer.sendRawMessage(String.format("<%s> %s", initiatingPlayerName, msg));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        // Cancels the event to prevent broadcast
        event.setCancelled(true);

        Player initiatingPlayer = event.getPlayer();
        String initiatingPlayerName = initiatingPlayer.getDisplayName();
        String msg = event.getMessage();

        sendMessage(msg, initiatingPlayerName, initiatingPlayer);

        Set<Player> relevantRecipients = event.getRecipients();
        relevantRecipients.remove(initiatingPlayer);
        relevantRecipients = filterChatGroup(initiatingPlayer, relevantRecipients);

        for (Player receivingPlayer : relevantRecipients) {
            String translatedMsg = msg;
            if(receivingPlayer != initiatingPlayer) {
                translatedMsg = _translator.translate(msg, receivingPlayer.getDisplayName());
            }

            sendMessage(translatedMsg, initiatingPlayerName, receivingPlayer);
        }
    }
}
