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
package de.fuberlin.agcsw.svont.preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

import de.fuberlin.agcsw.svont.SVoNtException;

public class Preprocessing {

	
	private static String SVNLOOK;
	
	
	final static Logger log = Logger.getLogger(Preprocessing.class);
	
	private static BufferedReader executeCommand(String command) throws IOException {
		log.debug("Executing Command: "+command);
		Process p = Runtime.getRuntime().exec(command);
		return new BufferedReader (new InputStreamReader(p.getInputStream()));
		
	}
	
	
	
	
	public static String findOntologyToProcess(String txn, String repository,String processingOnt) throws IOException, SVoNtException {
		String line;
		log.info("------- SEARCHING FOR ONTOLOGY FILE -------");
		String command = SVNLOOK+" changed -t "+txn+" "+ repository;
		
		BufferedReader r = executeCommand(command);
		while ((line = r.readLine()) != null) {
			
			log.debug("reading line: " + line);
			
			String path = line.split("   ")[1];
			String[] patharray= path.split("/");
			String file = patharray[patharray.length-1];
			log.debug("testing file :"+file);
			
			if (file.equals(processingOnt)) {
				// found our Ontology File
				if (line.startsWith("D")) {
					// bad if ontology File is going to get deleted --
					// return error and abort preprocessing/commit
					String errorString = "OntologyFile is not allowed to get deleted";
					throw new SVoNtException(errorString);
	
				}
				else {
					log.info("----- FOUND Ontology in transaction: "+ path + " -----");
					return path;
				}
			}
 
		}
	    r.close();
	      

		return null;
		
	}
	
	
	
	/**
	 * Extrahiert aus dem SVN Repository über den "svnlook cat" Befehl die aktuelle und die
	 * Update Ontologie schreibt sie ein Temporäres Verzeichnis und erstellt ein CommitInfo
	 * Objekt welches die Pfade zu den Ontologien beinhaltet.
	 * 
	 * @param txn
	 * @param repository
	 * @param tmpDir
	 * @param ontoPath
	 * @return
	 * @throws IOException
	 */
	private static CommitInfo getOntologies(String txn, String repository,String tmpDir, String ontoPath) throws IOException {
		
		String owlBase = "owlBase.owl";
		String owlUpdate = "owlUpdate.owl";
		
		String line;
		String ontoBaseFilePath = repository+tmpDir;
		
		log.debug("extracting Update Ontology from SVN");
		//current revision of ontology
		String command = SVNLOOK+" cat -t "+txn+" "+ repository + " " + ontoPath;
		BufferedReader r = executeCommand(command);
		
		String owlUpdatePath = ontoBaseFilePath+owlBase;
		
		log.info("owlUpdatePath = \"" + owlUpdatePath + "\"");
		File file = new File(owlUpdatePath);
		log.info("owlUpdatePathFile = " + file.getAbsolutePath());
		log.info("File " + owlUpdatePath + " exists = " + file.exists());
		log.info("File " + owlUpdatePath + " is directory = " + file.isDirectory());
		
	    BufferedWriter w = new BufferedWriter(new FileWriter(owlUpdatePath));
		while ((line = r.readLine()) != null) {
			w.write(line);
		}
		r.close();
		w.close();
		
		
		log.debug("extracting Base Ontology from SVN");
		// the "old revision"
		command = SVNLOOK+" cat "+ repository + " " + ontoPath;
		r = executeCommand(command);
		
		String owlBasePath = ontoBaseFilePath+owlUpdate;
		w = new BufferedWriter(new FileWriter(owlBasePath));
		while ((line = r.readLine()) != null) {
			w.write(line);
		}
		r.close();
		w.close();
		
		
		log.debug("extracting of Ontologies from svn finished");
		CommitInfo ci = new CommitInfo(owlBasePath, owlUpdatePath, repository, txn, null);
	
		return ci;
	}
	
	
	
	
	public static CommitInfo run(String txn, String repository,String tmpDir,Properties props) throws IOException, SVoNtException {
		
		Preprocessing.SVNLOOK = props.getProperty("svnlook");
		
		String onto = Preprocessing.findOntologyToProcess(txn, repository, props.getProperty("ontologyFile"));
		if (onto == null) {
			//problem or no ontology found --- breaking
			return null;
			
		}
		
		CommitInfo ci = Preprocessing.getOntologies(txn, repository,tmpDir, onto);
		return ci;
	}
	
	
	
}
