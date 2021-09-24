package kz.hapyl.fight.game.talents.storage;

import kz.hapyl.fight.game.Response;
import kz.hapyl.fight.game.heroes.HeroHandle;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Utils;
import kz.hapyl.spigotutils.module.entity.Entities;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FlowerEscape extends Talent {

	private final int flowerLifeTicks = 120;

	private final double flowerRadius = 2.5;
	private final double flowerDamage = 5.0d;

	public FlowerEscape() {
		// Throw a deadly flower at your current location and dash backwards. The flower will continuously pulse and deal damage to surrounding opponents. After duration is over, it will explode dealing double the damage.
		super("Flower Escape", "Throw a deadly flower at your current location and dash backwards.__The flower will continuously pulse and deal damage to nearby players.__After duration is over, it will explode dealing double the damage.", Type.COMBAT);
		this.setItem(Material.RED_TULIP);
		this.setCd(flowerLifeTicks * 2);
	}

	@Override
	public Response execute(Player player) {
		final Location location = player.getLocation();
		final Vector vector = player.getLocation().getDirection().normalize().multiply(-1.5);
		player.setVelocity(vector.setY(0.5d));

		final ArmorStand entity = Entities.ARMOR_STAND.spawn(location, me -> {
			me.setMarker(true);
			me.setInvisible(true);
			if (me.getEquipment() != null) {
				me.getEquipment().setHelmet(new ItemStack(this.getItem().getType()));
			}
		});

		final double finalDamage = HeroHandle.PYTARIA.calculateDamage(player, flowerDamage);

		new GameTask() {
			private int tick = flowerLifeTicks;

			@Override
			public void run() {

				if (tick-- <= 0) {
					entity.remove();
					PlayerLib.playSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
					PlayerLib.spawnParticle(location, Particle.SPELL_MOB, 15, 1, 0.5, 1, 0);
					Utils.getPlayersInRange(location, flowerRadius).forEach(victim -> victim.damage(finalDamage * 2.0d, player));
					this.cancel();
					return;
				}

				// pulse
				if (tick % 20 == 0) {

					// fx
					Geometry.drawCircle(location, flowerRadius, Quality.LOW, new Draw(Particle.TOTEM) {
						@Override
						public void draw(Location location) {
							if (location.getWorld() == null) {
								return;
							}
							location.getWorld().spawnParticle(this.getParticle(), location, 1, 0, 0, 0, 0.2);
						}
					});

					Utils.getPlayersInRange(location, flowerRadius).forEach(target -> target.damage(finalDamage, player));

					final float pitch = Math.min(0.5f + ((0.1f * (((float)flowerLifeTicks - tick) / 20))), 2.0f);
					PlayerLib.playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.75f);
					PlayerLib.playSound(location, Sound.BLOCK_NOTE_BLOCK_COW_BELL, pitch);

				}

			}
		}.runTaskTimer(0, 1);
		return Response.OK;
	}
}
