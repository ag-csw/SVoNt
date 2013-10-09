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

public class ChangeLogTest {

	Properties props;
	CommitInfo ci;

	static String dataDir = StringUtil.normalizePaths(System
			.getProperty("user.dir")
			+ "/ontologies/testdata/diff/");
	final static String repRoot = "d:/workspace/svont";

	// final static String repRoot = "/home/mrothe/workspace/SVoNt";

	@Before
	public void setUp() {
		dataDir = StringUtil.normalizePaths(dataDir);
		Configurator.initLogging("valitest");
		try {
			props = Configurator.loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Test writing a Changelog for Base Diff
	 */
	@Test
	public void testWriteChangeLog() {
		String baseFile = "complex/univ-bench-original.owl";
		String updateFile = "complex/univ-bench-update.owl";
		String baseOntologyFile = dataDir + baseFile;
		String updateOntologyFile = dataDir + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile,repRoot+"/Base/" ,
				"1-aa", "Mario");
		props.setProperty("diff", "base");
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, true, true);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} catch (Exception e) {
			assertTrue(e.toString(), false);
		}
		assertTrue(true);
	}
	
	/**
	 * Test Wrtiting a ChangeLog for CEX Diff
	 */
	@Test
	public void testWriteChangeLogCEX() {
		String baseFile = "base.owl";
		String updateFile = "update.owl";
		String baseOntologyFile = dataDir + "owldifftest/6/" + baseFile;
		String updateOntologyFile = dataDir + "owldifftest/6/" + updateFile;
		ci = new CommitInfo(baseOntologyFile, updateOntologyFile, repRoot+"/CEX/",
				"1-aa", "Mario");
		props.setProperty("diff", "CEX");
		SVoNtRunner.init(props, ci);
		try {
			SVoNtRunner.run(true, true, true);
		} catch (SVoNtException se) {
			assertTrue(se.getErrorMessage(), false);
		} catch (Exception e) {
			assertTrue(e.toString(), false);
		}
		assertTrue(true);
	}
	

	public static void main(String[] args) {

		ChangeLogTest clt1 = new ChangeLogTest();
		clt1.setUp();

		clt1.testWriteChangeLog();
		clt1.testWriteChangeLogCEX();
	}

}
