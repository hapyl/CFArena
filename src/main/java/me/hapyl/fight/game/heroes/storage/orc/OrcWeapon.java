package me.hapyl.fight.game.heroes.storage.orc;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class OrcWeapon extends Weapon implements Cooldown {

    private final Map<Player, OrcWeaponEntity> thrownAxe;

    public OrcWeapon() {
        super(Material.IRON_AXE);

        thrownAxe = Maps.newHashMap();

        setDamage(12.0d);
        setAttackSpeed(-0.5d);
        setId("orc_axe");
        setName("Poleaxe");
    }

    @Override
    public void onRightClick(Player player, ItemStack item) {
        final Weapon weapon = Heroes.ORC.getHero().getWeapon();

        if (thrownAxe.containsKey(player) || player.hasCooldown(weapon.getMaterial())) {
            Chat.sendMessage(player, "FUCK YOU");
            return;
        }

        player.getInventory().setItem(0, ItemStacks.AIR);

        final OrcWeaponEntity entity = new OrcWeaponEntity(player) {
            @Override
            public void callback(OrcWeaponEntity orcWeaponEntity) {
                thrownAxe.remove(player);

                player.setCooldown(weapon.getMaterial(), getCooldown());
                player.getInventory().setItem(0, weapon.getItem());
            }
        };

        thrownAxe.put(player, entity);
    }

    @Override
    public int getCooldown() {
        return 15 * 20;
    }

    @Override
    public Cooldown setCooldown(int cooldown) {
        return this;
    }
}
