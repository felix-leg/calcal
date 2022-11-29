package pl.felixspeagel.calcal.controllers;

import pl.felixspeagel.calcal.file.ProjectReader;

import java.io.File;

/**
 * Controls the whole application.
 */
public class MainController {
	
	public MainController() {
	
	}
	
	public CreateOrEditWizard newCreateWizard() {
		return new CreateOrEditWizard(null);
	}
	
	public CreateOrEditWizard newEditWizard(File project_file) throws ProjectReader.ReaderException {
		var project = new ProjectReader( project_file );
		return new CreateOrEditWizard(project);
	}
	
	public Simulation newSimulation(File project_file) throws ProjectReader.ReaderException {
		var project = new ProjectReader( project_file );
		return new Simulation( project );
	}
	
}
