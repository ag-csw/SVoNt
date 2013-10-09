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


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class SwoogleResponseField  extends Composite{

	private Label ontNameLabel = null;
	private Label ontDescLabel = null;
	private Label rankLabel = null;
	private Label ratioLabel = null;
	

	private String uri;
	private Composite left;
	
	
	public SwoogleResponseField(Composite parent, int style) {
		super(parent, style);
		initialize();
	}
	
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {

		this.setLayout(new GridLayout(10,false));
	
		
//		Composite Left
		left = new Composite(this, SWT.NONE);
	    left.setLayout(new GridLayout(1,false));
	    GridData leftData = new GridData(GridData.FILL_BOTH);
	    leftData.horizontalSpan = 9;
		left.setLayoutData(leftData);
		
//		Composite right
	    Composite right = new Composite(this, SWT.NONE);
	    right.setLayout(new GridLayout(1,false));
	    GridData rightData = new GridData(GridData.HORIZONTAL_ALIGN_END);
	    right.setLayoutData(rightData);  
	    
		
		ontNameLabel = new Label(left, SWT.NONE);
		ontNameLabel.setFont(new Font(Display.getDefault(), "Segoe UI", 9, SWT.BOLD));
		ontDescLabel = new Label(left,SWT.WRAP);
		
		
		rankLabel = new Label(right,SWT.NONE);
		ratioLabel = new Label(right, SWT.NONE);

			
	}


	public void setOntNameText(String text) {
		ontNameLabel.setText(text);
		this.uri= text;
		layout(true,true);
		
	}
	
	public void setDescriptionText(String text) {
		ontDescLabel.setText(text);
		layout(true,true);
	}
	
	public void setRankText(String text) {
		rankLabel.setText(text);
		layout(true,true);
	}
	
	public void setRatioText(String text) {
		ratioLabel.setText(text);
		layout(true);
	}
	
	
	protected String getURIString() {
		return uri;
		
	}
	

}  //  @jve:decl-index=0:visual-constraint="-3,57"
