package me.hapyl.fight.game;

import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentQueue;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NullGamePlayer implements IGamePlayer {
    @Nonnull
    @Override
    public Hero getHero() {
        return Heroes.ARCHER.getHero();
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public void setDead(boolean dead) {

    }

    @Nonnull
    @Override
    public TalentQueue getTalentQueue() {
        return TalentQueue.EMPTY;
    }

    @Nullable
    @Override
    public InputTalent getInputTalent() {
        return null;
    }

    @Override
    public void setInputTalent(@Nullable InputTalent inputTalent) {

    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public void markLastMoved() {

    }

    @Nullable
    @Override
    public Skins getSkin() {
        return null;
    }

    @Override
    public double getUltimateAccelerationModifier() {
        return 0;
    }

    @Override
    public void setUltimateAccelerationModifier(double d) {

    }

    @Override
    public long getLastMoved() {
        return 0;
    }

    @Override
    public void interrupt() {

    }

    @Override
    public void heal(double d) {

    }

    @Override
    public void damage(double d) {

    }

    @Override
    public void damage(double d, EnumDamageCause cause) {

    }

    @Override
    public void damage(double d, LivingEntity damager) {

    }

    @Override
    public void damage(double d, LivingEntity damager, EnumDamageCause cause) {

    }

    @Override
    public void die(boolean force) {

    }

    @Override
    public void addEffect(GameEffectType type, int ticks) {

    }

    @Override
    public void addEffect(GameEffectType type, int ticks, boolean override) {

    }

    @Override
    public boolean hasEffect(GameEffectType type) {
        return false;
    }

    @Override
    public void removeEffect(GameEffectType type) {

    }

    @Override
    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {

    }

    @Override
    public void removePotionEffect(PotionEffectType type) {

    }

    @Override
    public void addUltimatePoints(int i) {

    }

    @Nonnull
    @Override
    public EnumDamageCause getLastDamageCause() {
        return EnumDamageCause.NONE;
    }

    @Override
    public void setLastDamageCause(EnumDamageCause cause) {

    }

    @Nullable
    @Override
    public LivingEntity getLastDamager() {
        return null;
    }

    @Override
    public void setLastDamager(LivingEntity entity) {

    }

    @Override
    public double getHealth() {
        return 0;
    }

    @Override
    public void setHealth(double d) {

    }

    @Override
    public String getHealthFormatted() {
        return "0";
    }

    @Override
    public double getMaxHealth() {
        return 0;
    }

    @Override
    public double getMinHealth() {
        return 0;
    }

    @Override
    public boolean isUltimateReady() {
        return false;
    }

    @Override
    public void sendMessage(String message, Object... objects) {

    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {

    }

    @Override
    public void sendActionbar(String text, Object... objects) {

    }

    @Override
    public void playSound(Sound sound, float pitch) {

    }

    @Nullable
    @Override
    public StatContainer getStats() {
        return null;
    }

    @Override
    public boolean isRespawning() {
        return false;
    }

    @Override
    public int getKillStreak() {
        return 0;
    }

    @Override
    public void respawn() {

    }

    @Override
    public void respawnIn(int tick) {

    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public boolean isReal() {
        return false;
    }

}
