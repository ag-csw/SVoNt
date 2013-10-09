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
package de.fuberlin.agcsw.svont.changedetection.smartcex;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.NonMappingOntologyIRIMapper;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import de.fuberlin.agcsw.svont.changedetection.smartcex.profiles.CEXOWL2ELProfile;
import de.fuberlin.agcsw.svont.changedetection.smartcex.profiles.PelletOWL2ELProfile;

/**
 * @author ralph
 * 
 */
public class PartitionEL {

	enum ReasonerCompatibilityMode {
		NONE(OWL2ELProfile.class, null),
		PELLET(PelletOWL2ELProfile.class, new PelletReasonerFactory()),
		CEX(CEXOWL2ELProfile.class, null);

		private Class<? extends OWLProfile> profileClass;
		private OWLReasonerFactory reasonerFactory;

		private ReasonerCompatibilityMode(Class<? extends OWLProfile> profileClass, OWLReasonerFactory reasonerFactory) {
			this.profileClass = profileClass;
			this.reasonerFactory = reasonerFactory;
		}

		private Class<? extends OWLProfile> getProfileClass() {
			return profileClass;
		}

		private OWLReasonerFactory getReasonerFactory() {
			return reasonerFactory;
		}
	}

	/**
	 * Splits the given ontology into two partitions: The set of OWL EL
	 * compliant axioms and the set of axioms which are not compliant with the
	 * OWL EL profile. The EL compliant partition is stored in the left part of
	 * resulting pair, and the EL non-compliant partition is stored in the right
	 * part.
	 * 
	 * @param sourceOnto
	 *            The source ontology to be partitioned.
	 * @param compatibilityMode
	 *            Specifies the reasoner with which the resulting partition
	 *            should be compatible (e.g. Pellet has a different notion of EL
	 *            than other reasoners).
	 * @return A pair containing two ontologies. The left part is the partition
	 *         of the source ontology with all EL-compliant axioms. The right
	 *         part is the partition of the source ontology with all
	 *         non-EL-compliant axioms. If the source ontology already conforms
	 *         to the OWL-EL profile, then the left part of the result contains
	 *         the source ontology, and the right part is null.
	 * @throws OWLOntologyCreationException
	 *             If there is an error loading the source ontology.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Pair<OWLOntology, OWLOntology> partition(OWLOntology sourceOnto, ReasonerCompatibilityMode compatibilityMode)
			throws OWLOntologyCreationException, InstantiationException, IllegalAccessException {

		OWLProfile elProfile = compatibilityMode.getProfileClass().newInstance();

		OWLProfileReport report = elProfile.checkOntology(sourceOnto);

		if (report.isInProfile()) {
			return new ImmutablePair<OWLOntology, OWLOntology>(sourceOnto, null);
		}

		HashSet<OWLAxiom> nonELAxioms = new HashSet<OWLAxiom>();

		Set<OWLProfileViolation> violations = report.getViolations();
		for (OWLProfileViolation violation : violations) {
			nonELAxioms.add(violation.getAxiom());
		}

		OWLOntologyID ontologyID = sourceOnto.getOntologyID();
		IRI ontologyIRI = ontologyID.getOntologyIRI();

		IRI targetELOntologyIRI = IRI.create(ontologyIRI.toString() + "/ELpart");
		IRI targetNonELOntologyIRI = IRI.create(ontologyIRI.toString() + "/nonELpart");

		OWLOntologyManager targetELOntoManager = OWLManager.createOWLOntologyManager();
		targetELOntoManager.addIRIMapper(new NonMappingOntologyIRIMapper());
		OWLOntology targetELOnto = targetELOntoManager.createOntology(new OWLOntologyID(targetELOntologyIRI));

		OWLOntologyManager targetNonELOntoManager = OWLManager.createOWLOntologyManager();
		targetNonELOntoManager.addIRIMapper(new NonMappingOntologyIRIMapper());
		OWLOntology targetNonELOnto = targetNonELOntoManager.createOntology(new OWLOntologyID(targetNonELOntologyIRI));

		Set<OWLAxiom> allAxioms = sourceOnto.getAxioms();
		for (OWLAxiom axiom : allAxioms) {
			if (nonELAxioms.contains(axiom)) {
				targetNonELOntoManager.addAxiom(targetNonELOnto, axiom);
				System.out.println("- " + axiom);
			} else {
				targetELOntoManager.addAxiom(targetELOnto, axiom);
				System.out.println("+ " + axiom);
			}
		}

		return new ImmutablePair<OWLOntology, OWLOntology>(targetELOnto, targetNonELOnto);
	}

	/**
	 * Splits the given ontology into two partitions: The set of OWL EL
	 * compliant axioms and the set of axioms which are not compliant with the
	 * OWL EL profile. The EL compliant partition is stored in the left part of
	 * resulting pair, and the EL non-compliant partition is stored in the right
	 * part.
	 * 
	 * @param input
	 *            The IRI from where the source ontology can be loaded.
	 * @param compatibilityMode
	 *            Specifies the reasoner with which the resulting partition
	 *            should be compatible (e.g. Pellet has a different notion of EL
	 *            than other reasoners).
	 * @return A pair containing two ontologies. The left part is the partition
	 *         of the source ontology with all EL-compliant axioms. The right
	 *         part is the partition of the source ontology with all
	 *         non-EL-compliant axioms. If the source ontology already conforms
	 *         to the OWL-EL profile, then the left part of the result contains
	 *         the source ontology, and the right part is null.
	 * 
	 * @throws OWLOntologyCreationException
	 *             If there is an error loading the source ontology.
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static Pair<OWLOntology, OWLOntology> partition(IRI input, ReasonerCompatibilityMode compatibilityMode) throws OWLOntologyCreationException, InstantiationException, IllegalAccessException {
		OWLOntologyManager sourceOntoManager = OWLManager.createOWLOntologyManager();
		sourceOntoManager.addIRIMapper(new NonMappingOntologyIRIMapper());

		OWLOntology sourceOnto = sourceOntoManager.loadOntologyFromOntologyDocument(input);

		return partition(sourceOnto, compatibilityMode);
	}

	public static void main(String[] args) {
		if (args.length != 4) {
			usage();
		}
		
		String compatibilityModeStr = args[0];
		ReasonerCompatibilityMode compatiblilityMode = null; 
		try {
			compatiblilityMode = ReasonerCompatibilityMode.valueOf(compatibilityModeStr);
		} catch (IllegalArgumentException e) {
			usage();
		}
		
		try {
			Pair<OWLOntology, OWLOntology> result = partition(IRI.create(new File(args[1])), compatiblilityMode);
			result.getLeft().getOWLOntologyManager().saveOntology(result.getLeft(), IRI.create(new File(args[2])));
			result.getRight().getOWLOntologyManager().saveOntology(result.getRight(), IRI.create(new File(args[3])));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void usage() {
		System.out.println("Usage:");
		System.out.println("java de.fuberlin.agcsw.svont.changedetection.smartcex.PartionEL <COMPATIBILITY_MODE> <SOURCE_ONTOLOGY_URI> <TARGET_EL_PARTITION_URI> <TARGET_NON_EL_PARTITION_URI>");
		System.out.print("Where <COMPATIBILITY_MODE> must be one of: ");
		ReasonerCompatibilityMode[] modes = ReasonerCompatibilityMode.values();
		for(int i=0; i < modes.length; i++) {
			System.out.print(modes[i]);
			if (i != modes.length - 1) {
				System.out.print(", ");
			}
		}
		System.exit(1);
	}
}
