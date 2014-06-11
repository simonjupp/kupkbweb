package uk.ac.manchester.ac.uk.client.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.*;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.manchester.ac.uk.client.*;

import java.io.IOException;
import java.util.*;

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
 * Author: Simon Jupp<br>
 * Date: May 25, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class SPARQLWidget extends Composite {

    private KUPKBQueryServiceAsync kupkbQuerySvc = GWT.create(KUPKBQueryService.class);

    private static final String GET_EXPRESSION = "get_expression";
    private static final String GET_LOCATIONS = "get_locations";
    private static final String GET_CONDITIONS = "get_conditions";
    private static final String GET_PROCESS = "get_processes";

    final ArrayList<String> geneids = new ArrayList<String>();

    final ArrayList<String> locationids = new ArrayList<String>();

    private KUPKBResources resources = GWT.create(KUPKBResources.class);

    private HTML currentFilter = new HTML();

    private final VerticalPanel mainResultsPanel = new VerticalPanel();

    public final Image executingQueryIcon = new Image(resources.tableLoadingIcon());

    private KUPKBCellTable resultsTable = new KUPKBCellTable(50);

    private ListDataProvider<KUPKBExpData> dataProvider;

    private Set<String> allFilteredIds = new HashSet<String>();
//    private Set<String> diseaseFilteredIds = new HashSet<String>();
//    private Set<String> expressionFilteredIds = new HashSet<String>();
//    private Set<String> goFilteredIds = new HashSet<String>();

    private FormPanel submitToVizForm = new FormPanel("_blank");

    private CustomTreeModel model = new CustomTreeModel(){
        @Override
        public void fireTreeStartLoadingAction() {
            loadingTree.setVisible(true);
        }

        @Override
        public void fireTreeStopLoadingAction() {
            loadingTree.setVisible(false);
        }

        @Override
        public void fireExpandRootNotesAction() {
            TreeNode node = tree.getRootTreeNode();
            node.setChildOpen(0, true, true);
            node.setChildOpen(1, true, true);
            node.setChildOpen(2, true, true);
            node.setChildOpen(3, true, true);
        }
    };

    private RadioButton logicalAnd = new RadioButton("queryTypeRadio", "Intersecting compounds (AND)");
    private RadioButton logicalOr = new RadioButton("queryTypeRadio", "All compounds (OR)");

    private CellTree tree = new CellTree(model, model.getRoot(), KUPKBEntryPoint.MyCellTreeResources.INSTANCE);

    public final Image loadingTree = new Image(resources.treeLoadingIcon());

    private Button addFilterButton = new Button("Add Filter");
    private Button removeFilterButton = new Button("Remove Filter");

    private List<KUPKBExpData> originalResults = new ArrayList<KUPKBExpData>();

    final TextArea textArea = new TextArea();

    AsyncCallback<KUPKBExpData[]> callbackAdvancedQuery = new AsyncCallback<KUPKBExpData[]>() {

        public void onFailure(Throwable throwable) {
            String details = throwable.getMessage();
            System.err.println(details);
//            errorMsgLabel.setText("Error, an administrator has been notified:  " + details);
//            errorMsgLabel.setVisible(true);
//            loadingImage.setVisible(false);
        }

        public void onSuccess(KUPKBExpData[] result) {

            rebuildResultsTable(result);
            executingQueryIcon.setVisible(false);

        }
    };


    public void buildResultsTable (KUPKBExpData[] result) {

        dataProvider.getList().clear();
        List<KUPKBExpData> list = dataProvider.getList();
        list.addAll(Arrays.asList(result));
        resultsTable.getColumnSortList().push(resultsTable.getColumn(4));

    }


    public SPARQLWidget () {


        dataProvider = new ListDataProvider<KUPKBExpData>();
        resultsTable.setHeaders(dataProvider);
        dataProvider.addDataDisplay(resultsTable);


        VerticalPanel mainPanel = new VerticalPanel();


//        VerticalPanel geneSearchPanel = new VerticalPanel();
//        geneSearchPanel.add(new Label("List of Genes/Protein/miRNA ids..."));
//        final GeneMultivalueSuggestBox queryBox = new GeneMultivalueSuggestBox("130", "330");
//        queryBox.setText("(Any)");
//        geneSearchPanel.add(queryBox);

        VerticalPanel locationSearchPanel = new VerticalPanel();

        String advancedText = "<p style=\"width:500px;font-size:smaller;\">" +
                "Select an anatomical location or cell type to find molecules " +
                "reported as up/down/present/absent. You can combine this search" +
                " with disease conditions / disease models.  " +
                "Please be patient when querying (up to 10 seconds)." +
                " We welcome any feedback or comments to our mailing list (support[at]kupkb.org).</p>";

//                "<p style=\"width:500px;font-style:italic;\">Please note, this search is currently experimental and limited to 100 results and only single combinations of location to disease can be searched.</p>";

        locationSearchPanel.add(new Label("Select an anatomical location or cell type"));

        final KUPOMultivalueSuggestBox kupoQueryBox = new KUPOMultivalueSuggestBox("90", "330");
        kupoQueryBox.setText("(Any)");
        // this is where the search box is added
        locationSearchPanel.add(kupoQueryBox);

        final ListWidget locationListBox = new ListWidget(GET_LOCATIONS);
        locationSearchPanel.add(locationListBox);

        HorizontalPanel topPanel = new HorizontalPanel();
        topPanel.setSpacing(10);
//        topPanel.add(geneSearchPanel);
        topPanel.add(locationSearchPanel);
        locationSearchPanel.add(new HTMLPanel(advancedText));
        mainPanel.add(topPanel);



//        mainPanel.add(new HTMLPanel("<p style=\"font-size:smaller\">Additional filters...</p>"));

        FlexTable advancedQueryTable = new FlexTable();

        advancedQueryTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        advancedQueryTable.getCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
        advancedQueryTable.getCellFormatter().setVerticalAlignment(1, 3, HasVerticalAlignment.ALIGN_TOP);

        advancedQueryTable.setText(0, 1, "Expression value");
        advancedQueryTable.setText(2, 1, "Condition/disease value");
//        advancedQueryTable.setText(4, 1, "Biological Process value");

        final ListWidget expressionListBox = new ListWidget(GET_EXPRESSION);
        advancedQueryTable.setWidget(1, 1, expressionListBox);
        final ListWidget conditionsListBox = new ListWidget(GET_CONDITIONS);
        advancedQueryTable.setWidget(3, 1, conditionsListBox);
        final ListWidget processesListBox = new ListWidget(GET_PROCESS);
//        advancedQueryTable.setWidget(5, 1, processesListBox);

        mainPanel.add(advancedQueryTable);

//        HorizontalPanel radioPanel = new HorizontalPanel();
//        logicalOr.setChecked(true);
//        radioPanel.add(logicalOr);
//        radioPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
//        radioPanel.add(logicalAnd);


//        mainPanel.add(radioPanel);

        Button advancedSearch = new Button("Query");

        Button resetSearch = new Button("Reset");

        HorizontalPanel buttonPanel1 = new HorizontalPanel();
        buttonPanel1.add(advancedSearch);
        buttonPanel1.add(resetSearch);
        buttonPanel1.add(executingQueryIcon);
        executingQueryIcon.setVisible(false);

        mainPanel.add(buttonPanel1);

//        HTML toptext = new HTML("        <div style=\"padding:5px;\" class=\"generalText\">" +
//                "<h4>If you are brave you can attempt to construct your own SPARQL query. Choose the \"Generate SPARQL\" button above to " +
//                "see what the SPARQL query looks like below</h4>");

//        Button search = new Button("SPARQL!");



        textArea.setWidth("600");
        textArea.setHeight("300");




//        final VerticalPanel hiddenForm = new VerticalPanel();
//        final FormPanel fp = new FormPanel();
//        fp.setAction(GWT.getHostPageBaseURL() + "kupkb/query");
//        fp.setMethod(FormPanel.METHOD_POST);
//        final Hidden sparqlQuery = new Hidden("sparql", "");
//        final Hidden format = new Hidden("format", "csv");
//        hiddenForm.add(sparqlQuery);
//        hiddenForm.add(format);
//        hiddenForm.add(new Button("Show SPARQL", new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                if (!sparqlQuery.getValue().equals("")) {
//                    PopupPanel sparqlpopup = new PopupPanel();
//                    sparqlpopup.add(new HTML("<p>" + sparqlQuery.getValue() + "</p>"));
//                    sparqlpopup.setVisible(true);
//                }
//            }
//        }));
//        hiddenForm.add(new Button("Download results", new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                if (!sparqlQuery.getValue().equals("")) {
//                    Window.alert("Coming soon...");
//                }
//            }
//        }));
//        fp.add(hiddenForm);
//
//        fp.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
//            @Override
//            public void onSubmitComplete(FormPanel.SubmitCompleteEvent submitCompleteEvent) {
//                Window.alert(submitCompleteEvent.getResults());
//            }
//        });
//
//
//        hiddenForm.setVisible(false);
//        mainPanel.add(hiddenForm);


        String helpFilterText = "You can navigate the tree by clicking the small triangle. Each terms shows the number of unique genes/miRNAs that are annotated with that term or any of its children. " +
                "To filter select a term of interest from the tree and select the apply filter button. To remove the filter select the remove filter button";
        String helpIcon = "<img  ONMOUSEOVER=\"writetxt(\'" + helpFilterText + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">";
        HTML panel = new HTML("<div class=\"ikupHomeText\"><h2>Results View</h2>The results table shows the KUPKB experiments that reference your search terms. " +
                "You can sort the results table by clicking on the column headers. The navigation tree below gives you a summary of your results and can be used to filter the results table. ");
        VerticalPanel browserPanel = new VerticalPanel();
        browserPanel.add(panel);

        HorizontalPanel bottomResultsPanel = new HorizontalPanel();


        HorizontalPanel filterPanel = new HorizontalPanel();
        filterPanel.add(addFilterButton);
        filterPanel.add(new HTMLPanel(helpIcon));
        filterPanel.add(removeFilterButton);
        filterPanel.add(loadingTree);
        loadingTree.setVisible(false);
        filterPanel.add(currentFilter);

        currentFilter.setStyleName("currentFilterBox");
        addFilterButton.setStyleName("filterButton");
        removeFilterButton.setStyleName("filterButton");
        addFilterButton.setEnabled(true);
        removeFilterButton.setEnabled(false);
        browserPanel.add(filterPanel);


        FlowPanel treePanel = new FlowPanel();
        treePanel.add(tree);
        treePanel.setStyleName("treePanel");

        bottomResultsPanel.add(treePanel);

        SimplePager pager = new SimplePager(SimplePager.TextLocation.RIGHT);
        pager.setPageSize(50);
        pager.setDisplay(resultsTable);
        VerticalPanel pagerAndResultsTabelPanel = new VerticalPanel();

        submitToVizForm.setAction("http://www.kupkb.org/vis/index.php");
        submitToVizForm.setMethod(FormPanel.METHOD_POST);
        final VerticalPanel formPanel = new VerticalPanel();
        final Hidden ikup_terms_field = new Hidden("ikup_terms");
        formPanel.add(ikup_terms_field);
        submitToVizForm.setWidget(formPanel);

        Button vizSubmitButton = new Button("Visualize these results in KUPNetViz!");
        vizSubmitButton.setStyleName("vizButton");
        vizSubmitButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                JSONArray array = new JSONArray();
                int s = 0;
                Set<String> seen = new HashSet<String>();
                for (KUPKBExpData data : dataProvider.getList()) {

                    if (!seen.contains(data.getGeneid())) {
                        array.set(s, new JSONNumber(Double.parseDouble(data.getGeneid())));
                        s++;
                        seen.add(data.getGeneid());
                    }
                }
                ikup_terms_field.setValue(seen.toString());
                submitToVizForm.submit();
            }
        });
        formPanel.add(vizSubmitButton);


        pagerAndResultsTabelPanel.add(submitToVizForm);

        pagerAndResultsTabelPanel.add(pager);
        pagerAndResultsTabelPanel.add(resultsTable);
        bottomResultsPanel.add(pagerAndResultsTabelPanel);


        mainPanel.add(new HTML("<div><hr style=\"height:8px;;border-width:0;color:#E33B43;background-color:#E33B43;\"></div>"));


        mainResultsPanel.add(browserPanel);
        mainResultsPanel.add(bottomResultsPanel);
        mainResultsPanel.setVisible(false);
        mainPanel.add(mainResultsPanel);

        addFilterButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent clickEvent) {
                filter();
            }
        });

        removeFilterButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent clickEvent) {
                rebuildResultsTable(originalResults.toArray(new KUPKBExpData[originalResults.size()]));
                removeFilterButton.setEnabled(false);
                currentFilter.setText("");
                addFilterButton.setEnabled(true);
            }
        });

        mainPanel.setSpacing(20);
