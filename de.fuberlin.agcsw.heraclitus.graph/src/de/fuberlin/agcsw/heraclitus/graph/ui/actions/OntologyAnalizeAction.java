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
package de.fuberlin.agcsw.heraclitus.graph.ui.actions;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformation;
import de.fuberlin.agcsw.heraclitus.graph.GraphAnalyse;

public class OntologyAnalizeAction implements IObjectActionDelegate {

	private IWorkbenchPart part;
	
	
	public OntologyAnalizeAction() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
		
	}

	@Override
	public void run(IAction action) {
		
		OntologyStore os;
		URI ontURI;
		ITreeSelection sel= (ITreeSelection) part.getSite().getSelectionProvider().getSelection();

		IResource p = (IResource) sel.iterator().next();
		System.out.println("selected resource: "+p.getName());
		try {
		
			if (OntoEclipseManager.getOntologyStore(p.getProject().getName()) == null) {
				System.out.println("Using InfoStore to analyse Ontology");
				//this Project isn't loaded as Ontologyproject
				//use Infostore as Ontologystore
				
				os = OntoEclipseManager.getInfoStore();
				OWLOntology ont = os.loadOntologyFromPhysicalURI(p.getLocationURI());
				ontURI = ont.getURI();
				
			}
			else {
				System.out.println("Using OntologyStore to analyse Ontology");
				//its an ontology in an ontology project -- means it is loaded into a store
				// so just find the right store and refresh tree
				os = OntoEclipseManager.getOntologyStore(p.getProject().getName());
				ontURI = os.getMainOntologyURI();

				
			}
			
			//refresh the Viewers
			ConceptTree.refreshConceptTree(os, ontURI);
			OntologyInformation.refreshOntologyInformation(os,ontURI);
			
			
			//
			GraphAnalyse.ontologyInfo = os.getOntologyInfos(ontURI);
			GraphAnalyse.ontologyStore = os;
			
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.graph.ui.GraphAnalyseView");
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.OntologyInformationView");
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLReasonerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
