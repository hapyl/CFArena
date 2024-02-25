package me.hapyl.fight.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.ux.Message;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStreamReader;

public class SkinCommand extends CFCommand {

    private final String nameToUuidRequest = "https://api.mojang.com/users/profiles/minecraft/%s";
    private final String uuidToProfileRequest = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    public SkinCommand() {
        super("skin", PlayerRank.PREMIUM);

        addCompleterValues(1, "reset");
    }

    @Override
    protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
        final String argument = args.get(0).toString();

        if (argument.equalsIgnoreCase("reset")) {
            PlayerSkin.reset(player);
            Message.success(player, "Reset your skin!");
            return;
        }

        PlayerSkin cachedSkin = PlayerSkin.cache.getCached(argument);

        if (cachedSkin != null) {
            applySkin(player, null, cachedSkin);
            Message.success(player, "Applied skin!");
            return;
        }

        Message.info(player, ChatColor.ITALIC + "Fetching skin...");

        new BukkitRunnable() {
            @Override
            public void run() {
                final JsonObject uuidJson = getJson(nameToUuidRequest.formatted(argument));

                if (uuidJson == null) {
                    Message.error(player, "Invalid username!");
                    return;
                }

                final JsonElement uuid = uuidJson.get("id");

                if (uuid == null) {
                    Message.error(player, "Invalid username!");
                    return;
                }

                final JsonObject profileObject = getJson(uuidToProfileRequest.formatted(uuid.getAsString()));

                if (profileObject == null) {
                    Message.error(player, "Could not get profile, try again in a minute.");
                    return;
                }

                final JsonArray jsonArray = profileObject.get("properties").getAsJsonArray();

                if (jsonArray.isEmpty()) {
                    Message.error(player, "Somehow there are no textures for {}!", argument);
                    return;
                }

                final JsonObject textures = jsonArray.get(0).getAsJsonObject();

                final String value = textures.get("value").getAsString();
                final String signature = textures.get("signature").getAsString();

                applySkin(player, argument, new PlayerSkin(value, signature));
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    private void applySkin(Player player, String skinName, PlayerSkin skin) {
        skin.apply(player);

        Message.success(player, "Applied skin!");
        Message.success(player, getUsage() + " reset to reset your skin!");

        if (skinName != null) {
            PlayerSkin.cache.cache(skinName, skin);
        }
    }

    @Nullable
    private JsonObject getJson(String url) {
        try {
            final CloseableHttpClient client = HttpClients.createDefault();
            final CloseableHttpResponse response = client.execute(new HttpGet(url));

            return new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
        } catch (Exception e) {
            return null;
        }
    }
}
