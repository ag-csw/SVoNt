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
package de.fuberlin.agcsw.heraclitus.graph;

import java.awt.Dimension;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

public class JungTest {
	
	
		static Graph<String, String> g; 
		static void test(){
			// Graph<V, E> where V is the type of the vertices
			// and E is the type of the edges
			g = new DirectedSparseMultigraph<String, String>();
			// Add some vertices. From above we defined these to be type Integer.
			g.addVertex("A");
			g.addVertex("B");
			g.addVertex("C");
			g.addVertex("D");
			
			// Add some edges. From above we defined these to be of type String
			// Note that the default is for undirected edges.
			g.addEdge("Edge-A", "A", "B"); // Note that Java 1.5 auto-boxes primitives
			g.addEdge("Edge-B", "C", "D");
			g.addEdge("Edge-C", "D", "A");
			g.addEdge("Edge-2", "C", "A");
			// Let's see what we have. Note the nice output from the
			// SparseMultigraph<V,E> toString() method
			System.out.println("The graph g = " + g.toString());
			

		}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test();
		
		DirectedSparseMultigraph<String,String> dGraph = new DirectedSparseMultigraph<String, String>();;
		
//		SimpleGraphView sgv = new SimpleGraphView(); //We create our graph in here
		// The Layout<V, E> is parameterized by the vertex and edge types
		
		
		
		//Layout<Integer, String> layout = new CircleLayout<Integer,String>(g);
		
		
		Layout<String, String> layout = new KKLayout(g);
		layout.setSize(new Dimension(300,300)); // sets the initial size of the space
		 // The BasicVisualizationServer<V,E> is parameterized by the edge types
		 BasicVisualizationServer<String,String> vv =
		          new BasicVisualizationServer<String,String>(layout); 
		 
		 
		 
		 vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
		
		 
		 vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		 

		 
		 JFrame frame = new JFrame("Simple Graph View");
		 frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 frame.getContentPane().add(vv);
		

		 frame.pack();
		 frame.setVisible(true);
		 
		
		 frame.pack();
	//	
//		 g.addVertex((Integer)5);
//		 
//		 //layout.reset();
//		 
//		 Thread.sleep(2000);
//		 
	//g.addVertex((Integer)6);
//		frame.repaint(); 
	// //layout.reset();
//		 
	//g.addVertex((Integer)8);
		 
		// layout.reset();

	}

}
