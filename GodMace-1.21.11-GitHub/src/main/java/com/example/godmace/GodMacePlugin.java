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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public final class GodMacePlugin extends JavaPlugin implements Listener {
    private NamespacedKey itemKey;

    @Override
    public void onEnable() {
        itemKey = new NamespacedKey(this, "god_mace");
        getServer().getPluginManager().registerEvents(this, this);
        if (!getServer().addRecipe(createRecipe())) {
            getLogger().warning("Could not register the God Mace recipe.");
        }
        getServer().getOnlinePlayers().forEach(this::unlockRecipe);
        registerCommand("give", new GodMaceGiveCommand());
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        unlockRecipe(event.getPlayer());
    }

    private void unlockRecipe(Player player) {
        player.discoverRecipe(itemKey);
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

    private final class GodMaceGiveCommand implements BasicCommand {
        @Override
        public void execute(CommandSourceStack source, String[] arguments) {
            if (arguments.length == 2 && arguments[1].equalsIgnoreCase("god_mace")) {
                Player recipient = Bukkit.getPlayerExact(arguments[0]);
                if (recipient == null) {
                    source.getSender().sendMessage(Component.text("That player is not online."));
                    return;
                }

                Bukkit.getOnlinePlayers().forEach(GodMacePlugin.this::unlockRecipe);
                recipient.getInventory().addItem(createGodMace()).values()
                        .forEach(item -> recipient.getWorld().dropItemNaturally(recipient.getLocation(), item));
                source.getSender().sendMessage(Component.text("God Mace given to " + recipient.getName() + "."));
                return;
            }

            String vanillaCommand = "minecraft:give";
            if (arguments.length > 0) {
                vanillaCommand += " " + String.join(" ", arguments);
            }
            Bukkit.dispatchCommand(source.getSender(), vanillaCommand);
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

    private ItemStack createGodMace() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();
        meta.displayName(Component.text("God Mace"));
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.BYTE, (byte) 1);
        mace.setItemMeta(meta);
        return mace;
    }
}
