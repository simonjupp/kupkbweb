package uk.ac.manchester.ac.uk.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

import java.util.ArrayList;/*
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
public class ItemSuggestOracle extends SuggestOracle {

    public boolean isDisplayStringHTML() {
        return true;
    }
    

    @Override
    public void requestSuggestions(Request request, Callback callback) {

        KUPKBQueryServiceAsync service = GWT.create(KUPKBQueryService.class);
        service.suggest(request.getQuery(), new ItemSuggestCallback(request, callback));
    }

    class ItemSuggestCallback implements AsyncCallback<KUPKBQueryObject[]> {

           private SuggestOracle.Request req;
           private SuggestOracle.Callback callback;

           public ItemSuggestCallback(SuggestOracle.Request _req,
                   SuggestOracle.Callback _callback) {
               req = _req;
               callback = _callback;
           }

           public void onFailure(Throwable error) {
               callback.onSuggestionsReady(req, new SuggestOracle.Response());
           }

           public void onSuccess(KUPKBQueryObject[] retValue) {

               ArrayList<KUPKBQueryObject> results = new ArrayList<KUPKBQueryObject>();
               for (KUPKBQueryObject q : retValue) {
                   results.add(q);
               }
               Response response = new Response(new ArrayList<KUPKBQueryObject> (results));
               callback.onSuggestionsReady(req,
                       response);
           }
       }


}
