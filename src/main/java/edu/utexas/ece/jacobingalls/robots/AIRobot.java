package edu.utexas.ece.jacobingalls.robots;

import edu.utexas.ece.jacobingalls.player.Team;

/**
 * Created by jacobingalls on 11/16/15.
 */
public class AIRobot extends Robot {
	private long AINavRefreshTime = 1000;
	private long AINavRefreshCurrentTime = 0;


	public AIRobot(Team team) {
		super(team);
	}
	public AIRobot() {super();}

	@Override
	public void tick(long time_elapsed){
		super.tick(time_elapsed);

		AINavRefreshCurrentTime += time_elapsed;
		if(AINavRefreshCurrentTime > AINavRefreshTime){
			AINavRefreshCurrentTime = 0;

			Thing t = getClosestEnemy();
			if(t != null) {

				boolean c = false;
				double tx = t.getX();
				if (Math.abs(t.getX() - getXCenter()) > 200) {
					tx -= (t.getX() - getXCenter()) * .5;
					c = true;
				}


				double ty = t.getY();
				if (Math.abs(t.getY() - getYCenter()) > 200) {
					c = true;
					ty -= (t.getY() - getYCenter()) * .5;
				}

				if(c)
					setTargetLocation(tx, ty);
			}
		}
	}
}
