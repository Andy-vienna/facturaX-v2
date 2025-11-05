package org.andy.fx.code.misc;

public interface Identified {
	default String id() { return getClass().getSimpleName(); }
}
