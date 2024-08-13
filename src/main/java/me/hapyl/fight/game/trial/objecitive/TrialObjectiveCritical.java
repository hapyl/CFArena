package me.hapyl.fight.game.trial.objecitive;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.trial.Trial;
import me.hapyl.fight.game.trial.TrialEntity;
import me.hapyl.fight.game.ui.display.AscendingDisplay;
import org.bukkit.Material;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class TrialObjectiveCritical extends TrialObjective {

    private static final ItemStack huskHead = ItemBuilder.playerHeadUrl("29d17856eeee5b2315edce7306460b12e98df7feb56017542d1f4ed4f1982041")
            .asIcon();

    public TrialObjectiveCritical(Trial trial) {
        super(trial, "Critical Intermission", "Score a critical hit on a Husking Husk.");
    }

    @Override
    public void onStart() {
        super.onStart();

        final GamePlayer player = trial.getPlayer();

        player.sendTextBlockMessage("""
                &4&lᴅᴀᴍᴀɢᴇ sʏsᴛᴇᴍ
                The game uses completely &ncustom&7 &cdamage &bsystem&7!
                                
                One example is no &nvanilla&7 &ecritical hits&7, meaning you &ccannot&7 score &ecritical hit &7while &3falling&7.
                                
                Instead of that, your character has &eattributes&7, such as %1$s, which determines the chance to &ecrit&7!
                &8&oYou can check each hero's attributes in the preview GUI.

                This husk in front of you looks &ntough&7! It can only be &cdamaged&7 by &ecritical&7 hits!

                For the &bTutorial&7, your %1$s has been increased!
                """.formatted(AttributeType.CRIT_CHANCE));

        player.getAttributes().add(AttributeType.CRIT_CHANCE, 0.4d);

        // Spawn husk
        trial.spawnEntity(BukkitUtils.defLocation(-200.5, 66.0, 234.5, 90, 0), husk -> {
            final EntityEquipment equipment = husk.getEquipment();

            if (equipment != null) {
                equipment.setHelmet(huskHead);
                equipment.setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE));
            }

            return new TrialEntity(trial, husk) {
                @Override
                public void onDamageTaken(@Nonnull DamageInstance instance) {
                    if (!instance.isCrit()) {
                        instance.multiplyDamage(0.0d);
                        instance.setCancelled(true);

                        new AscendingDisplay("&4Non-Crit", 20).display(getLocation());
                    }
                }
            };
        });
    }
}
