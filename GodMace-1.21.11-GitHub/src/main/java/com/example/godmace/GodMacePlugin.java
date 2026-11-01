package com.example.godmace;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class GodMacePlugin extends JavaPlugin implements Listener {
    private NamespacedKey itemKey;

    @Override
    public void onEnable() {
        itemKey = new NamespacedKey(this, "god_mace");
        getServer().getPluginManager().registerEvents(this, this);
        getServer().addRecipe(createRecipe());
    }

    private ShapedRecipe createRecipe() {
        ItemStack result = new ItemStack(Material.MACE);
        ItemMeta meta = result.getItemMeta();
        meta.displayName(Component.text("God Mace"));
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
        result.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(itemKey, result);
        recipe.shape("NSN", "HMH", "NSN");
        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        recipe.setIngredient('S', Material.SCULK_CATALYST);
        recipe.setIngredient('H', Material.HEAVY_CORE);
        recipe.setIngredient('M', Material.MACE);
        return recipe;
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
                .get(itemKey, PersistentDataType.BYTE);
        return marker != null && marker == (byte) 1;
    }
}
