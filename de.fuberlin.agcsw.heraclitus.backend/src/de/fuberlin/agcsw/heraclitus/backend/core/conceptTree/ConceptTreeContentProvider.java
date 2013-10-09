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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ConceptTreeContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{

	public Object[] getElements(final Object inputElement) {
//		System.out.println("IN GET ELEMENTS");
//		System.out.println(inputElement);
		if (inputElement instanceof TreeNode[]) {
			return (TreeNode[]) inputElement;
		}
		return new Object[0];
	}
	
	
	
	public boolean hasChildren(final Object element) {
//		System.out.println("IN HASCHILDREN");
//		System.out.println(element);
		if (element == null) return false;
		final TreeNode node = (TreeNode) element;
		return node.hasChildren();
	}


	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
//		System.out.println("IN INPUITCHANGED");
//		System.out.println(oldInput);
//		System.out.println(newInput);
		// Do nothing
	}
}
