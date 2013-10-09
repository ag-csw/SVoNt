/*******************************************************************************
* This file is part of the Coporate Semantic Web Project.
*
* This work has been partially supported by the ``InnoProfile-Corporate Semantic Web" project funded by the German Federal
* Ministry of Education and Research (BMBF) and the BMBF Innovation Initiative for the New German Laender - Entrepreneurial Regions.
*
* http://www.corporate-semantic-web.de/
*
*
* Freie Universitaet Berlin
* Copyright (c) 2007-2013
*
*
* Institut fuer Informatik
* Working Group Coporate Semantic Web
* Koenigin-Luise-Strasse 24-26
* 14195 Berlin
*
* http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/
*
*
*
* This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published
* by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
* You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation,
* Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or see <http://www.gnu.org/licenses/>
******************************************************************************/
package de.fuberlin.agcsw.svont.changelog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import de.fuberlin.agcsw.svont.changedetection.DiffResult;
import de.fuberlin.agcsw.svont.preprocessing.CommitInfo;

/**
 * OWLAPI Implementation of the Change Log Writer Interface
 * 
 * It appends a ChangeLog owl File to the Change Log Directory with OMV-Changes Individuals
 * 
 * @author mario
 *
 */
public class OWLAPIChangeLogWriter implements ChangeLogWriter {

	final Logger log = Logger.getLogger(OWLAPIChangeLogWriter.class);

	/**
	 * URI of this part of ChangeLog
	 */
	private String changeLogURI;

	/**
	 * Directory of Changelog
	 */
	private String chgLogDir;

	
	/**
	 * DataManager of OMV-Change Ontology Manager
	 */
	private OWLDataFactory OMVChangeFactory;

	/**
	 * Commit Info 
	 */
	private CommitInfo ci;

	/**
	 * Counter for ChangesURIS in ChangeLog
	 */
	private int chgCounter = 1;
	
	
	/**
	 * Filename of ChangeLog owl File
	 */
	private String outputFilename;

	
	/**
	 * List of changes for the ChangeLog 
	 */
	private List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();


	/**
	 * Creats the Writer Object
	 * 
	 * @param props Properties from Configuration File
	 * @param ci CommitInfo Object
	 */
	public OWLAPIChangeLogWriter(Properties props, CommitInfo ci) {

		this.ci = ci;

		String newRev = String.valueOf((Integer.parseInt(ci.getRevision())+1)); 
		
		this.chgLogDir = getChangeLogDirectory(props, ci);
		this.outputFilename =  newRev + ".owl";

		// create URI for Change Log
		changeLogURI = props.getProperty("changeLogBaseURI")
				+ ci.getRepositoryName() + "/" + outputFilename + "#";

	}

	/**
	 * Get Directory for ChangeLog
	 * 
	 * @param props Properties
	 * @param ci CommitInfo Container
	 * @return String of Directory
	 */
	private String getChangeLogDirectory(Properties props, CommitInfo ci) {
		boolean inSVNRepo = props.getProperty("changeLogInSVNRepo") == "true";

		if (inSVNRepo) {
			return ci.getRepository() + "/" + props.getProperty("changeLogDir");
		} else {
			String svontRoot = props.getProperty("svontBase");
			return svontRoot + "/" + props.getProperty("changeLogDir")
					+ ci.getRepositoryName() + "/";
		}

	}

	/**
	 * Initalize the Writer
	 * It Creates the Change Log Directory if it doesnt exist and loads the OMV-Change Ontology
	 * 
	 * @throws Exception if creating of changeLogDirectory failed
	 */
	private void initWriter() throws Exception {


		log.debug("ChangeLog URI: " + changeLogURI);
		
		// test if changeLog Directory exists
		File dir = new File(chgLogDir);
		if (!dir.exists()) {
			// newChangeLog = true;
			log.debug("ChangeLog Directory doesn't exist..." + dir);
			boolean success = dir.mkdir();
			if (success) {
				log.debug("Directory: " + dir + " created");
			} else {
				throw new Exception("Creating of ChangeLog Directory Failed");
			}
		}

		// load OMVChange Ontology as schema
		log.debug("Loading OMVChange Schema from uri:" + OMVChangesURI);

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OMVChangeFactory = man.getOWLDataFactory();

		log.debug("ChangeLog initialization done");

	}

