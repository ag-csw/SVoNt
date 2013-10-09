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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInfoLabelProvider;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformation;
import de.fuberlin.agcsw.heraclitus.backend.core.info.OntologyInformationContentProvider;

public class OntologyInformationView extends ViewPart{
	
	
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("Ontology Information View gets created");
		
		OntologyInformation.InformationViewer = new TableViewer(parent,SWT.BORDER |  SWT.V_SCROLL);
		
		createColumns(OntologyInformation.InformationViewer);
		
		OntologyInformation.InformationViewer.setContentProvider(new OntologyInformationContentProvider());
		OntologyInformation.InformationViewer.setLabelProvider(new OntologyInfoLabelProvider());
		OntologyInformation.InformationViewer.setInput(OntologyInformation.ontologyInfoData);
		OntologyInformation.InformationViewer.refresh(true);

		
	}
	
	// This will create the columns for the table
	private void createColumns(TableViewer viewer) {

		String[] titles = { "Key", "Value" };
		int[] bounds = { 100, 100, 100, 100 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	



	
	
	@Override
	public void setFocus() {

	}

}
