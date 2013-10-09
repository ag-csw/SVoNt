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

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.owldiff.diff.owlapi.Diff;
import cz.cvut.kbss.owldiff.diff.syntactic.OntologyDiff;

/**
 * Basic ontology difference Checker 
 * 
 * http://krizik.felk.cvut.cz/km/owldiff/
 * 
 * It uses the OWLdiff Pellet difference checking of Ontologies and map the results to a concept level difference
 * 
 * @author mario
 *
 */
public class BaseDiffAlgorithm implements DiffExecutor {

	final Logger log = Logger.getLogger(BaseDiffAlgorithm.class);

	/**
	 * OWLAPI Base Ontology object
	 */
	private OWLOntology baseOnt;
	
	/**
	 * OWLAPU Update Ontologz Object
	 */
	private OWLOntology updateOnt;
	
	/**
	 * DiffResult Container object
	 */
	private DiffResult diffResult;

	/* (non-Javadoc)
	 * @see de.fuberlin.agcsw.svont.changedetection.DiffExecutor#executeDiff(java.net.URI, java.net.URI)
	 */
	public DiffResult executeDiff(URI baseOntologyURI, URI updateOntologyURI)
			throws Exception {

		//create Ontology Manager
		OWLOntologyManager baseM = OWLManager.createOWLOntologyManager();
		OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();

		
		//create OWLOntology Objects
		baseOnt = baseM.loadOntologyFromOntologyDocument(IRI.create(baseOntologyURI));
		updateOnt = updateM.loadOntologyFromOntologyDocument(IRI.create(updateOntologyURI));

		//create Basic Diff Object from OWLdiff
		Diff d = new Diff(null, baseM, baseOnt, updateM, updateOnt);
		
		// execute base owlDiff algorithm from the OWLdiff 
		// this does a simple structural diff on an axiomatic level
		log.debug("Executing basic structural diff");
		d.diff(false);

		// create a diff Result Object
		diffResult = new DiffResult();

		//recieve difference Axioms
//		Collection<OWLAxiom> addedAxioms = d.getUpdateDiff();
//		Collection<OWLAxiom> removedAxioms = d.getOriginalDiff();

		OntologyDiff addedAxioms = d.getUpdateDiff();
		OntologyDiff removedAxioms = d.getOriginalDiff();

		// test if the structual diff is empty
		if (addedAxioms.isEmpty() && removedAxioms.isEmpty()) {
			// then we finished diffing the ontologies cause there was no
			// structural change
			log.info("Ontologies are structural equal!");
			return diffResult;
		}
		
		//test if URI changed -- do not check for entailments if different
		boolean differentURI =  baseOnt.getOntologyID().getOntologyIRI().compareTo(updateOnt.getOntologyID().getOntologyIRI())  != 0 ;
		
		if ( !differentURI ) {
			// check for semantical difference
			log.info("Checking for Entailments");
			d.diff(true);
			// axioms that may be redunant in the update ontology, cause they can be
			// inferred by the base one
			Collection<OWLAxiom> possiblyRemove = d.getUpdateInferred();
			// axioms that are not lost when ontology gets replaced by the new one
			Collection<OWLAxiom> inferred = d.getOriginalInferred();
			// these axioms dont need to be considered for the semantical diff
			addedAxioms.getAxioms().removeAll(possiblyRemove);
			removedAxioms.getAxioms().removeAll(inferred);
	
			// test if semantical diff is empty
			if (addedAxioms.isEmpty() && removedAxioms.isEmpty()) {
				// then we finished diffing the ontologies cause there was no
				// semantical change
				log.info("Ontologies are semantically equal!");
				return diffResult;
			}
		} else {	
			log.info("URI changed, skipping Entailments-Check");
		}
		
		// we can be sure that there are differences which will be written
		diffResult.setEmpty(false);

		// now we have a (semantical) diff on the axiomatic level.
		// we need the diff on the conceptional level so we have to map the
		// axioms to a conceptional diff model
		log.debug("Mapping axiomatic difference to conceptional level");
		processAddedAxioms(addedAxioms);
		processRemovedAxioms(removedAxioms);

		// print the diff to the log
		if (log.isDebugEnabled()) {
			logAxiomaticDiffResult(diffResult);
			logConceptionalDiffResult(diffResult);
		}

		return diffResult;

	}

