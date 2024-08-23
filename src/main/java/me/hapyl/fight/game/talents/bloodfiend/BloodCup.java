package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.bloodfield.BloodfiendData;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class BloodCup extends Talent {

    @DisplayField(scaleFactor = 100, suffix = "%", suffixSpace = false) public final double chance = 0.25d;
    @DisplayField public final short maxBlood = 6;
    @DisplayField public final double healingPerBottle = 6.0d;

    private final String[] bloodTextures;

    public BloodCup(@Nonnull DatabaseKey key) {
        super(key, "Blood Cup");

        setDescription("""
                Biting &cenemies&7 has a &b&l{chance}&7 to drain &cblood&7 from them.
                
                Drinking the &cblood&7 &aheals&7 you for &c{healingPerBottle} &c❤&7 per &cblood&7.
                """
        );

        setType(TalentType.SUPPORT);
        setTexture("f2c251d42546cff35365efe378e79fca9c8d60ff588f5b2111c4ce35c8401a9e");
        setCooldownSec(15);

        bloodTextures = new String[] {
                "e1a3dba81fdf89157c177693b7eb2240c53e52cc0db1959a9aace0a01d2f5c6",
                "5cf7cfe1b2c5ce5e3c992e4634d98a480b8ed3a28686653f6a47d86a31f3ba71",
                "b2f50a860e81b7c07ed46a8ab6fac9d837ef3985d093ebea0d2199bfa446fc1a",
                "9d71f6679bb773490a0b7105edb8e9451c4232bc15e8736ea73f59338fed27cd",
                "ce0b75df0163b4b268d46e3d73215d81b2339e4ea224e85559695416b99b08a7",
                "4c6347f035f0c97bb7cde75e11c5c607330fddaa2a42296610bb3e18fe5e28f0"
        };
    }

    public void updateTexture(BloodfiendData data) {
        final GamePlayer player = data.getPlayer();
        final int blood = data.getBlood();
        final HotbarSlots slot = HeroRegistry.BLOODFIEND.getTalentSlotByHandle(this);
        final ItemStack item = player.getItem(slot);

        if (item == null) {
            return;
        }

        player.setItem(slot, new ItemBuilder(item)
                .setHeadTextureUrl(getTexture(blood))
                .asIcon());
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final BloodfiendData data = HeroRegistry.BLOODFIEND.getData(player);
        final int blood = data.getBlood();

        if (blood <= 0) {
            return Response.error("The bottle is empty!");
        }

        final double healing = healingPerBottle * blood;

        player.heal(healing);
        player.sendMessage("&4&l🩸 &aHealed for &c%s &c❤&a!".formatted(healing));
        player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 0.0f);

        data.clearBlood();
        updateTexture(data);

        return Response.OK;
    }

    private String getTexture(int blood) {
        return blood == 0 ? getTexture64() : bloodTextures[blood - 1];
    }

}
