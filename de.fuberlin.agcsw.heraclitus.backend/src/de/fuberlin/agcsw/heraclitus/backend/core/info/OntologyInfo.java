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
package de.fuberlin.agcsw.heraclitus.backend.core.info;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class OntologyInfo {

	private int numClasses;
	private int numObjectProperties;
	private int numDataProperties;
	private int numIndividuals;

	private URI uri;
	private String expressivity;
	
	
	
	public OntologyInfo() {
		
	}
	
	public OntologyInfo(URI uri,int numClasses,int numOP, int numDP, int numInd, String express) {
		this.setURI(uri);
		this.numClasses = numClasses;
		this.numObjectProperties = numOP;
		this.numDataProperties = numDP;
		this.numIndividuals = numInd;
		this.expressivity = express;
	}

	public int getNumClasses() {
		return numClasses;
	}

	public void setNumClasses(int numClasses) {
		this.numClasses = numClasses;
	}

	public int getNumObjectProperties() {
		return numObjectProperties;
	}

	public void setNumObjectProperties(int numObjectProperties) {
		this.numObjectProperties = numObjectProperties;
	}

	public int getNumDataProperties() {
		return numDataProperties;
	}

	public void setNumDataProperties(int numDataProperties) {
		this.numDataProperties = numDataProperties;
	}

	public int getNumIndividuals() {
		return numIndividuals;
	}

	public void setNumIndividuals(int numIndividuals) {
		this.numIndividuals = numIndividuals;
	}

	public String getExpressivity() {
		return expressivity;
	}

	public void setExpressivity(String expressivity) {
		this.expressivity = expressivity;
	}

	public void setURI(URI uRI) {
		uri = uRI;
	}

	public URI getURI() {
		return uri;
	}


	public List<Tuple<String>> getPairTuple() {
		
		List<Tuple<String>> res = new ArrayList<Tuple<String>>();
		
		res.add(new Tuple<String>("URI",getURI().toString()));	
		res.add(new Tuple<String>("#Classes",String.valueOf(getNumClasses())));	
		res.add(new Tuple<String>("#Dataproperties",String.valueOf(getNumDataProperties())));	
		res.add(new Tuple<String>("#Objectproperties",String.valueOf(getNumObjectProperties())));	
		res.add(new Tuple<String>("#Individuals",String.valueOf(getNumIndividuals())));	
		res.add(new Tuple<String>("Expressivity",getExpressivity()));	
		return res;
		
		
		
		
	}
	
	
	
	
}
