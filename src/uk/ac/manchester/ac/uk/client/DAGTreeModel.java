package uk.ac.manchester.ac.uk.client;

//import javax.swing.event.TreeModelListener;
//import javax.swing.tree.TreePath;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;/*
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
 * Date: May 13, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class DAGTreeModel implements Serializable, IsSerializable {


    private Map<MyTreeNode, List<MyTreeNode>> parent2Child ;// = new HashMap<MyTreeNode, List<MyTreeNode>>();

    private MyTreeNode root ;//= new MyTreeNode("http://ikup.org/browser_root", "Summary");

    private MyTreeNode cell = new MyTreeNode("dummy1", "Cell");
    private MyTreeNode disease = new MyTreeNode("dummy2", "Disease/Model");
    private MyTreeNode anatomy = new MyTreeNode("dummy3", "Anatomy");
    private MyTreeNode bp = new MyTreeNode("dummy4", "Biological process");


    public DAGTreeModel () {
        parent2Child = new HashMap<MyTreeNode, List<MyTreeNode>>();
        root =  new MyTreeNode("http://ikup.org/browser_root", "Summary");
        parent2Child.put(root, new ArrayList<MyTreeNode>());
        // jsut create some summy nodes

        parent2Child.get(root).add(cell);
        parent2Child.get(root).add(disease);
        parent2Child.get(root).add(anatomy);
        parent2Child.get(root).add(bp);


    }

    public Map<MyTreeNode, List<MyTreeNode>> getParentChildMap () {
        return parent2Child;
    }

    public void addParentChildRelationship(MyTreeNode parent, MyTreeNode child) {

        if (!parent2Child.containsKey(parent)) {
            parent2Child.put(parent, new ArrayList<MyTreeNode>());
        }

        if (!parent2Child.get(parent).contains(child)) {
            parent2Child.get(parent).add(child);
        }
    }

//    @Override
    public MyTreeNode getRoot() {
        return root;
    }

//    @Override
    public MyTreeNode getChild(Object parent, int index) {
        MyTreeNode p = (MyTreeNode) parent;
        return parent2Child.get(p).get(index);
    }

//    @Override
    public int getChildCount(Object parent) {
        MyTreeNode p = (MyTreeNode) parent;
        return parent2Child.get(p).size();
    }

//    @Override
    public boolean isLeaf(Object node) {
        return !parent2Child.containsKey((MyTreeNode) node);

    }

    public int getIndexOfChild(Object parent, Object child) {
        MyTreeNode p = (MyTreeNode) parent;
        MyTreeNode c = (MyTreeNode) child;

        return parent2Child.get(p).indexOf(c);
    }

    public void  removeDummy() {

        parent2Child.get(root).remove(cell);
        parent2Child.get(root).remove(disease);
        parent2Child.get(root).remove(anatomy);
        parent2Child.get(root).remove(bp);

    }
}
