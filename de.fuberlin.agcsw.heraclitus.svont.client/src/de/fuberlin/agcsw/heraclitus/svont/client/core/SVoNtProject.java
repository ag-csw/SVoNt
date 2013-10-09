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
package de.fuberlin.agcsw.heraclitus.svont.client.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.svont.client.utils.SVoNtConstants;

public class SVoNtProject {

	private IProject project;
	
	private URI changelogURI;
	
	private URI repositoryProjectRootURI;
	
	private IFolder changeLogFolder;
	
	private ArrayList<ChangeLogElement> changelog;
	
	/**
	 * Map of Information of a specific Revision like Author, Date
	 */
	private HashMap<Integer, RevisionInfo> revisionInformationMap;
	
	/**
	 * Map of the Revisions for every Concept in this Project
	 */
	private HashMap<OWLClass,Integer> revisionMap;
	
	
	private int headRevision;
	

	public SVoNtProject (IProject pro) {
		this.project = pro;
		this.revisionInformationMap = new HashMap<Integer, RevisionInfo>();
		this.setRevisionMap(new HashMap<OWLClass,Integer>());
		this.changelog = new ArrayList<ChangeLogElement>();
		this.headRevision = 0;
	}
	
	
	
	public void setProject(IProject project) {
		this.project = project;
	}

	public IProject getProject() {
		return project;
	}

	public void setChangelogURI(URI changelogURI) {
		this.changelogURI = changelogURI;
	}

	public URI getChangelogURI() {
		return changelogURI;
	}



	public void setRevisionInformationMap(HashMap<Integer, RevisionInfo> revisionMap) {
		this.revisionInformationMap = revisionMap;
	}



	public HashMap<Integer, RevisionInfo> getRevisionInformationMap() {
		return revisionInformationMap;
	}



	public void setRepositoryProjectRootURI(URI repositoryProjectRootURI) {
		this.repositoryProjectRootURI = repositoryProjectRootURI;
	}



	public URI getRepositoryProjectRootURI() {
		return repositoryProjectRootURI;
	}



	public void setChangeLogFolder(IFolder changeLogFolder) {
		this.changeLogFolder = changeLogFolder;
	}



	public IFolder getChangeLogFolder() {
		return changeLogFolder;
	}



	public void reloadChangeLog() {
		// TODO Auto-generated method stub
		
	}
	
	
	public ArrayList<ChangeLogElement> getChangelog() {
		return changelog;
	}


	public void setChangelog(ArrayList<ChangeLogElement> changelog) {
		this.changelog = changelog;
	}
	
	public int getHeadRev(){
		return headRevision;
	}
	
	public void setHeadRev(int headRevision) {
		this.headRevision = headRevision;
	}



	public void setRevisionMap(HashMap<OWLClass,Integer> revisionMap) {
		this.revisionMap = revisionMap;
	}



	public HashMap<OWLClass,Integer> getRevisionMap() {
		return revisionMap;
	}



	public void saveRevisionMap() throws CoreException {
		
		System.out.println("Saving Revision Map");
		IFile revMapFile = project.getFolder(SVoNtConstants.SVONTFOLDER).getFile(SVoNtConstants.REVISIONMAPFILE);
		
		
		String out = "";
		for (OWLClass c: revisionMap.keySet()) {
			out += c.getURI()+SVoNtConstants.REVMAPSEPERATOR+revisionMap.get(c)+"\r\n";
			
		}
		if (revMapFile.exists()) {
			revMapFile.delete(true, null);
		}
		
		revMapFile.create(new ByteArrayInputStream(out.getBytes()), true, null);
		System.out.println("Revision Map saved");
	}
	
	
	public void loadRevisionMap() throws IOException {
		System.out.println("Loading Revision Map");
		
		revisionMap.clear();
		
		IFile revMapFile = project.getFolder(SVoNtConstants.SVONTFOLDER).getFile(SVoNtConstants.REVISIONMAPFILE);
		
		if (revMapFile.exists()) {
			OntologyStore os = OntoEclipseManager.getOntologyStore(project.getName());
			OWLOntology ont = os.getLoadedOntologie(os.getMainOntologyURI());
			
			BufferedReader br = new BufferedReader(new FileReader(revMapFile.getLocation().toFile()));
			String line;
			while((line = br.readLine()) != null) {
				String[] lineArray = line.split(SVoNtConstants.REVMAPSEPERATOR);
				for (OWLClass c : ont.getReferencedClasses()) {
					if (c.getURI().toString().equals(lineArray[0])) {
						revisionMap.put(c, Integer.valueOf(lineArray[1]));
						break;
					}
				}
			}
			
			System.out.println("Revision Map successfully loaded");
		}

		
	}



	public void saveRevisionInformationMap() throws CoreException {
		System.out.println("Saving Revision Information Map");
		IFile revMapInfoFile = project.getFolder(SVoNtConstants.SVONTFOLDER).getFile(SVoNtConstants.REVISIONINFORMATIONFILE);
		

		String out = "";
		for (int i: revisionInformationMap.keySet()) {
			RevisionInfo revInfo = revisionInformationMap.get(i);
			out += String.valueOf(i)+SVoNtConstants.REVINFOMAPSEPERATOR+revInfo.getAuthor()+SVoNtConstants.REVINFOMAPSEPERATOR+revInfo.getDate()+"\r\n";
			
		}
		if (revMapInfoFile.exists()) {
			revMapInfoFile.delete(true, null);
		}
		
		revMapInfoFile.create(new ByteArrayInputStream(out.getBytes()), true, null);
		System.out.println("Revision Information Map saved");
		
	}



	public void loadRevisionInformationMap() throws NumberFormatException, IOException {
		System.out.println("Loading Revision Information Map");
		
		revisionInformationMap.clear();
	
		IFile revMapInfoFile = project.getFolder(SVoNtConstants.SVONTFOLDER).getFile(SVoNtConstants.REVISIONINFORMATIONFILE);
		
		if (revMapInfoFile.exists()) {
	
			BufferedReader br = new BufferedReader(new FileReader(revMapInfoFile.getLocation().toFile()));
			String line;
			while((line = br.readLine()) != null) {
				String[] lineArray = line.split(SVoNtConstants.REVINFOMAPSEPERATOR);
				int rev = Integer.parseInt(lineArray[0]);
				
				if (rev > headRevision) {
					headRevision = rev;
				}
				
				RevisionInfo revInfo = new RevisionInfo();
				revInfo.setRevision(rev);
				revInfo.setAuthor(lineArray[1]);
				revInfo.setDate(lineArray[2]);
				
				
				revisionInformationMap.put(Integer.parseInt(lineArray[0]), revInfo);
			}
			
			System.out.println("Revision Information Map successfully loaded");
		}
		
	}
	
	
}
