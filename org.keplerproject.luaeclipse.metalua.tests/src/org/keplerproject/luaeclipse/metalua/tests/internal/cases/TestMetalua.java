/******************************************************************************
 * Copyright (c) 2009 KeplerProject, Sierra Wireless.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Kevin KIN-FOO <kkin-foo@sierrawireless.com>
 *          - initial API and implementation and initial documentation
 *****************************************************************************/


package org.keplerproject.luaeclipse.metalua.tests.internal.cases;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.keplerproject.luaeclipse.metalua.Metalua;
import org.keplerproject.luaeclipse.metalua.tests.Suite;
import org.keplerproject.luajava.LuaException;
import org.osgi.framework.Bundle;


/**
 * Make sure that calls to Metalua work
 * 
 * @author kkinfoo
 * 
 */
public class TestMetalua extends TestCase {
	private static Bundle BUNDLE;
	static {
		BUNDLE = Platform.getBundle(Suite.PLUGIN_ID);
	}

	/** Make sure that syntax errors are catchable by Lua exception */
	public void testHandleErrors() {
		boolean error = false;
		String message = new String();
		try {
			Metalua.run("for");
		} catch (LuaException e) {
			error = true;
			message = e.getMessage();
		}
		assertTrue(message, error);
	}

	/** Run from source */
	public void testRunLuaCode() {

		boolean success = true;
		String message = new String();

		// Proofing valid code
		try {
			Metalua.run("var = 1+1");
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		}
		assertTrue("Single assignment does not work: " + message, success);

		// Proofing wrong code
		try {
			Metalua.run("var local = 'trashed'");
			success = true;
		} catch (LuaException e) {
			success = false;
		}
		assertFalse("Wrong code is accepted", success);
	}

	/** Run Lua source file */
	public void testRunLuaFile() {

		boolean success = false;
		String message = new String();

		// Proofing valid file
		try {
			Metalua.runFile(path("/scripts/assignment.lua"));
			success = true;
		} catch (LuaException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		}
		assertTrue("Single assignment from a file does not work: " + message,
				success);

		// Proofing wrong file
		success = false;
		try {
			Metalua.runFile(path("/scripts/john.doe"));
			success = true;
		} catch (LuaException e) {
			message = e.getMessage();
		} catch (IOException e) {
			message = e.getMessage();
		}
		assertFalse("Inexistant file call works.", success);
	}

	/** Run from source */
	public void testRunMetaluaCode() {

		boolean success = true;
		String message = "";

		// Proofing valid code
		try {
			Metalua.run("ast = mlc.luastring_to_ast ( \"var = 1 + 2\" )");
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		}
		assertTrue("Single assignment does not work: " + message, success);
	}

	/** Run Metalua source file */
	public void testRunMetaluaFile() {
		boolean success = true;
		String message = new String();

		// Proofing valid file
		try {
			Metalua.runFile(path("/scripts/introspection.mlua"));
		} catch (LuaException e) {
			message = e.getMessage();
			success = false;
		} catch (IOException e) {
			message = e.getMessage();
			success = false;
		}
		assertTrue("Code from a file does not work: " + message, success);
	}

	/** Ensure access to portable file locations */
	private String path(String uri) throws IOException {

		// Stop when plug-in's root can't be located
		try {
			URL url = BUNDLE.getEntry(uri);
			String path = FileLocator.toFileURL(url).getFile();
			return new File(path).getPath();
		} catch (NullPointerException e) {
			throw new IOException(uri + " not found.");
		}
	}
}