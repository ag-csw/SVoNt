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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import de.fuberlin.agcsw.heraclitus.svont.client.utils.SVoNtConstants;

public class SVoNtManager {

	

	
	
	private static List<SVoNtProject> svontProjects;
	
	
	
	public static void init() {
		// find all open svont Stores in this workbench
		System.out.println("Init SVoNtManager");
		svontProjects = new ArrayList<SVoNtProject>();
		
		loadSVoNtProjects();
		
	}
	
	
	
	private static void loadSVoNtProjects() {
		System.out.println("search for available SVoNt Projects...");
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for( int i =0; i < projects.length; i++){ 
			
			IProject proj = projects[i];
			IFolder fold = proj.getFolder(SVoNtConstants.SVONTFOLDER);
			if (!fold.exists()) {continue;}
			IFile svFile = fold.getFile(SVoNtConstants.SVONTFILE);
			if (!svFile.exists()) continue;
			
			//file exists -- load change log URI
			try {
				
				//refresh workbench locally so that changed resources are handled
				proj.refreshLocal(IProject.DEPTH_INFINITE, null);
				
				BufferedReader br = new BufferedReader(new FileReader(svFile.getLocation().toFile()));
				String changeLogURI = br.readLine();
				
				
				SVoNtProject sp = new SVoNtProject(proj);
				URI repProjectRootURI = getRepProjectRootURI(proj);
				URI clu = URI.create(changeLogURI);
				
				sp.setRepositoryProjectRootURI(repProjectRootURI);
				sp.setChangelogURI(clu);
				sp.setChangeLogFolder(fold.getFolder(SVoNtConstants.CHANGELOGFOLDER));
				
				sp.reloadChangeLog();
				sp.loadRevisionMap();
				sp.loadRevisionInformationMap();
				
				svontProjects.add(sp);
			} catch (IllegalArgumentException e) {
				System.out.println("Project "+proj.getName() +" had problems loading svont config:");
				System.out.println("Specified URL in svont File of this project is not a URL");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		System.out.println("Found following SVoNt Projects: ");
		for (SVoNtProject sp :svontProjects) {
			System.out.println(" - "+sp.getProject().getName() + " CLU: " + sp.getChangelogURI().toString());
		}
		
		
	}



	private static URI getRepProjectRootURI(IProject proj) throws IOException {
		IFolder fold = proj.getFolder(".svn");
		IFile file  = fold.getFile("entries");
		BufferedReader br = new BufferedReader(new FileReader(file.getLocation().toFile()));
		br.readLine();
		br.readLine();
		br.readLine();
		br.readLine();
		String repProRoot = br.readLine();
		System.out.println("Found following SVN Project Repository Path: "+repProRoot);
		return URI.create(repProRoot);
		
	}



	public static SVoNtProject createSVoNtProject(IProject project,String changeLogURI) {
		

		try {
			// if here, this Project will be created.
			IFolder fold = project.getFolder(SVoNtConstants.SVONTFOLDER);
			if (!fold.exists()) {
				fold.create(true, true, null);
			}
			
			//folder created, now its time for file
			
			IFile file = fold.getFile(SVoNtConstants.SVONTFILE);
			

			InputStream is = new ByteArrayInputStream(changeLogURI.getBytes("UTF-8"));

			if (file.exists()) {
				file.delete(true, null);
			}
			
			file.create(is,true,null);
			
			System.out.println("File created");
			
			// create local changelog Folder
			IFolder chlFold = fold.getFolder(SVoNtConstants.CHANGELOGFOLDER);
			if (!chlFold.exists()) {
				chlFold.create(true, true, null);
				System.out.println("Change Log Folder created");
			}
			
			
			for (SVoNtProject sp: svontProjects) {
				if (sp.getProject().equals(project)) {
					System.out.println("Project "+project.getName() + " was loaded as SVoNt Project before");
					System.out.println("Changing ChangeLog URI");
					
					sp.setChangelogURI(URI.create(changeLogURI));
					return sp;
					
				}
			}
			
			SVoNtProject sp = new SVoNtProject(project);
			sp.setChangelogURI(URI.create(changeLogURI));
			sp.setChangeLogFolder(chlFold);
			sp.setRepositoryProjectRootURI(getRepProjectRootURI(project));
			svontProjects.add(sp);
			return sp;
			
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
	
	
	public static List<SVoNtProject> getSVoNtProjects() {
		return svontProjects;
	}
	
	
	public static SVoNtProject getSVoNtProjectByID(String id) {
		for (SVoNtProject sp : svontProjects) {
			if (sp.getProject().getName().equals(id)){
				return sp;
			}
			
		}
		
		return null;
		
	}
	
}
