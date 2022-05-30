package me.hapyl.fight.game.weapons;

import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PackedParticle {

	private final Particle particle;
	private int amount;
	private double oX, oY, oZ;
	private float speed;

	public PackedParticle(Particle particle) {
		this(particle, 1, 0.0d, 0.0d, 0.0d, 0.0f);
	}

	public PackedParticle(Particle particle, int amount, double oX, double oY, double oZ, float speed) {
		this.particle = particle;
		this.amount = amount;
		this.oX = oX;
		this.oY = oY;
		this.oZ = oZ;
		this.speed = speed;
	}

	public int getAmount() {
		return amount;
	}

	public PackedParticle setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public double getoX() {
		return oX;
	}

	public PackedParticle setOffsetX(double oX) {
		this.oX = oX;
		return this;
	}

	public double getoY() {
		return oY;
	}

	public PackedParticle setOffsetY(double oY) {
		this.oY = oY;
		return this;
	}

	public double getoZ() {
		return oZ;
	}

	public PackedParticle setOffsetZ(double oZ) {
		this.oZ = oZ;
		return this;
	}

	public float getSpeed() {
		return speed;
	}

	public PackedParticle setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public Particle getParticle() {
		return particle;
	}

	public void display(Location location) {
		PlayerLib.spawnParticle(location, particle, amount, oX, oY, oZ, speed);
	}

	public void display(Player player, Location location) {
		PlayerLib.spawnParticle(player, location, particle, amount, oX, oY, oZ, speed);
	}

}