	/**
	 * Map the added Axioms to the concept level and write Results to the DiffResult object
	 * 
	 * @param axioms Collection of Axioms to process
	 */
	private void processAddedAxioms(OntologyDiff diff) {
		Collection<OWLAxiom> axioms = diff.getAxioms();
		DiffResult dr = diffResult;

		for (OWLAxiom a : axioms) {
			
			if ((a instanceof OWLClassAxiom)) {

				// search for classes that "uses" this axiom
				for (OWLClass entity : updateOnt.getClassesInSignature()) {
					if (updateOnt.getAxioms(entity).contains(a)) {
						// this class c has been added or changed
						if (baseOnt.containsEntityInSignature(entity.getIRI())) {
							// this class exists in the base ontology so its just a change
							dr.getChangedClasses().add(entity);
						} else {// this class has been added
							dr.getAddedClasses().add(entity);
						}
					}
				}
				dr.getAddedClassAxioms().add(a);

			} else if (a instanceof OWLDataPropertyAxiom) {
				// search for data properties that "uses" this axiom
				for (OWLDataProperty entity : updateOnt
						.getDataPropertiesInSignature()) {
					if (updateOnt.getAxioms(entity).contains(a)) {
						if (baseOnt.containsDataPropertyInSignature(entity.getIRI())) {
							// this data property exists in the base ontology so its just a change
							dr.getChangedDataProperties().add(entity);
						} else {
							// this data property has been added
							dr.getAddedDataProperties().add(entity);
						}
					}
				}
				dr.getAddedDataPropertyAxioms().add(a);

			} else if (a instanceof OWLObjectPropertyAxiom) {
				// search for object properties that "uses" this axiom
				for (OWLObjectProperty entity : updateOnt
						.getObjectPropertiesInSignature()) {
					if (updateOnt.getAxioms(entity).contains(a)) {
						if (baseOnt
								.containsObjectPropertyInSignature(entity.getIRI())) {
							// this data property exists in the base ontology so
							// its just a change
							dr.getChangedObjectProperties().add(entity);
						} else {
							// this data property has been added
							dr.getAddedObjectProperties().add(entity);
						}
					}
				}
				dr.getAddedObjectPropertyAxioms().add(a);

			} else if (a instanceof OWLIndividualAxiom) {
				// search for Individuals that "use" this axiom
				for (OWLIndividual entity : updateOnt.getIndividualsInSignature()) {
					if (updateOnt.getAxioms(entity).contains(a)) {
						if (entity instanceof OWLNamedIndividual) {
							OWLNamedIndividual namedIndividual = (OWLNamedIndividual)entity;
							if (baseOnt.containsIndividualInSignature(namedIndividual.getIRI())) {
								// this Individual exists in the base ontology so
								// its just a change
								dr.getChangedIndividuals().add(entity);
							} else {
								// this Individual has been added
								dr.getAddedIndividuals().add(entity);
							}
						}
					}
				}
				dr.getAddedIndividualAxioms().add(a);

			} else if (a instanceof OWLAnnotationAssertionAxiom) {
				log.debug("processing an AnnotationAxiom");
				
				OWLAnnotationAssertionAxiom annotationAx = (OWLAnnotationAssertionAxiom) a;
				OWLAnnotationSubject subject = annotationAx.getSubject();
				if (subject instanceof IRI) {
					// TODO what about annotations of anonymous classes?
					Set<OWLEntity> subjectEntities = diff.getOntology().getEntitiesInSignature((IRI)subject);
					if (subjectEntities.size() != 1) {
						log.warn("Set of subjects of annotation axiom " + a + " has size " + subjectEntities.size() + ". Should be 1");
						continue;
					}
					OWLEntity entity = subjectEntities.iterator().next();
					if (!baseOnt.containsEntityInSignature(entity)) {
						processAddingHiddenEntities(dr, entity);
					} 
					dr.getAddedAnnotationAxioms().add(a);
				} else {
					// subject is anonymous 
					log.debug("Annotation with anonymous subject: " + a);
				}
			} else if (a instanceof OWLDeclarationAxiom) {
				log.debug("processing an DeclarationAxiom");
				OWLDeclarationAxiom decAx = (OWLDeclarationAxiom) a;
				OWLEntity entity = decAx.getEntity();
				if (!baseOnt.containsEntityInSignature(entity)) {
					processAddingHiddenEntities(dr, entity);
				} 
				dr.getAddedDeclarationAxioms().add(a);
			} else
				dr.getAddedOtherAxioms().add(a);
		}

	}
	
	
	private void processAddingHiddenEntities(DiffResult dr, OWLEntity entity) {
			// its an annotation of something that wasn't there before

			if (entity.isOWLClass()) {
				dr.getAddedClasses().add((OWLClass) entity );
			}
			if (entity.isOWLDataProperty()) {
				dr.getAddedDataProperties().add((OWLDataProperty) entity );
			}
			if (entity.isOWLObjectProperty()) {
				dr.getAddedObjectProperties().add((OWLObjectProperty) entity );
			}
			if (entity.isOWLNamedIndividual()) {
				dr.getAddedIndividuals().add((OWLIndividual) entity );
			}
	}
	
	
	

