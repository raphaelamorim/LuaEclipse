/*******************************************************************************
 * Copyright (c) 2003 Ideais Tecnologia LTDA.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.keplerproject.ldt.ui.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * Lua Multiline comment rule.
 * @author guilherme
 *
 */
public class LuaMultilineCommentrule extends MultiLineRule {

	/** The pattern's nesting start sequence */
	protected char[] fNestingStartSequence;

	/** The counting of nested patterns * */
	private int fNestingCount;

	/**
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 * @param escapeCharacter
	 * @param breaksOnEOL
	 */
	public LuaMultilineCommentrule(IToken token, char escapeCharacter,
			boolean breaksOnEOL) {
		super("--[[", "]]", token, escapeCharacter, breaksOnEOL);
		String nestingStartSequence = "[[";
		fNestingStartSequence = nestingStartSequence.toCharArray();
		fNestingCount = 0;
	}

	public LuaMultilineCommentrule(IToken token, char escapeCharacter,
			boolean breaksOnEOL, boolean breaksOnEOF) {
		this(token, escapeCharacter, breaksOnEOL);
	}

	public LuaMultilineCommentrule(IToken token) {
		this(token, (char) 0, false);
	}

	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		// TODO Bacalha s�rio.. tem que testar mais
		if (resume) {
			/*
			 * if (endSequenceDetected(scanner)) { return fToken; }
			 */
			scanner.unread();
			int c = scanner.read();
			int d = 0, e = 0, f = 0;
			while (c != fStartSequence[0] || f != fStartSequence[1]
					|| e != fStartSequence[2] || d != fStartSequence[3]) {
				scanner.unread();
				scanner.unread();
				d = e;
				e = f;
				f = c;
				c = scanner.read();
			}
			scanner.unread();
			scanner.unread();
			c = scanner.read();
			if (c == fStartSequence[0]) {
				if (sequenceDetected(scanner, fStartSequence, false)) {
					fNestingCount = 1;

					if (endSequenceDetected(scanner)) {
						return fToken;
					}
				}
			}

		} else {

			int c = scanner.read();
			if (c == fStartSequence[0]) {
				if (sequenceDetected(scanner, fStartSequence, false)) {
					fNestingCount = 1;

					if (endSequenceDetected(scanner)) {
						return fToken;
					}
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}

	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		int c;
		char[][] delimiters = scanner.getLegalLineDelimiters();

		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip the escaped character.
				scanner.read();
			} else if (fNestingStartSequence.length > 0
					&& c == fNestingStartSequence[0]) {
				// Check if the specified nesting start sequence has been found.
				if (sequenceDetected(scanner, fNestingStartSequence, true))
					fNestingCount++;
			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true)) {
					fNestingCount--;
					if (fNestingCount == 0)
						return true;
				}
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the
				// pattern.
				for (int i = 0; i < delimiters.length; i++) {
					if (c == delimiters[i][0]
							&& sequenceDetected(scanner, delimiters[i], true))
						return true;
				}
			}
		}
		if (fBreaksOnEOF)
			return true;
		scanner.unread();
		return false;
	}

}