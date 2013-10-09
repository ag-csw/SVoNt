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
package de.fuberlin.agcsw.heraclitus.backend.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTree;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTreeContentProvider;
import de.fuberlin.agcsw.heraclitus.backend.core.conceptTree.ConceptTreeLabelProvider;

public class ConceptTreeExplorerView extends ViewPart{

	@Override
	public void createPartControl(Composite parent) {
		

		ILabelDecorator ld = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		DecoratingLabelProvider dlp = new DecoratingLabelProvider(new ConceptTreeLabelProvider(),ld);
		
		ConceptTree.ontologyTreeViewer = new TreeViewer(parent,SWT.BORDER |  SWT.V_SCROLL);
		ConceptTree.ontologyTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		ConceptTree.ontologyTreeViewer.setContentProvider(new ConceptTreeContentProvider());
		ConceptTree.ontologyTreeViewer.setLabelProvider(dlp);
		
		
		ConceptTree.ontologyTreeViewer.setInput(ConceptTree.ontologyTreeData);
		ConceptTree.ontologyTreeViewer.refresh(true);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
