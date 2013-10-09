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
import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import cz.cvut.kbss.owldiff.diff.cex.impl.Diff;

/**
 * Implementation of the CEX Diff from OWLdiff
 * All DiffL and DiffR Results from the CEX Diff are semantical Changed Classes 
 * 
 * http://krizik.felk.cvut.cz/km/owldiff/
 * 
 * @author mario
 *
 */
public class CEXDiffAlgorithm implements DiffExecutor {

	final Logger log = Logger.getLogger(CEXDiffAlgorithm.class);

	private Diff cexDiffForward, cexDiffReverse;
	private OWLOntology baseOnt;
	private OWLOntology updateOnt;
	private DiffResult diffResult;

	/* (non-Javadoc)
	 * @see de.fuberlin.agcsw.svont.changedetection.DiffExecutor#executeDiff(java.net.URI, java.net.URI)
	 */
	public DiffResult executeDiff(URI baseOntologyURI, URI updateOntologyURI)
			throws Exception {

		
		//inits the OntologyManager
		OWLOntologyManager baseM = OWLManager.createOWLOntologyManager();
		OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();

		//loads the Ontologies
		baseOnt = baseM.loadOntologyFromOntologyDocument(IRI.create(baseOntologyURI));
		updateOnt = updateM.loadOntologyFromOntologyDocument(IRI.create(updateOntologyURI));

		// first we execute the BaseDiff and use its results
		diffResult = new BaseDiffAlgorithm().executeDiff(baseOntologyURI,
				updateOntologyURI);

		cexDiffForward = new Diff(null);
		cexDiffReverse = new Diff(null);
		
		//clean all except classes out of the ontologies, cause CEX-Diff will fail with "ontologie is no EL" else
		prepareOntologyForCEX(baseM,baseOnt);
		prepareOntologyForCEX(updateM,updateOnt);

		
		//then we execute the CEX Diff from of the OWLdiff implementation
		cexDiffForward.diff(baseM, baseOnt, updateM, updateOnt);
		cexDiffReverse.diff(updateM, updateOnt, baseM, baseOnt);

		
		//all classes that are in the sets of DiffL and DiffR has been changed and gets added to the diffResult
		processChangedClasses(cexDiffForward.getDiffL());
		processChangedClasses(cexDiffForward.getDiffR());
		
		processChangedClasses(cexDiffReverse.getDiffL());
		processChangedClasses(cexDiffReverse.getDiffR());
		
		
		if (log.isDebugEnabled()) {
			 showDiffClasses(cexDiffForward.getDiffL(),"ForwardDiff Left Classes:");
			 showDiffClasses(cexDiffForward.getDiffR(),"ForwardDiff Right Classes:");
			 showDiffClasses(cexDiffReverse.getDiffL(),"ReverseDiff Left Classes:");
			 showDiffClasses(cexDiffReverse.getDiffR(),"ReverseDiff Right Classes:");	
		}

		return diffResult;
	}
	
	/**
	 * Adds Set of Changed Classes to the DiffResult
	 * 
	 * @param classes Set of Classes to add
	 */
	private void processChangedClasses(Set<OWLClass> classes) {
		for (OWLClass c : classes) {
			if (!diffResult.getChangedClasses().contains(c)) {
				diffResult.getChangedClasses().add(c);
			}
		}
	}
	
	
	/**
	 * Preparing an Ontology for the EL Description Logic Language used for OWLdiff.
	 * It removes Individuals, DataProperties and ObjectProperties
	 * 
	 * @param man OntologyManager of the Ontology
	 * @param ont The Ontology to Prepare
	 * @throws OWLOntologyChangeException
	 */
	private void prepareOntologyForCEX(OWLOntologyManager man, OWLOntology ont) throws OWLOntologyChangeException {
		if (log.isDebugEnabled())
			log.debug("Preparing Ontology for CEX Diff");
		OWLEntityRemover remover = new OWLEntityRemover(man, Collections.singleton(ont));
		
		// TODO check if this means the same thing as ont.getReferencedIndividuals()
		Set<OWLNamedIndividual> individuals = ont.getIndividualsInSignature();
		if (log.isDebugEnabled())
			log.debug("Removing "+individuals.size()+" Individuals from Ontology");
		for(OWLIndividual ind : individuals) {
			if (ind.isNamed()) {
				((OWLNamedIndividual)ind).accept(remover);
			} else {
				log.warn("Cannot remove anoynmous individual: " + ind);
			}
		}
		
		Set<OWLDataProperty> dataProperties = ont.getDataPropertiesInSignature();
		if (log.isDebugEnabled())
			log.debug("Removing "+dataProperties.size()+" DataProperties from Ontology");
		for (OWLDataProperty dp : dataProperties) {
			dp.accept(remover);
		}
		
		Set<OWLObjectProperty> objectProperties = ont.getObjectPropertiesInSignature();
		if (log.isDebugEnabled())
			log.debug("Removing "+objectProperties.size()+" ObjectProperties from Ontology");
		for (OWLObjectProperty op : objectProperties) {
			op.accept(remover);
		}
		
		man.applyChanges(remover.getChanges());
		
		log.debug("Ontology Prepared for CEX");
	}
	

	/**
	 * Logs the CEX Result
	 * 
	 * @param classes
	 * @param type
	 */
	private void showDiffClasses(Set<OWLClass> classes, String type) {
		log.debug(type);
		for (OWLClass c : classes) {
			log.debug(c);
		}
		log.debug("---------------");
	}

}
