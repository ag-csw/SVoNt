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
package de.fuberlin.agcsw.heraclitus.backend.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.owl.inference.OWLReasonerException;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformation;

public class OpenConceptTreeAction implements IObjectActionDelegate {

	private IWorkbenchPart part;
 


	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
		
	}
	@Override
	public void run(IAction action) {
		ITreeSelection sel= (ITreeSelection) part.getSite().getSelectionProvider().getSelection();

		IProject p = (IProject) sel.iterator().next();
		OntologyStore os = OntoEclipseManager.getOntologyStore(p.getName());
		
		if (os != null) {
			
			//then this store exist
			try {
				ConceptTree.refreshConceptTree(os, os.getMainOntologyURI());
				OntologyInformation.refreshOntologyInformation(os,os.getMainOntologyURI());
				
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.OntologyInformationView");
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
				
			} catch (OWLReasonerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}


}
