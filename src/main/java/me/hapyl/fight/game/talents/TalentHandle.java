package me.hapyl.fight.game.talents;

import me.hapyl.fight.game.talents.storage.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.storage.harbinger.MeleeStance;
import me.hapyl.fight.game.talents.storage.moonwalker.MoonSliteBomb;
import me.hapyl.fight.game.talents.storage.shadowassassin.ShadowPrism;
import me.hapyl.fight.game.talents.storage.shaman.Totem;
import me.hapyl.fight.game.talents.storage.tamer.MineOBall;
import me.hapyl.fight.game.talents.storage.techie.TrapCage;
import me.hapyl.fight.game.talents.storage.techie.TrapWire;
import me.hapyl.fight.game.talents.storage.vortex.VortexStar;

public class TalentHandle {

	public static final MeleeStance MELEE_STANCE = (MeleeStance)Talents.STANCE.getTalent();
	public static final TransmissionBeacon TRANSMISSION_BEACON = (TransmissionBeacon)Talents.TRANSMISSION_BEACON.getTalent();
	public static final VortexStar VORTEX_STAR = (VortexStar)Talents.VORTEX_STAR.getTalent();
	public static final MoonSliteBomb MOON_SLITE_BOMB = (MoonSliteBomb)Talents.MOONSLITE_BOMB.getTalent();
	public static final TrapCage TRAP_CAGE = (TrapCage)Talents.TRAP_CAGE.getTalent();
	public static final TrapWire TRAP_WIRE = (TrapWire) Talents.TRAP_WIRE.getTalent();
	public static final Totem TOTEM = (Totem) Talents.TOTEM.getTalent();
	public static final ShadowPrism SHADOW_PRISM = (ShadowPrism) Talents.SHADOW_PRISM.getTalent();
	public static final MineOBall MINE_O_BALL = (MineOBall) Talents.MINE_O_BALL.getTalent();

}
