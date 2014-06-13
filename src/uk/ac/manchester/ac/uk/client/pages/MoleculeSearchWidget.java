package uk.ac.manchester.ac.uk.client.pages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import uk.ac.manchester.ac.uk.client.*;

import java.util.*;/*
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
import java.util.logging.Logger;

/**
 * Author: Simon Jupp<br>
 * Date: Oct 19, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class MoleculeSearchWidget extends Composite {

    private KUPKBQueryServiceAsync kupkbQuerySvc = GWT.create(KUPKBQueryService.class);

    private KUPKBCellTable resultsTable = new KUPKBCellTable(50);

    private GeneMultivalueSuggestBox queryBox = new GeneMultivalueSuggestBox("130", "330");

    private Button searchButton = new Button("Search");

    private Label errorMsgLabel = new Label();

    private HTML currentFilter = new HTML();

    private KUPKBResources resources = GWT.create(KUPKBResources.class);

    private ListDataProvider<KUPKBExpData> dataProvider;

    public final Image loadingImage = new Image(resources.tableLoadingIcon());

    private HorizontalPanel buttonPanel = new HorizontalPanel();

    private final VerticalPanel mainResultsPanel = new VerticalPanel();

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

    private CellTree tree = new CellTree(model, model.getRoot(), KUPKBEntryPoint.MyCellTreeResources.INSTANCE);

    public final Image loadingTree = new Image(resources.treeLoadingIcon());

    private Button addFilterButton = new Button("Add Filter");
    private Button removeFilterButton = new Button("Remove Filter");

    private List<KUPKBExpData> originalResults = new ArrayList<KUPKBExpData>();

    private FormPanel submitToVizForm = new FormPanel("_blank");

    public MoleculeSearchWidget () {

        dataProvider = new ListDataProvider<KUPKBExpData>();

        resultsTable.setHeaders(dataProvider);

        dataProvider.addDataDisplay(resultsTable);

        VerticalPanel rootPanel = new VerticalPanel();

        HorizontalPanel topSearchPanel = new HorizontalPanel();

        HorizontalPanel bottomResultsPanel = new HorizontalPanel();

        // the search box and top half of page

        buttonPanel.setSpacing(4);
        buttonPanel.add(searchButton);
        buttonPanel.add(loadingImage);

        errorMsgLabel.setStyleName("error");
        loadingImage.setVisible(false);

        HTMLPanel searchCaption = new HTMLPanel("<p>Search genes, proteins, miRNAs or metabolites</p>");
        VerticalPanel searchPanel = new VerticalPanel();

        searchPanel.add(searchCaption);

        //  searchPanel.add(new Label("(HINT: Search wildcards with * and multi-word terms with +)"));
        searchPanel.add(queryBox);
        searchPanel.add(buttonPanel);
        searchPanel.add(errorMsgLabel);


//        searchCaption.add(searchPanel);

//
        // TODO pull this free text out into a template
        String miRnaHelpText = "When querying miRNA please note that nomenclature for miRNA includes the species (e.g hsa for homo sapiens, mmu for mouse and rno for rat). " +
                "If you want to search for the same miRNA in different species (orthologs, eg hsa-miR-21 and mmu-miR-21) query the KUPKB using the * wildcard e.g.  *miR-21." +
                "Nomenclature for miRNA has recently evolved. Progressively names such as hsa-miR-17 will be replaced by " +
                "hsa-miR-17-5p and hsa-miR-17* will be replaced by hsa-miR-17-3p. However because data from the KUPKB might " +
                "include both old and new nomenclatures, we strongly suggest to query for *miR-17* that will retrieve all the possible names and will avoid empty search results";

        String miRnaIcon = "<img  ONMOUSEOVER=\"writetxt(\'" + miRnaHelpText + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">";

        searchPanel.add(
                new HTML("<p class=\"searchCaptionText\">" +
                        "Simply enter your gene, protein or miRNA " + miRnaIcon + " of interest into the query box and press search. You can search for multiple entities per line" +
                        " and we support a range of identifiers including entrez gene ids, gene names, uniprot ids and miRNA ids from MirBase DB. " +
                        "e.g. Search for TGFB1 or transforming growth factor or 3172. We have currently collected over 220 experiments, a summary of all the experiments collected is available <a href=\"etc/summary_KUPKBcontent.html\" _target=\"blank\">here</a>." +
                        " If you would like to submit your own datasets please choose the submit data tab above</p>"));




        topSearchPanel.add(searchPanel);

        String helpFilterText = "You can navigate the tree by clicking the small triangle. Each terms shows the number of unique genes/miRNAs that are annotated with that term or any of its children. " +
                "To filter select a term of interest from the tree and select the apply filter button. To remove the filter select the remove filter button";
        String helpIcon = "<img  ONMOUSEOVER=\"writetxt(\'" + helpFilterText + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">";
        // the tree and results table bit of the page
        HTML panel = new HTML("<div class=\"ikupHomeText\"><h2>Results View</h2>The results table shows the KUPKB experiments that reference your search terms. " +
                "You can sort the results table by clicking on the column headers. The navigation tree below gives you a summary of your results and can be used to filter the results table." +
                "(hint: hold down ctrl to select multiple filters)");


        VerticalPanel browserPanel = new VerticalPanel();
        browserPanel.add(panel);


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
//        browserPanel.add(filterPanel);


        FlowPanel treePanel = new FlowPanel();
        treePanel.add(tree);
        treePanel.setStyleName("treePanel");

//        bottomResultsPanel.add(treePanel);

        SimplePager pager = new SimplePager(SimplePager.TextLocation.RIGHT);
        pager.setPageSize(50);
        pager.setDisplay(resultsTable);
        VerticalPanel pagerAndResultsTabelPanel = new VerticalPanel();

        submitToVizForm.setAction("http://www.kupkb.org/vis/index.php?");
        submitToVizForm.setMethod(FormPanel.METHOD_POST);

        // code for submitting results to KUPVizNet
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
        // end of KUPVizNet code



//        pagerAndResultsTabelPanel.add(submitToVizForm);
        pagerAndResultsTabelPanel.add(pager);
        pagerAndResultsTabelPanel.add(resultsTable);
        bottomResultsPanel.add(pagerAndResultsTabelPanel);

        rootPanel.add(topSearchPanel);

        rootPanel.add(new HTML("<div><hr style=\"height:8px;;border-width:0;color:#E33B43;background-color:#E33B43;\"></div>"));


        mainResultsPanel.add(browserPanel);
        mainResultsPanel.add(bottomResultsPanel);
        mainResultsPanel.setVisible(false);
        rootPanel.add(mainResultsPanel);
//        rootPanel.add(bottomResultsPanel);

        queryBox.setFocus(true);

        // Listen for mouse events on the Add button.
        searchButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                query();
            }
        });

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

        initWidget(rootPanel);

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
            Loggable.log(LoggingAction.A11, filterLabel.toString());
        }
        else {
            Loggable.log(LoggingAction.A12, filterLabel.toString());
        }
        sb.append("</span>");
        currentFilter.setHTML(sb.toString());
    }



    private void query() {

        final String queryText = queryBox.getText().trim();
        queryBox.setFocus(true);

        // Initialize the service proxy.
        if (kupkbQuerySvc == null) {
            kupkbQuerySvc = GWT.create(KUPKBQueryService.class);
        }

        // do the gene confirmation query

        String[] query = queryText.split("\\r?\\n");

        if (query.length == 1) {
            Loggable.log(LoggingAction.A01, queryText.replaceAll("\\r?\\n" , ","));
        }
        else {
            Loggable.log(LoggingAction.A02, queryText.replaceAll("\\r?\\n" , ","));
        }


        if (query.length >100) {
            errorMsgLabel.setText("Sorry, The KUPKB is currently limited to 100 query items at a time");
            return;
        }

        // should probably check for dodgy characters etc here...

        final GeneSelectPopUpPanel panel = new GeneSelectPopUpPanel();

        panel.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                generateResultsTable (panel.getSelectedResults());

            }
        });

        // generate the callback for the gene search event
        AsyncCallback<KUPKBQueryObject[]> callbackIntermResult = new AsyncCallback<KUPKBQueryObject[]>() {

            public void onFailure(Throwable throwable) {
                String details = throwable.getMessage();
                errorMsgLabel.setText("Error, an administrator has been notified: " + details);
                errorMsgLabel.setVisible(true);
            }

            public void onSuccess(KUPKBQueryObject[] result) {

                if (result.length == 0) {
                    errorMsgLabel.setText("Sorry, but we couldn't find what you were looking for in any of the experiments in the KUPKB.");
                    errorMsgLabel.setVisible(true);
                    loadingImage.setVisible(false);
                }
                else {
                    errorMsgLabel.setVisible(false);
                    panel.showResults(result);
                    loadingImage.setVisible(false);

                }
            }
        };

        kupkbQuerySvc.suggest(query, callbackIntermResult);
        loadingImage.setVisible(true);
    }

    // this method generates the results table based on the genes selected and confirmed form the query box
    public void generateResultsTable(Set<String> selectedResults) {

        dataProvider.getList().clear();

        if (removeFilterButton.isEnabled()) {
            // remove any previous filters that were set
            removeFilters();
        }

        // create a string for the history token
        StringBuilder sb = new StringBuilder();

        if (selectedResults.size() == 0) {
            Loggable.log(LoggingAction.A03, selectedResults.toString());
        }
        else {
            Loggable.log(LoggingAction.A04, selectedResults.toString());
        }


        for (String selectedResult : selectedResults) {
            if (!selectedResult.isEmpty()) {
                sb.append(selectedResult);
                sb.append(",");
            }
        }
//        System.err.println("creating history item: " + sb.toString());
//        History.newItem("queryids=" + sb.toString());


        if (selectedResults.size() >0) {
            // Set up the callback object.
            AsyncCallback<KUPKBExpData[]> callback = new AsyncCallback<KUPKBExpData[]>() {

                public void onFailure(Throwable throwable) {
                    String details = throwable.getMessage();
                    errorMsgLabel.setText("Error, an administrator has been notified:  " + details);
                    errorMsgLabel.setVisible(true);
                    loadingImage.setVisible(false);
                    mainResultsPanel.setVisible(false);
                }

                public void onSuccess(KUPKBExpData[] result) {
                    rebuildResultsTable(result);
                }
            };

            // Make the call to the KUPKB query price service.
            kupkbQuerySvc.query(selectedResults.toArray(new String[selectedResults.size()]), callback);
            loadingImage.setVisible(true);

        }
    }

    public void rebuildResultsTable(KUPKBExpData[] result) {

        dataProvider.getList().clear();
        List<KUPKBExpData> list = dataProvider.getList();
        list.addAll(Arrays.asList(result));
        resultsTable.getColumnSortList().push(resultsTable.getColumn(6));
        if (dataProvider.getList().size() == 0) {
            Loggable.log(LoggingAction.A20, "");
            errorMsgLabel.setText("Sorry, no hits for your selected molecule(s) in the KUPKB");
            errorMsgLabel.setVisible(true);
            mainResultsPanel.setVisible(false);
        }
        else {
            Loggable.log(LoggingAction.A19, "");
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

//        model.regenerateTree(iriSet);
//        model.refreshRoot();

        loadingImage.setVisible(false);


    }

    private void removeFilters() {
        removeFilterButton.setEnabled(false);
        currentFilter.setText("");
        addFilterButton.setEnabled(true);
    }


    // this is and inner class for the widget for the gene select popup panel
    private static class GeneSelectPopUpPanel extends PopupPanel {

        private VerticalPanel poppupPanel = new VerticalPanel();

        private FlexTable geneResultsTable = new FlexTable();

        private Button searchButton = new Button("Continue");
        private Button cancelButton = new Button("Cancel");
        private Button selectButton = new Button("Invert selection");

        public GeneSelectPopUpPanel () {
            super();
            setTitle("Select Entities");
            setStyleName("popUpPanel");
            setGlassEnabled(true);
            setPopupPosition(240, 40);

            geneResultsTable.setCellPadding(4);
            geneResultsTable.setCellSpacing(4);
//            geneResultsTable.setStyleName("geneInterimResultsTable");

        }


        public void showResults (KUPKBQueryObject[] kupkbQueryObjects) {
            if (kupkbQueryObjects.length < 1) {
                poppupPanel.add(new Label("Sorry no results :-("));
            }
            else {

                poppupPanel.add(new HTML("<div style=\"padding-left:5px;\">These molecules were found in the KUPKB matching your query.</div>"));

                geneResultsTable.setHTML(0, 0, "Select");
                geneResultsTable.setHTML(0, 1, "Id");
                geneResultsTable.setHTML(0, 2, "Name");
                geneResultsTable.setHTML(0, 3, "Species");
                geneResultsTable.setHTML(0, 4, "Description");
                geneResultsTable.getRowFormatter().addStyleName(0, "geneListHeader");

                int counter = 1;
                for (KUPKBQueryObject object : kupkbQueryObjects) {

                    geneResultsTable.setWidget(counter, 0, new MyCheckBox());
                    geneResultsTable.setText(counter, 1, object.getGeneid());
                    geneResultsTable.setText(counter, 2, object.getSymbol() + " (" + object.getType() + ")");
                    geneResultsTable.setText(counter, 3, object.getSpecies());
                    geneResultsTable.setText(counter, 4, object.getComment());
                    counter ++;

                }

                ScrollPanel scrollPanel = new ScrollPanel();
                scrollPanel.add(geneResultsTable);
                scrollPanel.setSize("600px", "400px");
                poppupPanel.add(scrollPanel);
                HorizontalPanel buttonPanel = new HorizontalPanel();
                selectButton.setStyleName("buttonPadding");
                searchButton.setStyleName("buttonPadding");
                cancelButton.setStyleName("buttonPadding");
                buttonPanel.add(selectButton);
                buttonPanel.add(searchButton);
                buttonPanel.add(cancelButton);
                poppupPanel.add(buttonPanel);

                // Listen for mouse events on the Add button.
                searchButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        hide(true);
                    }
                });

                selectButton.addClickHandler(new ClickHandler(){

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        int rowCount = geneResultsTable.getRowCount();
                        for (int x = 1; x<rowCount; x++) {
                            MyCheckBox box = (MyCheckBox) geneResultsTable.getWidget(x, 0);
                            box.setValue(!box.isSelected());
                        }
                    }
                });
                // Listen for mouse events on the Add button.
                cancelButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        geneResultsTable.clear();
                        hide();
                    }
                });

                add(poppupPanel);
                show();

            }
        }

        public void hide() {
            setVisible(false);
        }

        public Set<String> getSelectedResults () {

            Set<String> geneids = new HashSet<String>();
            int rowcount = geneResultsTable.getRowCount();
            for (int i = 1; i<rowcount; i++) {
                if (((MyCheckBox) geneResultsTable.getWidget(i, 0)).isSelected()) {
                    if (geneResultsTable.getText(i, 1) != null ) {
                        geneids.add(geneResultsTable.getText(i, 1));
                    }
                }
            }
            return geneids;
        }

        class MyCheckBox extends CheckBox {

            private boolean selected = true;
            public MyCheckBox () {
                super();
                setValue(selected);
                addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent clickEvent) {
                        selected = !selected;
                    }
                })    ;
            }

            @Override
            public void setValue(Boolean value) {
                super.setValue(value);
                selected = value;
            }

            public boolean isSelected() {
                return selected;
            }
        }
    }


}
