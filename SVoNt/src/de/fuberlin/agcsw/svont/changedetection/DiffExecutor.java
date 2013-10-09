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

/**
 * Interface for the Change Detection Modul
 * 
 * @author mario
 *
 */
public interface DiffExecutor {

	/**
	 * Executes a Diff with two OntologyURIs
	 * 
	 * @param baseOntologyFile
	 *            Physical Path of Base Ontology URI
	 * @param updateOntologyFile
	 *            Physical Path of Update Ontology URI
	 * @return DiffResult Object containing the informations of the Diff
	 * @throws Exception
	 *             if something really bad happenend
	 */
	public DiffResult executeDiff(URI baseOntologyFile, URI updateOntologyFile)
			throws Exception;

}
