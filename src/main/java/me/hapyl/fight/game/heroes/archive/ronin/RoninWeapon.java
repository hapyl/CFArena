package me.hapyl.fight.game.heroes.archive.ronin;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class RoninWeapon extends Weapon {

    private final Set<Player> blockingSet;

    public RoninWeapon() {
        super(Material.IRON_SWORD);

        setName("Kensei");
        setDamage(10.0d);

        setAbility(AbilityType.RIGHT_CLICK, new DeflectAbility());

        blockingSet = Sets.newHashSet();
    }

    /**
     * Idea for sword thingy.
     * Maybe add sword that goes right and changes types:
     * Wood -> Iron -> Diamond -> Gold?
     */

    public class DeflectAbility extends Ability {

        public DeflectAbility() {
            super("Deflect", "Deflect incoming projectiles towards the direction you are aiming and block melee attacks.");

            setDurationSec(3);
            setCooldownSec(30);
        }

        @Override
        public Response execute(@Nonnull Player player, @Nonnull ItemStack item) {
            blockingSet.add(player);
            return Response.OK;
        }
    }
}
