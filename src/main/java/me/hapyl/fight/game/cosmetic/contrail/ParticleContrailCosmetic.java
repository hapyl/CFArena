package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class ParticleContrailCosmetic extends ContrailCosmetic {

    private final Particle particle;

    public ParticleContrailCosmetic(Particle particle, String name, String description, long cost, Rarity rarity) {
        super(name, description, rarity);
        this.particle = particle;
    }

    public Particle getParticle() {
        return particle;
    }

    @Override
    public void addExtraLore(@Nonnull ItemBuilder builder, @Nonnull Player player) {
        builder.addLore();
        builder.addLore("&6&lThis is a particle contrail!");
        builder.addSmartLore("It will follow behind you and display a particle.", "&e");
    }

    @Override
    public void onMove(Display display) {
        display.particle0(display.getLocation(), particle, 1, 0.0d, 0.0d, 0.0d, 0.0f, Setting.SEE_OTHERS_CONTRAIL::isDisabled, null);
    }
}
