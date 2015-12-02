package edu.utexas.ece.jacobingalls.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class TupleTest {
	Tuple<String, String> tuple = new Tuple<>("hello", "world");

	@Test
	public void tupleTest() throws Exception{
		assertEquals("hello", tuple.a);
		assertEquals("world", tuple.b);
	}
}