/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.autocomplete;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;

public class InputComponentProposalProvider implements IContentProposalProvider {

	private InputComponent<?, ?> component;
	private UICompleter<?> completer;
	private UIContextImpl context;

	public InputComponentProposalProvider(UIContextImpl context,
			InputComponent<?, ?> component, UICompleter<?> completer) {
		this.context = context;
		this.component = component;
		this.completer = completer;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		List<IContentProposal> proposals = new ArrayList<IContentProposal>();
		for (Object proposal : completer.getCompletionProposals(context,
				(InputComponent) component, contents)) {
			if (proposal != null) {
				proposals.add(new ContentProposal(proposal.toString()));
			}
		}
		return proposals.toArray(new IContentProposal[proposals.size()]);
	}
}
