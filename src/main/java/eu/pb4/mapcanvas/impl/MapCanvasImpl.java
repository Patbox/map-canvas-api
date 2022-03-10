package eu.pb4.mapcanvas.impl;

import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class MapCanvasImpl {
    public static final Team FAKE_TEAM = new Team(new Scoreboard(), "■MapCanvasApiFakeTeam");

    static {
        FAKE_TEAM.setCollisionRule(AbstractTeam.CollisionRule.NEVER);
    }
}
