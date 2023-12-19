package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.PlayerGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ZealotWeapon extends Weapon implements HeroReference<Zealot> {

    private final Zealot zealot;

    public ZealotWeapon(Zealot zealot) {
        super(Material.DIAMOND_SWORD);
        this.zealot = zealot;

        setId("zealot_weapon");
        setDamage(3.0d);

        setName("Psionic Blade");
        setDescription("""
                An ordinary space katana.
                """);

        setAbility(AbilityType.RIGHT_CLICK, new SoulCryAbility());
    }

    @Nonnull
    @Override
    public Zealot getHero() {
        return zealot;
    }

    public class SoulCryAbility extends Ability {

        @DisplayField(scaleFactor = 100)
        private final double ferocityIncrease = 1.5d;

        @DisplayField(scaleFactor = 500)
        private final double speedIncrease = 0.02; // 10%

        private final TemperInstance temperInstance = Temper.SOUL_CRY.newInstance()
                .increase(AttributeType.FEROCITY, ferocityIncrease)
                .increase(AttributeType.SPEED, speedIncrease);

        public SoulCryAbility() {
            super(
                    "Soul Cry",
                    "Gain &a{ferocityIncrease} %s and &b{speedIncrease} %s for {duration}.",
                    AttributeType.FEROCITY,
                    AttributeType.SPEED
            );

            setDurationSec(4);
            setCooldownSec(16);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            if (item.getType() == Material.GOLDEN_SWORD) {
                return Response.OK;
            }

            temperInstance.temper(player, getDuration());

            zealot.abilityEquipment.equip(player);
            item.setType(Material.GOLDEN_SWORD);

            new PlayerGameTask(player) {
                @Override
                public void run() {
                    zealot.getEquipment().equip(player);
                    item.setType(Material.DIAMOND_SWORD);

                    // Fx
                    player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 1.0f);
                }
            }.runTaskLater(getDuration());

            // Fx
            player.playWorldSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.0f);

            return Response.OK;
        }
    }
}
