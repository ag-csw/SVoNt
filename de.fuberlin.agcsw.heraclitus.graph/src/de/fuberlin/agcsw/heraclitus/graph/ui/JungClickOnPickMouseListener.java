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
package de.fuberlin.agcsw.heraclitus.graph.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JToolTip;

import org.eclipse.swt.widgets.Label;

import de.fuberlin.agcsw.heraclitus.graph.GraphAnalyse;
import de.fuberlin.agcsw.heraclitus.graph.model.JungEdge;
import de.fuberlin.agcsw.heraclitus.graph.model.JungVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class JungClickOnPickMouseListener implements MouseListener,MouseMotionListener{

	Label lab;
	
	
	public JungClickOnPickMouseListener() {
		this.lab= null;
	}
	
	
	public JungClickOnPickMouseListener(Label lab) {
		this.lab= lab;
	}
	
	
	@Override
	public void mouseClicked(MouseEvent me) {
		VisualizationViewer<JungVertex, JungEdge> vv = (VisualizationViewer<JungVertex, JungEdge>) me.getComponent();
		
		if (me.getClickCount() == 2) {
			//with 2 clicks, just use this Node as new Root
			
			for (JungVertex v : vv.getPickedVertexState().getPicked()) {
				System.out.println("DOuble Clicked on: "+v.toString());
				
				Graph<JungVertex,JungEdge> g = GraphAnalyse.createGraphModel(v.getUri());
				//if g == null, a exception occured, or a literal got double clicked;
				if (g != null) {
					GraphAnalyse.vv.getGraphLayout().setGraph(g);
				}
				
//				GraphAnalyse.GraphInfoLabel.setText("NODE: "+v.getLabel());
				
			}
			
			vv.doLayout();
			vv.getGraphLayout().reset();
			vv.repaint();
			return;
			
		}
		
		
		if (me.getClickCount() == 1) {
		//with 1 click, extend the node
			
			for (JungVertex v : vv.getPickedVertexState().getPicked()) {
				System.out.println("1-Clicked on: "+v.toString());
				GraphAnalyse.extendGraph(vv.getGraphLayout().getGraph(),v);
				
				JToolTip tt = vv.createToolTip();
				
				tt.setBounds(0, 0, 50, 200);
				tt.setLocation(me.getPoint());
				tt.setTipText(v.getLabel());
				tt.setVisible(true);
				
				tt.repaint();
				
//				me.get
//				Container c = vv.getParent().getParent(); 
//				Composite c2 = (Composite) c;
				
				//).getDisplay().syncExec(
//
//						  new Runnable() {
//
//						    public void run(){
//
//						    	lab.setText("NODE: ");
////						      updateRoster();
//
//						    }
//
//						  });
//				this.lab.setText("NODE:");
			}
			
			vv.doLayout();
			vv.getGraphLayout().reset();
			vv.repaint();
			
		}
		
		

		
	}

	@Override
	public void mouseEntered(MouseEvent me) {
//		System.out.println(me.getSource());
//		GraphAnalyse.vv.getGraphLayout().
//		VisualizationViewer<JungVertex, JungEdge> vv = (VisualizationViewer<JungVertex, JungEdge>) me.getComponent();
//		System.out.println("Mouse entered: "+vv.getNextFocusableComponent().toString());
//		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent me) {
//		System.out.println(me.getSource());
//		VisualizationViewer<JungVertex, JungEdge> vv = (VisualizationViewer<JungVertex, JungEdge>) me.getComponent();
//		
//		System.out.println("COMP: "+ vv.getComponentAt(me.getPoint()));
//		vv.getGraphLayout().
//		for (JungVertex v : vv.getComponentAt(p)) {
//			System.out.println("1-Clicked on: "+v.toString());
//			GraphAnalyse.extendGraph(vv.getGraphLayout().getGraph(),v);
//		}
		
	}

}
