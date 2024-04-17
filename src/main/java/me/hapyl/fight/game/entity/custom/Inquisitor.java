package me.hapyl.fight.game.entity.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.entity.EntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.fight.game.entity.overlay.OverlayGameEntityType;
import me.hapyl.fight.game.entity.overlay.OverlayNamedGameEntity;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Inquisitor extends OverlayGameEntityType {
    public Inquisitor() {
        super(
                "Minos Inquisitor",
                new PlayerSkin(
                        "ewogICJ0aW1lc3RhbXAiIDogMTU5OTgxNDE2MTY3NywKICAicHJvZmlsZUlkIiA6ICJkOWQ2YTNjZDQ0ZWI0MjBlYWM2MTA2ZmQyMTNmZGRiYSIsCiAgInByb2ZpbGVOYW1lIiA6ICI5VDkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTlhYWMzNTZkYmJlZjNlYTdlZTM0NDBkN2IzYWI5ZWZjMjk2NWUyNTlmYWRjYjliMDg1MWIwYjI0NDM0N2I3ZCIKICAgIH0KICB9Cn0=",
                        "GI/M+VRVKg5FC5oi1IRt6GTtBpUTDk6j6cA2ktwVJKK4SXNVX/As1ay0fuL4z6ausTIxEixeijndci6i6a0fkTWCxP2J/l2Sc0aUolAWbjRRDvd/msVP+1Pcs6pit8KaHsoY1H9V2w0IQGxA47JyXCc19UqGI64B5Sz9TEfd3tpyuyQD5p2QnVuBwQFQ75HE81cCB52imyMxh3DoRntWOZ2sQbdoKdBDZroBa2Fsl9vQ8vfRC0lRelfnA0fYBO9njZLxHPSP4PJr6q0UcIYoyUXFR2FshM923Ov+BfBHwuHLqqj4caGQC8hIT1s/vu0OlizTjigNrXBV2pUeJ4JJVGMYi7HuF8AHBWh6OclVnsrddb+yiw8DswOKmTiPWgFguEj//d/E96WbrXF+YRxkyvFrTFmcM8zCmtlptZfJaaFp31Ku/21dYrDam1OT6BTnFeCOHXe4+vkcc5+DeepQD5TKGTVJ2k9H//kFXSt0YXLZaY+vGYg2rtLlfAXmY1xEzzFQIkZHq7A4fnkYmN9bMCMUo8Kbu1h81jR9cSdOgAROxDhzqoIcOURknMgGwmT+qR+WX5xZkr/pBHx2yiF3UytPXO1px1XOHVmqSOps0ED3iueRFtMRKl8EHR61CCRyeXgFNfd+v3u/o4sbi7BiH2oeTD/Gb8kG3gbvvi7QBnE="
                )
        );

        setType(EntityType.MINIBOSS);

        final Attributes attributes = getAttributes();
        attributes.setHealth(250);
        attributes.setSpeed(150);

        final Equipment equipment = getEquipment();

        equipment.setHandItem(Material.ARMOR_STAND);
    }

    @Override
    public void onSpawn(@Nonnull NamedGameEntity<Zombie> entity) {
        entity.playWorldSound(Sound.ENTITY_WITHER_SPAWN, 2.0f);
    }

    @Nonnull
    @Override
    public NamedGameEntity<Zombie> create(@Nonnull Zombie bukkitEntity) {
        return new Instance(bukkitEntity);
    }

    public class Instance extends OverlayNamedGameEntity {

        public Instance(Zombie entity) {
            super(Inquisitor.this, entity);
        }

        @Nullable
        @Override
        public String[] getExtraHologramLines() {
            return new String[] { Chat.format("&c+%s%% damage".formatted(AttributeType.ATTACK.getDecimalFormatted(attributes))) };
        }

        @Override
        public void onTick() {
            super.onTick();

            attributes.addSilent(AttributeType.ATTACK, 0.006d);

            if (attributes.get(AttributeType.ATTACK) >= 6.0d) {
                forceRemove();
                getNpc().remove();
                spawnWorldParticle(Particle.EXPLOSION_LARGE, 1);
                return;
            }
        }
    }


}
