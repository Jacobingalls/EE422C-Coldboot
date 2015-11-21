package edu.utexas.ece.jacobingalls.utils;

import java.util.Objects;

public class Tuple<M, N> {

	public final M a;
	public final N b;

	public Tuple(M a, N b) {
		this.a = a;
		this.b = b;
	}
}