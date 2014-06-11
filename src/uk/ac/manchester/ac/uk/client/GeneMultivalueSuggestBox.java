package uk.ac.manchester.ac.uk.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;/*
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
 * Date: Oct 11, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class GeneMultivalueSuggestBox extends MultivalueSuggestBox {
    /**
     * Constructor.
     * @param height - height in pixels
     * @param width - width in pixels
     */
    public GeneMultivalueSuggestBox(String height, String width) {
        super(height, width);
    }

    @Override
    public void queryOptions(String query, int from, int to, final MultivalueSuggestBox.OptionQueryCallback callback) {
        AsyncCallback<KUPKBQueryObject[]> callbackAsync = new AsyncCallback<KUPKBQueryObject[]>() {

            public void onFailure(Throwable throwable) {
                callback.error(throwable);
            }

            public void onSuccess(KUPKBQueryObject[] kupkbQueryObjects) {
                OptionResultSet options = new OptionResultSet(kupkbQueryObjects.length);
                for (KUPKBQueryObject ob : kupkbQueryObjects) {
                    Option option = new Option();
                    option.setName(ob.getSymbol());
                    options.addOption(option);
                }
                callback.success(options);

            }
        };

        KUPKBQueryServiceAsync service = GWT.create(KUPKBQueryService.class);
        service.suggest(query, callbackAsync);
    }
}
