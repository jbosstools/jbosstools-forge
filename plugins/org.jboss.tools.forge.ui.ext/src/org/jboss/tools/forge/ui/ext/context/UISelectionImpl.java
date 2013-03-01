/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.ui.context.UISelection;

public class UISelectionImpl<T> implements UISelection<T> {

    private final List<T> selection;

    public UISelectionImpl(List<T> selection) {
        Assert.notNull(selection, "Selection must not be null.");
        Assert.isTrue(!selection.isEmpty(), "Selection must not be empty.");
        this.selection = Collections.unmodifiableList(selection);
    }

    @Override
    public T get() {
        return selection.get(0);
    }

    @Override
    public Iterator<T> iterator() {
        return selection.iterator();
    }

    @Override
    public int size() {
        return selection.size();
    }
}
