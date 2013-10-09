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
package de.fuberlin.agcsw.svont.changedetection.smartcex.profiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

/**
 * @author ralph
 * 
 */
public class CEXOWL2ELProfile implements OWLProfile, OWLAxiomVisitor {

	private static final Logger log = Logger.getLogger(CEXOWL2ELProfile.class);

	private final HashSet<OWLProfileViolation> violations = new HashSet<OWLProfileViolation>();

	private OWLOntology ontology;
	private OWLOntologyManager manager;
	private OWLDataFactory factory;

	/**
	 * @see org.semanticweb.owlapi.profiles.OWLProfile#checkOntology(org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	public OWLProfileReport checkOntology(OWLOntology ontology) {

		this.ontology = ontology;
		manager = OWLManager.createOWLOntologyManager();
		factory = manager.getOWLDataFactory();

		for (OWLAxiom axiom : ontology.getAxioms()) {
			axiom.accept(this);
		}

		return new OWLProfileReport(this, violations);
	}

	/**
	 * @see org.semanticweb.owlapi.profiles.OWLProfile#getName()
	 */
	@Override
	public String getName() {
		return "CEX algorithm compatible OWL 2 EL profile";
	}

	private void unusedAxiom(OWLAxiom axiom, boolean error) {
		if (error) {
			violations.add(new OWLProfileViolation(ontology, axiom));
		}
	}

	private void unusedAxiom(OWLAxiom axiom) {
		unusedAxiom(axiom, true);
	}

	public void visit(OWLSubClassOfAxiom axiom) {
		OWLClassExpression sub = axiom.getSubClass();
		// OWLClassExpression sup = axiom.getSuperClass();
		if (!(sub.isOWLThing() || sub.isOWLNothing() || sub instanceof OWLClass)) {
			// LOG.log(Level.SEVERE,
			// "On the left side of a subAxiom is not a single class, skipping");
			unusedAxiom(axiom);
		}
	}

	private List<OWLClassExpression> getListOfConjunctions(OWLClassExpression in) throws UnusedClassExpressionException {
		List<OWLClassExpression> list = new ArrayList<OWLClassExpression>();
		new NormalizatorClassExpressionVisitor(in, list);
		return list;
	}

	List<OWLObjectIntersectionOf> getConjunctionsFromLists(List<List<OWLClassExpression>> lists) {
		List<OWLObjectIntersectionOf> conjs = new ArrayList<OWLObjectIntersectionOf>();
		for (List<OWLClassExpression> list : lists) {
			conjs.add(factory.getOWLObjectIntersectionOf(new HashSet<OWLClassExpression>(list)));
		}
		return conjs;
	}

	private List<List<OWLClassExpression>> getCartesianProduct(List<List<OWLClassExpression>> lists) {
		List<List<OWLClassExpression>> out = new ArrayList<List<OWLClassExpression>>();
		if (lists.size() == 0) {
			return out;
		}
		if (lists.size() == 1) {
			for (OWLClassExpression d : lists.get(0)) {
				List<OWLClassExpression> l = new ArrayList<OWLClassExpression>();
				l.add(d);
				out.add(l);
			}
			return out;
		}
		List<OWLClassExpression> ll = lists.remove(lists.size() - 1);
		List<List<OWLClassExpression>> fout = getCartesianProduct(lists);
		for (List<OWLClassExpression> flist : fout) {
			for (OWLClassExpression d : ll) {
				List<OWLClassExpression> l = (List<OWLClassExpression>) ((ArrayList<OWLClassExpression>) flist).clone();
				l.add(d);
				out.add(l);
			}
		}
		return out;
	}