//        mainPanel.add(toptext);
//        mainPanel.add(textArea);
//        mainPanel.add(search);
        // close the section with a div tag
        mainPanel.add(new HTML("</div>"));

//        kupkbQuerySvc.getNameSpaces(callbackNamespaces);

        initWidget(mainPanel);

        advancedSearch.addClickHandler(new ClickHandler() {

            public void generateSparql() {


                allFilteredIds.clear();

                boolean nothingSelected = true;

                ArrayList<String> expressionList = new ArrayList<String>();
                for (ValueListBox vlb : expressionListBox.getListBoxList()) {
                    String selected = (String) vlb.getValue();
                    if (!selected.equals("any")) {
                        nothingSelected = false;
                        expressionList.add(selected);
                        allFilteredIds.add(selected);
                        Loggable.log(LoggingAction.A13, selected);

                    }
                }

//                ArrayList<String> locationList = new ArrayList<String>();
                if (locationids.size()>0) {
                    nothingSelected = false;
                    allFilteredIds.addAll(locationids);
                }
                for (ValueListBox vlb : locationListBox.getListBoxList()) {
                    String selected = (String) vlb.getValue();
                    if (!selected.equals("any")) {
                        nothingSelected = false;
                        locationids.add(selected);
                        allFilteredIds.add(selected);
                        Loggable.log(LoggingAction.A14, selected);

                    }
                }

                ArrayList<String> conditionList = new ArrayList<String>();
                for (ValueListBox vlb : conditionsListBox.getListBoxList()) {
                    String selected = (String) vlb.getValue();
                    if (!selected.equals("any")) {
                        nothingSelected = false;
                        conditionList.add(selected);
                        allFilteredIds.add(selected);
                        Loggable.log(LoggingAction.A15, selected);

                    }
                }

                ArrayList<String> processList = new ArrayList<String>();
                for (ValueListBox vlb : processesListBox.getListBoxList()) {
                    String selected = (String) vlb.getValue();
                    if (!selected.equals("any")) {
                        nothingSelected = false;
                        processList.add(selected);
                        Loggable.log(LoggingAction.A16, selected);

                    }
                }

                if (nothingSelected) {
                    executingQueryIcon.setVisible(false);
                    Window.alert("Please choose at least one experimental factor to filter on");
                }
                else {
                    //                 result.toArray(new KUPKBExpData[result.size()]);
                    SPARQLQueryBuilder sb = new SPARQLQueryBuilder(geneids, expressionList, locationids, conditionList, processList);
                    textArea.setText(sb.getSparqlQuery());
                    kupkbQuerySvc.advancedQuery(new String[0],
                            expressionList.toArray(new String[expressionList.size()]),
                            locationids.toArray(new String[locationids.size()]),
                            conditionList.toArray(new String[conditionList.size()]),
                            processList.toArray(new String[processList.size()]),
                            logicalAnd.isChecked(),
                            callbackAdvancedQuery);
                }
            }

            @Override
            public void onClick(ClickEvent clickEvent) {

                geneids.clear();
                locationids.clear();
                executingQueryIcon.setVisible(true);
                if (removeFilterButton.isEnabled()) {
                    // remove any previous filters that were set
                    removeFilters();
                }


//                String entityString = queryBox.getText().trim();
                String locationString = kupoQueryBox.getText().trim();

                if (locationString.isEmpty() || locationString.startsWith("(Any)")) {
//                    textArea.setText(generateSparql());
                    generateSparql();
//                    hiddenForm.setVisible(true);
//                    kupkbQuerySvc.evaluateSparqlQuery(textArea.getText(), "html", callbackSPARQL);
                }
                else {

                    // go and query the server to get the gene ids
                    String[] query = locationString.split("\\r?\\n");
                    // generate the callback for the gene search event
                    AsyncCallback<KUPKBOWLClass[]> callbackIntermResult = new AsyncCallback<KUPKBOWLClass[]>() {

                        public void onFailure(Throwable throwable) {
                            String details = throwable.getMessage();
                            executingQueryIcon.setVisible(false);
                            mainResultsPanel.setVisible(false);
                            Window.alert("Oops, something went wrong..sorry!");
                        }

                        public void onSuccess(KUPKBOWLClass[] result) {

                            for (KUPKBOWLClass obj : result) {
                                locationids.add(obj.getIRI());
                            }
//                            textArea.setText(generateSparql());
//                            sparqlQuery.setValue(generateSparql());
                            generateSparql();
//                            hiddenForm.setVisible(true);
//                            kupkbQuerySvc.evaluateSparqlQuery(textArea.getText(), "html", callbackSPARQL);
                        }
                    };

                    kupkbQuerySvc.suggestClass(query, callbackIntermResult);
                }
            }
        });


        resetSearch.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

