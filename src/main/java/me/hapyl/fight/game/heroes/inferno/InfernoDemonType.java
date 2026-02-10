package me.hapyl.fight.game.heroes.inferno;

import me.hapyl.eterna.module.component.ComponentList;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.inferno.DemonSplitTalent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public enum InfernoDemonType implements Named {
    
    QUAZII(
            "Quazii",
            "ⓆⓊⒶⓏⒾⒾ",
            me.hapyl.fight.game.color.Color.AQUA,
            Entities.WITHER_SKELETON,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchantment.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHeadUrl("16ca145ba435b375f763ff53b4ce04b2a0c873e8ff547e8b14b392fde6fbfd94").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .boots(Material.DIAMOND_BOOTS)
                     .build()
    ),
    
    TYPHOEUS(
            "Typhoeus",
            "ⓉⓎⓅⒽⓄⒺⓊⓈ",
            me.hapyl.fight.game.color.Color.RED,
            Entities.ZOMBIFIED_PIGLIN,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.BLAZE_ROD).addEnchant(Enchantment.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHeadUrl("e2f29945aa53cd95a0978a62ef1a8c1978803395a8ad5c0921d9cbe5e196bb8b").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .leggings(ItemBuilder.leatherPants(org.bukkit.Color.fromRGB(96, 0, 0)).asIcon())
                     .boots(Material.IRON_BOOTS)
                     .build()
    );
    
    private final String name;
    private final String demonName;
    private final Color demonColor;
    private final Entities<? extends LivingEntity> type;
    private final Equipment equipment;
    
    InfernoDemonType(@Nonnull String name, @Nonnull String demonName, @Nonnull Color demonColor, @Nonnull Entities<? extends LivingEntity> type, @Nonnull Equipment equipment) {
        this.name = name;
        this.demonName = demonName;
        this.demonColor = demonColor;
        this.type = type;
        this.equipment = equipment;
    }
    
    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }
    
    @Nonnull
    public InfernoDemon createDemon(@Nonnull DemonSplitTalent talent, @Nonnull GamePlayer player) {
        return new InfernoDemon(player, this, talent.getDuration(), create(player, type));
    }
    
    private LivingGameEntity create(GamePlayer player, Entities<? extends LivingEntity> type) {
        return player.spawnAlliedEntity(
                player.getLocation(), type, bukkitEntity -> {
                    bukkitEntity.setAI(false);
                    bukkitEntity.setVisibleByDefault(false);
                    
                    if (bukkitEntity instanceof Ageable ageable) {
                        ageable.setAdult();
                    }
                    
                    // Inherit player attributes
                    final BaseAttributes attributesCopy = player.getAttributes().snapshot();
                    
                    final LivingGameEntity entity = new LivingGameEntity(bukkitEntity, attributesCopy) {
                        @Override
                        public void onDamageTaken(@Nonnull DamageInstance instance) {
                            player.redirectDamage(instance);
                        }
                        
                        @Override
                        public double getMaxHealth() {
                            return player.getMaxHealth();
                        }
                    };
                    
                    entity.aboveHead(pl -> ComponentList.of(
                            Component.text("\uD83D\uDC7F ", NamedTextColor.DARK_RED)
                                     .append(Component.text(demonName, demonColor))
                    ));
                    entity.setEquipment(equipment);
                    
                    // Make them immune
                    entity.setImmune(DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA);
                    entity.setInformImmune(false);
                    
                    entity.setCollision(EntityUtils.Collision.DENY);
                    entity.setValidState(true);
                    
                    // Show the entity
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                        if (onlinePlayer.equals(player.getEntity())) {
                            return;
                        }
                        
                        entity.show(onlinePlayer);
                    });
                    
                    return entity;
                }
        );
    }
}
