package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class BloodCup extends Talent implements HeroReference<Bloodfiend> {

    private final ItemStack[] TEXTURES;

    @DisplayField(scaleFactor = 100, suffix = "%", suffixSpace = false) public final double chance = 0.25d;
    @DisplayField public final short maxBlood = 6;
    @DisplayField private final double healingPerBottle = 6.0d;

    public BloodCup() {
        super("Blood Cup");

        setTexture("f2c251d42546cff35365efe378e79fca9c8d60ff588f5b2111c4ce35c8401a9e");
        setDescription("""
                Biting enemies has a &b{chance}&7 to drain blood from them up to &b{maxBlood}&7.
                                
                Drinking the bottle heals you for &c{healingPerBottle} &c❤&7/&cblood&7.
                """);

        setCooldownSec(15);

        TEXTURES = new ItemStack[6];
        TEXTURES[0] = create("e1a3dba81fdf89157c177693b7eb2240c53e52cc0db1959a9aace0a01d2f5c6");
        TEXTURES[1] = create("5cf7cfe1b2c5ce5e3c992e4634d98a480b8ed3a28686653f6a47d86a31f3ba71");
        TEXTURES[2] = create("b2f50a860e81b7c07ed46a8ab6fac9d837ef3985d093ebea0d2199bfa446fc1a");
        TEXTURES[3] = create("9d71f6679bb773490a0b7105edb8e9451c4232bc15e8736ea73f59338fed27cd");
        TEXTURES[4] = create("ce0b75df0163b4b268d46e3d73215d81b2339e4ea224e85559695416b99b08a7");
        TEXTURES[5] = create("4c6347f035f0c97bb7cde75e11c5c607330fddaa2a42296610bb3e18fe5e28f0");
    }

    public void updateTexture(BloodfiendData data) {
        final GamePlayer player = data.getPlayer();
        final int blood = data.getBlood();

        player.getInventory().setItem(Bloodfiend.BLOOD_SLOT, getTexture(blood));
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Bloodfiend bloodfiend = getHero();
        final BloodfiendData data = bloodfiend.getData(player);
        final int blood = data.getBlood();

        if (blood <= 0) {
            return Response.error("The bottle is empty!");
        }

        final double healing = healingPerBottle * blood;

        player.heal(healing);
        player.sendMessage("&4&l🩸 &aHealed for &c%s &c❤&a!", healing);
        player.playSound(Sound.BLOCK_BREWING_STAND_BREW, 0.0f);

        data.clearBlood();
        updateTexture(data);

        return Response.OK;
    }

    @Nonnull
    @Override
    public Bloodfiend getHero() {
        return Heroes.BLOODFIEND.getHero(Bloodfiend.class);
    }

    private ItemStack getTexture(int blood) {
        return blood == 0 ? getItem() : TEXTURES[blood - 1];
    }

    private ItemStack create(String texture) {
        return new ItemBuilder(getItem())
                .setHeadTextureUrl(texture)
                .asIcon();
    }
}
