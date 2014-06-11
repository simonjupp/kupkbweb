package uk.ac.manchester.ac.uk.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import java.util.List;
import java.util.Map;
import java.util.Set;/*
 * Copyright (C) 2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Oct 19, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
// The model that defines the nodes in the tree.
public class CustomTreeModel implements TreeViewModel {

    private DAGTreeModel dagTreeModel = new DAGTreeModel();

    private KUPKBOWLQueryAsync OwlQuerySvc = GWT.create(KUPKBOWLQuery.class);

    final MultiSelectionModel<MyTreeNode> selectionModel = new MultiSelectionModel<MyTreeNode>();

    private ListDataProvider<MyTreeNode> rootDataProvider = new ListDataProvider<MyTreeNode>();

    public CustomTreeModel() {
        OwlQuerySvc = GWT.create(KUPKBOWLQuery.class);
        dagTreeModel = new DAGTreeModel();
    }

    public MyTreeNode getRoot () {
        return dagTreeModel.getRoot();
    }


    // generate the callback for the gene search event
    private AsyncCallback<DAGTreeModel> callbackIntermResult = new AsyncCallback<DAGTreeModel>() {

        public void onFailure(Throwable throwable) {
            String details = throwable.getMessage();
            fireTreeStopLoadingAction();
            System.err.println(details);

        }

        public void onSuccess(DAGTreeModel result) {

            dagTreeModel = result;

            rootDataProvider.getList().clear();
            rootDataProvider.refresh();
            // if result was  success, remove everything and add the new results
            dagTreeModel.removeDummy();
            // if result was  failure, put the dummy roots back in

            MyTreeNode root = dagTreeModel.getRoot();

            List<MyTreeNode> newChildren = dagTreeModel.getParentChildMap().get(root);
//                for (MyTreeNode no : newChildren) {
//                    System.out.println("setting new root children" + no.getLabel() + " " + no.getIri());
//                }
            rootDataProvider.getList().addAll(newChildren);
            rootDataProvider.refresh();

            fireTreeStopLoadingAction();
            fireExpandRootNotesAction();
        }
    };


    // Get the NodeInfo that provides the children of the specified value.
    public <T> NodeInfo<MyTreeNode> getNodeInfo(T value) {

        MyTreeNode current = (MyTreeNode) value;

        ListDataProvider<MyTreeNode> dataProvider;
        if (current.equals(dagTreeModel.getRoot())) {
            dataProvider = rootDataProvider;
        }
        else {
            dataProvider = new ListDataProvider<MyTreeNode>();
        }

        // Create some data in a data provider. Use the parent value as a prefix for the next level.
        for (MyTreeNode node : dagTreeModel.getParentChildMap().get(current)) {
            dataProvider.getList().add(node);
        }


        // Return a node info that pairs the data with a cell.
        return new TreeViewModel.DefaultNodeInfo<MyTreeNode>(dataProvider, new AbstractCell<MyTreeNode>() {

            @Override
            public void render(Context context, MyTreeNode myTreeNode, SafeHtmlBuilder safeHtmlBuilder) {
                safeHtmlBuilder.append(SafeHtmlUtils.fromString(myTreeNode.toString()));
            }
        }, selectionModel, null );


    }

    public Set<MyTreeNode> getSelectedNodes () {
        return selectionModel.getSelectedSet();
    }



    // Check if the specified value represents a leaf node. Leaf nodes cannot be opened.
    public boolean isLeaf(Object value) {
        MyTreeNode checkingNode = (MyTreeNode) value ;
        return !dagTreeModel.getParentChildMap().containsKey(checkingNode);
    }


    public void refreshRoot () {
        rootDataProvider.refresh();
    }

    public void regenerateTree (Map<String, Set<String>> querySet) {

        if (OwlQuerySvc == null) {
            OwlQuerySvc = GWT.create(KUPKBOWLQuery.class);
        }
        selectionModel.clear();
        OwlQuerySvc.getTreeModel(querySet, callbackIntermResult);
        fireTreeStartLoadingAction();
    }

    public void fireTreeStartLoadingAction () {

    }

    public void fireTreeStopLoadingAction () {

    }

    public void fireExpandRootNotesAction() {

    }


    public KUPKBOWLQueryAsync getQueryService () {

        if (OwlQuerySvc == null) {
            OwlQuerySvc = GWT.create(KUPKBOWLQuery.class);
        }
        return OwlQuerySvc;

    }
}




