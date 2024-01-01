package me.hapyl.fight.github;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.hapyl.fight.util.ErrorHandlingRunnable;
import me.hapyl.fight.util.Requests;

import javax.annotation.Nonnull;
import java.util.List;

public class Contributors {

    private static final String CONTRIBUTORS_URL = "https://api.github.com/repos/hapyl/ClassesFightArena/contributors";
    private static final String USERNAME_URL = "https://api.github.com/users/";

    private static Contributor[] contributors;

    public static void loadContributors() throws IllegalStateException {
        if (contributors != null) {
            throw new IllegalStateException("Contributors already loaded!");
        }

        Requests.async(new ErrorHandlingRunnable() {
            @Override
            public void run() {
                final Contributor[] contributors = Requests.get(CONTRIBUTORS_URL, Contributor[].class);

                // Fetch name instead of login if possible
                for (Contributor contributor : contributors) {
                    final JsonObject response = Requests.get(USERNAME_URL + contributor.login, JsonObject.class);
                    final JsonElement name = response.get("name");

                    if (name.isJsonNull()) {
                        continue;
                    }

                    contributor.setName(name.getAsString());
                }

                Contributors.contributors = contributors;
            }

            @Override
            public void handle(@Nonnull Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets a {@link List} of GitHub contributors; or an empty list if still loading.
     *
     * @return a list of GitHub contributors.
     */
    @Nonnull
    public static List<Contributor> getContributors() {
        if (contributors == null) {
            return Lists.newArrayList(); // still loading
        }

        return Lists.newArrayList(contributors);
    }

}