	/**
	 * Map the removed Axioms to the concept level and write Results to the DiffResult object
	 * 
	 * @param axioms Collection of Axioms to process
	 */
	private void processRemovedAxioms(OntologyDiff diff) {
		Collection<OWLAxiom> axioms = diff.getAxioms();
		
		DiffResult dr = diffResult;

		for (OWLAxiom a : axioms) {
			if ((a instanceof OWLClassAxiom)) {
				// search for classes that "uses" this axiom
				for (OWLClass c : baseOnt.getClassesInSignature()) {
					Set<OWLClassAxiom> caxioms = baseOnt.getAxioms(c);
					if (caxioms.contains(a)) {
						// this class c has been removed or changed
						if (updateOnt.containsClassInSignature(c.getIRI())) {
							// this class exists in the base ontology so its
							// just a change
							dr.getChangedClasses().add(c);
						} else {
							// this class has been removed
							dr.getRemovedClasses().add(c);
						}
					}
				}
				dr.getRemovedClassAxioms().add(a);
			} else if (a instanceof OWLDataPropertyAxiom) {
				// search for classes that "uses" this axiom
				for (OWLDataProperty dp : baseOnt.getDataPropertiesInSignature()) {
					Set<OWLDataPropertyAxiom> dpaxioms = baseOnt.getAxioms(dp);
					if (dpaxioms.contains(a)) {
						// this class c has been removed or changed
						if (updateOnt
								.containsDataPropertyInSignature(dp.getIRI())) {
							// this class exists in the base ontology so its
							// just a change
							dr.getChangedDataProperties().add(dp);
						} else {
							// this class has been removed
							dr.getRemovedDataProperties().add(dp);
						}
					}
				}

				dr.getRemovedDataPropertyAxioms().add(a);
			} else if (a instanceof OWLObjectPropertyAxiom) {
				// search for classes that "uses" this axiom
				for (OWLObjectProperty op : baseOnt.getObjectPropertiesInSignature()) {
					Set<OWLObjectPropertyAxiom> opaxioms = baseOnt
							.getAxioms(op);
					if (opaxioms.contains(a)) {
						// this class c has been removed or changed
						if (updateOnt.containsObjectPropertyInSignature(op
								.getIRI())) {
							// this class exists in the base ontology so its
							// just a change
							dr.getChangedObjectProperties().add(op);
						} else {
							// this class has been removed
							dr.getRemovedObjectProperties().add(op);
						}
					}
				}
				dr.getRemovedObjectPropertyAxioms().add(a);
			} else if (a instanceof OWLIndividualAxiom) {
				// search for classes that "uses" this axiom
				for (OWLIndividual in : baseOnt.getIndividualsInSignature()) {
					Set<OWLIndividualAxiom> inaxioms = baseOnt.getAxioms(in);
					if (inaxioms.contains(a)) {
						if (in.isNamed()) {
							// this class c has been removed or changed
							if (updateOnt.containsIndividualInSignature(((OWLNamedIndividual)in).getIRI())) {
								// this class exists in the base ontology so its
								// just a change
								dr.getChangedIndividuals().add(in);
							} else {
								// this class has been removed
								dr.getRemovedIndividuals().add(in);
							}
						}
					}
				}
				dr.getRemovedIndividualAxioms().add(a);
			} else if (a instanceof OWLAnnotationAssertionAxiom) {
				log.debug("processing an Annotation Assertion Axiom");
				OWLAnnotationAssertionAxiom annotationAx = (OWLAnnotationAssertionAxiom) a;
				OWLAnnotationSubject subject = annotationAx.getSubject();
				if (subject instanceof IRI) {
					// TODO what about annotations of anonymous classes?
					Set<OWLEntity> subjectEntities = diff.getOntology().getEntitiesInSignature((IRI)subject);
					if (subjectEntities.size() != 1) {
						log.warn("Set of subjects of annotation axiom " + a + " has size " + subjectEntities.size() + ". Should be 1");
						continue;
					}
					OWLEntity entity = subjectEntities.iterator().next();

					if (!updateOnt.containsEntityInSignature(entity)) {
						processRemovingHiddenEntities(dr, entity);
					}
				} else {
					// subject is anonymous 
					log.debug("Annotation with anonymous subject: " + a);
				}
				
				dr.getRemovedAnnotationAxioms().add(a);
			} else if (a instanceof OWLDeclarationAxiom) {
					log.debug("processing an DeclarationAxiom");
					OWLDeclarationAxiom decAx = (OWLDeclarationAxiom) a;
					OWLEntity entity = decAx.getEntity();
					if (!updateOnt.containsEntityInSignature(entity)) {
						processRemovingHiddenEntities(dr, entity);
					} 
					dr.getRemovedDeclarationAxioms().add(a);
			} else
				dr.getRemovedOtherAxioms().add(a);
		}

	}
	
	
	private void processRemovingHiddenEntities(DiffResult dr, OWLEntity entity) {
		// its an annotation of something that wasn't there before
		if (entity.isOWLClass()) {
			dr.getRemovedClasses().add((OWLClass) entity );
		}
		if (entity.isOWLDataProperty()) {
			dr.getRemovedDataProperties().add((OWLDataProperty) entity );
		}
		if (entity.isOWLObjectProperty()) {
			dr.getRemovedObjectProperties().add((OWLObjectProperty) entity );
		}
		if (entity.isOWLNamedIndividual()) {
			dr.getRemovedIndividuals().add((OWLIndividual) entity );
		}
}
	

