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
package de.fuberlin.agcsw.svont;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import cz.cvut.kbss.owldiff.OWLDiffException;
import cz.cvut.kbss.owldiff.diff.OWLDiffConfiguration;

import de.fuberlin.agcsw.svont.changedetection.BaseDiffAlgorithm;
import de.fuberlin.agcsw.svont.changedetection.CEXDiffAlgorithm;
import de.fuberlin.agcsw.svont.changedetection.DiffExecutor;
import de.fuberlin.agcsw.svont.changedetection.DiffResult;
import de.fuberlin.agcsw.svont.changelog.ChangeLogWriter;
import de.fuberlin.agcsw.svont.changelog.OWLAPIChangeLogWriter;
import de.fuberlin.agcsw.svont.preprocessing.CommitInfo;
import de.fuberlin.agcsw.svont.validation.PelletValidator;
import de.fuberlin.agcsw.svont.validation.ValidationReport;
import de.fuberlin.agcsw.svont.validation.Validator;

/**
 * Main class for the Ontology specific processing of the SVoNt Server System It
 * initialises the implementations of the Modules used and runs them.
 * 
 * 
 * @author mario
 * 
 */
public class SVoNtRunner {

	final static Logger log = Logger.getLogger(SVoNtRunner.class);

	/**
	 * the Validator
	 */
	private static Validator validator;

	/**
	 * the DiffExecutor
	 */
	private static DiffExecutor diffExecutor;

	/**
	 * the Change Log Writer
	 */
	private static ChangeLogWriter clWriter;

	/**
	 * Properties that got loaded from svont.conf
	 */
	private static Properties props;

	/**
	 * represents the state of the ontology processing this class is in
	 */
	private static String state = "None";

	/**
	 * Informations about the Commitment
	 */
	private static CommitInfo commitInfo;

	/**
	 * URI of physical path of owl File that just got commited
	 */
	private static URI owlUpdateFile;

	/**
	 * URI of physical path of owl File that represents the Repository Base
	 */
	private static URI owlBaseFile;

	
	/**
	 * Result of the Validator
	 */
	private static ValidationReport validationReport;
	
	/**
	 * Result of the Diff
	 */
	private static DiffResult diffResult;

	/**
	 * Initialises the Modules used by coniguration and prepares the Runner for
	 * the SVoNt processing
	 * 
	 * @param p
	 *            Property Object, containing the configuration of the system
	 * @param ci
	 *            CommitInformations
	 */
	public static void init(Properties p, CommitInfo ci) {
		state = "init";
		props = p;
		commitInfo = ci;
		
		OWLDiffConfiguration.setReasonerProvider(new StructuralReasonerFactory());

		String prefix = (!ci.getOwlUpdateFile().startsWith("/")) ? "/" : "";

		owlBaseFile = URI.create("file:" + prefix + ci.getOwlBaseFile())
				.normalize();
		owlUpdateFile = URI.create("file:" + prefix + ci.getOwlUpdateFile())
				.normalize();

		validator = getValidator(props.getProperty("validator", "Pellet"));
		diffExecutor = getDiffExecutor(props.getProperty("diff", "Base"));
		clWriter = getChangeLogWriter(props.getProperty("CLWriter", "OWLAPI"));

		log.info("Using Validator of type: " + props.getProperty("validator"));
		log.info("Using DiffExecuter of type: " + props.getProperty("diff"));
		log.info("Using Change Log Writer of type: "
				+ props.getProperty("CLWriter"));

	}

	/**
	 * Return an java object for the given Validator type
	 * 
	 * @param validatorType
	 *            Type of validator
	 * @return ValidatorImplementation Object
	 */
	private static Validator getValidator(String validatorType) {
		if (validatorType.equals("Pellet")) {
			return new PelletValidator();
		}
		// ... more Validators to implement

		// Pellet is default;
		return new PelletValidator();
	}

	/**
	 * Return an java object for the given Diff Type
	 * 
	 * @param diffType
	 *            type of hangeDetector
	 * @return DiffExecuterImplementation Object
	 */
	private static DiffExecutor getDiffExecutor(String diffType) {
		if (diffType.equals("Base")) {
			return new BaseDiffAlgorithm();
		}
		if (diffType.equals("CEX")) {
			return new CEXDiffAlgorithm();
		}
		// ... more Diff Algorithmns can be implemented here

		// BaseDiff is default;
		return new BaseDiffAlgorithm();
	}

