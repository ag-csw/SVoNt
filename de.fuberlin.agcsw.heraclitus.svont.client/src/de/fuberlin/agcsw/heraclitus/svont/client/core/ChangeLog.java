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
package de.fuberlin.agcsw.heraclitus.svont.client.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.io.ReaderInputSource;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.tigris.subversion.subclipse.core.SVNException;
import org.tigris.subversion.subclipse.core.SVNProviderPlugin;
import org.tigris.subversion.svnclientadapter.ISVNInfo;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.utils.SVNUrlUtils;

import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformation;
import de.fuberlin.agcsw.heraclitus.svont.client.utils.SortChangeLogsElementsByRev;

public class ChangeLog {
	
	

	/**
	 * constant String of OMV-Change URI (OWLChanges)
	 */
	private static final String OMVChangesURI = "http://omv.ontoware.org/2007/07/OWLChanges#";
	
	
	/**
	 * constant String of OMV-Change URI  (changes)
	 */
	private static final String OMVChangesURI2 = "http://omv.ontoware.org/2007/10/changes#";
	
	
	
	
	public static void updateChangeLog(OntologyStore os,SVoNtProject sp,String user,String pwd) {
		
        //load the change log from server
		
		try {
			
			
			//1. fetch Changelog URI
			
			URI u = sp.getChangelogURI();

			
			//2. search for change log owl files
	    	DefaultHttpClient client = new DefaultHttpClient();
	        
	    	client.getCredentialsProvider().setCredentials(
	                new AuthScope(u.getHost(),AuthScope.ANY_PORT,AuthScope.ANY_SCHEME), 
	                new UsernamePasswordCredentials(user,pwd));
	        
	        HttpGet httpget = new HttpGet(u);
	        
	        System.out.println("executing request" + httpget.getRequestLine());
			
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String response = client.execute(httpget,responseHandler);
			System.out.println(response);
			List<String> files = ChangeLog.extractChangeLogFiles(response);
		

			
			ArrayList<ChangeLogElement> changelog = sp.getChangelog();
			changelog.clear();
			
			//4. sort the revisions
			
			
			for (int i=0; i<files.size(); i++) {
				String fileName = files.get(i);
				System.out.println("rev sort: "+fileName);
				int rev = Integer.parseInt(fileName.split("\\.")[0]);			
				changelog.add(new ChangeLogElement(URI.create(u+fileName), rev));
			}
			
			Collections.sort(changelog, new SortChangeLogsElementsByRev());
			
			//show sorted changelog
			
			System.out.print("[");
			for (ChangeLogElement cle: changelog) {
				System.out.print(cle.getRev()+",");
			}
			System.out.println("]");
			
			//5. map revision with SVN revisionInformations
			mapRevisionInformation(os,sp,changelog);
			
			
			
			//6. load change log files
			System.out.println("Load Changelog Files");
			for (String s:files) {
				System.out.println(s);
				String req = u+s;
				httpget = new HttpGet(req);
				response = client.execute(httpget,responseHandler);
//				System.out.println(response);
				
				
				// save the changelog File persistent
				IFolder chlFold = sp.getChangeLogFolder();
				IFile chlFile = chlFold.getFile(s);
				if (!chlFile.exists()) {
					chlFile.create(new ByteArrayInputStream(response.getBytes()), true, null);
				}
				
				os.getOntologyManager().loadOntology(new ReaderInputSource(new StringReader(response)));
				
			}
			System.out.println("Changelog Ontology successfully loaded");
			
			//Show loaded onts
			Set<OWLOntology> onts = os.getOntologyManager().getOntologies();
			for (OWLOntology o :onts) {
				System.out.println("loaded ont: "+o.getURI());
			}
			
			

			
			
			//7 refresh possibly modified Mainontology
			os.getOntologyManager().reloadOntology(os.getMainOntologyLocalURI());
			
			
			//8. recalculate Revision Information of the concept of this ontology
			sp.setRevisionMap(createConceptRevisionMap(os,sp));
			sp.saveRevisionMap();
			
			sp.saveRevisionInformationMap();
			
			//9. show MetaInfos on ConceptTree

			
			ConceptTree.refreshConceptTree(os, os.getMainOntologyURI());
			OntologyInformation.refreshOntologyInformation(os,os.getMainOntologyURI());
			
			//shutdown http connection
			
	        client.getConnectionManager().shutdown();  
	        
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLReasonerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVNClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		
	}
	
	
	public static HashMap<OWLClass,Integer> createConceptRevisionMap(OntologyStore os,
			SVoNtProject sp) {
		
		System.out.println("Recalculating Concept Revisions");
		
		HashMap<OWLClass,Integer> revMap = new HashMap<OWLClass,Integer>();

		OWLOntology ont = os.getLoadedOntologie(os.getMainOntologyURI());
		for (OWLClass c: ont.getReferencedClasses()) {
			
			int rev = getRevisionForConcept(os, sp, c);
			
			revMap.put(c, rev);
			
		}
		
		System.out.println("Recalculating of Concept Revisions done");
		return revMap;
		
	}
	
	
	private static int getRevisionForConcept(OntologyStore os,SVoNtProject sp,OWLClass cl) {
		
		if (cl.equals(os.getOntologyManager().getOWLDataFactory().getOWLThing())) {
			return sp.getHeadRev();
			
		}
		
//		System.out.println("Searching Rev for Concept: "+cl.getURI());
		
		ArrayList<ChangeLogElement> changelogfiles = sp.getChangelog();
		
		for (ChangeLogElement cle:changelogfiles) {
			int rev = cle.getRev();
			URI u = cle.getUri();
			System.out.println("Using URI: "+u);
			OWLOntology ont = os.getLoadedOntologie(u);
			
			OWLClass addClass = os.getOntologyManager().getOWLDataFactory().getOWLClass(URI
					.create(OMVChangesURI2 + "AddClass"));
			
			OWLClass changedClass = os.getOntologyManager().getOWLDataFactory().getOWLClass(URI
					.create(OMVChangesURI2 + "ClassChange"));
			
			OWLObjectProperty hasRE = os.getOntologyManager().getOWLDataFactory().getOWLObjectProperty(URI
					.create(OMVChangesURI + "hasRelatedEntity"));
			
			
			//test if changelog uri is valid
			if (ont == null) {
				return sp.getHeadRev();
			}
			
			
//			System.out.println("searching in Revision: "+rev);
//			System.out.println("Printing individuals");
			for (OWLIndividual in : ont.getReferencedIndividuals()) {
//				System.out.println("Individual: "+in);
				String type = null;
				
				for (OWLDescription d: in.getTypes(ont)) {
//					System.out.println("Type: "+d.asOWLClass().getURI());
					if (d.asOWLClass().equals(addClass)) {
						type = "add";
					}
					if (d.asOWLClass().equals(changedClass)) {
						type = "changed";
					}
				}
				if(type != null) {
//					System.out.println("searching related entites");
					Map<OWLObjectPropertyExpression,Set<OWLIndividual>> oprops = in.getObjectPropertyValues(ont);
					for (OWLObjectPropertyExpression pe:oprops.keySet()) {
//						System.out.println("Expression: "+pe);
						
						if (pe.asOWLObjectProperty().equals(hasRE)) {
							for (OWLIndividual i2: oprops.get(pe)) {
//								System.out.println("related individuals: "+i2.getURI());
//								System.out.println("comparing with "+cl.getURI());
								
								if (i2.getURI().equals(cl.getURI())) {
//									System.out.println("class info found in this rev");
//									System.out.println(i2.getURI());
									return rev;
								}

							}	
						}
					}
			
				}
			
			}
		}
		return sp.getHeadRev();
	}
	


	private static void mapRevisionInformation(OntologyStore os,SVoNtProject sp,
			ArrayList<ChangeLogElement> changelog) throws MalformedURLException, ParseException, SVNException, SVNClientException {
		
		// init for creating SVNUrl
		SVNUrl rootUrl = new SVNUrl(sp.getRepositoryProjectRootURI().toString()); 
		String rootPath = os.getProject().getLocation().toString()+"/";
		String localFile = os.getMainOntologyFile().getLocation().toString();
		System.out.println("Ontology local file: "+localFile);
		
		SVNUrl svnurl = SVNUrlUtils.getUrlFromLocalFileName(localFile, rootUrl, rootPath);
		
		int headRevision = 0;
		
		HashMap<Integer, RevisionInfo> revMap = sp.getRevisionInformationMap();
		for (ChangeLogElement cle : changelog) {
			int rev = cle.getRev();
			
			
			if (rev > headRevision) {
				headRevision = rev;
			}
			
			SVNRevision r1 = SVNRevision.getRevision(String.valueOf(rev));
			ISVNInfo infs= SVNProviderPlugin.getPlugin().getSVNClient().getInfo(svnurl,r1,r1);
			RevisionInfo revInfo = new RevisionInfo();
			revInfo.setRevision(rev);
			revInfo.setAuthor(infs.getLastCommitAuthor());
			
			Date d = infs.getLastChangedDate();
			DateFormat df;
			df =  new SimpleDateFormat("MM/dd/yy HH:mm");

			revInfo.setDate(df.format(d));
			
			revMap.put(rev, revInfo);
			System.out.println(infs.getLastChangedRevision()+" " +infs.getLastChangedDate().toString() +" "+infs.getLastCommitAuthor());
		
			
		}
		
		sp.setRevisionInformationMap(revMap);
		sp.setHeadRev(headRevision);
	}
	
	
	
	public static List<String> extractChangeLogFiles(String response) {
		List<String> res = new ArrayList<String>();
		
		int i = 0,j = 0,k = 0;
		while (i >-1) {
			i = response.indexOf(".owl", j);
			if (i == -1 ) continue;
			
			//search " before .owl
			
			k = i;
			
			boolean stepOver = false;
			while (response.charAt(k) != '\"') {
				
				if (response.charAt(k) == '>') {
					//this is is the label of the file -> dont add this
					stepOver = true;
					break;
				}
				k = k-1;
			}
			
			if (stepOver) {
				j = i+4;
				continue;
			}
			
			int begin = k+1;
			int end = i+4;
			
			String logfile = response.substring(begin, end);
			System.out.println("Found CL-File: "+logfile);
			res.add(logfile);
			
			j = i+4;
			
		}
		
		return res;
		
		
	}
	
	

	
	
	
}
