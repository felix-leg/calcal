package pl.felixspeagel.calcal.controllers;

@FunctionalInterface
public interface Refreshable {
	void refresh(boolean full);
}