	/**
	 * Returns an java object for the Given Change Log Writer type
	 * 
	 * @param clType
	 *            type of Change Log Writer
	 * @return ChangeLogWriterImplementation object
	 */
	private static ChangeLogWriter getChangeLogWriter(String clType) {
		if (clType.equals("OWLAPI")) {
			return new OWLAPIChangeLogWriter(props, commitInfo);
		}

		// ... more Change Log Writer can be implemented here

		// OWLAPI ChangeLogWrter is default;
		return new OWLAPIChangeLogWriter(props, commitInfo);
	}

	/**
	 * Starts the ontology processing 1. Validate the Update Ontology 2. Creates
	 * a Diff between the given Ontologies 3. Write the differences to the
	 * Change Log
	 * 
	 * @throws Exception
	 *             if error occured
	 */
	public static void run() throws Exception {

		// first step is to check the consistency of the ontology
		validateOntology();

		// second step is to diff the commit Ontology with the Base revision
		diff();

		// last step: we write the diffresult to a changelog.
		writeChangeLog();
	}

	/**
	 * Starts the ontology Processing with module section flags This exists for
	 * testing reasons
	 * 
	 * @param doVal
	 *            Flag for Validation
	 * @param doDiff
	 *            Flaf for Change Detection
	 * @param doWriteChangeLog
	 *            Flag for writing Changelog
	 * @throws Exception
	 */
	public static void run(boolean doVal, boolean doDiff,
			boolean doWriteChangeLog) throws Exception {
		if (doVal)
			validateOntology();
		if (doDiff)
			diff();
		if (doWriteChangeLog)
			writeChangeLog();
	}

	/**
	 * Validates the Update Ontology
	 * 
	 * @throws Exception
	 *             if something fails
	 */
	private static void validateOntology() throws Exception {
		state = "Validation";
		// only the updateOntology gets validated
		// baseOntology from the repository is consistent
		validationReport = validator.validate(owlUpdateFile);
		if (!validationReport.isValid()) {
			// Ontology is not valid, throwing Exception with ValidationReport
			String errorString = "Validation of Ontology Failed\r\n";
			errorString += validationReport.getTextReport();
			throw new SVoNtException(errorString);
		}
		log.info("Incomming Ontology successfully validated!");
	}

	/**
	 * Creates the Difference for the Base Ontology and Update Ontology
	 * 
	 * @throws Exception
	 *             if something fails
	 */
	private static void diff() throws Exception {
		try {
			state = "Diff";
			// test if BaseOntologyFile is emtpy -- happens if an ontology File just
			// got added to the Repository
			if (emptyOntologyFile(owlBaseFile)) {
				log
						.debug("No Ontology File in Repository for Diff... creating dummy Ontology");
				// replace baseOwlFile with empty Dummy Ontology File
				String prefix = (!commitInfo.getOwlUpdateFile().startsWith("/")) ? "/"
						: "";
	
				String replaceFile = "file:" + prefix
						+ props.getProperty("svontBase")
						+ props.getProperty("ontologyDir") + "emptyOntology.owl";
				owlBaseFile = URI.create(replaceFile);
				log.debug("owlBaseFile got replaced by Following URI:"
						+ owlBaseFile);

			}

			log.debug("Going to diff following Ontologie Files:" + owlUpdateFile
					+ "," + owlBaseFile);
			diffResult = diffExecutor.executeDiff(owlBaseFile, owlUpdateFile);
	
			log.info("Diffing of Ontologies successfully finished!");
		}
		catch (OWLDiffException oe) {
			throw new SVoNtException(oe.getMessage());
		}
	}

	/**
	 * Writes found differences to the ChangeLog
	 * 
	 * @throws Exception
	 */
	private static void writeChangeLog() throws Exception {
		state = "ChangeLog";
		if (diffResult.isEmpty()) {
			// no conceptional changes -- skip writing changelog
			log
					.info("Conceptional Diffresult is empty - skip writing ChangeLog");
			return;
		}

		clWriter.writeToChangeLog(diffResult);
		log.info("Changelog successfully written!");

	}

	/**
	 * Tests if given URI is an empty File
	 * 
	 * @param URI
	 *            of the Ontology to test
	 * @return true if empty file
	 * @throws Exception
	 *             if File doesnt exist
	 */
	private static boolean emptyOntologyFile(URI obf) throws Exception {
		FileInputStream fis = new FileInputStream(new File(obf));
		int b = fis.read();
		return b == -1;

	}

	/**
	 * Get the state of the SVoNtRunner
	 * 
	 * @return the state of this Runner
	 */
	public static String getState() {
		return state;
	}

	/**
	 * Get the Report of the Validation
	 * @return the validationReport
	 */
	public static ValidationReport getValidationReport() {
		return validationReport;
	}

	/**
	 * Get the Result of the Diff
	 * @return the diffResult
	 */
	public static DiffResult getDiffResult() {
		return diffResult;
	}

}
