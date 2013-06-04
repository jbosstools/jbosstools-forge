/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;

public class InputComponentProposalProvider implements IContentProposalProvider {

	private InputComponent<?, Object> component;
	private UICompleter<Object> completer;

	public InputComponentProposalProvider(InputComponent<?, Object> component,
			UICompleter<Object> completer) {
		this.component = component;
		this.completer = completer;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new ArrayList<IContentProposal>();
		for (String proposal : completer.getCompletionProposals(component,
				contents)) {
			proposals.add(new ContentProposal(proposal));
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}
}
