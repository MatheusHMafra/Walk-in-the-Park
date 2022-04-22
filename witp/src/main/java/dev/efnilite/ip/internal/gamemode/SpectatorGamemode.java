package dev.efnilite.ip.internal.gamemode;

import dev.efnilite.ip.IP;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.player.ParkourUser;
import dev.efnilite.vilib.inventory.Menu;
import dev.efnilite.vilib.inventory.PagedMenu;
import dev.efnilite.vilib.inventory.animation.SplitMiddleInAnimation;
import dev.efnilite.vilib.inventory.item.Item;
import dev.efnilite.vilib.inventory.item.MenuItem;
import dev.efnilite.vilib.util.SkullSetter;
import dev.efnilite.vilib.util.Unicodes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SpectatorGamemode implements Gamemode {

    @Override
    public @NotNull String getName() {
        return "spectator";
    }

    @Override
    public @NotNull Item getItem(String locale) {
        return IP.getConfiguration().getFromItemData(locale, "gamemodes.spectator");
    }

    @Override
    public void handleItemClick(Player player, ParkourUser user, Menu previousMenu) {
        PagedMenu spectator = new PagedMenu(4, "<white>Select a player");

        List<MenuItem> display = new ArrayList<>();

        for (ParkourPlayer pp : ParkourUser.getActivePlayers()) {
            Player bukkitPlayer = pp.getPlayer();
            if (bukkitPlayer.getUniqueId() == player.getUniqueId()) {
                continue;
            }

            Item item = IP.getConfiguration().getFromItemData(user.getLocale(),
                    "gamemodes.spectator-head", bukkitPlayer.getName());

            // Player head gathering
            item.material(Material.PLAYER_HEAD);
            ItemStack stack = item.build();
            stack.setType(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) stack.getItemMeta();
            if (meta == null) {
                continue;
            }
            SkullSetter.setPlayerHead(bukkitPlayer, meta);
            item.meta(meta);

            display.add(item.click((event) -> {
                if (ParkourUser.getActivePlayers().contains(pp)) {
                    ParkourSpectator.spectateSession(player, pp.getSession());
                }
            }));
        }

        spectator
                .displayRows(0, 1)
                .addToDisplay(display)

                .nextPage(35, new Item(Material.LIME_DYE, "<#0DCB07><bold>" + Unicodes.DOUBLE_ARROW_RIGHT) // next page
                        .click((event) -> spectator.page(1)))

                .prevPage(27, new Item(Material.RED_DYE, "<#DE1F1F><bold>" + Unicodes.DOUBLE_ARROW_LEFT) // previous page
                        .click((event) -> spectator.page(-1)))

                .item(31, IP.getConfiguration().getFromItemData(user.getLocale(), "general.close")
                        .click((event) -> player.closeInventory()))

                .fillBackground(Material.GRAY_STAINED_GLASS_PANE)
                .animation(new SplitMiddleInAnimation())
                .open(player);
    }
}