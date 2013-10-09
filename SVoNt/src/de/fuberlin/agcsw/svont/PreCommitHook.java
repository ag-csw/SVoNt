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

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.fuberlin.agcsw.svont.preprocessing.CommitInfo;
import de.fuberlin.agcsw.svont.preprocessing.Preprocessing;
import de.fuberlin.agcsw.svont.util.Configurator;
import de.fuberlin.agcsw.svont.util.HookCommunicator;
import de.fuberlin.agcsw.svont.util.StringUtil;

/**
 * This Class is the Entry Point for the ontology versioning processing. Its
 * started by the Precommit hook script with a given set of parameters it
 * creates a CommitInfo object containing the Informations of the Commit, and
 * initialises the Configuration and Logging settings After that a SVoNtRunner
 * Process is generated and started. It handles the exceptions that could be
 * thrown by the Runner process and writes them to a specified File which gets
 * inspected by hook script.
 * 
 * 
 * @author mario
 * 
 */
public class PreCommitHook {

	/**
	 * This main method gets started if this Class is called by the JVM
	 * 
	 * @param args
	 *            The set of parameters that are delivered by the caller
	 *            [0] root directory path of the repository for this commit
	 *            [1] the root directory path of the SVoNt System
	 *            [2] transaction number of this commit from svn
	 *            [3] temp directory used for the communication between script
	 *            and java
	 *            [4] File for Errorcommunication with Precommit Scripts

	 */
	public static void main(String[] args) {

// allow remote debugger to attach by short delay (for debugging only - comment in when done debugging)		

//		try {
//			Thread.sleep(10000);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
		
		String repRoot = StringUtil.normalizePaths(args[0]);
		String svontRoot = StringUtil.normalizePaths(args[1]);
		String txn = args[2];
		String tmpDir = args[3];
		String errorResultFile = args[4];
		
		
		// create the errorFilePath
		String errorFile = repRoot + tmpDir + errorResultFile;



		// Init the Logging System
		Configurator.initLogging(svontRoot, repRoot, "log", txn);
		Logger log = Logger.getLogger(PreCommitHook.class);

		try {

			log.info("------- PROCESSING PRE-COMMIT OF ONTOLOGY -------");
			log.info("Ontology Repository: " + repRoot);

			Properties props = Configurator.loadProperties(svontRoot);
			log.debug("Properties successfull loaded....");
			
			
			//Preprocessing the Repository
			CommitInfo ci = Preprocessing.run(txn, repRoot,tmpDir,props);
			if (ci == null) {
				// then we dont need to do SVoNt Processing
				//happens if commit doesnt contain ontology
				log.info("No Ontologie found. No SVoNt Processing of this commit");
				return;
				
			}

			// init the svont engine
			SVoNtRunner.init(props, ci);

			// do the SVoNt Precommit processing -- it will stop with some kind
			// of Exception if problem occurs
			SVoNtRunner.run();

			log.info("------- PROCESSING OF PRE-COMMIT SUCCESSFULLY FINISHED -------");

		} catch (SVoNtException e) {
			String errorText = e.getErrorMessage() + "\n" + getStackTrace(e);
			HookCommunicator.writeToFile(errorFile, errorText);
			log.error(errorText, e);
		} catch (Exception e) {
			String errorText = "Internal exception occured - contact your svn admin.\n" + getStackTrace(e);
			log.error("Exception occured in Runner state: "
					+ SVoNtRunner.getState());
			HookCommunicator.writeToFile(errorFile, errorText);
			log.error(errorText, e);
		} catch (Error e) {
			String errorText = "Internal error occured - contact your svn admin.\n" + getStackTrace(e);
			HookCommunicator.writeToFile(errorFile, errorText);
			log.error(errorText, e);
		}
	}
	
	private static String getStackTrace(Throwable t) {
		StringWriter s = new StringWriter();
		PrintWriter p = new PrintWriter(s);
		t.printStackTrace(p);
		return s.toString();
	}
}
