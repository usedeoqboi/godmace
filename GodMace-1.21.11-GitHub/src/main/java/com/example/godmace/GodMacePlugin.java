package com.example.godmace;

import net.kyori.adventure.text.Component;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class GodMacePlugin extends JavaPlugin implements Listener {
    private NamespacedKey godMaceKey;

    @Override
    public void onEnable() {
        godMaceKey = new NamespacedKey(this, "god_mace");
        getServer().getPluginManager().registerEvents(this, this);
        registerCommand("give", new GodMaceGiveCommand());
    }

    /** Creates a normal mace marked as this plugin's God Mace. */
    public ItemStack createGodMace() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();
        meta.displayName(Component.text("God Mace"));
        meta.getPersistentDataContainer().set(godMaceKey, PersistentDataType.BYTE, (byte) 1);
        mace.setItemMeta(meta);
        return mace;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Entity causingEntity = event.getDamageSource().getCausingEntity();
        Entity directEntity = event.getDamageSource().getDirectEntity();

        if (!(causingEntity instanceof Player attacker) || directEntity != attacker) {
            return;
        }

        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        if (weapon.getType() != Material.MACE || !isGodMace(weapon)) {
            return;
        }

        event.getPlayer().kick(Component.text("You were killed by the God Mace!"));
    }

    private boolean isGodMace(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        Byte marker = item.getItemMeta().getPersistentDataContainer()
                .get(godMaceKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }

    private final class GodMaceGiveCommand implements BasicCommand {
        @Override
        public void execute(CommandSourceStack source, String[] arguments) {
            if (arguments.length == 2 && arguments[1].equalsIgnoreCase("god_mace")) {
                Player recipient = getServer().getPlayerExact(arguments[0]);
                if (recipient == null) {
                    source.getSender().sendMessage(Component.text("That player is not online."));
                    return;
                }

                recipient.getInventory().addItem(createGodMace()).values()
                        .forEach(item -> recipient.getWorld().dropItemNaturally(recipient.getLocation(), item));
                source.getSender().sendMessage(Component.text("God Mace given to " + recipient.getName() + "."));
                return;
            }

            String vanillaGive = "minecraft:give";
            if (arguments.length > 0) {
                vanillaGive += " " + String.join(" ", arguments);
            }
            Bukkit.dispatchCommand(source.getSender(), vanillaGive);
        }

        @Override
        public Collection<String> suggest(CommandSourceStack source, String[] arguments) {
            if (arguments.length <= 1) {
                String input = arguments.length == 0 ? "" : arguments[0].toLowerCase(Locale.ROOT);
                return Stream.concat(
                                Stream.of("@a", "@e", "@p", "@s"),
                                Bukkit.getOnlinePlayers().stream().map(Player::getName))
                        .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(input))
                        .toList();
            }

            if (arguments.length == 2) {
                String input = arguments[1].toLowerCase(Locale.ROOT);
                return Stream.concat(
                                Stream.of("god_mace"),
                                Stream.of(Material.values())
                                        .filter(Material::isItem)
                                        .map(material -> material.getKey().toString()))
                        .filter(item -> item.toLowerCase(Locale.ROOT).startsWith(input))
                        .toList();
            }

            return List.of();
        }
    }
}
