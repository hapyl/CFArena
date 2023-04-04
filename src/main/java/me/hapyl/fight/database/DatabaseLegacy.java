package me.hapyl.fight.database;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.legacy.*;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

@Deprecated
public final class DatabaseLegacy extends Database {

    private File file;
    private YamlConfiguration config;

    public DatabaseLegacy(Player player) {
        super(player, true);
        this.loadFile();
        this.loadEntries();
    }

    public void saveToFile() {
        try {
            this.config.save(this.file);
            Bukkit.getLogger().info("Successfully saved database for %s.".formatted(player.getName()));
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().severe("An error occurred whilst trying to save database for %s.".formatted(player.getName()));
        }
    }

    public YamlConfiguration getYaml() {
        return config;
    }

    // entries start
    //private HeroEntry heroEntry;
    //private CurrencyEntry currencyEntry;
    //private StatisticEntry statisticEntry;
    //private SettingEntry settingEntry;
    //private ExperienceEntry experienceEntry;

    private void loadEntries() {
        this.heroEntry = new HeroEntryLegacy(this);
        this.currencyEntry = new CurrencyEntryLegacy(this);
        this.statisticEntry = new StatisticEntryLegacy(this);
        this.settingEntry = new SettingEntryLegacy(this);
        this.experienceEntry = new ExperienceEntryLegacy(this);
        this.cosmeticEntry = new CosmeticEntryLegacy(this);
    }

    //public ExperienceEntry getExperienceEntry() {
    //    return experienceEntry;
    //}
    //
    //public SettingEntry getSettings() {
    //    return settingEntry;
    //}
    //
    //public StatisticEntry getStatistics() {
    //    return statisticEntry;
    //}
    //
    //public CurrencyEntry getCurrency() {
    //    return currencyEntry;
    //}
    //
    //public HeroEntry getHeroEntry() {
    //    return heroEntry;
    //}

    // entries end
    public Object getValue(String path) {
        return config.get(path);
    }

    public <E> E getValue(String path, Type<E> type) {
        return type.fromObject(getValue(path));
    }

    public void setValue(String path, Object object) {
        config.set(path, object);
    }

    private void loadFile() {
        try {
            this.file = new File(Main.getPlugin().getDataFolder() + "/players", this.player.getUniqueId() + ".yml");
            this.config = YamlConfiguration.loadConfiguration(this.file);
            this.config.options().copyDefaults(true);
            this.saveToFile();
        } catch (Exception error) {
            error.printStackTrace();
            Bukkit.getLogger().severe("Error creating database instance for %s.".formatted(player.getName()));
        } finally {
            Bukkit.getLogger().info("Successfully created database instance for %s.".formatted(player.getName()));
        }
    }


    // this sent to console and admins
    private void sendInfo(String info, Object... toReplace) {
        final String format = Chat.format("&e&lDEBUG: &f" + info, toReplace);
        System.out.println(format);
        //Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> player.sendMessage(format));
    }


}
