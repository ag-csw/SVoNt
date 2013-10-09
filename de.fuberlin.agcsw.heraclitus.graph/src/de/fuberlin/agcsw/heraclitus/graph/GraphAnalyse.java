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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInfo;
import de.fuberlin.agcsw.heraclitus.graph.model.JungEdge;
import de.fuberlin.agcsw.heraclitus.graph.model.JungVertex;
import de.fuberlin.agcsw.heraclitus.graph.ui.ConceptTreeGraphDoubleClickListener;
import de.fuberlin.agcsw.heraclitus.graph.ui.FrameComponentListener;
import de.fuberlin.agcsw.heraclitus.graph.ui.JungClickOnPickMouseListener;
import de.fuberlin.agcsw.heraclitus.graph.ui.graphviewer.GraphViewer;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphAnalyse {

	public static OntologyInfo ontologyInfo;
	public static OntologyStore ontologyStore;
	
	public static String[] preferredRootConcepts;
	
	/**
	 * SAIL inMemory Repository for the loaded Ontology
	 */
	private static Repository rep;
	
	public static boolean fullGraph = false;
	
	//not using the jface viwer at the moment
	public static GraphViewer graphViewer;

	
	/**
	 * The Jung Viewer for the Graph
	 */
	public static VisualizationViewer<JungVertex, JungEdge> vv;

//	public static Label GraphInfoLabel;
	
	
	
	public static void init() {
		System.out.println("Initialising Graph Analyse");
		
		//we have to do this, else the Treeviewer might not been init. bad fix for that problem
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.fuberlin.agcsw.heraclitus.backend.ui.ConceptExplorerView");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConceptTree.ontologyTreeViewer.addDoubleClickListener(new ConceptTreeGraphDoubleClickListener());
	
	
		initJungViewer();
	}	
	
	
	
	private static void initJungViewer() {
		
//		Create Layout for Visualization
		Graph<JungVertex,JungEdge> g = new DirectedSparseMultigraph<JungVertex,JungEdge>();
		KKLayout<JungVertex, JungEdge> l = new KKLayout<JungVertex, JungEdge>(g);
		l.setMaxIterations(100);
		
		
		vv = new VisualizationViewer<JungVertex, JungEdge>(l);
		vv.addComponentListener(new FrameComponentListener());
		JungClickOnPickMouseListener lis = new JungClickOnPickMouseListener();
		vv.addMouseListener(lis);
		vv.addMouseMotionListener(lis);
		vv.setDoubleBuffered(true);
		
		 
		//init Mouse Picking
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		
		
		Transformer<JungVertex,Paint> vertexPaint = new Transformer<JungVertex,Paint>() {
		    public Paint transform(JungVertex v) {
		    	
		    	if (v.getUri() == null) {
		    		//this is a literal
		    		return Color.GRAY;
		    	}
		    	if (v.getLabel().startsWith("node")) {
		    		//this is a blank node
		    		return Color.orange;
		    	}
		        return Color.RED;
		    }
		};

		
		Transformer<JungVertex,Shape> vertexShape = new Transformer<JungVertex,Shape>() {

			@Override
			public Shape transform(JungVertex v) {
		    	if (v.getLabel().startsWith("node")) {
		    		//this is a blank node
		    		Arc2D.Double arc = new Arc2D.Double(-15, -15, 30,30, 0, 360, Arc2D.CHORD);
		    		return arc;
		    	}
				RoundRectangle2D.Double rec = new RoundRectangle2D.Double(-65, -12.5, 130, 25, 30, 30);
				return rec;
			}


		};
		
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<JungVertex>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<JungEdge>());
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setVertexShapeTransformer(vertexShape);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
		
	}

	
	
	public static void initSailRepository(URI physicalURI,URI namespace) {
		try {
			rep = new SailRepository(new MemoryStore());
			rep.initialize();
			RepositoryConnection con = rep.getConnection();
			ValueFactory f = rep.getValueFactory();
			con.add(physicalURI.toURL(), namespace.toString(), RDFFormat.RDFXML,f.createURI("http://graph.com"));
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RDFParseException e) {
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
	
	

	public static Graph<JungVertex,JungEdge> extendGraph(Graph<JungVertex,JungEdge> g,JungVertex startVertex) {
		ValueFactory f = rep.getValueFactory();
		
		try {
			
			//find the Subject Direction
			
			Resource startRes;
			if (startVertex.getLabel().startsWith("node")) {
				//its a blank Node
				startRes = f.createBNode(startVertex.getLabel());
			} else {
				
				//test if literal:
				if (startVertex.getUri() == null) {
					//looks like a lit
					// do nothing
					return g;
//					startRes = f.createLiteral(startVertex.getLabel()));
				}
				else {
					//normal node
					startRes = f.createURI(startVertex.getUri().toString());
				}
				
				
			}
			
			RepositoryResult<Statement> statements  = rep.getConnection().getStatements(startRes, null, null, true);
		
			
			while (statements.hasNext()) {
				   Statement st = statements.next();

				      
				   Resource sub = st.getSubject();
				   org.openrdf.model.URI pred = st.getPredicate();
				   Value obj = st.getObject();

				      
				      
//				   System.out.println("S: "+ sub.stringValue()+ " P: "+pred.stringValue()+" O: "+obj.stringValue());
			
				   String strPred = pred.stringValue().substring(pred.stringValue().indexOf("#")+1);
				   String strObj = obj.stringValue().substring(obj.stringValue().indexOf("#")+1);
	
				   
				   System.out.println("String Object (sub direction) : "+strObj);
				   
				   //test of obj is in Graph
				    JungVertex objVer = null;
					boolean objExists = false;
					for (JungVertex v: g.getVertices()) {
						if (v.getLabel().equals(strObj)) {
							objExists = true;
							objVer = v;
						}
						
					}

				    
					if (!objExists) {
						   if (obj instanceof Literal) {
							   objVer = new JungVertex(strObj,null);
						   } else {
							   objVer = new JungVertex(strObj,URI.create(obj.stringValue()));
						   }
						   
						   g.addVertex(objVer);
					}
			    
				   
					
//					g.c
					
				   //add Edge
				   JungEdge predV = new JungEdge(strPred);
				   
				   //find out if such an edge is in graph
				   boolean edgeInGraph = false;
				   for (JungEdge je : g.getEdges()) {
					   if (g.getSource(je).getUri().equals(URI.create(sub.stringValue())) ) {
						   if (strPred.equals(je.toString())) {
							   JungVertex obj2 = g.getDest(je);
							   if (obj2.getUri() != null) {
								   //no literal
								   if (obj2.getUri().equals(URI.create(obj.stringValue()))) {
									   edgeInGraph = true;
								   }
							   } else {
								   //is literal
								   if (obj2.getLabel().equals(obj.stringValue())) {
									   edgeInGraph = true;
								   }
							   }
							   
						   }
						   
					   }
					   
					   
				   }
				   
				   
				   if (!edgeInGraph) {
					   //test if circle (Schlinge)
					   if (strObj.equals(startVertex.getLabel())) {
						   g.addEdge(predV,startVertex,startVertex);
					   } else {
						   g.addEdge(predV,startVertex,objVer);
					   }	
				   }
   
				   
			}

			
			//find the object direction
			statements  = rep.getConnection().getStatements(null, null,startRes, true);
			while (statements.hasNext()) {
				   Statement st = statements.next();

				      
				   Resource sub = st.getSubject();
				   org.openrdf.model.URI pred = st.getPredicate();
				   Value obj = st.getObject();
				      
				      
//				   System.out.println("S: "+ sub.stringValue()+ " P: "+pred.stringValue()+" O: "+obj.stringValue());
				   
				   String strSub = sub.stringValue().substring(sub.stringValue().indexOf("#")+1);
				   String strPred = pred.stringValue().substring(pred.stringValue().indexOf("#")+1);
				   
				   
				   //test of obj is in Graph
				    JungVertex subVer = null;
					boolean subExists = false;
					for (JungVertex v: g.getVertices()) {
						if (v.getLabel().equals(strSub)) {
							subExists = true;
							subVer = v;
						}
						
					}

				    
					if (!subExists) {
						subVer = new JungVertex(strSub,URI.create(sub.stringValue()));
						g.addVertex(subVer);
					}
					
					
					
					
					JungEdge predV = new JungEdge(strPred);
					   
					//find out if such an edge is in graph
					boolean edgeInGraph = false;
					for (JungEdge je : g.getEdges()) {
						   if (g.getSource(je).getUri().equals(URI.create(sub.stringValue())) ) {
							   if (strPred.equals(je.toString())) {
								   JungVertex obj2 = g.getDest(je);
								   if (obj2.getUri() != null) {
									   //no literal
									   if (obj2.getUri().equals(URI.create(obj.stringValue()))) {
										   edgeInGraph = true;
									   }
								   } else {
									   //is literal
									   if (obj2.getLabel().equals(obj.stringValue())) {
										   edgeInGraph = true;
									   }
								   }
								   
							   }
							   
						   }
						   
						   
					   }
					   
					   
					   if (!edgeInGraph) {
						   //test if circle (Schlinge)
						   if (strSub.equals(startVertex.getLabel())) {
							   g.addEdge(predV,startVertex,startVertex);
						   } else {
							   g.addEdge(predV,subVer,startVertex);
						   }	
					   }

			}
			
			
			
			
		
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return g;
	}
	
	
	
	public static Graph<JungVertex,JungEdge> createGraphModel(URI start) {
		try {
			 Graph<JungVertex,JungEdge> g = new DirectedSparseMultigraph<JungVertex,JungEdge>();
			ValueFactory f = rep.getValueFactory();
			System.out.println("Start Node Fragment: "+start);
			
			if (start == null) {
				return null;
			}
			
			Resource startRes;
			if (start.toString().startsWith("node")) {
				//its a blank Node
				startRes = f.createBNode(start.toString());
			} else {
				startRes = f.createURI(start.toString());
			}
			
			
			//this is the subject direction
			
			RepositoryResult<Statement> statements  = rep.getConnection().getStatements(startRes, null, null, true);
			
			
			
			JungVertex subVer = new JungVertex(start.getFragment(),start);
			
			while (statements.hasNext()) {
				   Statement st = statements.next();

				      
				   Resource sub = st.getSubject();
				   org.openrdf.model.URI pred = st.getPredicate();
				   Value obj = st.getObject();
				      
				      
				   System.out.println("S: "+ sub.stringValue()+ " P: "+pred.stringValue()+" O: "+obj.stringValue());
			
				   String strPred = pred.stringValue().substring(pred.stringValue().indexOf("#")+1);
				   String strObj = obj.stringValue().substring(obj.stringValue().indexOf("#")+1);
				   
				   JungVertex objVer = null;
				   if (obj instanceof Literal) {
					   objVer = new JungVertex(strObj,null);
				   } else {
					   objVer = new JungVertex(strObj,URI.create(obj.stringValue()));
				   }
				   
				   JungEdge predV = new JungEdge(strPred);
				   
				   
				   //test if circle (Schlinge)
				   if (strObj.equals(subVer.toString())) {
					   g.addEdge(predV,subVer,subVer);
				   } else {
					   g.addEdge(predV,subVer,objVer);
				   }	   
			}
			
			
			//do the object direction
			statements  = rep.getConnection().getStatements(null, null, startRes, true);
			JungVertex objVer = subVer;
			
			while (statements.hasNext()) {
				   Statement st = statements.next();

				      
				   Resource sub = st.getSubject();
				   org.openrdf.model.URI pred = st.getPredicate();
				   Value obj = st.getObject();
				      
				      
				   System.out.println("S: "+ sub.stringValue()+ " P: "+pred.stringValue()+" O: "+obj.stringValue());
				   String strSub = sub.stringValue().substring(sub.stringValue().indexOf("#")+1);
				   String strPred = pred.stringValue().substring(pred.stringValue().indexOf("#")+1);

				   
				   subVer = new JungVertex(strSub,URI.create(sub.stringValue()));
				   g.addVertex(subVer);
				   
				   
				   
				   JungEdge predV = new JungEdge(strPred);
				   
				   //test if circle (Schlinge)
				   if (strSub.equals(objVer.toString())) {
//					   g.addEdge(predV,subVer,subVer);
				   } else {
					   g.addEdge(predV,subVer,objVer);
				   }	   
			}
			
			
			return g;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	

	
	
	
	public static Graph<JungVertex,JungEdge> mapOntologyToGraphModel(URI physicalURI,URI namespace) {
		System.out.println("Starting mapping full Ontology to full Graph Model");
		Graph<JungVertex,JungEdge> g = new DirectedSparseMultigraph<JungVertex,JungEdge>();
		
		 try {
				rep = new SailRepository(new MemoryStore());
				rep.initialize();
				RepositoryConnection con = rep.getConnection();
				ValueFactory f = rep.getValueFactory();
				
				con.add(physicalURI.toURL(), namespace.toString(), RDFFormat.RDFXML,f.createURI("http://graph.com"));
				
				
//				con.getStatements(arg0, arg1, arg2, arg3, arg4)
				RepositoryResult<Statement> statements  = con.getStatements(null, null, null, true);
				
//				System.out.println("There are "+statements.asList().size()+" statements in the graph");
				while (statements.hasNext()) {
					   Statement st = statements.next();

					      
					   Resource sub = st.getSubject();
					   org.openrdf.model.URI pred = st.getPredicate();
					   Value obj = st.getObject();
					      
					      
					    System.out.println("S: "+ sub.stringValue()+ " P: "+pred.stringValue()+" O: "+obj.stringValue());
					      
//					    if (pred.stringValue().equals("http://www.w3.org/2000/01/rdf-schema#subClassOf")) {
					    
						    String strSub = sub.stringValue().substring(sub.stringValue().indexOf("#")+1);
						    String strPred = pred.stringValue().substring(pred.stringValue().indexOf("#")+1);
						    String strObj = obj.stringValue().substring(obj.stringValue().indexOf("#")+1);
						    
						    
						    
						    
						    
//						if (strPred.equals("subClassOf")) { 
							
							//test of obj is blank node -- continue then
//							if (strObj.startsWith("node")) continue;
							
							
							//test id Sub Vertex is in graph
							
							JungVertex subV = null;
						    JungVertex objV = null;
							boolean subExists = false;
							boolean objExists = false;
							for (JungVertex v: g.getVertices()) {
								if (v.toString().equals(strSub)) {
									subExists = true;
									subV = v;
								}
								if (v.toString().equals(strObj)) {
									objExists = true;
									objV = v;
								}
								
							}
							if (!subExists) {
								subV = new JungVertex(strSub,URI.create(sub.stringValue()));
								g.addVertex(subV);
							}

						    
							if (!objExists) {
								//findout if obj is literal
								if (obj instanceof Literal) {
									objV = new JungVertex(strObj,null);
								} else
									objV = new JungVertex(strObj,URI.create(obj.stringValue()));
								g.addVertex(objV);
							}
					    
	//					    g.getV
	//					    
	//					    boolean end = false;
	//					    Collection<String> edges = g.findEdgeSet(strSub, strObj);
	//					    for (String se : edges) {
	//					    	if (se.equals(strPred)) {
	//					    		System.out.println("Edge still exists -- dont add it again");
	//					    		end = true;
	//					    	}
	//					    }
	//					    if (end) continue;
						    
						    
						    JungEdge predV = new JungEdge(strPred);
						    
							g.addEdge(predV,subV,objV);
					    }
					    
//			    }
					    
//					    Node e1 = m.createNode(strSub,false);
//					 
//					    Node e2 = m.createNode(strObj,(obj instanceof Literal));
//
//						m.addEdge(new Edge(e1,e2,strPred));
					      
					   
				   
				   
				return g;

		
				
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RDFParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		
		return g;
	}
	
	
	
	public static URI getStartNode() {
		
		OWLOntology ont = ontologyStore.getLoadedOntologie(ontologyInfo.getURI());
		
		URI start;
		
		List<URI> niceStartList = new ArrayList<URI>();
		// find a nice start node:
		String[] pref = preferredRootConcepts;
		
		
		//TODO bad workaround to fix NullPointerExcept
		if (pref == null) {
			pref = new String[0];
		}
		
		if (pref.length != 0) {
				
				for (int i = 0; i<pref.length;i++) {
					String prefString = pref[i];
					System.out.println("Searching for "+prefString +"concept as start Node");
					
					for (OWLClass o : ont.getReferencedClasses()) {
//						System.out.println("Fragment: "+o.getURI().getFragment());
						if (o.getURI().getFragment().toLowerCase().contains(prefString)) {
							niceStartList.add(o.getURI());
							System.out.println("Found a nice Node to Start:"+ o.getURI());
						}
					}
					
				}
				
				if (niceStartList.isEmpty()) {
					System.out.println("Nice start is empty");
					return ont.getReferencedClasses().iterator().next().getURI();
				}
				else {
					//get a random node from that list
					start = niceStartList.get(0);
					System.out.println("nice start list not empty using the first:");
					System.out.println(start);
					return start;
				}
				
		}
		else return ont.getReferencedClasses().iterator().next().getURI();
		

		
	}
	


	public static Repository getRep() {
		return rep;
	}






	
	
}
