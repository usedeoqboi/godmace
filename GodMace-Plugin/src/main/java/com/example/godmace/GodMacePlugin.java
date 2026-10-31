package com.example.godmace;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class GodMacePlugin extends JavaPlugin implements Listener {
    private NamespacedKey godMaceKey;

    @Override
    public void onEnable() {
        godMaceKey = new NamespacedKey(this, "god_mace");
        getServer().getPluginManager().registerEvents(this, this);
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

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String[] arguments = event.getMessage().substring(1).trim().split("\\s+");
        if (arguments.length != 3
                || !arguments[0].equalsIgnoreCase("give")
                || !arguments[2].equalsIgnoreCase("god_mace")) {
            return;
        }

        event.setCancelled(true);
        Player recipient = getServer().getPlayerExact(arguments[1]);
        if (recipient == null) {
            event.getPlayer().sendMessage(Component.text("That player is not online."));
            return;
        }

        recipient.getInventory().addItem(createGodMace()).values()
                .forEach(item -> recipient.getWorld().dropItemNaturally(recipient.getLocation(), item));
        event.getPlayer().sendMessage(Component.text("God Mace given to " + recipient.getName() + "."));
    }

    private boolean isGodMace(ItemStack item) {
        if (!item.hasItemMeta()) {
            return false;
        }

        Byte marker = item.getItemMeta().getPersistentDataContainer()
                .get(godMaceKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }
}
