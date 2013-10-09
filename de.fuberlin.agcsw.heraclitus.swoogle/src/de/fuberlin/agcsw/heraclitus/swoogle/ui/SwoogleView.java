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
package de.fuberlin.agcsw.heraclitus.swoogle.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformation;
import de.fuberlin.agcsw.heraclitus.graph.GraphAnalyse;
import de.fuberlin.agcsw.heraclitus.swoogle.SwoogleClient;
import de.fuberlin.agcsw.heraclitus.swoogle.SwoogleQueries;

public class SwoogleView extends ViewPart {

	public static final String ID = "de.fuberlin.agcsw.swooglePlugin.views.SwoogleView";
	
	private Button queryButton = null;
	private Text queryText = null;
	private SwoogleResponseFieldList srfl;
	
//	private Label infoLabel;
	private Button infoButton;
	private Button importToProjectButton;
	
	private Combo projectCombo;
	
	private Composite p;
	
	private URI lastOntology;
	private OntologyStore infoStore;
	
	private IProject[] projects;
	
	
	
	@Override
	public void createPartControl(Composite parent) {
		
		this.p = parent;
		this.infoStore = OntoEclipseManager.getInfoStore();
		parent.setLayout(new GridLayout(1,false));
		
		
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(2,false));
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		queryText = new Text(top, SWT.BORDER);
		queryText.setText("person organization team");
		queryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		queryButton = new Button(top, SWT.NONE);
		queryButton.setText("Run Query");
		queryButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				queryButtonClick();
			}
		});

		srfl = new SwoogleResponseFieldList(parent, SWT.BORDER );
		srfl.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Composite bottom = new Composite(parent,SWT.NONE);
		bottom.setLayout(new GridLayout(4,false));
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		
//		infoLabel = new Label(bottom,SWT.NONE);
//		infoLabel.setText("");
//		infoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		projectCombo = new Combo(bottom,SWT.NONE);
		projectCombo.setEnabled(false);
		projectCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectCombo.addSelectionListener(new SelectionListener() {
	        public void widgetSelected(SelectionEvent e) {
	          System.out.println("Selected index: " + projectCombo.getSelectionIndex() + ", selected item: " + projectCombo.getItem(projectCombo.getSelectionIndex()) + ", text content in the text field: " + projectCombo.getText());
	        }

	        public void widgetDefaultSelected(SelectionEvent e) {
	          System.out.println("Default selected index: " + projectCombo.getSelectionIndex() + ", selected item: " + (projectCombo.getSelectionIndex() == -1 ? "<null>" : projectCombo.getItem(projectCombo.getSelectionIndex())) + ", text content in the text field: " + projectCombo.getText());
	          
	        }
	      });
		
		String[] projectItems = getProjectItems();
		projectCombo.setItems(projectItems);
		projectCombo.setText("Choose Project to import Ontology");
		
		importToProjectButton = new Button(bottom,SWT.NONE);
		importToProjectButton.setText("Load into Project");
		importToProjectButton.setEnabled(false);
		importToProjectButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				importButtonClick();
			}
		});
		
		
		infoButton = new Button(bottom,SWT.NONE);
		infoButton.setText("Load Info");
		infoButton.setEnabled(false);
		infoButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				infoButtonClick(false);
			}
		});
				
	}
	
	
	
	private String[] getProjectItems() {
		

		projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		String[] nameOfProjects = new String[projects.length];
		for( int i =0; i < projects.length; i++){  
			nameOfProjects[i] = projects[i].getName();
		}

		return nameOfProjects;
	}

	
	


	protected void importButtonClick() {
		
		IProject p = projects[projectCombo.getSelectionIndex()];
		
		System.out.println("Importing Ontology to Project: "+p.getName());
	    IFile ontFile = p.getFile("ontology.owl");
	    if (ontFile.exists()) {
	    	System.out.println("Ontology exist -- break import");
	    }
	    try {
	    	URL u = lastOntology.toURL();
			System.out.println("URI is: "+u);
			ontFile.create(u.openStream(),false,null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 

	}



	private void queryButtonClick() {
		srfl.removeAll();
		SwoogleClient sc = new SwoogleClient();
		Repository rep  = sc.executeQuery(queryText.getText());
		loadResponseToList(rep,srfl);
		p.layout(true, true);
		infoButton.setEnabled(true);
	}
	

	/**
	 * 
	 */
	private void infoButtonClick(boolean full){
		URI physicalInfoURI = URI.create(srfl.getSelectedURI());
		
		//delete the last information Ontology
		if (lastOntology != null) {
			infoStore.removeOntologyByURI(lastOntology);
		}
		try {
			OWLOntology ont = infoStore.loadData(physicalInfoURI);
			lastOntology = ont.getURI();
			
			
			//refreshing the Views
			ConceptTree.refreshConceptTree(OntoEclipseManager.getInfoStore(), ont.getURI());
			OntologyInformation.refreshOntologyInformation(OntoEclipseManager.getInfoStore(), ont.getURI());

			if (physicalInfoURI != null) {
				System.out.println("Enabled buttons");
				importToProjectButton.setEnabled(true);
				projectCombo.setEnabled(true);
			}
			
			
			// init Graph Analysis
			GraphAnalyse.ontologyInfo = infoStore.getOntologyInfos(lastOntology);
			GraphAnalyse.ontologyStore = infoStore;
			GraphAnalyse.preferredRootConcepts = queryText.getText().split(" ");
			GraphAnalyse.fullGraph = full;
			
			
		} catch (OWLReasonerException e2) {
			System.out.println("OWL Reasoner Exception");
			e2.printStackTrace();
		} catch (OWLOntologyCreationException e3) {
			System.out.println("Ontology creation Exception");
			e3.printStackTrace();
		}
		

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.OntologyInformationView");
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.graph.ui.GraphAnalyseView");
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}
	}

	
	
	
	
	
	protected void loadResponseToList(Repository rep,
			SwoogleResponseFieldList list) {
		
		try {
			
			RepositoryConnection con = rep.getConnection();
			
			TupleQueryResult result;
			System.out.println("Starting SPARQL");

		    TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, SwoogleQueries.swoogleResultQuery);
		    result = tupleQuery.evaluate();
			

			for (int i = 0; i<list.getMaxRows();i++) {
			   while (result.hasNext()) {
			    	BindingSet b = result.next();
					Value url = b.getValue("url");
					Value desc = b.getValue("desc");
					Value rank = b.getValue("rank");
					Value ratio = b.getValue("ratio");
					System.out.println("URL: "+url.stringValue() +" DESC: "+desc.stringValue() +" RANK: "+rank.stringValue() +" RATIO: "+ratio.stringValue());

					list.addRow(url.stringValue(), desc.stringValue(), rank.stringValue(), ratio.stringValue());
				}
			}
			      
			//not considered
//			System.out.println("NOT ADDED");
//			while (result.hasNext()) {
//		    	BindingSet b = result.next();
//				Value url = b.getValue("url");
//				Value desc = b.getValue("desc");
//				Value rank = b.getValue("rank");
//				Value ratio = b.getValue("ratio");
//				System.out.println("URL: "+url.stringValue() +" DESC: "+desc.stringValue() +" RANK: "+rank.stringValue() +" RATIO: "+ratio.stringValue());
//
//				list.addRow(url.stringValue(), desc.stringValue(), rank.stringValue(), ratio.stringValue());
//			}
			
			
			System.out.println("SPARQL END");
			System.out.println("Finished loading ");
			
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

	

	@Override
	public void setFocus() {
		
		projectCombo.setItems(getProjectItems());
		
		// TODO Auto-generated method stub

	}

}  //  @jve:decl-index=0:visual-constraint="39,5,610,704"
