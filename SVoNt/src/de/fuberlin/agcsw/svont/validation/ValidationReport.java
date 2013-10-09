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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * Representing a Report of the Validation 
 * 
 * 
 * @author mario
 *
 */
public class ValidationReport {

	/**
	 * Flag for all positive Properties of the validation
	 */
	private boolean valid;

	/**
	 * Flag for valid syntax
	 */
	private boolean syntax;
	
	/**
	 * Flaf for consistency
	 */
	private boolean consistent;
	
	/**
	 * Flag for right expressivity
	 */
	private boolean expressivity;

	/**
	 * Set of Classes that looks inconsistent
	 */
	private Set<OWLClass> inconsistentClasses;

	/**
	 * detailed Message about the Validation
	 */
	private String validationMessage;

	/**
	 * Inits the Report
	 */
	public ValidationReport() {
		valid = false;
		syntax = false;
		consistent = false;
		expressivity = false;

	}

	/**
	 * Creates and returns a TextReport with the Information this Object contains
	 * 
	 * @return String of Text report
	 */
	public String getTextReport() {
		String result = "";
		result += "Syntax: " + isSyntax() + "\r\n";
		result += "Consistent: " + isConsistent() + "\r\n";
		result += "Expressivity EL?: " + isExpressivity() + "\r\n";
		// syntax?
		if (!isSyntax()) {
			// syntaxerror
			result += getValidationMessage();
			return result;
		}
		if (!isConsistent()) {
			result += getValidationMessage() + "\r\n";
			Set<OWLClass> incon = getInconsistentClasses();
			if (incon != null) {
				result += "Following Classes are inconsistent: \r\n";
				for (OWLClass oc : incon) {
					result += oc + "\r\n";
				}
			}
			return result;
		}
		if (!isExpressivity()) {
			result += getValidationMessage();
			return result;
		}
		return result;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isSyntax() {
		return syntax;
	}

	public void setSyntax(boolean syntax) {
		this.syntax = syntax;
	}

	public boolean isConsistent() {
		return consistent;
	}

	public void setConsistent(boolean consistent) {
		this.consistent = consistent;
	}

	public boolean isExpressivity() {
		return expressivity;
	}

	public void setExpressivity(boolean expressivity) {
		this.expressivity = expressivity;
	}

	public Set<OWLClass> getInconsistentClasses() {
		return inconsistentClasses;
	}

	public void setInconsistentClasses(Set<OWLClass> inconsistentClasses) {
		this.inconsistentClasses = inconsistentClasses;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}

}
