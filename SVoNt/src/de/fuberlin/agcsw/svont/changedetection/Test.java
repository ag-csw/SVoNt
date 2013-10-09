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
package de.fuberlin.agcsw.svont.changedetection;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI iri = IRI.create(new File("/Users/ralph/Uni/Corporate Semantic Web/Arbeitspaket SVoNt/runtime-EclipseApplication/ChangelogTest4/ontology/ontology.owl"));
		try {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);
			
			IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI();
			System.out.println(ontologyIRI);
			System.out.println();
			
			Set<OWLEntity> entities = ontology.getSignature();
			
			for (OWLEntity entity : entities) {
				System.out.println(entity + ":");
				
				Set<OWLAnnotationAssertionAxiom> axioms = entity.getAnnotationAssertionAxioms(ontology);
				for (OWLAnnotationAssertionAxiom axiom : axioms) {
					
					System.out.print(axiom.getClass().getName()+": ");
					System.out.print(axiom.getAxiomType() + ", ");
					System.out.println(axiom);
					
					OWLAnnotationSubject subject = axiom.getSubject();
					if (subject instanceof IRI) {
						Set<OWLEntity> subjects = ontology.getEntitiesInSignature((IRI)subject);
						if (subjects.size() != 1) {
							System.out.println("Ooops, subject size is " + subjects.size());
						} else {
							OWLEntity subjectEntity = subjects.iterator().next();
							System.out.println(subjectEntity);
						}
					}
					
					System.out.println();
				}
				System.out.println();
			}
			
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

	}

}
