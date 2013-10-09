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
package de.fuberlin.agcsw.heraclitus.backend;

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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTreeContentProvider;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTreeLabelProvider;

public class OntoEclipseManager {

	
	
	
	/**
	 * The InfoStore ist a non-persistent Ontology Store without a corresponding 
	 * Ontology Project
	 */
	private static OntologyStore infoStore;
	
	
	private static List<OntologyStore> projectStores;
	
	
	
	public static void init() {
		System.out.println("Initialising Heraclitus Backend");
		setInfoStore(new OntologyStore("info"));
		
		projectStores = new ArrayList<OntologyStore>();
		loadExistingOntologyProjects();
		
		
		//Init ConceptTree 
//		try {
//			IViewDescriptor desc = PlatformUI.getWorkbench().getViewRegistry().find("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
//			if (desc == null) {
//				System.out.println("No Descriptor for View foudn in Registry");
//			}
//			
//			IViewPart vp = desc.createView();
//			vp.setFocus();
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
//			if (ConceptTree.ontologyTreeViewer == null) {
//				System.out.println("Treeviewer not init");
//			}
//			
////			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.OntologyInformationView");
//
//		} catch (PartInitException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		

		
		
	}



	private static void loadExistingOntologyProjects() {
		System.out.println("search for available Ontology Projects in Workspace");
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for( int i =0; i < projects.length; i++){  
			IProject proj = projects[i];
			IFolder fold = proj.getFolder(".ontology");
			if (!fold.exists()) {continue;}
			IFile svFile = fold.getFile("ontology");
			if (!svFile.exists()) continue;
			
			//file exists -- load change log URI
			try {
				BufferedReader br = new BufferedReader(new FileReader(svFile.getLocation().toFile()));
				String relMainOntologyPath = br.readLine();
				
				initOntologyStore(proj.getFile(relMainOntologyPath),proj);
				
			} catch (IllegalArgumentException e) {
				System.out.println("Project "+proj.getName() +" had problems loading ontology config:");
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		System.out.println("Following ontology Projects found: ");
		for (OntologyStore os:projectStores) {
			System.out.println(" - " + os.getId());
		}
		
	}

		
	public static OntologyStore initOntologyStore(IFile ontFile, IProject p) {
			
			// create OntologyStore
			
			OntologyStore os = getOntologyStore(p.getName());
			if (os == null) {
				os = createNewProjectStore(p.getName());
			}

			System.out.println("Adding ontology: "+ontFile.getName());
			URI ontURI =  ontFile.getLocationURI();
			System.out.println("OntURI: "+ontURI);
			try {
				OWLOntology ont = os.loadData(ontURI);
				os.setMainOntology(ont.getURI(),ontFile,ontURI);
				os.setProject(p);
				System.out.println("Ontology successfully loaded: "+ ont.getURI());
				System.out.println("To OntologyStore: "+ os.getId());
				return os;
			} catch (OWLOntologyCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
			return null;
	}
	
	
	
	public static OntologyStore createNewOntologyStore(IProject p) throws CoreException {
		IFile file = searchForOntology(p.members());
			
		if (file != null) {
			//Found Ont
			OntologyStore os = initOntologyStore(file, p);
			//persistent set this Project as ontology project
			savePersistentOntologyProject(file,p);
			return os;	
		} else return null;
				

		
	}

	
	private static void savePersistentOntologyProject(IFile f, IProject p) {
		try {
			// if here, this Project will be created.
			IFolder fold = p.getFolder(".ontology");
			if (!fold.exists()) {
				fold.create(true, true, null);
			}
			
			//folder created, now its time for file
			
			IFile file = fold.getFile("ontology");
			System.out.println("Relativ Path of ontologyFile: "+f.getProjectRelativePath().toString());

			InputStream is = new ByteArrayInputStream(f.getProjectRelativePath().toString().getBytes("UTF-8"));

			if (file.exists()) {
				file.delete(true, null);
			}
			
			file.create(is,true,null);
			
			System.out.println("Ontology Prop File created");
			
			
			
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private static IFile searchForOntology(IResource[] res) throws CoreException {
		for (int i = 0;i<res.length;i++) {
			if (res[i].getType() == IResource.FOLDER) {
				IFolder f = (IFolder) res[i];
				//dont look into changelog
				if (f.getName().equals("changelog")) continue;
				System.out.println("Inspecting: "+f.getName());
				IFile file = searchForOntology(f.members());
				if (file != null) {
					return file;
				}
				
			}
			if (res[i].getType() == IResource.FILE) {
				IFile file = (IFile) res [i];
				System.out.println("Inspecting: "+file.getName());
				System.out.println("Extension: "+file.getFileExtension());
				if (file.getFileExtension() != null) {
					if (file.getFileExtension().equals("owl")) {
						//found ontology
						return file;
					}
				}
			}
		}
	

	return null;
}


	public static void setInfoStore(OntologyStore infoStore) {
		OntoEclipseManager.infoStore = infoStore;
	}



	public static OntologyStore getInfoStore() {
		return infoStore;
	}
	
	
	public static OntologyStore createNewProjectStore(String id) {
		OntologyStore os = new OntologyStore(id);
		projectStores.add(os);
		return os;
	}
	
	
	public static OntologyStore getOntologyStore(String id) {
		
		for (OntologyStore os: projectStores) {
			if (os.getId().equals(id)) return os;
			
		}
		
		return null;
	}
	
}
