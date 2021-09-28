package kz.hapyl.fight.game.effect;

import kz.hapyl.fight.game.GamePlayer;
import kz.hapyl.fight.game.task.GameTask;
import org.bukkit.entity.Player;

public class ActiveGameEffect {

	private final Player player;
	private final GameEffectType type;
	private int remainingTicks;

	public ActiveGameEffect(Player owner, GameEffectType type, int initTicks) {
		this.player = owner;
		this.type = type;
		this.remainingTicks = initTicks;
		startTicking();
	}

	public Player getPlayer() {
		return player;
	}

	public GameEffectType getType() {
		return type;
	}

	public void triggerUpdate() {
		this.type.getGameEffect().onUpdate(player);
	}

	public void setRemainingTicks(int ticks) {
		this.remainingTicks = ticks;
	}

	public void addRemainingTicks(int ticks) {
		this.remainingTicks += ticks;
	}

	public void removeRemainingTicks(int ticks) {
		this.remainingTicks -= ticks;
	}

	public int getRemainingTicks() {
		return remainingTicks;
	}

	public void forceStop() {
		this.remainingTicks = 0;
		type.getGameEffect().onStop(player);
		final GamePlayer gp = GamePlayer.getAlivePlayer(this.player);
		if (gp != null) {
			gp.clearEffect(type);
		}
	}

	private void startTicking() {
		type.getGameEffect().onStart(player);
		new GameTask() {
			@Override
			public void run() {

				// stop ticking
				if (remainingTicks <= 0) {
					forceStop();
					this.cancel();
					return;
				}

				type.getGameEffect().onTick(player, remainingTicks % 20);

				// actually tick down
				--remainingTicks;

			}
		}.runTaskTimer(0, 1);
	}

}
