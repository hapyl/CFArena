package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.talents.storage.MeleeStance;
import kz.hapyl.fight.game.talents.storage.TransmissionBeacon;
import kz.hapyl.fight.game.talents.storage.VortexStar;

public class TalentHandle {

	public static final MeleeStance MELEE_STANCE = (MeleeStance)Talents.STANCE.getTalent();
	public static final TransmissionBeacon TRANSMISSION_BEACON = (TransmissionBeacon)Talents.TRANSMISSION_BEACON.getTalent();
	public static final VortexStar VORTEX_STAR = (VortexStar)Talents.VORTEX_STAR.getTalent();

}
