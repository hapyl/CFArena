package kz.hapyl.fight.game.feedback;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {

	private final String version;
	private final List<LogEntry> entries;

	public ChangeLog(String version) {
		this.version = version;
		this.entries = new ArrayList<>();
	}

	public void addEntry(LogEntry entry) {
		this.entries.add(entry);
	}

	public void addBugFix(String str, Object... replacements) {
		this.addEntry(new LogEntry(LogEntry.Type.BUG_FIX, str, replacements));
	}

	public void addUpdate(String str, Object... replacements) {
		this.addEntry(new LogEntry(LogEntry.Type.UPDATE, str, replacements));
	}

	public void addNerf(String str, Object... replacements) {
		this.addEntry(new LogEntry(LogEntry.Type.NERF, str, replacements));
	}

	public void addBuff(String str, Object... replacements) {
		this.addEntry(new LogEntry(LogEntry.Type.BUFF, str, replacements));
	}

	public String getVersion() {
		return version;
	}

	public List<LogEntry> getEntries() {
		return entries;
	}
}
