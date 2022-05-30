package me.hapyl.fight.game.feedback;

public class ChangeLogs {

	public static final ChangeLog B0_1 = new ChangeLog("Beta-0.1");

	static {
		B0_1.addBugFix("Fixed bow and crossbows sometimes not working.");
		B0_1.addBugFix("Fixed nicknames being shows in game.");
		B0_1.addBugFix("Fixed damage calculation for certain abilities.");

		B0_1.addUpdate("Add victory screen.");
	}

}
