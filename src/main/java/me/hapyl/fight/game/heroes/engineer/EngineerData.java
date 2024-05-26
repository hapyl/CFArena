package me.hapyl.fight.game.heroes.engineer;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.engineer.Construct;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EngineerData extends PlayerData implements HeroReference<Engineer> {

    private final Engineer engineer;

    private final TemperInstance temperInstance = Temper.MECHA_INDUSTRY.newInstance()
            .decrease(AttributeType.SPEED, 0.05d); // 25%

    @Nullable private Construct construct;
    @Nullable private MechaIndustries mecha;

    private int iron;

    public EngineerData(GamePlayer player, Engineer engineer) {
        super(player);

        this.engineer = engineer;

        setIron(engineer.startIron);
    }

    @Nonnull
    @Override
    public Engineer getHero() {
        return engineer;
    }

    public int getIron() {
        return iron;
    }

    public void setIron(int iron) {
        this.iron = Numbers.clamp(iron, 0, engineer.maxIron);

        // Update iron
        player.setItem(
                HotbarSlots.HERO_ITEM,
                ItemBuilder.of(Material.IRON_INGOT, "&aIron", "Use the iron to build construct!")
                        .setAmount(Math.max(1, this.iron))
                        .asIcon()
        );
    }

    public void addIron(int iron) {
        setIron(this.iron + iron);
    }

    public void subtractIron(int iron) {
        addIron(-iron);
    }

    @Nullable
    public Construct getConstruct() {
        return construct;
    }

    @Nullable
    public Construct setConstruct(@Nullable Construct construct) {
        final Construct previousConstruct = this.construct;

        this.construct = construct;
        return previousConstruct;
    }

    @Override
    public void remove() {
        iron = 0;

        if (construct != null) {
            construct.remove();
        }

        if (mecha != null) {
            mecha.remove();
        }
    }

    public void createMechaIndustries(Engineer hero) {
        removeMechaIndustries();

        mecha = new MechaIndustries(player, hero);

        player.setItemAndSnap(HotbarSlots.TALENT_4, engineer.ironFist.getItem());
        player.setCooldown(engineer.ironFist.getMaterial(), engineer.ultimateHitCd);

        temperInstance.temper(player, hero.getUltimate().getDuration());
    }

    public void removeMechaIndustries() {
        if (mecha == null) {
            return;
        }

        player.setItem(HotbarSlots.TALENT_4, null);
        player.snapToWeapon();

        player.getAttributes().resetTemper(Temper.MECHA_INDUSTRY);

        mecha.remove();
        mecha = null;
    }

    @Nullable
    public MechaIndustries getMechaIndustries() {
        return mecha;
    }

    public void swingMechaIndustriesHand() {
        if (mecha == null) {
            return;
        }

        mecha.swing();
    }
}
