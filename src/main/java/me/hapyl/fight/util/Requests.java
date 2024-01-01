package me.hapyl.fight.util;

import com.google.gson.Gson;
import me.hapyl.fight.Main;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Requests {

    private Requests() {
    }

    /**
     * Executes a GET request at the given URI and returns an object.
     *
     * @param url   - URI.
     * @param clazz - Object class.
     * @return an object.
     * @throws IllegalArgumentException if invalid class or could not parse.
     */
    @Nonnull
    public static <T> T get(@Nonnull String url, @Nonnull Class<T> clazz) throws IllegalArgumentException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            return jsonElementFromResponse(client.execute(new HttpGet(url)), clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T jsonElementFromResponse(@Nonnull CloseableHttpResponse response, @Nonnull Class<T> clazz) throws IOException {
        try (response) {
            return new Gson().fromJson(new InputStreamReader(response.getEntity().getContent()), clazz);
        }
    }

    public static void async(@Nonnull ErrorHandlingRunnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    runnable.handle(e);
                }
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

}