	/**
	 * Writes the axiomatic Changes to Log
	 * 
	 * @param dr The diffResult 
	 */
	private void logAxiomaticDiffResult(DiffResult dr) {
		log.debug("------------------------------------------");
		log.debug("Axiomatic Diff Result:");
		log.debug("------------------------------------------");
		logAxiomTypes(dr.getAddedClassAxioms(), "Class", "added");
		logAxiomTypes(dr.getAddedIndividualAxioms(), "Individual", "added");
		logAxiomTypes(dr.getAddedDataPropertyAxioms(), "DataProperty", "added");
		logAxiomTypes(dr.getAddedObjectPropertyAxioms(), "ObjectProperty",
				"added");
		logAxiomTypes(dr.getAddedAnnotationAxioms(), "Annotation", "added");
		logAxiomTypes(dr.getAddedDeclarationAxioms(), "Declaration", "added");
		logAxiomTypes(dr.getAddedOtherAxioms(), "Other", "added");

		logAxiomTypes(dr.getRemovedClassAxioms(), "Class", "removed");
		logAxiomTypes(dr.getRemovedIndividualAxioms(), "Individual", "removed");
		logAxiomTypes(dr.getRemovedDataPropertyAxioms(), "DataProperty",
				"removed");
		logAxiomTypes(dr.getRemovedObjectPropertyAxioms(), "ObjectProperty",
				"removed");
		logAxiomTypes(dr.getRemovedAnnotationAxioms(), "Annotation", "removed");
		logAxiomTypes(dr.getRemovedDeclarationAxioms(), "Declaration", "removed");
		logAxiomTypes(dr.getRemovedOtherAxioms(), "Other", "removed");
		log.debug("-------- COMPLETE ----------");

	}

