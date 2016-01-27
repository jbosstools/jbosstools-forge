/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.context;

import java.util.Optional;

import org.eclipse.jface.text.ITextSelection;
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.furnace.util.Assert;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class UIRegionImpl<T> implements UIRegion<T> {
	private final T resource;
	private final ITextSelection textSelection;

	public UIRegionImpl(T resource, ITextSelection textSelection) {
		Assert.notNull(textSelection, "Text selection cannot be null");
		this.textSelection = textSelection;
		this.resource = resource;
	}

	@Override
	public int getEndPosition() {
		int length = textSelection.getLength();
		return (length == -1) ? -1 : textSelection.getOffset() + length;
	}

	@Override
	public int getStartLine() {
		int startLine = textSelection.getStartLine();
		return (startLine == -1) ? -1 : startLine + 1;
	}

	@Override
	public int getEndLine() {
		int endLine = textSelection.getEndLine();
		return (endLine == -1) ? -1 : endLine + 1;
	}

	@Override
	public int getStartPosition() {
		return textSelection.getOffset();
	}

	@Override
	public Optional<String> getText() {
		return Optional.ofNullable(textSelection.getText());
	}

	@Override
	public T getResource() {
		return resource;
	}
}
