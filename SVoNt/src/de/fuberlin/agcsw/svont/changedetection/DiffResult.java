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

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Container of Informations generated by the DiffExecution Modul
 * 
 * @author mario
 *
 */
public class DiffResult {

	/**
	 * Flag for emptieness of this Container
	 */
	private boolean empty;

	Set<OWLClass> addedClasses = new HashSet<OWLClass>();
	Set<OWLClass> removedClasses = new HashSet<OWLClass>();
	Set<OWLClass> changedClasses = new HashSet<OWLClass>();

	Set<OWLDataProperty> addedDataProperties = new HashSet<OWLDataProperty>();
	Set<OWLDataProperty> removedDataProperties = new HashSet<OWLDataProperty>();
	Set<OWLDataProperty> changedDataProperties = new HashSet<OWLDataProperty>();

	Set<OWLObjectProperty> addedObjectProperties = new HashSet<OWLObjectProperty>();
	Set<OWLObjectProperty> removedObjectProperties = new HashSet<OWLObjectProperty>();
	Set<OWLObjectProperty> changedObjectProperties = new HashSet<OWLObjectProperty>();

	Set<OWLIndividual> addedIndividuals = new HashSet<OWLIndividual>();
	Set<OWLIndividual> removedIndividuals = new HashSet<OWLIndividual>();
	Set<OWLIndividual> changedIndividuals = new HashSet<OWLIndividual>();

	Set<OWLDatatype> addedDataTypes = new HashSet<OWLDatatype>();
	Set<OWLDatatype> removedDataTypes = new HashSet<OWLDatatype>();
	Set<OWLDatatype> changedDataTypes = new HashSet<OWLDatatype>();

	Set<OWLAxiom> addedIndividualAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedDataPropertyAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedObjectPropertyAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedClassAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedAnnotationAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedDeclarationAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> addedOtherAxioms = new HashSet<OWLAxiom>();

	Set<OWLAxiom> removedIndividualAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedDataPropertyAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedObjectPropertyAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedClassAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedAnnotationAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedDeclarationAxioms = new HashSet<OWLAxiom>();
	Set<OWLAxiom> removedOtherAxioms = new HashSet<OWLAxiom>();

	public DiffResult() {

		empty = true;
	}

	public Set<OWLClass> getAddedClasses() {
		return addedClasses;
	}

	public void setAddedClasses(Set<OWLClass> addedClasses) {
		this.addedClasses = addedClasses;
	}

	public Set<OWLClass> getRemovedClasses() {
		return removedClasses;
	}

	public void setRemovedClasses(Set<OWLClass> removedClasses) {
		this.removedClasses = removedClasses;
	}

	public Set<OWLClass> getChangedClasses() {
		return changedClasses;
	}

	public void setChangedClasses(Set<OWLClass> changedClasses) {
		this.changedClasses = changedClasses;
	}

	public Set<OWLDataProperty> getAddedDataProperties() {
		return addedDataProperties;
	}

	public void setAddedDataProperties(Set<OWLDataProperty> addedDataProperties) {
		this.addedDataProperties = addedDataProperties;
	}

	public Set<OWLDataProperty> getRemovedDataProperties() {
		return removedDataProperties;
	}

	public void setRemovedDataProperties(
			Set<OWLDataProperty> removedDataProperties) {
		this.removedDataProperties = removedDataProperties;
	}

	public Set<OWLDataProperty> getChangedDataProperties() {
		return changedDataProperties;
	}

	public void setChangedDataProperties(
			Set<OWLDataProperty> changedDataProperties) {
		this.changedDataProperties = changedDataProperties;
	}

	public Set<OWLObjectProperty> getAddedObjectProperties() {
		return addedObjectProperties;
	}

	public void setAddedObjectProperties(
			Set<OWLObjectProperty> addedObjectProperties) {
		this.addedObjectProperties = addedObjectProperties;
	}

	public Set<OWLObjectProperty> getRemovedObjectProperties() {
		return removedObjectProperties;
	}

	public void setRemovedObjectProperties(
			Set<OWLObjectProperty> removedObjectProperties) {
		this.removedObjectProperties = removedObjectProperties;
	}

	public Set<OWLObjectProperty> getChangedObjectProperties() {
		return changedObjectProperties;
	}

	public void setChangedObjectProperties(
			Set<OWLObjectProperty> changedObjectProperties) {
		this.changedObjectProperties = changedObjectProperties;
	}

	public Set<OWLIndividual> getAddedIndividuals() {
		return addedIndividuals;
	}

	public void setAddedIndividuals(Set<OWLIndividual> addedIndividuals) {
		this.addedIndividuals = addedIndividuals;
	}

	/**
	 * @return the addedDeclarationAxioms
	 */
	public Set<OWLAxiom> getAddedDeclarationAxioms() {
		return addedDeclarationAxioms;
	}

	/**
	 * @param addedDeclarationAxioms the addedDeclarationAxioms to set
	 */
	public void setAddedDeclarationAxioms(Set<OWLAxiom> addedDeclarationAxioms) {
		this.addedDeclarationAxioms = addedDeclarationAxioms;
	}

	/**
	 * @return the removedDeclarationAxioms
	 */
	public Set<OWLAxiom> getRemovedDeclarationAxioms() {
		return removedDeclarationAxioms;
	}