//                queryBox.setText("(Any)");
                kupoQueryBox.setText("(Any)");
//                kupkbQuerySvc.getNameSpaces(callbackNamespaces);
                expressionListBox.reset();
                locationListBox.reset();
                conditionsListBox.reset();
                processesListBox.reset();
                mainResultsPanel.setVisible(false);
//                hiddenForm.setVisible(false);
//                resultsTable.setHTML("");
            }
        });

//        search.addClickHandler(new ClickHandler() {
//
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                kupkbQuerySvc.evaluateSparqlQuery(textArea.getText(), "html", callbackSPARQL);
//            }
//        });
    }


    /* static inner class, this is the widget that generates the various drop down lists */
    private static class ListWidget extends Composite {

        private Button plusButton = new Button("+");
        private Button minusButton = new Button("-");
        private VerticalPanel listBoxPanel = new VerticalPanel();

        private KUPKBQueryServiceAsync kupkbQuerySvc = GWT.create(KUPKBQueryService.class);


        private ArrayList<ValueListBox> listBoxList = new ArrayList<ValueListBox>();

        private ValueListBox<String> defaultListBox;

        private String LIST_BOX_TEMPLATE_ID;

        private Map<String, String> valuesMap = new LinkedHashMap<String, String>();

        private Renderer<String> renderer = new Renderer<String>() {
            @Override
            public String render(String s) {
                return valuesMap.get(s);
            }

            @Override
            public void render(String s, Appendable appendable) throws IOException {
            }
        };

        public ArrayList<ValueListBox> getListBoxList() {
            return listBoxList;
        }

        public Map<String, String> getValuesMap() {
            return valuesMap;
        }

        private KUPKBResources resources = GWT.create(KUPKBResources.class);

        final VerticalPanel mainPanel = new VerticalPanel();

        public final Image loadingListBoxIcon = new Image(resources.treeLoadingIcon());

        public ListWidget(String listBoxTemplateId) {

            this.LIST_BOX_TEMPLATE_ID = listBoxTemplateId;

            defaultListBox = new ValueListBox<String>(renderer);

            valuesMap.put("any", "Any");
            defaultListBox.setAcceptableValues(valuesMap.keySet());
            defaultListBox.setValue("any");
            listBoxList.add(defaultListBox);

            HorizontalPanel buttonPanel = new HorizontalPanel();

            plusButton.setStyleName("plusButton");
            minusButton.setStyleName("minusButton");
            buttonPanel.add(plusButton);
            buttonPanel.add(minusButton);
            buttonPanel.add(loadingListBoxIcon);
            loadingListBoxIcon.setVisible(true);

//          this is where the + buttons get added
            mainPanel.add(buttonPanel);
            mainPanel.add(listBoxPanel);

            listBoxPanel.add(defaultListBox);

            plusButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {

                    ValueListBox<String> newListBox = new ValueListBox<String>(renderer);
                    newListBox.setAcceptableValues(valuesMap.keySet());
                    newListBox.setValue("any");
                    listBoxList.add(newListBox);
                    mainPanel.add(newListBox);
                }
            });

            minusButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    if (listBoxList.size() >1) {
                        listBoxList.remove(listBoxList.size() -1);
                        mainPanel.remove(mainPanel.getWidgetCount() -1);
                    }
                }
            });

            updateListBox();
            initWidget(mainPanel);
        }

        public void updateListBox ( ) {

            AsyncCallback<String> callbackListBoxJson = new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable throwable) {
                }

                @Override
                public void onSuccess(String s) {

                    if (s != null) {

                        JSONValue value = JSONParser.parseLenient(s);
                        JSONObject object = value.isObject();
                        JSONObject results = object.get("results").isObject();
                        JSONArray bindings = results.get("bindings").isArray();

                        if (bindings.size() >0) {

                            for (int i=0; i<bindings.size(); i++) {
                                JSONObject productObj = bindings.get(i).isObject();
                                JSONObject label = productObj.get("label").isObject();
                                JSONValue labelValue = label.get("value").isString();

                                JSONObject resource = productObj.get("resource").isObject();
                                JSONValue resourceValue = resource.get("value").isString();

                                valuesMap.put(resourceValue.toString().replaceAll("\"", ""), labelValue.toString().replaceAll("\"", ""));
                            }

                            defaultListBox.setAcceptableValues(valuesMap.keySet());
                            loadingListBoxIcon.setVisible(false);

                        }
                    }
                }
            };

            kupkbQuerySvc.executeTemplateQuery(LIST_BOX_TEMPLATE_ID, "json", callbackListBoxJson);
        }

        public void reset() {
            if (listBoxList.size() >1) {
                for (int x = listBoxList.size(); x > 1 ; x-- ) {
                    listBoxList.remove(x -1);
                    mainPanel.remove(mainPanel.getWidgetCount() -1);
                }
            }

            ((ValueListBox<String>)listBoxList.get(0)).setValue("any");

        }
    }

    private void filter() {

        Set<String> terms = new HashSet<String>();
        if (model.getSelectedNodes().isEmpty()) {
            return;
        }

        // generate the callback for the gene search event
        AsyncCallback<String[]> callbackIntermResult = new AsyncCallback<String[]>() {

            public void onFailure(Throwable throwable) {
                String details = throwable.getMessage();
                System.out.println("Error:" + details);
                loadingTree.setVisible(false);

            }

            public void onSuccess(String [] result) {

                List list = Arrays.asList(result);

                if (!dataProvider.getList().isEmpty()) {
                    originalResults = new ArrayList<KUPKBExpData>(dataProvider.getList());
                    Set<KUPKBExpData> exp = new HashSet<KUPKBExpData>();
                    for (KUPKBExpData inList : dataProvider.getList()) {
                        String diseaseIRI = inList.getAnalyteDiseaseURI();
                        String anatomyIRI = inList.getAnalyteAnatomyURI();
                        String[] goIRIs = inList.getGoAnnotations();

                        for (String s : goIRIs) {
                            if (list.contains(s)) {
                                exp.add(inList);
                            }
                        }

                        if (list.contains(diseaseIRI)) {
                            exp.add(inList);
                        }
                        if (list.contains(anatomyIRI)) {
                            exp.add(inList);
                        }
                    }
                    rebuildResultsTable(exp.toArray(new KUPKBExpData[exp.size()]));
                    addFilterButton.setEnabled(false);
                    removeFilterButton.setEnabled(true);
//                    removeFilterButton.setVisible(true);
                }

                loadingTree.setVisible(false);

            }
        };
        List<String> filterLabel = new ArrayList<String>();
        for (MyTreeNode node : model.getSelectedNodes()) {
            if (node.getLabel().startsWith("Cell") || node.getLabel().startsWith("Anatomy")
                    || node.getLabel().startsWith("Disease") || node.getLabel().startsWith("Biological process")) {
                PopupPanel mes = new PopupPanel(true);
                mes.add(new Label("You can't filter on root terms " + node.getLabel()));
                mes.setVisible(true);
                return;
            }
            if (!filterLabel.contains(node.getLabel())) {
                filterLabel.add(node.getLabel());
            }
            terms.add(node.getIri());
        }

        model.getQueryService().getChildren(terms.toArray(new String[terms.size()]), callbackIntermResult);
        loadingTree.setVisible(true);
        StringBuilder sb = new StringBuilder("<b>Current Filter Set:</b> <span style=\"color:#E33B43\">");
        for (int x= 0; x <filterLabel.size(); x++) {
            sb.append(filterLabel.get(x));
            if (x != filterLabel.size() -1) {
                sb.append(", ");
            }
        }
        if (filterLabel.size() == 1) {
            Loggable.log(LoggingAction.A17, filterLabel.toString());
        }
        else {
            Loggable.log(LoggingAction.A18, filterLabel.toString());
        }

        sb.append("</span>");
        currentFilter.setHTML(sb.toString());
    }

    public void rebuildResultsTable(KUPKBExpData[] result) {

        dataProvider.getList().clear();
        List<KUPKBExpData> list = dataProvider.getList();

//        if (logicalAnd.isChecked()) {

        result = getIntersectingResults(result);

//        }

        resultsTable.getColumnSortList().push(resultsTable.getColumn(6));
        if (result.length == 0) {
            Window.alert("Sorry, no hits for your selected molecule(s) in the KUPKB");
            mainResultsPanel.setVisible(false);
            Loggable.log(LoggingAction.A21, "");
        }
        else if (result.length >= 999) {
            Window.alert("Sorry, this query has over 1000 results, please consider filtering by some more attributes (e.g. Up/Down) or choose a more specific location/cell type.");
            mainResultsPanel.setVisible(false);
            Loggable.log(LoggingAction.A21, "Greater than 1000 advanced search results");
        }
        else {
            list.addAll(Arrays.asList(result));
            Loggable.log(LoggingAction.A22, "");
            mainResultsPanel.setVisible(true);
        }

        Map<String, Set<String>> iriSet = new HashMap<String, Set<String>>();

        for (KUPKBExpData row : result) {
            if (row.getAnalyteAnatomyURI() != null) {
                if (!iriSet.containsKey(row.getAnalyteAnatomyURI())) {
                    iriSet.put(row.getAnalyteAnatomyURI(), new HashSet<String>());
                }
                iriSet.get(row.getAnalyteAnatomyURI()).add(row.getGeneid());
            }

            if (row.getAnalyteDiseaseURI() != null) {
                if (!iriSet.containsKey(row.getAnalyteDiseaseURI())) {
                    iriSet.put(row.getAnalyteDiseaseURI(), new HashSet<String>());
                }
                iriSet.get(row.getAnalyteDiseaseURI()).add(row.getGeneid());
            }

            if (row.getGoAnnotations() != null) {
                for (String goIri : row.getGoAnnotations()) {
                    if (!iriSet.containsKey(goIri)) {
                        iriSet.put(goIri, new HashSet<String>());
                    }
                    iriSet.get(goIri).add(row.getGeneid());
                }
            }
        }

        model.regenerateTree(iriSet);
        model.refreshRoot();

    }

    private KUPKBExpData[] getIntersectingResults(KUPKBExpData[] result) {

        Map<String, Set<String>> seenGenes = new HashMap<String, Set<String>>();
        Set<KUPKBExpData> itemsToKeep =new HashSet<KUPKBExpData>();

        for (KUPKBExpData data : result) {

            if (!seenGenes.containsKey(data.getGeneid())) {
                seenGenes.put(data.getGeneid(), new HashSet<String>());
            }

            seenGenes.get(data.getGeneid()).add(data.getExpressionURI());

            if (!data.getAnalyteAnatomyURI().equals("")) {
                seenGenes.get(data.getGeneid()).add(data.getAnalyteAnatomyURI());
            }
            if (!data.getAnalyteDiseaseURI().equals("")) {
                seenGenes.get(data.getGeneid()).add(data.getAnalyteDiseaseURI());
            }

        }

        for (KUPKBExpData data : result) {

//            Set<String> annotations = seenGenes.get(data.getGeneid());

            if (seenGenes.get(data.getGeneid()).containsAll(allFilteredIds)) {
                itemsToKeep.add(data);
            }
//            else {
//                for (String os : data.getOrthologs()) {
//                    annotations.addAll(seenGenes.get(os));
//                }
//                if (annotations.containsAll(allFilteredIds)) {
//                    itemsToKeep.add(data);
//                }
//            }
        }
        return itemsToKeep.toArray(new KUPKBExpData[itemsToKeep.size()]);
    }

    private void removeFilters() {
        removeFilterButton.setEnabled(false);
        currentFilter.setText("");
        addFilterButton.setEnabled(true);
    }


}