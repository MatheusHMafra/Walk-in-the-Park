package dev.efnilite.ip.player;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.event.ParkourSpectateEvent;
import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.player.data.PreviousData;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.util.Util;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.vilib.util.Task;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Class for spectators of a Session.
 *
 * @author Efnilite
 */
public class ParkourSpectator extends ParkourUser {

    public ParkourPlayer closest;
    private final BukkitTask closestChecker;

    public ParkourSpectator(@NotNull Player player, @NotNull Session session, @Nullable PreviousData previousData) {
        super(player, previousData);

        this.session = session;
        this.closest = session.getPlayers().get(0);

        new ParkourSpectateEvent(this).call();

        teleport(closest.getLocation());
        sendTranslated("play.spectator.join");

        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);
        if (Util.isBedrockPlayer(player)) {  // bedrock has no spectator mode, so just make the player invisible
            player.setInvisible(true);
            player.setCollidable(false);
        }

        closestChecker = Task.create(IP.getPlugin())
                .async()
                .execute(() -> closest = session.getPlayers().stream()
                        .min(Comparator.comparing(other -> other.getLocation().distance(player.getLocation())))
                        .orElseThrow())
                .repeat(10)
                .run();
    }

    /**
     * Updates the spectator's action bar, scoreboard and checks distance.
     */
    public void update() {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Strings.colour(Locales.getString(player, "play.spectator.action_bar"))));
        player.setGameMode(GameMode.SPECTATOR);
        updateScoreboard(closest.generator);

        if (closest.player.getLocation().distanceSquared(player.getLocation()) < 10000) { // avoid sqrt
            return;
        }

        if (player.getGameMode() != GameMode.SPECTATOR) { // if player isn't in spectator or is a bedrock player
            teleport(closest.player.getLocation());
            return;
        }

        // if player is a spectator
        player.setSpectatorTarget(null);
        teleport(closest.player.getLocation());
        player.setSpectatorTarget(player.getSpectatorTarget());
    }

    /**
     * Stops the closest checker runnable.
     */
    public void unregister() {
        closestChecker.cancel();
        session.removeSpectators(this);
        player.setInvisible(false);
    }
}