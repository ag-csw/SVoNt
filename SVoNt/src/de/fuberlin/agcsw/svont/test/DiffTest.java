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
import de.fuberlin.agcsw.svont.changedetection.DiffResult;
import de.fuberlin.agcsw.svont.preprocessing.CommitInfo;
import de.fuberlin.agcsw.svont.util.Configurator;
import de.fuberlin.agcsw.svont.util.StringUtil;

public class DiffTest {
	Properties props;
	CommitInfo ci;

	static String dataDir = System.getProperty("user.dir")
			+ "/ontologies/testdata/diff/";

	// final static String repRoot = "d:/workspace/svont";

	@Before
	public void setUp() {
		Configurator.initLogging("valitest");
		dataDir = StringUtil.normalizePaths(dataDir);
		try {
			props = Configurator.loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Test for semantical equality
	 */
	@Test
	public void testDiffBaseAlgSemanticalEquality() {
		String baseFile = "complex/miniEconomy-original.owl";
		String updateFile = "complex/miniEconomy-original.owl";
		String baseOntologyFile = dataDir + baseFile;
		String updateOntologyFile = dataDir + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile, null, "1-aa",
				null);
		props.setProperty("diff", "Base");
		SVoNtRunner.init(props, ci);

		try {
			SVoNtRunner.run(true, true, false);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} catch (Exception e) {
			assertTrue(e.toString(), false);
		}
		//test if diffresult is empty		
		assertTrue("structural differences found",SVoNtRunner.getDiffResult().isEmpty());
	}
	

	/**
	 * Test for no semantical equality
	 */
	@Test
	public void testDiffBaseAlgComplexExample() {
		String baseFile = "complex/univ-bench-original.owl";
		String updateFile = "complex/univ-bench-update.owl";
		String baseOntologyFile = dataDir + baseFile;
		String updateOntologyFile = dataDir + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile, null, "1-aa",
				null);
		props.setProperty("diff", "Base");
		SVoNtRunner.init(props, ci);

		try {
			SVoNtRunner.run(true, true, false);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} catch (Exception e) {
			assertTrue(e.toString(), false);
		}
		//test if diffresult is not empty
		
		assertTrue("structural differences found",!SVoNtRunner.getDiffResult().isEmpty());
	}



	/**
	 * Test if semantical changed classes are found
	 */
	@Test
	public void testOWLDiffCEX() {
		String baseFile = "base.owl";
		String updateFile = "update.owl";
		String baseOntologyFile = dataDir + "owldifftest/6/" + baseFile;
		String updateOntologyFile = dataDir + "owldifftest/6/" + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile, null, "1-aa",
				null);
		props.setProperty("diff", "CEX");
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, true, false);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} catch (Exception e) {
			assertTrue(e.toString(), false);
		}
		//Test if 3 semantical changed classes are found
		DiffResult dr = SVoNtRunner.getDiffResult();
		assertTrue(dr.getChangedClasses().size() == 3);
	}
	
	
	/**
	 * Test or ChangedURI
	 */
	@Test
	public void testDiffBaseChangedURI() throws Exception {
		String baseFile = "ChangedURI-base.owl";
		String updateFile = "ChangedURI-update.owl";
		String baseOntologyFile = dataDir + baseFile;
		String updateOntologyFile = dataDir + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile, null, "1-aa",
				null);
		props.setProperty("diff", "CEX");
		SVoNtRunner.init(props, ci);

		try {
			SVoNtRunner.run(true, true, false);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} 
		//test if diffresult is empty		
		assertTrue("structural differences found",!SVoNtRunner.getDiffResult().isEmpty());
	}


	public static void main(String[] args) throws Exception{
		DiffTest dt1 = new DiffTest();
		dt1.setUp();
		dt1.testDiffBaseAlgSemanticalEquality();
		dt1.testDiffBaseAlgComplexExample();
		dt1.testOWLDiffCEX();

		dt1.testDiffBaseChangedURI();

	}

}
