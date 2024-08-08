package me.hapyl.fight.npc;

import me.hapyl.fight.npc.archive.BloodfiendNpc;
import me.hapyl.fight.npc.archive.MuseumManagerNpc;
import me.hapyl.fight.npc.archive.SlimeParkourEnjoyer;
import me.hapyl.fight.npc.archive.TheEyeNPC;
import me.hapyl.eterna.module.reflect.npc.NPCPose;

import javax.annotation.Nonnull;

public enum PersistentNPCs {

    THE_EYE(new TheEyeNPC()),

    HYPIXEL(
            new PersistentNPC(10, 65, 24, 102.5f, 0.0f, null) {
                @Override
                public void onPrepare() {
                    setSkin(
                            "eyJ0aW1lc3RhbXAiOjE1ODY5NTMzOTMyMjMsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwMGI1M2Y2Y2RmZmZhNDg1MTM3YjdmMzRmN2ZkMjQyNjUwNWJiZjQ1NGU2MWNiZGY2ZmFjMGRkODRhNDc2ZTUifX19",
                            "RJBBaFik7HpM7SMIpjbB6BP2LOSc/iBTKRutAEYYV8GtbVQzUoKw1mnLQAi2GKilzP65i/qBKUBpgZM805uMXT6SFVuvnLlO54e6T6Hs71hYR366Fd03NCohD7W+ssMqdoMRyM/e/90h7Mt5mCBzvVnhtxeAZBxkF/7+Y8jCwIY8ozBX696k6gxCpRTiAnh21NCs6ZH+AEcAqLd0CcSGVzUCeXC6x5PERUYKh2uR/fr1ouvh9F7jAkTiTEwtYties/Rzl8EgQxstRDaiJVCBhb4adOKwG14tQhLPPLJD0HvXOqEsblWR8dRzFK6ZzkgsXEgJ66rGhgkKACLmVycleLMtashKMFlXwYAsV/6uq2xdyHiRIya+p9F8pSHqTuLQzuUGg4tT9Z4eDtNszsa+sm4II6T5q/vdm+Qd+zvhKtlXJ9hXcY+8pDqjoEaGbuhEMCrQvTl64ACEApu8Mj7NDldINuBkHKz1YFIlBpNkpDtV6M5Qz48AriYZ0eWNhxa6Gl6una7iSBR8GfTv7D9OrKvHlb2H/O8QA9HNEyfJ3a05eOGX4jUqfEmasoeYCrbu6lNnCKTeIPyuLfogWlhrkWuLmRjz92o4ygWoNksHrXUmDn/zYaufKqxrLBgDweRkJDs/5XeGR6ouTbdPR+owM3gnPBV3JiB6CqDt1QHUxxc="
                    );

                    setLookAtCloseDist(0);
                    setPose(NPCPose.CROUCHING);
                }
            }
    ),

    SLIME_PARKOUR_ENJOYER(new SlimeParkourEnjoyer()),
    BLOODFIEND(new BloodfiendNpc()),
    MUSEUM_MANAGER(new MuseumManagerNpc()),

    ;

    private final PersistentNPC npc;

    PersistentNPCs(PersistentNPC npc) {
        this.npc = npc;
    }

    @Nonnull
    public PersistentNPC getNpc() {
        return npc;
    }
}
