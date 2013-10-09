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
package de.fuberlin.agcsw.svont.validation;

import java.net.URI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;

import com.clarkparsia.pellet.expressivity.ExpressivityChecker;
import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * The Pellet Validation Implementation
 * 
 * @author mario
 * 
 */
public class PelletValidator implements Validator {
	// Pellet 2.0 RC6

	Logger log = Logger.getLogger(PelletValidator.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.agcsw.svont.validation.Validator#validate(java.net.URI)
	 */
	public ValidationReport validate(URI ontURI) throws Exception {

		log.debug("Loading URI:" + ontURI);
		ValidationReport vr = new ValidationReport();
		try {

			//Ceate OWL API OntologyManager and Ontology Object
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(ontURI));

			//Create reasoner and bind Ontology 
			PelletReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();			
			PelletReasoner reasoner = reasonerFactory.createReasoner(ontology);
			reasoner.prepareReasoner();

			//1.  if ontologie got successfully loaded its syntax is ok
			vr.setSyntax(true);

			//2.  test for inconsistencies
			if (!reasoner.isConsistent()) {
				log.debug("check consistency of Ontologie: "
						+ reasoner.isConsistent());
				try {
					vr.setInconsistentClasses(reasoner.getUnsatisfiableClasses().getEntities());
				} catch (InconsistentOntologyException e) {
					log.debug("Got InconsistentOntologyException while fetching inconsistent classes");
					//this sucks -> why we get this exception if we try to get the inconsistent classes!!??
				}
					vr.setValidationMessage("Ontology is not consistent");
				return vr;
			}
			// else ontology is consistent
			vr.setConsistent(true);

			//3. check expressivity
			ExpressivityChecker checker = new ExpressivityChecker(reasoner
					.getKB());
			log.debug("Expressivity: " + checker.getExpressivity());
			boolean isEL = checker.getExpressivity().isEL();
			if (!isEL) {
				log.debug("expressivity is el?:" + isEL);
				vr.setValidationMessage("Expressivity is not EL");
				return vr;
			}

			//all done all ok, set valid to true
			vr.setExpressivity(true);
			vr.setValid(true);
		} catch (UnparsableOntologyException pe) {
			vr
					.setValidationMessage("There are syntactic errors in this Ontologie\r\n"
							+ pe.getMessage());
		}
		return vr;

	}

}