	/* (non-Javadoc)
	 * @see de.fuberlin.agcsw.svont.changelog.ChangeLogWriter#writeToChangeLog(de.fuberlin.agcsw.svont.changedetection.DiffResult)
	 */
	public void writeToChangeLog(DiffResult dr) throws Exception {

		initWriter();

		log.debug("Appending DiffResult to ChangeLog");

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology ont = man.createOntology(IRI.create(changeLogURI));

		// Create Added Concept Changes
		log.debug("processing added Concepts in ChangeLog");
		OWLClass changeTypeClass = OMVChangeFactory.getOWLClass(IRI
				.create(OMVChangesURI2 + "AddClass"));
		processConcepts(changeTypeClass, dr.getAddedClasses(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "AddDataProperty"));
		processConcepts(changeTypeClass, dr.getAddedDataProperties(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "AddObjectProperty"));
		processConcepts(changeTypeClass, dr.getAddedObjectProperties(), man,
				ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "AddDatatype"));
		processConcepts(changeTypeClass, dr.getAddedDataTypes(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI
				.create(OMVChangesURI2 + "AddIndividual"));
		processConcepts(changeTypeClass, dr.getAddedIndividuals(), man, ont);

		
		
		// Create Removed Concept Changes
		log.debug("processing removed Concepts in ChangeLog");
		changeTypeClass = OMVChangeFactory.getOWLClass(IRI
				.create(OMVChangesURI2 + "RemoveClass"));
		processConcepts(changeTypeClass, dr.getRemovedClasses(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "RemoveDataProperty"));
		processConcepts(changeTypeClass, dr.getRemovedDataProperties(), man,
				ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "RemoveObjectProperty"));
		processConcepts(changeTypeClass, dr.getRemovedObjectProperties(), man,
				ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "RemoveDatatype"));
		processConcepts(changeTypeClass, dr.getRemovedDataTypes(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI
				.create(OMVChangesURI2 + "RemoveIndividual"));
		processConcepts(changeTypeClass, dr.getRemovedIndividuals(), man, ont);

		
		
		// Create Changed Concept Changes
		log.debug("processing changed Concepts in ChangeLog");
		changeTypeClass = OMVChangeFactory.getOWLClass(IRI
				.create(OMVChangesURI2 + "ClassChange"));
		processConcepts(changeTypeClass, dr.getChangedClasses(), man, ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "DataPropertyChange"));
		processConcepts(changeTypeClass, dr.getChangedDataProperties(), man,
				ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "ObjectPropertyChange"));
		processConcepts(changeTypeClass, dr.getChangedObjectProperties(), man,
				ont);

		// FIXME: not in omv???
		// changeTypeClass =
		// OMVChangeFactory.getOWLClass(IRI.create(OMVChanges2URI +
		// "RemoveDatatype"));
		// processConcepts(changeTypeClass, dr.getChangedDataTypes(),man,ont);

		changeTypeClass = OMVChangeFactory.getOWLClass(IRI.create(OMVChangesURI
				+ "IndividualChange"));
		processConcepts(changeTypeClass, dr.getChangedIndividuals(), man, ont);

		// apply changes to change log ontology 
		man.applyChanges(changes);

		// OS URI Fixing ... Windows file:/D:/<dir> Unix file:/<dir>
		String prefix = (!chgLogDir.startsWith("/")) ? "/" : "";

		IRI outFileIRI = IRI.create("file:" + prefix + chgLogDir
				+ outputFilename);
		// log.debug(outFileURI);

		man.saveOntology(ont, outFileIRI);

		log.debug("DiffResult successfully appended to ChangeLog: "
				+ outFileIRI);
	}

	/**
	 * Creates OMVChanges Individuals for a specific Change Type Class
	 * 
	 * @param changeTypeClass The Class the individuals belongs to
	 * @param set Set of Entities to process
	 * @param man OWLManager for the ChangeLog 
	 * @param ont OWLOntology for the ChangeLog
	 */
	private void processConcepts(OWLClass changeTypeClass,
			Set<? extends OWLObject> set, OWLOntologyManager man,
			OWLOntology ont) {
		
		// TODO: Check spec if we want to capture anonymous entities too
		OWLDataFactory factory = man.getOWLDataFactory();
		String date = getDateTime();

		for (OWLObject o : set) {
			
			// only take into account named objects with an IRI
			// TODO handle anonymous entities
			if (o instanceof OWLNamedObject) {
				
				OWLNamedObject c = (OWLNamedObject)o;
				
				log.debug("adding to changeLog:" + c.getIRI());

				OWLIndividual in = factory.getOWLNamedIndividual(IRI
						.create(changeLogURI + "change" + chgCounter));

				// Create pure Change Class
				OWLAxiom axiom = factory.getOWLClassAssertionAxiom(
						changeTypeClass, in);
				changes.add(new AddAxiom(ont, axiom));

				// Create connection to changed Entity ("hasRelatedEntity");
				OWLObjectProperty hasRE = OMVChangeFactory
						.getOWLObjectProperty(IRI.create(OMVChangesURI
								+ "hasRelatedEntity"));
				OWLIndividual cIndivid = factory.getOWLNamedIndividual(c
						.getIRI());
				OWLObjectPropertyAssertionAxiom hasREAssertion = factory
						.getOWLObjectPropertyAssertionAxiom(hasRE, in, cIndivid);
				changes.add(new AddAxiom(ont, hasREAssertion));

				// set timestamp DataProperty ("date")
				OWLDataProperty dateDP = OMVChangeFactory
						.getOWLDataProperty(IRI.create(OMVChangesURI + "date"));

				OWLLiteral dateLiteral = factory.getOWLTypedLiteral(date,
						OWL2Datatype.XSD_DATE_TIME);

				OWLDataPropertyAssertionAxiom dateAssertion = factory
						.getOWLDataPropertyAssertionAxiom(dateDP, in,
								dateLiteral);
				changes.add(new AddAxiom(ont, dateAssertion));

				// create author Property if available
				if (ci.getAuthor() != null) {
					// not implemented yet
				}

				chgCounter++;
			}
		}

	}

	/**
	 * Returns the current Date and Time
	 * 
	 * @return String representation of Date and Time
	 */
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
