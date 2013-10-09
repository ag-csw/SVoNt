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
package de.fuberlin.agcsw.svont.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This static class is for configuration purpose
 * 
 * 
 * @author mario
 * 
 */
public class Configurator {

	/**
	 * costant path to SVoNt Config File
	 */
	private static final String ontSVNPropertyFile = "config/svont.xml";

	/**
	 * constant path to Logging config File
	 */
	private static final String log4jPropertyFile = "config/log4j.xml";

	/**
	 * init the Logging with current Path as SVoNt and Repository root
	 * 
	 * used for debugging
	 * 
	 * @param txn
	 *            Dummy Transaction number
	 */
	public static void initLogging(String txn) {
		initLogging("", "", "log", txn);
	}

	/**
	 * Initialise the Log4j Logging System
	 * 
	 * @param svontRoot
	 *            root of SVoNt directory
	 * @param repRoot
	 *            root of Repository
	 * @param logDir
	 *            log directory
	 * @param txn
	 *            Transaction number
	 */
	public static void initLogging(String svontRoot, String repRoot,
			String logDir, String txn) {
		try {
			String sb = svontRoot;
			System.out.println("Init logging system");
			System.out.println("LogPropertyFile: " + sb + log4jPropertyFile);

			// read logging configuration File
			InputStream input = new FileInputStream(new File(sb
					+ log4jPropertyFile));
			new DOMConfigurator().doConfigure(input, LogManager
					.getLoggerRepository());

			// we create a new Logging appender which logs this execution of the
			// Pre-Commit execution into
			// a seperate File identified by its svn-transaction id for further
			// identification if a problem occured
			// this file will be placed into a seperate directory in the
			// repository of the server
			String filename = repRoot + "/log/" + "/" + txn + ".log";
			Appender fappend = Logger.getRootLogger().getAppender(
					"FileAppender");
			Layout l = fappend.getLayout();
			FileAppender fa = new FileAppender(l, filename, true);
			Logger.getRootLogger().addAppender(fa);

			System.out.println("Log Configured! Ready to log...");

		} catch (FileNotFoundException e) {
			System.out
					.println("Couln't load Log4jProperties. PropertyFile not found: "
							+ log4jPropertyFile);
			System.out
					.println("BasicConfigurator will be used -- but no log file available");
			BasicConfigurator.configure();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Reads the Properties from the Configuration file with current directory
	 * as SVoNt Root
	 * 
	 * For debugging
	 * 
	 * @return Properties Object read
	 * @throws Exception
	 *             if file not available and xml format
	 */
	public static Properties loadProperties() throws Exception {
		return loadProperties("");
	}

	/**
	 * Reads the Properties from the Configuration file
	 * 
	 * @param svontRoot
	 *            directory of SVoNt System
	 * @return Properties Object read
	 * @throws Exception
	 *             if file not available and xml format
	 */
	public static Properties loadProperties(String svontRoot) throws Exception {
		String sb = svontRoot;
		Logger log = Logger.getLogger(Configurator.class);
		log.debug("SvontPropertyFile: " + sb + ontSVNPropertyFile);
		// Init Properties
		Properties props = new Properties();
		props
				.loadFromXML(new FileInputStream(new File(sb
						+ ontSVNPropertyFile)));
		log.info("Properties successfully loaded from File: "
				+ ontSVNPropertyFile);
		return props;

	}

}
