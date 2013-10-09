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
package de.fuberlin.agcsw.heraclitus.swoogle;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;



public class SwoogleClient {

	
	public SwoogleClient() {
		
	}
	
	
	
	private String constructQueryParaString(String searchString) throws UnsupportedEncodingException {
		
		String encoded = URLEncoder.encode(searchString, "UTF-8");
		String result = "http://logos.cs.umbc.edu:8080/swoogle31/q?";
		result += "queryType=search_swd_ontology&";
		result += "searchString="+encoded+"&";
		result += "searchSortField=hasOntoRank&";
		result += "key=demo";
		return result;
		
	}
	
	
	public Repository executeQuery(String searchString) {
		try {
        	HttpClient httpclient = new DefaultHttpClient();
        	String queryString = constructQueryParaString(searchString);
 
        	HttpGet method = new HttpGet(queryString); 
  
        	System.out.println("executing request " + method.getURI());

        	ResponseHandler<String> responseHandler = new BasicResponseHandler();
        	String response = httpclient.execute(method,responseHandler);
        	
        	Repository rep = loadResponseToRepository(response);
        	
        	System.out.println("Recieved response from swoogle");
//        	System.out.println(response);
//        	System.out.println("----------------------------------------");
        	
        	return rep;
        	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	private Repository loadResponseToRepository(String response) throws RepositoryException, RDFParseException, IOException {
		 Repository repository = new SailRepository(new MemoryStore());
		 repository.initialize();
		 
		 RepositoryConnection con = repository.getConnection();
		 Reader sr = new StringReader(response);
		 con.add(sr, "http://daml.umbc.edu/ontologies/webofbelief/1.4/swoogle.owl#", RDFFormat.RDFXML);
		 System.out.println("Loaded SwoogleResponse to Repository");
		 
		 
		 return repository;
		
	}
	
	
	
}