	public void visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLAsymmetricObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLReflexiveObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDisjointClassesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDataPropertyDomainAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLAnnotationAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLObjectPropertyDomainAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLEquivalentObjectPropertiesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDifferentIndividualsAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDisjointDataPropertiesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDisjointObjectPropertiesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLObjectPropertyRangeAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLObjectPropertyAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLFunctionalObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLSubObjectPropertyOfAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDisjointUnionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDeclarationAxiom arg0) {
	}

	public void visit(OWLSymmetricObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDataPropertyRangeAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLFunctionalDataPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLEquivalentDataPropertiesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLClassAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLEquivalentClassesAxiom axiom) {
		java.util.Set<OWLClassExpression> descrs = axiom.getClassExpressions();
		OWLClass left = null;
		OWLClassExpression right = null;
		if (descrs.size() != 2) {
			// LOG.log(Level.SEVERE, "Unsupported equAxiom - not 2 sides");
			unusedAxiom(axiom);
			return;
		}
		for (OWLClassExpression d : descrs) {
			if ((d instanceof OWLClass) && (left == null)) {
				left = (OWLClass) d;
			} else {
				right = d;
			}
		}
		if (left == null) {
			// LOG.log(Level.SEVERE,
			// "Unsupported equAxiom - at least one side has to be class");
			unusedAxiom(axiom);
			return;
		}
		List<OWLClassExpression> list;
		try {
			list = getListOfConjunctions(right);
			// TODO does it make sense to break down equivalence axiom like this ???
			// Probably not:
			if (list.size() != 1) {
				// LOG.severe("Unsupported equAxiom - union of some things");
				unusedAxiom(axiom);
			}
		} catch (UnusedClassExpressionException e) {
			unusedAxiom(axiom);
		}
	}

	public void visit(OWLDataPropertyAssertionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLTransitiveObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLSubDataPropertyOfAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLSameIndividualAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLSubPropertyChainOfAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLInverseObjectPropertiesAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(SWRLRule arg0) {
		unusedAxiom(arg0);
	}

	private class NormalizatorClassExpressionVisitor implements OWLClassExpressionVisitor {

		private List<OWLClassExpression> list;
		int errorCnt = 0;

		public NormalizatorClassExpressionVisitor(OWLClassExpression in, List<OWLClassExpression> list) throws UnusedClassExpressionException {
			this.list = list;

			in.accept(this);

			if (errorCnt != 0)
				throw new UnusedClassExpressionException();

		}

		private void unusedClassExpression(OWLClassExpression descr) {
			// LOG.warning("Ignored description: " + descr);
			errorCnt++;
		}

		public void visit(OWLClass c) {
			list.add(c);
		}

		public void visit(OWLObjectIntersectionOf inters) {

			Set<OWLClassExpression> ops = inters.getOperands();
			List<List<OWLClassExpression>> lists = new ArrayList<List<OWLClassExpression>>();
			for (OWLClassExpression op : ops) {
				try {
					lists.add(getListOfConjunctions(op));
					list.addAll(getConjunctionsFromLists(getCartesianProduct(lists)));
				} catch (UnusedClassExpressionException e) {
					errorCnt++;
				}
			}

		}

		public void visit(OWLObjectUnionOf union) {

			Set<OWLClassExpression> ops = union.getOperands();
			for (OWLClassExpression op : ops) {
				/*
				 * ArrayList<OWLClassExpression> l = new
				 * ArrayList<OWLClassExpression>(); new
				 * NormalizatorClassExpressionVisitor(op, l); list.addAll(l);
				 */
				try {
					list.addAll(getListOfConjunctions(op));
				} catch (UnusedClassExpressionException e) {
					errorCnt++;
				}
			}

		}

		public void visit(OWLObjectComplementOf arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectSomeValuesFrom restr) {
			list.add(restr);
		}

		public void visit(OWLObjectAllValuesFrom arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectHasValue arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectMinCardinality arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectExactCardinality arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectMaxCardinality arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectHasSelf arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLObjectOneOf arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataSomeValuesFrom arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataAllValuesFrom arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataHasValue arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataMinCardinality arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataExactCardinality arg0) {
			unusedClassExpression(arg0);
		}

		public void visit(OWLDataMaxCardinality arg0) {
			unusedClassExpression(arg0);
		}

	}

	public void visit(OWLHasKeyAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLDatatypeDefinitionAxiom arg0) {
		unusedAxiom(arg0);
	}

	public void visit(OWLSubAnnotationPropertyOfAxiom arg0) {
		unusedAxiom(arg0, false);
	}

	public void visit(OWLAnnotationPropertyDomainAxiom arg0) {
		unusedAxiom(arg0, false);
	}

	public void visit(OWLAnnotationPropertyRangeAxiom arg0) {
		unusedAxiom(arg0, false);
	}

	private class UnusedClassExpressionException extends Exception {
		private static final long serialVersionUID = -944929993434077728L;
	}
}
