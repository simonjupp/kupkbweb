package uk.ac.manchester.ac.uk.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
 * Date: May 2, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public interface KUPKBQueryServiceAsync {
    
    void query(String[] symbols, AsyncCallback<KUPKBExpData[]> callback);

    void query(String symbols, AsyncCallback<KUPKBExpData[]> callback);

    void advancedQuery(String[] geneIds, 
                              String[] expressionIds, String[] locationIds,
                              String[] conditionIds, String[] processIds, boolean intersection, AsyncCallback<KUPKBExpData[]> callback);

    void suggest(String input, AsyncCallback<KUPKBQueryObject[]> callback);

    void suggest(String[] input, AsyncCallback<KUPKBQueryObject[]> callback);

    void suggestClass(String input, AsyncCallback<KUPKBOWLClass[]> callback);

    void suggestClass(String[] input, AsyncCallback<KUPKBOWLClass[]> callback);

    void getNameSpaces(AsyncCallback<String> callback);

    void executeTemplateQuery(String templateId, String format, AsyncCallback<String> callback);
    
    void evaluateSparqlQuery(String query, String format, AsyncCallback<String> callback);


}
