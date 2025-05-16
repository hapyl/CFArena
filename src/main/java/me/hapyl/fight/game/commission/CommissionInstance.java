package me.hapyl.fight.game.commission;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.RomanNumber;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.CommissionLevel;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.EnumGameType;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ListIterator;

public class CommissionInstance extends GameInstance {

    private static final GameTeam COMMISSION_TEAM = GameTeam.GREEN;
    private static final AttributeType[] SCALED_ATTRIBUTES = { AttributeType.MAX_HEALTH, AttributeType.ATTACK, AttributeType.DEFENSE };

    private final EnumTier tier;
    private final List<MonsterSpawn> monsterSpawns;

    public CommissionInstance(@Nonnull EnumLevel enumLevel, @Nonnull EnumTier tier) {
        super(EnumGameType.COMMISSION, enumLevel);

        if (!(enumLevel.getLevel() instanceof CommissionLevel level)) {
            throw new IllegalArgumentException("Cannot start commission in non-commission level!");
        }

        this.tier = tier;
        this.monsterSpawns = Lists.newArrayList(level.monsterSpawns());
    }

    @Override
    public void run(int tick) {
        super.run(tick);

        // Tick monster spawns
        final ListIterator<MonsterSpawn> iterator = this.monsterSpawns.listIterator();

        while (iterator.hasNext()) {
            final MonsterSpawn next = iterator.next();

            if (next.shouldSpawn()) {
                next.doSpawn(this);
                iterator.remove();
            }
        }
    }

    @Nonnull
    public EnumTier tier() {
        return tier;
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        updatePlayer(player);

        // Notify
        player.sendMessage("");
        player.sendCenteredMessage("&4&lCOMMISSION ACCEPTED");
        player.sendCenteredMessage("&c%s &8[%s&8]".formatted(currentLevel().getName(), tier.toString()));
        player.sendMessage("");

        final int level = player.getCommissionLevel();
        final EntityAttributes attributes = player.getAttributes();

        player.sendCenteredMessage("&c&lBLOOD BLESSING &4%s:".formatted(RomanNumber.toRoman(level)));

        for (AttributeType type : SCALED_ATTRIBUTES) {
            final double base = attributes.base(type);

            player.sendCenteredMessage(
                    "  &8%s &b➠ &a%s %s".formatted(
                            type.toString(base),
                            type.toString(attributes.get(type)),
                            type.toString()
                    ));
        }

        player.sendMessage("");
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        updatePlayer(player);
    }

    @Override
    public void playStartAnimation() {
        CF.getPlayers().forEach(player -> {
            player.sendTitle("&c&l" + currentLevel().getName(), "&4&lᴄᴏᴍᴍɪꜱꜱɪᴏɴ ᴀᴄᴄᴇᴘᴛᴇᴅ", 10, 30, 10);
            player.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 0.0f);
        });
    }

    @Override
    protected void createGamePlayers() {
        super.createGamePlayers();
        
        // Put all players in the same team
        COMMISSION_TEAM.empty();
        CF.getAlivePlayers().forEach(COMMISSION_TEAM::addPlayerForce);

    }

    protected void updatePlayer(@Nonnull GamePlayer player) {
        // Update attributes
        final EntityAttributes attributes = player.getAttributes();
        final int level = player.getCommissionLevel();

        Commission.scaleAttributes(attributes, level);
        player.heal(player.getMaxHealth(), null);
    }

    protected static void message(@Nonnull GamePlayer player, @Nonnull String message) {
        player.sendMessage("&4&lCOMMISION&c " + message);
    }
}
