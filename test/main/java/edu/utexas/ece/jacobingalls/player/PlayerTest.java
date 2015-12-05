package edu.utexas.ece.jacobingalls.player;

import javafx.scene.paint.Color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest{
	static Player p1;
	static Player p2;

	@Before
	public void setUp() throws Exception {
		p1 = new Player(Color.RED);

		p2 = new AIPlayer(Color.GREEN, AIPlayer.PlayStyle.SIMPLE);

	}

	@Test
	public void testTick() throws Exception {
		p1.setIronReplenishRate(5);
		p1.setEnergyReplenishRate(5);
		p1.setGoldReplenishRate(5);
		p1.setGold(100);
		p1.setEnergy(100);
		p1.setIron(100);

		p2.setIronReplenishRate(5);
		p2.setEnergyReplenishRate(5);
		p2.setGoldReplenishRate(5);
		p2.setGold(100);
		p2.setEnergy(100);
		p2.setIron(100);

		p1.tick(100 * 1000);
		p2.tick(100 * 1000);

		Assert.assertEquals(600, (int)p1.getGold());
		Assert.assertEquals(600, (int)p1.getEnergy());
		Assert.assertEquals(600, (int)p1.getIron());
		Assert.assertEquals(600, (int)p2.getGold());
		Assert.assertEquals(600, (int)p2.getEnergy());
		Assert.assertEquals(600, (int)p2.getIron());

		p1.tick(500  * 1000);
		p2.tick(500  * 1000);
		Assert.assertEquals(3100, (int)p1.getGold());
		Assert.assertEquals(3100, (int)p1.getEnergy());
		Assert.assertEquals(3100, (int)p1.getIron());
		Assert.assertEquals(3100, (int)p2.getGold());
		Assert.assertEquals(3100, (int)p2.getEnergy());
		Assert.assertEquals(3100, (int)p2.getIron());
	}

	@Test
	public void testTeamColors() throws Exception{
		p1.setTeamAlternate1Color(Color.BLUE);
		p2.setTeamAlternate1Color(Color.BLUE);
		p1.setTeamAlternate2Color(Color.WHITE);
		p2.setTeamAlternate2Color(Color.WHITE);

		p1.setTeamBackgroundColor(Color.BLACK);
		p2.setTeamBackgroundColor(Color.BLACK);

		Assert.assertEquals(Color.RED, p1.getTeamColor());
		Assert.assertEquals(Color.GREEN, p2.getTeamColor());

		Assert.assertEquals(Color.BLUE, p1.getTeamAlternate1Color());
		Assert.assertEquals(Color.BLUE, p2.getTeamAlternate1Color());
		Assert.assertEquals(Color.WHITE, p1.getTeamAlternate2Color());
		Assert.assertEquals(Color.WHITE, p2.getTeamAlternate2Color());
	}

}