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
package de.fuberlin.agcsw.heraclitus.backend.core.conceptTree;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

public class ConceptTreeLabelProvider extends LabelProvider {

	
	
	public ConceptTreeLabelProvider() {
		super();
	}
	
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element) {

	      TreeNode td = (TreeNode) element;
	      ConceptTreeNode ctn = (ConceptTreeNode) td.getValue();
	      String result = "";
	      result += ctn.getConcept().toString();

	      return result;

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
//		System.out.println(element);
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public Font getFont(Object element) {
//		Font res;
//		TreeNode td = (TreeNode) element;
//		ConceptTreeNode ctn = (ConceptTreeNode) td.getValue();
//		OWLClass cl = ctn.getConcept();
//		String classBaseURI = cl.getURI().toString().substring(0,cl.getURI().toString().indexOf('#'));
//		System.out.println("BaseURI: "+ classBaseURI);
//		System.out.println("Compare with URI: "+ ConceptTree.ontologyInfo.getURI());
//		
//		if (ConceptTree.ontologyInfo.getURI().equals(classBaseURI)) {
//			res = new Font(Display.getDefault(), "Segoe UI", 9, SWT.BOLD);
//		} else 
//			res = new Font(Display.getDefault(), "Segoe UI", 9, SWT.NORMAL);
//		return res;
//	}



}
