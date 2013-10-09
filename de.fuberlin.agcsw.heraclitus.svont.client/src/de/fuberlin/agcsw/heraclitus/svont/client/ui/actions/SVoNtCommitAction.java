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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.tigris.subversion.subclipse.ui.actions.CommitAction;

import de.fuberlin.agcsw.heraclitus.backend.OntoEclipseManager;
import de.fuberlin.agcsw.heraclitus.backend.core.OntologyStore;
import de.fuberlin.agcsw.heraclitus.svont.client.core.ChangeLog;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtManager;
import de.fuberlin.agcsw.heraclitus.svont.client.core.SVoNtProject;

public class SVoNtCommitAction extends CommitAction {

	
	String user = "mario";
	String pwd = "redhot"; 
	
	
    public SVoNtCommitAction() {
    	
    }
	
	
	public SVoNtCommitAction(String proposedComment) {
		super(proposedComment);
	}
	
	
	public void execute(IAction action) throws InvocationTargetException, InterruptedException {
		super.execute(action);
		
		
		//refresh conceptTree here
		
		String host;
		String repPath;
		SVoNtProject svontProject;
		IProject pro = super.getSelectedResources()[0].getProject();
		for (SVoNtProject sp: SVoNtManager.getSVoNtProjects()) {
			if (sp.getProject().equals(pro)) {
				
				URI clu = sp.getChangelogURI();
				host = clu.getHost();
				repPath = clu.getPath();
				svontProject = sp;
				System.out.println("Found Change Log Location for Project: "+pro.getName());
				System.out.println("Host: "+host);
				System.out.println("Path: "+repPath);
				
				
				if (host != null && repPath != null) {
					OntologyStore os = OntoEclipseManager.getOntologyStore(pro.getName());
//					OntologyStore os = Manager.getOntologyStore("teststore");
					ChangeLog.updateChangeLog(os,svontProject,user, pwd);
				} else {
					System.out.println("No SvoNt Config found for Project: "+ pro.getName());
				}
				
				break;
			}
			
		}
		
	}
	

	
}
