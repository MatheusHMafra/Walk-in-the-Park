package dev.efnilite.witp.internal.gamemode;

import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.witp.IP;
import dev.efnilite.witp.api.Gamemode;
import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The default parkour gamemode
 */
public class DefaultGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "default";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return IP.getConfiguration().getFromItemData(locale, "gamemodes.default");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu previousMenu) {
        player.closeInventory();

        if (user instanceof ParkourPlayer) {
            ParkourPlayer pp = (ParkourPlayer) user;
            if (pp.getGenerator() instanceof DefaultGenerator) {
                return;
            }
        }

        ParkourPlayer pp = ParkourPlayer.register(player);
        IP.getDivider().generate(pp);
    }
}
