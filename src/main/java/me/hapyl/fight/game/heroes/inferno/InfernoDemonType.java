package me.hapyl.fight.game.heroes.inferno;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.entity.EntityUtils;
import me.hapyl.eterna.module.inventory.Enchant;
import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.inferno.DemonSplitTalent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;

public enum InfernoDemonType implements Named {

    QUAZII(
            "&bQuazii",
            Entities.WITHER_SKELETON,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.DIAMOND_AXE).addEnchant(Enchant.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHeadUrl("16ca145ba435b375f763ff53b4ce04b2a0c873e8ff547e8b14b392fde6fbfd94").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .boots(Material.DIAMOND_BOOTS)
                     .build()
    ),

    TYPHOEUS(
            "&cTyphoeus",
            Entities.ZOMBIFIED_PIGLIN,
            Equipment.builder()
                     .mainHand(new ItemBuilder(Material.BLAZE_ROD).addEnchant(Enchant.LUCK_OF_THE_SEA, 1).asIcon())
                     .helmet(ItemBuilder.playerHeadUrl("e2f29945aa53cd95a0978a62ef1a8c1978803395a8ad5c0921d9cbe5e196bb8b").asIcon())
                     .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                     .leggings(ItemBuilder.leatherPants(Color.fromRGB(96, 0, 0)).asIcon())
                     .boots(Material.IRON_BOOTS)
                     .build()
    );

    private final String name;
    private final Entities<? extends LivingEntity> type;
    private final Equipment equipment;

    InfernoDemonType(@Nonnull String name, @Nonnull Entities<? extends LivingEntity> type, @Nonnull Equipment equipment) {
        this.name = name;
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

                    final LivingGameEntity entity = new LivingGameEntity(bukkitEntity, new BaseAttributes().put(AttributeType.MAX_HEALTH, 99999)) {
                        @Override
                        public void onDamageTaken(@Nonnull DamageInstance instance) {
                            // Redirect damage to the player
                            player.redirectDamage(instance);
                        }
                    };

                    entity.setEquipment(equipment);

                    // Make them immune
                    entity.setImmune(DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA);
                    entity.setInformImmune(false);

                    entity.setCollision(EntityUtils.Collision.DENY);
                    entity.setValidState(true);

                    // Show the entity
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                        if (player.is(onlinePlayer)) {
                            return;
                        }

                        entity.show(onlinePlayer);
                    });

                    return entity;
                }
        );
    }
}
