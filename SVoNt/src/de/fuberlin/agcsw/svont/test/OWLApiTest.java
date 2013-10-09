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
package de.fuberlin.agcsw.svont.test;

import java.net.URI;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.fuberlin.agcsw.svont.util.Configurator;

public class OWLApiTest {
	Properties props;
	final static String dataDir = System.getProperty("user.dir")
			+ "/ontologies/testdata/diff/";

	@Before
	public void setUp() {
		Configurator.initLogging("valitest");
		try {
			props = Configurator.loadProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String ontFile = dataDir + "ChangedURI-update.owl";

		String prefix = "file:";

		// windows path fixing
		if (!ontFile.startsWith("/"))
			prefix += "/";

		URI basePhysicalURI = URI.create(prefix + ontFile.replace("\\", "/"));
		OWLOntologyManager baseM = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology baseOnt = baseM
					.loadOntologyFromOntologyDocument(IRI.create(basePhysicalURI));
			Set<OWLAxiom> axioms = baseOnt.getAxioms();
			printAxioms(axioms);
			Set<OWLClass> classes = baseOnt.getClassesInSignature();
			printClasses(classes);
			printLogicalAxioms(baseOnt.getLogicalAxioms());

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

	}

	private static void printLogicalAxioms(Set<OWLLogicalAxiom> logicalAxioms) {
		System.out.println("ALL LOGICAL AXIOMS (" + logicalAxioms.size() + ")");

	}

	private static void printClasses(Set<OWLClass> classes) {
		System.out.println("ALL CLASSES (" + classes.size() + ")");
		for (OWLClass c : classes) {
			System.out.println(c.toString());
		}
		System.out.println("-----------------------------------");
	}

	private static void printAxioms(Set<OWLAxiom> axioms) {

		Set<OWLAxiom> axIndividual = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axDataProperty = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axObjectProperty = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axClass = new HashSet<OWLAxiom>();
		Set<OWLAxiom> axOther = new HashSet<OWLAxiom>();

		for (OWLAxiom a : axioms) {
			a.getSignature();
			if ((a instanceof OWLClassAxiom)) {
				axClass.add(a);
			} else if (a instanceof OWLDataPropertyAxiom) {
				axDataProperty.add(a);
			} else if (a instanceof OWLObjectPropertyAxiom) {
				axDataProperty.add(a);
			} else if (a instanceof OWLIndividualAxiom) {
				axIndividual.add(a);
			} else
				axOther.add(a);
		}

		System.out.println("ALL AXIOMS (" + axioms.size() + ")");
		for (OWLAxiom ax : axIndividual) {
			String line;
			line = ax.toString() + " TYPE: Individual";
			System.out.println(line);
		}
		for (OWLAxiom ax : axDataProperty) {
			String line;
			line = ax.toString() + " TYPE: DataProperty";
			System.out.println(line);
		}
		for (OWLAxiom ax : axObjectProperty) {
			String line;
			line = ax.toString() + " TYPE: ObjectProperty";
			System.out.println(line);
		}
		for (OWLAxiom ax : axClass) {
			String line;
			line = ax.toString() + " TYPE: Class";
			System.out.println(line);
		}
		for (OWLAxiom ax : axOther) {
			String line;
			line = ax.toString() + " TYPE: Other";
			System.out.println(line);
		}
		System.out.println("-----------------------------------");

	}

}