	/**
	 * @param removedDeclarationAxioms the removedDeclarationAxioms to set
	 */
	public void setRemovedDeclarationAxioms(Set<OWLAxiom> removedDeclarationAxioms) {
		this.removedDeclarationAxioms = removedDeclarationAxioms;
	}

	public Set<OWLIndividual> getRemovedIndividuals() {
		return removedIndividuals;
	}

	public void setRemovedIndividuals(Set<OWLIndividual> removedIndividuals) {
		this.removedIndividuals = removedIndividuals;
	}

	public Set<OWLIndividual> getChangedIndividuals() {
		return changedIndividuals;
	}

	public void setChangedIndividuals(Set<OWLIndividual> changedIndividuals) {
		this.changedIndividuals = changedIndividuals;
	}

	public Set<OWLDatatype> getAddedDataTypes() {
		return addedDataTypes;
	}

	public void setAddedDataTypes(Set<OWLDatatype> addedDataTypes) {
		this.addedDataTypes = addedDataTypes;
	}

	public Set<OWLDatatype> getRemovedDataTypes() {
		return removedDataTypes;
	}

	public void setRemovedDataTypes(Set<OWLDatatype> removedDataTypes) {
		this.removedDataTypes = removedDataTypes;
	}

	public Set<OWLDatatype> getChangedDataTypes() {
		return changedDataTypes;
	}

	public void setChangedDataTypes(Set<OWLDatatype> changedDataTypes) {
		this.changedDataTypes = changedDataTypes;
	}

	public Set<OWLAxiom> getAddedIndividualAxioms() {
		return addedIndividualAxioms;
	}

	public void setAddedIndividualAxioms(Set<OWLAxiom> addedIndividualAxioms) {
		this.addedIndividualAxioms = addedIndividualAxioms;
	}

	public Set<OWLAxiom> getAddedDataPropertyAxioms() {
		return addedDataPropertyAxioms;
	}

	public void setAddedDataPropertyAxioms(Set<OWLAxiom> addedDataPropertyAxioms) {
		this.addedDataPropertyAxioms = addedDataPropertyAxioms;
	}

	public Set<OWLAxiom> getAddedObjectPropertyAxioms() {
		return addedObjectPropertyAxioms;
	}

	public void setAddedObjectPropertyAxioms(
			Set<OWLAxiom> addedObjectPropertyAxioms) {
		this.addedObjectPropertyAxioms = addedObjectPropertyAxioms;
	}

	public Set<OWLAxiom> getAddedClassAxioms() {
		return addedClassAxioms;
	}

	public void setAddedClassAxioms(Set<OWLAxiom> addedClassAxioms) {
		this.addedClassAxioms = addedClassAxioms;
	}

	public Set<OWLAxiom> getAddedAnnotationAxioms() {
		return addedAnnotationAxioms;
	}

	public void setAddedAnnotationAxioms(Set<OWLAxiom> addedAnnotationAxioms) {
		this.addedAnnotationAxioms = addedAnnotationAxioms;
	}

	public Set<OWLAxiom> getAddedOtherAxioms() {
		return addedOtherAxioms;
	}

	public void setAddedOtherAxioms(Set<OWLAxiom> addedOtherAxioms) {
		this.addedOtherAxioms = addedOtherAxioms;
	}

	public Set<OWLAxiom> getRemovedIndividualAxioms() {
		return removedIndividualAxioms;
	}

	public void setRemovedIndividualAxioms(Set<OWLAxiom> removedIndividualAxioms) {
		this.removedIndividualAxioms = removedIndividualAxioms;
	}

	public Set<OWLAxiom> getRemovedDataPropertyAxioms() {
		return removedDataPropertyAxioms;
	}

	public void setRemovedDataPropertyAxioms(
			Set<OWLAxiom> removedDataPropertyAxioms) {
		this.removedDataPropertyAxioms = removedDataPropertyAxioms;
	}

	public Set<OWLAxiom> getRemovedObjectPropertyAxioms() {
		return removedObjectPropertyAxioms;
	}

	public void setRemovedObjectPropertyAxioms(
			Set<OWLAxiom> removedObjectPropertyAxioms) {
		this.removedObjectPropertyAxioms = removedObjectPropertyAxioms;
	}

	public Set<OWLAxiom> getRemovedClassAxioms() {
		return removedClassAxioms;
	}

	public void setRemovedClassAxioms(Set<OWLAxiom> removedClassAxioms) {
		this.removedClassAxioms = removedClassAxioms;
	}

	public Set<OWLAxiom> getRemovedAnnotationAxioms() {
		return removedAnnotationAxioms;
	}

	public void setRemovedAnnotationAxioms(Set<OWLAxiom> removedAnnotationAxioms) {
		this.removedAnnotationAxioms = removedAnnotationAxioms;
	}

	public Set<OWLAxiom> getRemovedOtherAxioms() {
		return removedOtherAxioms;
	}

	public void setRemovedOtherAxioms(Set<OWLAxiom> removedOtherAxioms) {
		this.removedOtherAxioms = removedOtherAxioms;
	}

	public boolean isEmpty() {
		return this.empty;

	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}
