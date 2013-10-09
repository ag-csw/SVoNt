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
package de.fuberlin.agcsw.heraclitus.svont.client.ui;


import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.semanticweb.owl.model.OWLClass;

import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTreeNode;
import de.fuberlin.agcsw.heraclitus.svont.client.core.RevisionInfo;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtManager;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtProject;

public class SVoNtLightweightDecorator extends LabelProvider implements ILightweightLabelDecorator {

	@Override
	public void decorate(Object element, IDecoration decoration) {
		
		if (element instanceof TreeNode) {
			

			SVoNtProject sp = SVoNtManager.getSVoNtProjectByID(ConceptTree.ontologyStore.getId());
			
			if (sp!= null) {
				
			    TreeNode td = (TreeNode) element;
			    ConceptTreeNode ctn = (ConceptTreeNode) td.getValue();
			    OWLClass cl = ctn.getConcept();
			    
			    String suffix = " ";
			    RevisionInfo ri;
			    
			    ri = (RevisionInfo) ((sp.getRevisionMap().get(cl) != null) 
			    	? sp.getRevisionInformationMap().get(sp.getRevisionMap().get(cl))
			    	: sp.getRevisionInformationMap().get(sp.getHeadRev()));

				suffix += "["+String.valueOf(ri.getRevision()+ " "+ri.getDate() + " "+ ri.getAuthor()+"]");
//				System.out.println("Decorating a TreeNode");
				decoration.addSuffix(suffix);
				
//				decoration.
//				ITheme current = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
//				decoration.
//				
//				Color c = new Color(Display.getDefault(),149,125,71);
//				decoration.
//				decoration.setForegroundColor(c);
				
			}


		    
		    
		    
		    
		    
		} 
		

		
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		System.out.println("LIGHT: is label prop");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}




}
