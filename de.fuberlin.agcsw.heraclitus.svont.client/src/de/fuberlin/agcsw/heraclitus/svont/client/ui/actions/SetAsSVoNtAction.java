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
package de.fuberlin.agcsw.heraclitus.svont.client.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtManager;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtProject;
import de.fuberlin.agcsw.heraclitus.svont.client.ui.wizards.SVoNtProjectWizard;

public class SetAsSVoNtAction implements IObjectActionDelegate {

	
	private IWorkbenchPart part;
	
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
		
	}

	@Override
	public void run(IAction action) {
		
		Shell s = new Shell();
		ITreeSelection sel= (ITreeSelection) part.getSite().getSelectionProvider().getSelection();

		IProject p = (IProject) sel.iterator().next();
		
		
		SVoNtProjectWizard wiz = new SVoNtProjectWizard();
		WizardDialog dia = new WizardDialog(s, wiz);
		
		SVoNtProject sp = SVoNtManager.getSVoNtProjectByID(p.getName());
		

		dia.create();
		
		if (sp != null) {
			//then this is a initialized svont project -- 
			wiz.setChangeLogText(sp.getChangelogURI().toString());
			
		}
		
		dia.open();
		
		String changeLogRepURI = wiz.getChangeLogText();
		SVoNtManager.createSVoNtProject(p, changeLogRepURI);
		
		
		
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
