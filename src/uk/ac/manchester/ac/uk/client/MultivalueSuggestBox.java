package uk.ac.manchester.ac.uk.client;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.SuggestOracle.Request;

import java.util.ArrayList;
import java.util.List;


/*
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
 * Adapted from "A SuggestBox that uses REST and allows for multiple values, autocomplete and browsing"
 *
 * @author Bess Siegal <bsiegal@novell.com>
 *
 * Licene
 * /*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
/*
 * Author: Simon Jupp<br>
 * Date: May 3, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public abstract class MultivalueSuggestBox extends Composite implements SelectionHandler<SuggestOracle.Suggestion>, Focusable, KeyUpHandler
{
    private SuggestBox m_field;
    private int m_indexFrom = 0;
    private int m_indexTo = 0;
    private int m_findExactMatchesTotal = 0;
    private int m_findExactMatchesFound = 0;
    private ArrayList<String> m_findExactMatchesNot = new ArrayList<String>();

    public static final String DISPLAY_SEPARATOR = "\n";
    private static final String VALUE_DELIM = ";";
    private static final int DELAY = 1500; //Increase auto-complete delay as it runs in Toulouse now...
    private static final int FIND_EXACT_MATCH_QUERY_LIMIT = 20;

//    private FormFeedback m_feedback;
    private List<String> suggestedTerms;

    /**
     * Constructor.
     */
    public MultivalueSuggestBox(String height, String width)
    {

        HorizontalPanel panel = new HorizontalPanel();
        TextBoxBase textfield;
        panel.addStyleName("textarearow");
        textfield = new TextArea();

        //Create our own SuggestOracle that queries via RPC
        SuggestOracle oracle = new RPCSuggestOracle();
        //intialize the SuggestBox
        m_field = new SuggestBox(oracle, textfield);
        //have to do this here b/c gwt suggest box wipes
        //style name if added in previous if
        textfield.setHeight(height);

        m_field.setWidth(width);
        m_field.addSelectionHandler(this);
        m_field.addKeyUpHandler(this);

        panel.add(m_field);
//        m_feedback = new FormFeedback();
//        panel.add(m_feedback);

        initWidget(panel);

        suggestedTerms = new ArrayList<String>();
    }


    /**
     * Convenience method to set the status and tooltip of the FormFeedback
     * @param status - a FormFeedback status
     * @param tooltip - a String tooltip
     */
    public void updateFormFeedback(int status, String tooltip)
    {
//        m_feedback.setStatus(status);
//        if (tooltip != null) m_feedback.setTitle(tooltip);

        TextBoxBase textBox = m_field.getTextBox();
        if (FormFeedback.LOADING == status) {
            textBox.setEnabled(false);
        } else {
            textBox.setEnabled(true);
            textBox.setFocus(false); //Blur then focus b/c of a strange problem with the cursor or selection highlights no longer visible within the textfield (this is a workaround)
            textBox.setFocus(true);
        }
    }


    /**
     * Get the value(s) as a String.  If allowing multivalues, separated by the VALUE_DELIM
     * @return value(s) as a String
     */
    public String getValue()
    {
        //String together all the values in the valueMap
        //based on the display values shown in the field
        String text = m_field.getText();

        String values = "";
        String invalids = "";
        String newKeys = "";
        String[] keys = text.split(DISPLAY_SEPARATOR);
        for (String key : keys) {
            key = key.trim();
            if (!key.isEmpty()) {
                if (suggestedTerms.contains(key)) {
                    values += key + VALUE_DELIM;
                    //rebuild newKeys removing invalids and dups
                    newKeys += key + DISPLAY_SEPARATOR;
                } else {
                    invalids += key + DISPLAY_SEPARATOR;
                }
            }
        }
        values = trimLastDelimiter(values, VALUE_DELIM);
        //set the new display values
        m_field.setText(newKeys);


        //if there were any invalid show warning
        if (!invalids.isEmpty()) {
            //trim last separator
            invalids = trimLastDelimiter(invalids, DISPLAY_SEPARATOR);
//            updateFormFeedback(FormFeedback.ERROR, "Invalids: " + invalids);
        }
        return values;
    }

    /**
     * If there is more than one key in the text field,
     * check that every key has a value in the map.
     * For any that do not, try to find its exact match.
     */
    private void findExactMatches()
    {
        String text = m_field.getText();
        String[] keys = text.split(DISPLAY_SEPARATOR.trim());
        int len = keys.length;
        if (len < 2) {
            //do not continue.  if there's 1, it is the last one, and getSuggestions can handle it
            return;
        }

        m_findExactMatchesTotal = 0;
        m_findExactMatchesFound = 0;
        m_findExactMatchesNot.clear();
        for (int pos = 0; pos < len; pos++) {
            String key = keys[pos].trim();

            if (!key.isEmpty()) {
                if (suggestedTerms.contains(key)) {
                    m_findExactMatchesTotal++;
                }
            }
        }
        //then loop through again and try to find them
        /*
         * We may have invalid values due to a multi-value copy-n-paste,
         * or going back and messing with a middle or first key;
         * so for each invalid value, try to find an exact match.                     *
         */
        for (int pos = 0; pos < len; pos++) {
            String key = keys[pos].trim();
            if (!key.isEmpty()) {
                if (suggestedTerms.contains(key)) {
                    findExactMatch(key, pos);
                }
            }
        }
    }

    private void findExactMatch(final String displayValue, final int position)
    {
//        updateFormFeedback(FormFeedback.LOADING, null);

        queryOptions(
            displayValue,
                0,
                FIND_EXACT_MATCH_QUERY_LIMIT, //return a relatively small amount in case wanted "Red" and "Brick Red" is the first thing returned
                 new OptionQueryCallback() {

                @Override
                public void error(Throwable exception)
                {
                    // an exact match couldn't be found, just increment not found
                    m_findExactMatchesNot.add(displayValue);
                    finalizeFindExactMatches();
                }

                @Override
                public void success(OptionResultSet optResults)
                {
                    int totSize = optResults.getTotalSize();
                    if (totSize == 1) {
                        //an exact match was found, so place it in the value map
                        Option option = optResults.getOptions()[0];
                        extactMatchFound(position, option);
                    } else {
                        //try to find the exact matches within the results
                        boolean found = false;
                        for (Option option : optResults.getOptions()) {
                            if (displayValue.equalsIgnoreCase(option.getName())) {
                                extactMatchFound(position, option);
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            m_findExactMatchesNot.add(displayValue);
                            System.out.println("RestExactMatchCallback -- exact match not found for display = " + displayValue);
                        }
                    }
                    finalizeFindExactMatches();
                }

                private void extactMatchFound(final int position, Option option)
                {
                    suggestedTerms.add(option.getName());
                    System.out.println("extactMatchFound ! exact match found for displ = " + displayValue);

                    //and replace the text
                    String text = m_field.getText();
                    String[] keys = text.split(DISPLAY_SEPARATOR.trim());
                    keys[position] = option.getName();
                    String join = "";
                    for (String n : keys) {
                        join += n.trim() + DISPLAY_SEPARATOR;
                    }
                    join = trimLastDelimiter(join, DISPLAY_SEPARATOR);
                    m_field.setText(join);

                    m_findExactMatchesFound++;
                }

                private void finalizeFindExactMatches()
                {
                    if (m_findExactMatchesFound + m_findExactMatchesNot.size() == m_findExactMatchesTotal) {
                        //when the found + not = total, we're done
                        if (m_findExactMatchesNot.size() > 0) {
                            String join = "";
                            for (String val : m_findExactMatchesNot) {
                                join += val.trim() + DISPLAY_SEPARATOR;
                            }
                            join = trimLastDelimiter(join, DISPLAY_SEPARATOR);
//                            updateFormFeedback(FormFeedback.ERROR, "Invalid:" + join);
                        } else {
//                            updateFormFeedback(FormFeedback.VALID, null);
                        }
                    }
                }
            });
    }


    /**
     * Returns a String without the last delimiter
     * @param s - String to trim
     * @param delim - the delimiter
     * @return the String without the last delimter
     */
    private static String trimLastDelimiter(String s, String delim)
    {
        if (s.length() > 0) {
            s = s.substring(0, s.length() - delim.length());
        }
        return s;
    }

    public void onSelection(SelectionEvent<SuggestOracle.Suggestion> event)
    {
        SuggestOracle.Suggestion suggestion = event.getSelectedItem();
        if (suggestion instanceof OptionSuggestion) {
            OptionSuggestion osugg = (OptionSuggestion) suggestion;
            //made a valid selection
//            updateFormFeedback(FormFeedback.VALID, null);

            //add the option's value to the value map
            suggestedTerms.add(osugg.getName());

            //put the focus back into the textfield so user
            //can enter more
            m_field.setFocus(true);
        }
    }

    private String getFullReplaceText(String displ, String replacePre)
    {
        //replace the last bit after the last comma
        if (replacePre.lastIndexOf(DISPLAY_SEPARATOR) > 0) {
            replacePre = replacePre.substring(0, replacePre.lastIndexOf(DISPLAY_SEPARATOR)) + DISPLAY_SEPARATOR;
        } else {
            replacePre = "";
        }
        //then add a comma
//        return replacePre + displ + DISPLAY_SEPARATOR;
        return replacePre + displ;

    }


    public int getTabIndex()
    {
        return m_field.getTabIndex();
    }


    public void setAccessKey(char key)
    {
        m_field.setAccessKey(key);
    }


    public void setFocus(boolean focused)
    {
        m_field.setFocus(focused);
    }


    public void setTabIndex(int index)
    {
        m_field.setTabIndex(index);
    }

    public void onKeyUp(KeyUpEvent event)
    {
        /*
         * Because SuggestOracle.requestSuggestions does not get called when the text field is empty
         * this key up handler is necessary for handling the case when there is an empty text field...
         * Here, the FormFeedback is reset.
         */
//        updateFormFeedback(FormFeedback.NONE, null);
    }

    public String getText() {
        return m_field.getText();
    }

    public void setText(String s) {
        m_field.setText(s);
    }

    /**
     * Retrieve Options (name-value pairs) that are suggested from the REST endpoint
     * @param query - the String search term
     * @param from - the 0-based begin index int
     * @param to - the end index inclusive int
     * @param callback - the OptionQueryCallback to handle the response
     */
    abstract void queryOptions(final String query, final int from, final int to,  final OptionQueryCallback callback);

    /*
     * Some custom inner classes for our SuggestOracle
     */
    /**
     * A custom Suggest Oracle
     */
    private class RPCSuggestOracle extends SuggestOracle
    {
        private SuggestOracle.Request m_request;
        private SuggestOracle.Callback m_callback;
        private Timer m_timer;

        RPCSuggestOracle()
        {
            m_timer = new Timer() {

                @Override
                public void run()
                {
                    /*
                     * The reason we check for empty string is found at
                     * http://development.lombardi.com/?p=39 --
                     * paraphrased, if you backspace quickly the contents of the field are emptied but a query for a single character is still executed.
                     * Workaround for this is to check for an empty string field here.
                     */

                    if (!m_field.getText().trim().isEmpty()) {
                        findExactMatches();
                        getSuggestions();
                    }
                }
            };
        }

        @Override
        public void requestSuggestions(SuggestOracle.Request request, SuggestOracle.Callback callback)
        {
            //This is the method that gets called by the SuggestBox whenever some types into the text field
            m_request = request;
            m_callback = callback;

            //If the user keeps triggering this event (e.g., keeps typing), cancel and restart the timer
            m_timer.cancel();
            m_timer.schedule(DELAY);
        }

        private void getSuggestions()
        {
            String query = m_request.getQuery();

            //find the last thing entered up to the last separator
            //and use that as the query
            int sep = query.lastIndexOf(DISPLAY_SEPARATOR);
            if (sep > 0) {
                query = query.substring(sep + DISPLAY_SEPARATOR.length());
            }
            query = query.trim();

            //do not query if it's just an empty String
            //also do not get suggestions you've already got an exact match for this string in the m_valueMap
            if (query.length() > 0 && !suggestedTerms.contains(query)) {
                //JSUtil.println("getting Suggestions for: " + query);
//                updateFormFeedback(FormFeedback.LOADING, null);

                queryOptions(
                        query,
                        m_indexFrom,
                        m_indexTo,
                    new RestSuggestCallback(m_request, m_callback, query));
            }
        }


        @Override
        public boolean isDisplayStringHTML()
        {
            return true;
        }
    }

    /**
     * A custom callback that has the original SuggestOracle.Request and SuggestOracle.Callback
     */
    private class RestSuggestCallback extends OptionQueryCallback
    {
        private SuggestOracle.Request m_request;
        private SuggestOracle.Callback m_callback;
        private String m_query; //this may be different from m_request.getQuery when multivalued it's only the substring after the last delimiter

        RestSuggestCallback(Request request, SuggestOracle.Callback callback, String query)
        {
            m_request = request;
            m_callback = callback;
            m_query = query;
        }

        public void success(OptionResultSet optResults)
        {
            SuggestOracle.Response resp = new SuggestOracle.Response();
            List<OptionSuggestion> suggs = new ArrayList<OptionSuggestion>();
            int totSize = optResults.getTotalSize();

            if (totSize < 1) {
                //if there were no suggestions, then it's an invalid value
//                updateFormFeedback(FormFeedback.ERROR, "Invalid: " + m_query);

            } else if (totSize == 1) {
                //it's an exact match, so do not bother with showing suggestions,
                Option o = optResults.getOptions()[0];
                String displ = o.getName();

                //remove the last bit up to separator
                m_field.setText(getFullReplaceText(displ, m_request.getQuery()));

                System.out.println("RestSuggestCallback.success! exact match found for displ = " + displ);

                //it's valid!
//                updateFormFeedback(FormFeedback.VALID, null);

                //set the value into the valueMap
                suggestedTerms.add(displ);

            } else {
                //more than 1 so show the suggestions

                // show the suggestions
                for (Option o : optResults.getOptions()) {
                    OptionSuggestion sugg = new OptionSuggestion(o.getName(), m_request.getQuery(), m_query);
                    suggs.add(sugg);
                }

                //nothing has been picked yet, so let the feedback show an error (unsaveable)
//                updateFormFeedback(FormFeedback.ERROR, "Invalid: " + m_query);
            }

            //it's ok (and good) to pass an empty suggestion list back to the suggest box's callback method
            //the list is not shown at all if the list is empty.
            resp.setSuggestions(suggs);
            m_callback.onSuggestionsReady(m_request, resp);
        }

        @Override
        public void error(Throwable exception)
        {
//            updateFormFeedback(FormFeedback.ERROR, "Invalid: " + m_query);
        }

    }

    /**
     * A bean to serve as a custom suggestion so that the value is available and the replace
     * will look like it is supporting multivalues
     */
    private class OptionSuggestion implements SuggestOracle.Suggestion
    {
        private String m_display;
        private String m_replace;
        private String m_name;


        /**
         * Constructor for regular options
         * @param displ - the name of the option
         * @param replacePre - the current contents of the text box
         * @param query - the query
         */
        OptionSuggestion(String displ, String replacePre, String query)
        {
            m_name = displ;
            int begin = displ.toLowerCase().indexOf(query.toLowerCase());
            if (begin >= 0) {
                int end = begin + query.length();
                String match = displ.substring(begin, end);
                m_display = displ.replaceFirst(match, "<b>" + match + "</b>");
            } else {
                //may not necessarily be a part of the query, for example if "*" was typed.
                m_display = displ;
            }
            m_replace = getFullReplaceText(displ, replacePre);
        }

        public String getDisplayString()
        {
            return m_display;
        }

        public String getReplacementString()
        {
            return m_replace;
        }


        /**
         * Get the name of the option.
         * (when not multivalued, this will be the same as getReplacementString)
         * @return name
         */
        public String getName()
        {
            return m_name;
        }
    }

    /**
     * An abstract class that handles success and error conditions from the REST call
     */
    public abstract class OptionQueryCallback
    {
        abstract void success(OptionResultSet optResults);
        abstract void error(Throwable exception);
    }

    /**
     * Bean for name-value pairs
     */
    public class Option
    {

        private String m_name;

        /**
         * No argument constructor
         */
        public Option()
        {
        }
        /**
         * @return Returns the name.
         */
        public String getName()
        {
            return m_name;
        }
        /**
         * @param name The name to set.
         */
        public void setName(String name)
        {
            m_name = name;
        }

    }

    /**
     * Bean for total size and options
     */
    public class OptionResultSet
    {
        /** JSON key for Options */
        public static final String OPTIONS = "Options";
        /** JSON key for DisplayName */
        public static final String DISPLAY_NAME = "DisplayName";
        /** JSON key for Value */
        public static final String VALUE = "Value";

        /** JSON key for the size of the Results */
        public static final String TOTAL_SIZE = "TotalSize";

        private final List<Option> m_options = new ArrayList<Option>();
        private int m_totalSize;


        /**
         * Constructor.  Must pass in the total size.
         * @param totalSize the total size of the template
         */
        public OptionResultSet(int totalSize)
        {
            setTotalSize(totalSize);
        }

        /**
         * Add an option
         * @param option - the Option to add
         */
        public void addOption(Option option)
        {
            m_options.add(option);
        }

        /**
         * @return an array of Options
         */
        public Option[] getOptions()
        {
            return m_options.toArray(new Option[m_options.size()]);
        }

        /**
         * @param totalSize The totalSize to set.
         */
        public void setTotalSize(int totalSize)
        {
            m_totalSize = totalSize;
        }

        /**
         * @return Returns the totalSize.
         */
        public int getTotalSize()
        {
            return m_totalSize;
        }
    }

}


