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
package de.fuberlin.agcsw.heraclitus.backend.core.conceptTree;

import java.net.URI;
import java.util.ArrayList;
import java.util.Set;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;


public class ConceptTree {


	

	public static TreeNode[] ontologyTreeData;
	public static TreeViewer ontologyTreeViewer;

	public static OntologyStore ontologyStore;
	
	
	private static TreeNode[] createConceptTree(URI uri,OntologyStore store) throws OWLReasonerException{

		ontologyStore = store;
		
		
		OWLOntology ont = store.getLoadedOntologie(uri);
		Set<OWLOntology> importsClosure = store.getOntologyManager().getImportsClosure(ont);
		OWLReasoner r = store.getReasoner();
		r.loadOntologies(importsClosure);
		r.classify();


//		System.out.println(ont.getURI().toString().substring(0,ont.getURI().toString().indexOf('#')));
		TreeNode res = buildConceptTree(null, ontologyStore.getOntologyManager().getOWLDataFactory().getOWLThing(),r);
		
		System.out.println("Finished building conceptTree");
		TreeNode[] resa = new TreeNode[1];
		resa[0]  = res;
		
		return resa;

	}
	
	private static TreeNode buildConceptTree(TreeNode parent,OWLClass cl,OWLReasoner r) throws OWLReasonerException {
		ConceptTreeNode ctn = new ConceptTreeNode(cl);
			
		
		TreeNode td = new TreeNode(ctn);
		td.setParent(parent);
		ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
		
		//recursivly build up tree
		Set<Set<OWLClass>> subs = r.getSubClasses(cl);
		for (Set<OWLClass> eqSubs:subs) {
			for (OWLClass sub:eqSubs) {
				if (sub != ontologyStore.getOntologyManager().getOWLDataFactory().getOWLNothing())
					childs.add(buildConceptTree(td, sub,r));
			}
		}
		
		if( !childs.isEmpty())
			td.setChildren(childs.toArray(new TreeNode[0]));
		else td.setChildren(null);
		return td;
	}
	
	


	public static void refreshConceptTree(OntologyStore os,URI ontURI) throws OWLReasonerException {

		System.out.println("Refreshing concept Tree");
		
		
		ontologyTreeData = createConceptTree(ontURI,os);
		System.out.println("Concept Tree build");
		if (ConceptTree.ontologyTreeViewer!= null) {
			System.out.println("Setting new Tree Data");
			ConceptTree.ontologyTreeViewer.setInput(ontologyTreeData);;
			
		} 
		
		
	}
	
	
}
