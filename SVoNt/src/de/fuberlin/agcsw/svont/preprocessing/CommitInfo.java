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
package de.fuberlin.agcsw.svont.preprocessing;

/**
 * Container class for the commit Informations
 * 
 * @author mario
 * 
 */
public class CommitInfo {

	/**
	 * Transaction number of the Commit
	 */
	private String txn;

	/**
	 * Path to SVN Repository
	 */
	private String repository;

	/**
	 * Name of the SVN Repository
	 */
	private String repositoryName;

	/**
	 * Author if the Commit
	 */
	private String author;

	/**
	 * Current Base Revision in the Repository
	 */
	private String revision;

	/**
	 * String representation of the Base Ontology File
	 */
	private String owlBaseFile;

	/**
	 * String representation of the Update Ontology File
	 */
	private String owlUpdateFile;
	
	
	
	/**
	 * Create a empty CommitInfo
	 */
	public CommitInfo() {
		//empty if 
	}
	
	
	/**
	 * Create a CommitInfo Object filled with the given Parameter
	 * 
	 * @param owlBaseFile Ontology File from the Repository
	 * @param owlUpdateFile Ontology File that was changed
	 * @param repositoryPath absolute Path to Repository
	 * @param txn Transactionnumber
	 * @param author The Author of the Commit
	 */
	public CommitInfo(String owlBaseFile, String owlUpdateFile,String repositoryPath,String txn,String author) {
		
		this.owlBaseFile = owlBaseFile;
		this.owlUpdateFile = owlUpdateFile;

		if (repositoryPath != null) {
			this.repository = repositoryPath;
			this.repositoryName = repositoryPath.split("/")[repositoryPath.split("/").length - 1];
		}
		
		if (txn != null) {
			this.txn = txn;
			this.revision = txn.split("-")[0];
		}

		
		this.author = author;
	}
	

	public String getTxn() {
		return txn;
	}

	public void setTxn(String txn) {
		this.txn = txn;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getOwlBaseFile() {
		return owlBaseFile;
	}

	public void setOwlBaseFile(String owlBaseFile) {
		this.owlBaseFile = owlBaseFile;
	}

	public String getOwlUpdateFile() {
		return owlUpdateFile;
	}

	public void setOwlUpdateFile(String owlUpdateFile) {
		this.owlUpdateFile = owlUpdateFile;
	}

}