	/**
	 * Logs specific axiomatic Type
	 * 
	 * @param ax Collection of Axioms to Log
	 * @param atype Type of Axioms
	 * @param chgtype ChangeType "added" "removed" "changed"
	 */
	private void logAxiomTypes(Collection<OWLAxiom> ax, String atype,
			String chgtype) {
		if (ax.size() != 0) {
			log.debug(chgtype.toUpperCase() + " " + atype + " Axioms:");
			for (OWLAxiom a : ax) {
				log.debug(a);
			}
		} else
			log.debug("No " + chgtype + " " + atype + " Axioms");
		log.debug("--------");
	}

	/**
	 * Writes Conceptional Changes to Log
	 * 
	 * @param dr The DiffResult Object
	 */
	private void logConceptionalDiffResult(DiffResult dr) {
		log.debug("------------------------------------------");
		log.debug("Conceptional Diff Mapping Result:");
		log.debug("------------------------------------------");
		logConceptionalTypes(dr.getAddedClasses(), "Class", "added");
		logConceptionalTypes(dr.getAddedDataProperties(), "Data Property",
				"added");
		logConceptionalTypes(dr.getAddedObjectProperties(), "Object Property",
				"added");
		logConceptionalTypes(dr.getAddedIndividuals(), "Individual", "added");
		logConceptionalTypes(dr.getAddedDataTypes(), "Datatype", "added");

		logConceptionalTypes(dr.getChangedClasses(), "Class", "changed");
		logConceptionalTypes(dr.getChangedDataProperties(), "Data Property",
				"changed");
		logConceptionalTypes(dr.getChangedObjectProperties(),
				"Object Property", "changed");
		logConceptionalTypes(dr.getChangedIndividuals(), "Individual",
				"changed");
		logConceptionalTypes(dr.getChangedDataTypes(), "Datatype", "changed");

		logConceptionalTypes(dr.getRemovedClasses(), "Class", "removed");
		logConceptionalTypes(dr.getRemovedDataProperties(), "Data Property",
				"removed");
		logConceptionalTypes(dr.getRemovedObjectProperties(),
				"Object Property", "removed");
		logConceptionalTypes(dr.getRemovedIndividuals(), "Individual",
				"removed");
		logConceptionalTypes(dr.getRemovedDataTypes(), "Datatype", "removed");

		log.debug("-------- COMPLETE ----------");

	}

	/**
	 * Logs the specific Entity Typ
	 * 
	 * @param ents Collection of Entities to log
	 * @param eType Typ of Entity
	 * @param chgtype Changetyp "added" "removed" "changed"
	 */
	private void logConceptionalTypes(Collection<? extends OWLObject> ents,
			String eType, String chgtype) {
		if (ents.size() != 0) {
			log.debug(chgtype.toUpperCase() + " " + eType + ":");
			for (OWLObject e : ents) {
				log.debug(e);
			}
		} else
			log.debug("No " + chgtype + " " + eType);
		log.debug("--------");

	}

}
