package uk.ac.manchester.ac.uk.client;

import java.io.Serializable;
import java.util.HashSet;
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
 * Date: May 13, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class MyTreeNode implements Serializable  {

    private String iri;
    private String label;

    private Set<String> childGenes = new HashSet<String>();

    public MyTreeNode(String iri, String label) {
        this.iri = iri;
        this.label = label;
    }

    public MyTreeNode () {


    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getCount() {
        return childGenes.size();
    }

    public Set<String> getChildGenes() {
        return childGenes;
    }


    public String toString() {
        return getLabel() + " (" + getCount() + ")";
    }

}
