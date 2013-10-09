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
package de.fuberlin.agcsw.heraclitus.svont.client.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizardPage;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.tigris.subversion.subclipse.core.ISVNRemoteFolder;
import org.tigris.subversion.subclipse.core.ISVNRepositoryLocation;
import org.tigris.subversion.subclipse.core.resources.SVNWorkspaceRoot;
import org.tigris.subversion.subclipse.ui.wizards.CheckoutWizard;
import org.tigris.subversion.subclipse.ui.wizards.CheckoutWizardCheckoutAsWithProjectFilePage;
import org.tigris.subversion.subclipse.ui.wizards.CheckoutWizardProjectPage;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.svont.client.core.ChangeLog;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtManager;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtProject;

public class SVoNtCheckoutWizard extends CheckoutWizard {
	
	SVoNtWizardPage page;
	String changeLogText;
	
	
	IWizardPage afterSelectionPage;
	
	public SVoNtCheckoutWizard() {
		super();
		
	}
	
	public SVoNtCheckoutWizard(ISVNRemoteFolder[] remoteFolders) {
		super(remoteFolders);
	}
	
	
	
	public void addPages() {
		super.addPages();
		page = new SVoNtWizardPage("ChangeLogLocation");
		addPage(page);
		
		
	}
	
	
	
	
	
	public IWizardPage getNextPage(IWizardPage page) {
		return getNextPage(page, true);
	}
	
	/* (non-Javadoc)
	 * @see org.tigris.subversion.subclipse.ui.wizards.CheckoutWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage, boolean)
	 */
	public IWizardPage getNextPage(IWizardPage p, boolean aboutToShow) {
//		System.out.println("Current Page: "+ p.getName());
		
//		if this is the Changelogpage then the next page is the next of the SelectionPage
		if (p == page) {
//			IWizardPage newNextPage = super.getNextPage(afterSelectionPage, aboutToShow);
//			System.out.println("After ChangeLogPage: "+newNextPage.getName());
			return afterSelectionPage;
		}
		
		IWizardPage normal =  super.getNextPage(p, aboutToShow);
		
		
		// if this is the last "normal svn" page then there is no page after that
		if (p instanceof CheckoutWizardProjectPage) {
//			System.out.println("this is last svn page returning null");
			return null;
		}
		
		
		//if this is the selection page, the next page should be the change log page
		if (normal instanceof CheckoutWizardCheckoutAsWithProjectFilePage ) {
			afterSelectionPage = normal;
//			System.out.println("Changing to SVoNT ChangeLog selector Page");
			return page;
		}
		
		
		else {
			System.out.println("Changing normal");
			return normal;
		}
//		getPages()
		
//		return normal;
		
	}
	
	
	public boolean canFinish() {
		boolean SVNcanFinish = super.canFinish();
		
		String clt = page.getChangeLogText();
		boolean correctCLT = true;
		try {
			URI.create(clt);
//			System.out.println("correct URI set as Changelog path");
		}
		catch (IllegalArgumentException e) {
//			System.out.println("No correct URI set as Changelog path");
			correctCLT = false;
		}
		
		return SVNcanFinish && correctCLT;
		
		
		
	}
	
	public void setLocation(ISVNRepositoryLocation repositoryLocation) {
		super.setLocation(repositoryLocation);
	}
	
	
	public boolean performFinish()  {
		boolean SVNRes = super.performFinish();
		if (!SVNRes) return false;
		
		String s = page.getChangeLogText();
		System.out.println("Found text: "+s);
		this.changeLogText = s;
	
		
		//do the  svont init stuff
		//create svont Project and such things
		System.out.println("Performing SVoNt Checkout finishing: Project Name :: "+getProjectName());
		
		IProject project = SVNWorkspaceRoot.getProject(getProjectName());
		
		try {
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
		
			//multithreading problem ... 
//			Thread.sleep(2000);

		

			//1. load as ontology project
			OntologyStore os = OntoEclipseManager.createNewOntologyStore(project);
			if (os != null) {
				
				//2. create svont Project
				SVoNtProject sp = SVoNtManager.createSVoNtProject(project, this.changeLogText);
			

				
				//3. do initial changelogupdate
				if (sp != null) {
					String user = "mario";
					String pwd = "redhot";
					ChangeLog.updateChangeLog(os,sp,user, pwd);
					
				}
				
				//4. get conceptTree

				ConceptTree.refreshConceptTree(os, os.getMainOntologyURI());
				
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (OWLReasonerException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		//

		
		return SVNRes;
		
	}
	
	
	public String getChangeLogText() {
		return changeLogText;
	}
	
	public void setChangeLogText(String clt) {
		this.page.setChangeLogText(clt);
	}
	

}
