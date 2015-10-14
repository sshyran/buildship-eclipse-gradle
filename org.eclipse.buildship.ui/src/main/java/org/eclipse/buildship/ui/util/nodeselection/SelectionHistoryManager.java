/*
 * Copyright (c) 2015 the original author or authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Etienne Studer & Donát Csikós (Gradle Inc.) - initial API and implementation and initial documentation
 *     Simon Scholz <simon.scholz@vogella.com> - Bug 479243
 */

package org.eclipse.buildship.ui.util.nodeselection;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Stores the sequence in which the currently selected nodes were selected in
 * the {@link TreeViewer} and acts as {@link ISelectionProvider}, which can be
 * added to an {@link org.eclipse.ui.IViewSite}.
 *
 * @see org.eclipse.buildship.ui.view.task.TaskView
 */
public final class SelectionHistoryManager implements ISelectionProvider {

    private List<ISelectionChangedListener> selectionChangedListeners = Lists.newCopyOnWriteArrayList();

    private final TreeViewer treeViewer;
    private final TreeViewerSelectionListener listener;
    private NodeSelection selectionHistory;

    public SelectionHistoryManager(TreeViewer treeViewer) {
        this.treeViewer = Preconditions.checkNotNull(treeViewer);
        this.listener = new TreeViewerSelectionListener();
        this.selectionHistory = NodeSelection.empty();

        init();
    }

    private void init() {
        this.treeViewer.addSelectionChangedListener(this.listener);
    }

    public void dispose() {
        this.treeViewer.removeSelectionChangedListener(this.listener);
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        this.selectionChangedListeners.add(listener);
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        this.selectionChangedListeners.remove(listener);
    }

    public NodeSelection getSelectionHistory() {
        return this.selectionHistory;
    }

    @Override
    public ISelection getSelection() {
        return getSelectionHistory();
    }

    @Override
    public void setSelection(ISelection selection) {
        handleSelection(selection);
    }

    protected void handleSelection(ISelection selection) {
        NodeSelection nodeSelection = NodeSelection.from(selection);
        this.selectionHistory = this.selectionHistory.mergeWith(nodeSelection);
        fireSelectionChanged();
    }

    protected void fireSelectionChanged() {
        final SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(
                SelectionHistoryManager.this.treeViewer, getSelection());
        for (final ISelectionChangedListener listener : this.selectionChangedListeners) {
            // using SafeRunnable here to ensure that no
            // ISelectionChangedListener from other plugins can break our code
            SafeRunnable.run(new SafeRunnable() {
                @Override
                public void run() {
                    listener.selectionChanged(selectionChangedEvent);
                }
            });
        }
    }

    /**
     * {@code ISelectionChangedListener} that, for each selection change in the
     * tree viewer, updates the selection history accordingly.
     */
    private final class TreeViewerSelectionListener implements ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            handleSelection(event.getSelection());
        }
    }
}
