package dev.efnilite.witp.api.gamemode;

import dev.efnilite.witp.player.ParkourPlayer;
import dev.efnilite.witp.player.ParkourSpectator;
import dev.efnilite.witp.player.ParkourUser;
import dev.efnilite.witp.util.Verbose;
import dev.efnilite.witp.util.inventory.InventoryBuilder;
import dev.efnilite.witp.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class DefaultGamemode implements Gamemode {

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.BARREL, "&c&lNormal").setLore("&7Play the game like normal").build();
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, InventoryBuilder builder) {
        try {
            player.closeInventory();
            if (user instanceof ParkourSpectator) {
                ParkourSpectator spectator = (ParkourSpectator) user;
                spectator.getWatching().removeSpectators(spectator);
            }
            ParkourPlayer.register(player);
        } catch (IOException ex) {
            ex.printStackTrace();
            Verbose.error("Error while trying to register player" + player.getName());
        }
    }
}
