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
package de.fuberlin.agcsw.heraclitus.svont.client.ui.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;

public class SVoNtProjectWizard extends Wizard implements IWizard{

	
	SVoNtWizardPage page;
	
	
	String changeLogText;
	


	@Override
	public void addPages() {
		page = new SVoNtWizardPage("ChangeLogLocation");
		addPage(page);
		
	}

	


	@Override
	public boolean performFinish() {
		
		
		String s = page.getChangeLogText();
		System.out.println("Found text: "+s);
		this.changeLogText = s;
	
		
		//do the  svont init stuff
		return true;
	}


	public String getChangeLogText() {
		return changeLogText;
	}
	
	public void setChangeLogText(String clt) {
		this.page.setChangeLogText(clt);
	}
	
	
}
