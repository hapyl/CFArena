package me.hapyl.fight.github;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

public final class Contributors {

    private static final List<Contributor> contributors = Lists.newArrayList();

    static {
        addContributor("hapyl");
        addContributor("RobCooos", "RobCos_");
        addContributor("DiDenPro");
    }

    @Nonnull
    public static List<Contributor> getContributors() {
        return Lists.newArrayList(contributors);
    }

    private static void addContributor(String githubName) {
        addContributor(githubName, null);
    }

    private static void addContributor(String githubName, String name) {
        contributors.add(new Contributor(githubName, name));
    }

}
