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
package de.fuberlin.agcsw.svont.test;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.fuberlin.agcsw.svont.SVoNtException;
import de.fuberlin.agcsw.svont.SVoNtRunner;
import de.fuberlin.agcsw.svont.preprocessing.CommitInfo;
import de.fuberlin.agcsw.svont.util.Configurator;
import de.fuberlin.agcsw.svont.util.StringUtil;
import de.fuberlin.agcsw.svont.validation.ValidationReport;

public class ValidationTest {

	Properties props;

	/**
	 * Datadirectory where the test-ontologies can be found
	 */
	static String dataDir = System.getProperty("user.dir")
			+ "/ontologies/testdata/validation/";

	/**
	 * Init the tests
	 */
	@Before
	public void setUp() {
		dataDir = StringUtil.normalizePaths(dataDir);
		System.out.println(dataDir);
		Configurator.initLogging("valitest");
		try {
			props = Configurator.loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests if a ontology with bad syntax is correctly detected
	 */
	@Test
	public void testValidFailedSyntax() {
		String ontologyFile = dataDir + "badSyntax.owl";
		CommitInfo ci = new CommitInfo(ontologyFile, ontologyFile, null, "1-aa", null);
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, false, false);
		} catch (SVoNtException se) {
			//SVoNtExeption is thrown by invalid ontology
			ValidationReport report = SVoNtRunner.getValidationReport();
			assertTrue("Bad Syntax Test failed", (!report.isValid() && !report.isSyntax() &&
					!report.isConsistent() && !report.isExpressivity() ) );
			return;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
			return;
		}
		assertTrue("Ontology wrongly validated .. there should be a syntax error",false);
	}

	/**
	 * Test if a ontology with inconsistency is correcly detected
	 */
	@Test
	public void testValidFailedConsistency() {
		String ontologyFile = dataDir + "badConsistency.owl";
		CommitInfo ci = new CommitInfo(ontologyFile, ontologyFile, null, "1-aa", null);
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, false, false);
		} catch (SVoNtException se) {
			//SVoNtExeption is thrown by invalid ontology
			ValidationReport report = SVoNtRunner.getValidationReport();
			assertTrue("Bad Consistency Test failed", (!report.isValid() && report.isSyntax() &&
					!report.isConsistent() && !report.isExpressivity() ) );
			return;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
			return;
		}
		assertTrue("Ontology wrongly validated .. there should be a syntax error",false);
		
	}
	
	/**
	 * Tests if bad Expressivity is correctly detected
	 *
	 */
	@Test
	public void testValidFailedExpressivity() {
		 /*
		String ontologyFile = dataDir + "badExpressivity.owl";
		CommitInfo ci = new CommitInfo(ontologyFile, ontologyFile, null, null, null);
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, false, false);
		} catch (SVoNtException se) {
			//SVoNtExeption is thrown by invalid ontology
			ValidationReport report = SVoNtRunner.getValidationReport();
			assertTrue("Bad Syntax Test failed", (!report.isValid() && report.isSyntax() &&
					report.isConsistent() && !report.isExpressivity() ) );
			return;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
			return;
		}
		assertTrue("Ontology wrongly validated .. there should be a syntax error",false);
		*/
		
		//Unfortunatly the OWL 2 EL Profile is not correctly checked by Pellet 2.0 RC6
		
				
	}
	
	/**
	 * Test for successfully validation of a correct ontology
	 */
	@Test
	public void testSuccess() {
		String ontologyFile = dataDir + "validOntology.owl";
		CommitInfo ci = new CommitInfo(ontologyFile, ontologyFile, null, "1-aa", null);
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, false, false);
		} catch (SVoNtException se) {
			assertTrue("Bad Validation", false);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
			return;
		}
		assertTrue(true);			
	}
	






	public static void main(String[] args) {
		ValidationTest vt1 = new ValidationTest();
		vt1.setUp();
		vt1.testValidFailedSyntax();
		vt1.testValidFailedConsistency();
		vt1.testValidFailedExpressivity();
		vt1.testSuccess();
	}

}
