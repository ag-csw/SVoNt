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
package de.fuberlin.agcsw.heraclitus.swoogle.ui;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class SwoogleResponseFieldList extends Composite{

	
	private ArrayList<SwoogleResponseField> fields;
	private ArrayList<Button> buttons;  //  @jve:decl-index=0:

	private int max = 25;
	
	
	
	public SwoogleResponseFieldList(Composite parent, int style) {
		super(parent, style);
		initialize();

	}

	private void initialize() {
		
		
//		this.setSize(new Point(600, 43 * max));
		GridLayout layout = new GridLayout();
		layout.numColumns = 10;
		
		this.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		this.setLayoutData(gd);
		
		this.fields = new ArrayList<SwoogleResponseField>(max);
		this.buttons = new ArrayList<Button>(max);
		
	}
	
	
	public void addRow(String url,String desc,String rank,String ratio) {
		
		if (fields.size() < max) {
			SwoogleResponseField srf = new SwoogleResponseField(this, SWT.NONE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 9;
			srf.setLayoutData(gd);
			srf.setOntNameText(url);
			srf.setDescriptionText(desc);
			srf.setRankText(rank);
			srf.setRatioText(ratio);
			
			fields.add(srf);
			
			Button button = new Button(this, SWT.RADIO);
			buttons.add(button);
			
			this.layout(true,true);
		}
	}
	
	public void removeByURL() {
		
	}
	
	public void removeAll() {
		for (SwoogleResponseField sr : fields) {
			sr.dispose();
		}
		for (Button b: buttons) {
			b.dispose();
		}
		this.fields.clear();
		this.buttons.clear();
		this.layout(true,true);
	}
	
	
	public String getSelectedURI() {
		for (int i = 0; i<max;i++) {
			if (buttons.get(i).getSelection()) {
				return fields.get(i).getURIString();
			}
		}

		//if nothing selected return null
		return null;
		
	}
	
	
	public ArrayList<SwoogleResponseField> getFields() {
		return fields;
	}
	
	public ArrayList<Button> getRadioButtons() {
		return buttons;
	}
	
	public int getMaxRows(){
		return this.max;
	}

}
