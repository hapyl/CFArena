package me.hapyl.fight.game.heroes.mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Mage extends Hero implements UIComponent {

    private final int maxSoulsAmount = 10;

    public final MageSpell spellWyvernHeart = new WyvernHeartSpell();
    public final MageSpell spellDragonSkin = new DragonSkinSpell();

    private final PlayerMap<Integer> soulsCharge = PlayerMap.newMap();

    public Mage(@Nonnull Key key) {
        super(key, "Mage");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.RANGE, Archetype.MOBILITY);
        profile.setGender(Gender.MALE);

        setDescription("""
                Necromancer with the ability to absorb soul fragments upon hitting his foes to use them as fuel for his &e&l&oSoul Eater&8&o.
                """);

        setItem("f41e6e4bcd2667bb284fb0dde361894840ea782efbfb717f6244e06b951c2b3f");

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(82, 12, 135, TrimPattern.VEX, TrimMaterial.AMETHYST);
        equipment.setLeggings(82, 12, 135, TrimPattern.TIDE, TrimMaterial.AMETHYST);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.TIDE, TrimMaterial.AMETHYST);

        setWeapon(new MageWeapon(this));
        setUltimate(new MageUltimate());
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity victim = instance.getEntity();
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (!victim.hasTag("LastDamage=Soul")) {
            addSouls(player, 1);
        }

        victim.removeTag("LastDamage=Soul");
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        soulsCharge.remove(player);
    }

    public void addSouls(GamePlayer player, int amount) {
        this.soulsCharge.put(player, Math.clamp(getSouls(player) + amount, 0, maxSoulsAmount));
    }

    public int getSouls(GamePlayer player) {
        return soulsCharge.getOrDefault(player, 0);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        soulsCharge.clear();
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.MAGE_TRANSMISSION;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.ARCANE_MUTE;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.SOUL_HARVEST;
    }

    @Override
    public @Nonnull String getString(@Nonnull GamePlayer player) {
        final int souls = getSouls(player);
        return "&e⦾ &l" + souls + (souls == maxSoulsAmount ? " FULL!" : "");
    }

    private class MageUltimate extends UltimateTalent {
        public MageUltimate() {
            super(Mage.this, "Magical Trainings", 50);

            setDescription("""
                    Retrieve two ancient spells and use one of them to your advantage!
                    
                    %s
                    %s
                    Only one of the spells can be used at the same time, and you will &nnot&7 gain &b&l※ &7until spell is over.
                    """.formatted(spellWyvernHeart.getFormatted(), spellDragonSkin.getFormatted())
            );

            setItem(Material.WRITABLE_BOOK);
            setType(TalentType.ENHANCE);

            setManualDuration();
            setCooldown(Constants.INFINITE_DURATION);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                player.setUsingUltimate(true);

                player.setItem(HotBarSlot.TALENT_3, spellWyvernHeart.getSpellItem());
                player.setItem(HotBarSlot.TALENT_5, spellDragonSkin.getSpellItem());
                player.snapTo(HotBarSlot.TALENT_4);
            });
        }
    }
}
