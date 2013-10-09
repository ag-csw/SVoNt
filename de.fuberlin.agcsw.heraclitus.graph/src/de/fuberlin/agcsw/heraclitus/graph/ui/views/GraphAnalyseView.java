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
package de.fuberlin.agcsw.heraclitus.graph.ui.views;


import java.awt.Frame;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.semanticweb.owl.model.OWLOntology;

import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInfo;
import de.fuberlin.agcsw.heraclitus.graph.GraphAnalyse;
import de.fuberlin.agcsw.heraclitus.graph.model.JungEdge;
import de.fuberlin.agcsw.heraclitus.graph.model.JungVertex;
import de.fuberlin.agcsw.heraclitus.graph.ui.graphviewer.GraphContentProvider;
import de.fuberlin.agcsw.heraclitus.graph.ui.graphviewer.GraphViewer;
import edu.uci.ics.jung.graph.Graph;

public class GraphAnalyseView extends ViewPart{
	
	
	private Composite pp;
	private Frame f;
	
	
	@Override
	public void createPartControl(Composite parent) {
		
		
		//not enabled yet
		GraphAnalyse.graphViewer = new GraphViewer(parent,SWT.BORDER |  SWT.V_SCROLL);
		GraphAnalyse.graphViewer.setContentProvider(new GraphContentProvider());
		GraphAnalyse.graphViewer.setInput(GraphAnalyse.ontologyStore);
		GraphAnalyse.graphViewer.refresh();
		
		parent.setLayout(new GridLayout(1,false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		pp = new Composite(parent,SWT.EMBEDDED);
		pp.setLayout(new GridLayout(1,false));
		pp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
//		Label lab = new Label(parent,SWT.BORDER_SOLID);
//		lab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
//		lab.setText("me is a text");
		
		f = SWT_AWT.new_Frame(pp);
		f.add(GraphAnalyse.vv);
		
		

		


	}



	@Override
	public void setFocus() {
		
		
		
		//workaround cause Graphviewer doesnt work 
		OntologyStore os = GraphAnalyse.ontologyStore;
		OntologyInfo oi = GraphAnalyse.ontologyInfo;
		
		//this should happen if underlying data changes
		if ((os != null) && (oi != null)) {
			
			OWLOntology ont = os.getLoadedOntologie(oi.getURI());	

			GraphAnalyse.initSailRepository(os.getOntologyManager().getPhysicalURIForOntology(ont),ont.getURI());
			
			URI start = GraphAnalyse.getStartNode();
			
//			Create Graph Model
			Graph<JungVertex, JungEdge> g = (!GraphAnalyse.fullGraph) 
				? GraphAnalyse.createGraphModel(start) 
				: GraphAnalyse.mapOntologyToGraphModel(os.getOntologyManager().getPhysicalURIForOntology(ont),ont.getURI());

			//reload with new Graph
			GraphAnalyse.vv.getGraphLayout().setGraph(g);

			System.out.println("GRAPH:");
			System.out.println(g.toString());
		}
		
	}

}
