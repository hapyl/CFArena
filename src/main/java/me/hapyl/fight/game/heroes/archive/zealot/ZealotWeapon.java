package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.PlayerGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class ZealotWeapon extends Weapon implements HeroReference<Zealot> {

    private final Zealot zealot;

    public ZealotWeapon(Zealot zealot) {
        super(Material.DIAMOND_SWORD);
        this.zealot = zealot;

        setId("zealot_weapon");
        setDamage(5);

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

        public SoulCryAbility() {
            super("Soul Cry", "Gain &a{ferocityIncrease} %s for {duration}.", AttributeType.FEROCITY);

            setDurationSec(4);
            setCooldownSec(16);
        }

        @Override
        public Response execute(@Nonnull Player player, @Nonnull ItemStack item) {
            if (item.getType() == Material.GOLDEN_SWORD) {
                return Response.OK;
            }

            final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);
            final EntityAttributes attributes = gamePlayer.getAttributes();

            attributes.add(AttributeType.FEROCITY, ferocityIncrease);

            zealot.abilityEquipment.equip(player);
            item.setType(Material.GOLDEN_SWORD);

            new PlayerGameTask(gamePlayer) {
                @Override
                public void run() {
                    attributes.subtract(AttributeType.FEROCITY, ferocityIncrease);
                    zealot.getEquipment().equip(player);
                    item.setType(Material.DIAMOND_SWORD);

                    // Fx
                    gamePlayer.playSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 1.0f);
                }
            }.runTaskLater(getDuration());

            // Fx
            gamePlayer.playSound(Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 0.0f);

            return Response.OK;
        }
    }
}
