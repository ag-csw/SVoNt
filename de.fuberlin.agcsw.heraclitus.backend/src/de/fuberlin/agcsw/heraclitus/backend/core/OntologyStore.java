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
package de.fuberlin.agcsw.heraclitus.backend.core;

import java.net.URI;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.mindswap.pellet.owlapi.PelletReasonerFactory;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerFactory;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.DLExpressivityChecker;

import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInfo;

public class OntologyStore {

	private OWLOntologyManager manager;
	
	
	/**
	 *  This is the Id of the Project -- equals the Eclipse Project Name
	 */
	private String id;
	

	/**
	 *  The URI of the Ontology File, e.g.
	 *  http://www.biopax.org/release/biopax-level2.owl
	 */
	private URI mainOntologyURI;
	
	
	/**
	 * Eclipse presentation of local ontology file
	 */
	private IFile mainOntologyFile;
	
	/**
	 * file://home/mrothe/eclipse-application/abc/ontology.owl
	 */
	private URI mainOntologyLocalURI;
		
	
	private IProject project;
	

	
	
	public OntologyStore(String id) {
		this.id = id;
		this.init();
	}
	
	
	private void init() {
		manager = OWLManager.createOWLOntologyManager();

		
		
	}

	public OWLOntology loadData(URI uri) throws OWLOntologyCreationException {
		return manager.loadOntology(uri);
	}
	
	
	
	public OWLOntology loadData(String uri) throws OWLOntologyCreationException {
		return loadData(URI.create(uri));
	}
	
	public OWLOntology loadOntologyFromPhysicalURI(URI uri) throws OWLOntologyCreationException {
		return manager.loadOntologyFromPhysicalURI(uri);
	}
	
	public void removeOntologyByURI(URI uri) {
		manager.removeOntology(uri);
	}
	
	public OWLOntologyManager getOntologyManager() {
		return manager;
	}
	
	
	public OWLOntology getLoadedOntologie(URI uri) {
		return manager.getOntology(uri);
	}
	
	public OWLOntology getLoadedOntologie(String uri) {
		URI physicalURI = URI.create(uri);
		return getLoadedOntologie(physicalURI);
	}
	
	public void printLoadedOnt(URI uri) {
		System.out.println("Concepts of Ontology: "+uri);
		OWLOntology ont = getLoadedOntologie(uri);
		for(OWLClass cls : ont.getReferencedClasses()) {
			System.out.println(cls);
		}
		
		System.out.println("----");
		System.out.println("-- Axioms --");
		for (OWLAxiom a: ont.getAxioms()) {
			System.out.println(a);
		}
		
		
	}
	
	public OntologyInfo getOntologyInfos(URI uri) {
		OntologyInfo oi = new OntologyInfo();
		OWLOntology ont = getLoadedOntologie(uri);

		Set<OWLOntology> importsClosure = manager.getImportsClosure(ont);
		oi.setNumClasses(ont.getReferencedClasses().size());
		oi.setNumDataProperties(ont.getReferencedDataProperties().size());
		oi.setNumObjectProperties(ont.getReferencedObjectProperties().size());
		oi.setNumIndividuals(ont.getReferencedIndividuals().size());
		oi.setURI(uri);
		
		
		DLExpressivityChecker checker = new DLExpressivityChecker(importsClosure);
//		System.out.println("Expressivity: " + checker.getDescriptionLogicName());
		oi.setExpressivity(checker.getDescriptionLogicName());

		return oi;
	}
	
	
	
	public OWLReasoner getReasoner() {
		OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
		return reasonerFactory.createReasoner(manager);
	}


	public String getId() {
		return id;
	}





	public URI getMainOntologyURI() {
		return mainOntologyURI;
	}


	/**
	 * @param mainOntology
	 */
	public void setMainOntologyURI(URI mainOntology) {
		this.mainOntologyURI = mainOntology;
	}


	public void setMainOntologyLocalURI(URI mainOntologyLocalURI) {
		this.mainOntologyLocalURI = mainOntologyLocalURI;
	}


	public URI getMainOntologyLocalURI() {
		return mainOntologyLocalURI;
	}
	




	/**
	 * @param uri
	 * @param localFile
	 * @param localURI
	 */
	public void setMainOntology(URI uri, IFile localFile, URI localURI) {
		this.mainOntologyURI = uri;
		this.mainOntologyFile = localFile;
		this.mainOntologyLocalURI = localURI;
		
	}
	
	public IFile getMainOntologyFile() {
		return this.mainOntologyFile;
	}



	public void setProject(IProject project) {
		this.project = project;
	}


	public IProject getProject() {
		return project;
	}
	
	
}
