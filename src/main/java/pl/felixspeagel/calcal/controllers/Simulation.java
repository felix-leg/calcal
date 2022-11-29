package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.file.ProjectReader;
import pl.felixspeagel.calcal.file.template.SimulationWriter;

import java.util.concurrent.atomic.AtomicReference;

public class Simulation {
	
	public final SimulationSettings settings;
	public final AtomicReference<pl.felixspeagel.calcal.calculators.simulation.Simulation> simulation;
	public final SimulationWriter writer;
	
	public Simulation(ProjectReader project) {
		settings = new SimulationSettings( project );
		simulation = new AtomicReference<>(null);
		writer = SimulationWriter.getInstance(simulation);
	}
	
	public void updateSimulationIfNecessary() {
		if( settings.isUpdateNeeded() ) {
			simulation.set(settings.getNewSimulation());
		}
	}
	
}
