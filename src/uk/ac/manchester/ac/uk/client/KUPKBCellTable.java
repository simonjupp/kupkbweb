package uk.ac.manchester.ac.uk.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.view.client.ListDataProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;/*
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
 * Date: Oct 17, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class KUPKBCellTable extends CellTable<KUPKBExpData> {


    // custom loader for results table css, the GWT CellTable loads it's css separately so if you want to modify the CSS you have to do this
    public interface MyCellTableResources extends CellTable.Resources {

        /**
         * The styles used in this widget.
         */
        @Source("results.css")
        CellTable.Style cellTableStyle();

    }

    public KUPKBCellTable(int maxValue) {
        super(maxValue, ( MyCellTableResources) GWT.create(MyCellTableResources.class) );

    }

    // setting up the results CellTable
    public void setHeaders(ListDataProvider<KUPKBExpData> dataProvider) {
        // setting headers
        // lots of hard coded urls here, these are only really temporary until we point to our own gene report/ experiment report pages...
        Column<KUPKBExpData, SafeHtml> geneColumn = new Column<KUPKBExpData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(KUPKBExpData kupkbExpData) {
                if (kupkbExpData.getExperimentType().equals("miRNA List")) {
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"http://www.mirbase.org/cgi-bin/" + kupkbExpData.getGeneid() + "\" target=\"blank\">" + kupkbExpData.getGeneSymbol() + "</a>").toSafeHtml();
                }
                else if (kupkbExpData.getExperimentType().toLowerCase().equals("metabolite list")) {
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"http://www.hmdb.ca/metabolites/" + kupkbExpData.getGeneid() + "\" target=\"blank\">" + kupkbExpData.getGeneSymbol() + "</a>").toSafeHtml();
                }
                else {
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"http://www.ncbi.nlm.nih.gov/gene/" + kupkbExpData.getGeneid() + "\" target=\"blank\">" + kupkbExpData.getGeneSymbol() + "</a>").toSafeHtml();
                }

            }
        };

        Column<KUPKBExpData, SafeHtml> experimentNameColumn = new Column<KUPKBExpData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(KUPKBExpData data) {
                if (data.getExperimentDisplayLabel().startsWith("http")) {
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"" + data.getExperimentDisplayLabel() + "\" target=\"blank\">" + data.getExperimentDisplayLabel() + "</a>&nbsp;&nbsp;<img  ONMOUSEOVER=\"writetxt(\'" + data.getExperimentDesc() + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">").toSafeHtml();
                }
                else if (data.getPmid() != null){
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"http://www.ncbi.nlm.nih.gov/pubmed/" + data.getPmid() +"\" target=\"blank\">" + data.getExperimentDisplayLabel() + "</a>&nbsp;&nbsp;<img  ONMOUSEOVER=\"writetxt(\'" + data.getExperimentDesc() + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">").toSafeHtml();
                }
                else if (data.getSeeAlsoLink() != null){
                    return new SafeHtmlBuilder().appendHtmlConstant("<a href=\"" + data.getSeeAlsoLink() + "\" target=\"blank\">" + data.getExperimentDisplayLabel() + "</a>&nbsp;&nbsp;<img  ONMOUSEOVER=\"writetxt(\'" + data.getExperimentDesc() + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">").toSafeHtml();
                }
                else if (data.getExperimentDisplayLabel().equals("")) {
                    return new SafeHtmlBuilder().appendHtmlConstant(data.getExperimentID() + "<span>&nbsp;&nbsp;<img  ONMOUSEOVER=\"writetxt(\'" + data.getExperimentDesc() + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">").toSafeHtml();
                }
                return new SafeHtmlBuilder().appendHtmlConstant(data.getExperimentDisplayLabel() + "<span>&nbsp;&nbsp;<img  ONMOUSEOVER=\"writetxt(\'" + data.getExperimentDesc() + "\')\" ONMOUSEOUT=\"writetxt(0)\" src=\"images/questionmark.png\">").toSafeHtml();
            }

        };

        TextColumn<KUPKBExpData> experimentTypeColumn = new TextColumn<KUPKBExpData>() {
            @Override
            public String getValue(KUPKBExpData data) {
                return data.getExperimentType().replaceAll(" List", "");
            }
        };

        TextColumn<KUPKBExpData> speciesColumn = new TextColumn<KUPKBExpData>() {
            @Override
            public String getValue(KUPKBExpData data) {
                if (data.getMaturity() != null) {
                    if (!data.getMaturity().equals(""))
                        return data.getSpecies() + " (" + data.getMaturity() + ")";
                }
                return data.getSpecies();
            }

            @Override
            public boolean isSortable() {
                return true;
            }
        };

        TextColumn<KUPKBExpData> anatomyColumn = new TextColumn<KUPKBExpData>() {
            @Override
            public String getValue(KUPKBExpData data) {
                return data.getAnalyteAnatomy();
            }
        };

        TextColumn<KUPKBExpData> diseaseColumn = new TextColumn<KUPKBExpData>() {
            @Override
            public String getValue(KUPKBExpData data) {
                if (data.getSeverity() != null) {
                    if (!data.getSeverity().equals("")) {
                        return data.getAnalyteDisease() + " (" + data.getSeverity() + ")";
                    }
                }
                return data.getAnalyteDisease();
            }
        };


        Column<KUPKBExpData, KUPKBExpData> expressionColumn = new Column<KUPKBExpData, KUPKBExpData>(new AbstractCell<KUPKBExpData>() {
            @Override
            public void render(Context context, KUPKBExpData o, SafeHtmlBuilder safeHtmlBuilder) {
                if (o == null) {
                    return;
                }
                // Append some HTML that sets the text color.
                if (o.getExpression().equals("Up")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:red;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Down")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#008F29;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("down")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:green;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Unmodified")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#FFFAA8;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Absent")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:white;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Possible")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#919191;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Present")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#515151;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Weak")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#D0E1F2;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Medium")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#67A2D8;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                else if (o.getExpression().equals("Strong")) {
                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#1D496E;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
                }
                safeHtmlBuilder.appendHtmlConstant("&nbsp;");
                safeHtmlBuilder.appendHtmlConstant("</div>");

            }
        }) {

            @Override
            public KUPKBExpData getValue(KUPKBExpData data) {
                return data;
            }
        };

        geneColumn.setSortable(true);
        experimentNameColumn.setSortable(true);
        experimentTypeColumn.setSortable(true);
        speciesColumn.setSortable(true);
        anatomyColumn.setSortable(true);
        diseaseColumn.setSortable(true);
        expressionColumn.setSortable(true);

        this.addColumn(geneColumn, "Entity id");
        this.addColumn(speciesColumn, "Species");
        this.addColumn(anatomyColumn, "Anatomy");
        this.addColumn(diseaseColumn, "Disease/Model");
        this.addColumn(expressionColumn, "Expression");
        this.addColumn(experimentNameColumn, "Experiment");
        this.addColumn(experimentTypeColumn, "Type");

        // set up sorters
        List<KUPKBExpData> list = dataProvider.getList();

        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler0 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);

                if (event.getColumn().equals(getColumn(0))) {
                    Loggable.log(LoggingAction.A05,"");
                }

            }
        };


        columnSortHandler0.setComparator(this.getColumn(0),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the genename columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getGeneSymbol().toLowerCase().compareTo(o2.getGeneSymbol().toLowerCase()) : 1;
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler0);


        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler2 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);

                if (event.getColumn().equals(getColumn(2))) {
                    Loggable.log(LoggingAction.A06,"");
                }
            }
        };
        columnSortHandler2.setComparator(this.getColumn(2),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the anatomy columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getAnalyteAnatomy().compareTo(o2.getAnalyteAnatomy()) : 1;
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler2);

        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler3 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);
                if (event.getColumn().equals(getColumn(03))) {

                    Loggable.log(LoggingAction.A07,"");

                }
            }
        };
        columnSortHandler3.setComparator(this.getColumn(3),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the anatomy columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getAnalyteDisease().compareTo(o2.getAnalyteDisease()) : 1;
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler3);

        final List<String> sortOrder = new ArrayList<String>();
        sortOrder.add("Up");
        sortOrder.add("Unmodified");
        sortOrder.add("Down");
        sortOrder.add("down");
        sortOrder.add("Strong");
        sortOrder.add("Medium");
        sortOrder.add("Weak");
        sortOrder.add("Present");
        sortOrder.add("Possible");
        sortOrder.add("Absent");

        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler4 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);
                if (event.getColumn().equals(getColumn(4))) {

                    Loggable.log(LoggingAction.A08,"");
                }

            }
        };
        columnSortHandler4.setComparator(this.getColumn(4),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        // Compare the name columns.
                        if (o1 != null) {
                            return sortOrder.indexOf(o1.getExpression()) - sortOrder.indexOf(o2.getExpression());
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler4);

        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler5 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);
                if (event.getColumn().equals(getColumn(5))) {

                    Loggable.log(LoggingAction.A09,"");
                }

            }
        };
        columnSortHandler5.setComparator(this.getColumn(5),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the anatomy columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getExperimentID().compareTo(o2.getExperimentID()) : 1;
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler5);

        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler6 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
                list) {
            @Override
            public void onColumnSort(ColumnSortEvent event) {
                super.onColumnSort(event);
                if (event.getColumn().equals(getColumn(6))) {

                    Loggable.log(LoggingAction.A10,"");
                }

            }
        };
        columnSortHandler6.setComparator(this.getColumn(6),
                new Comparator<KUPKBExpData>() {

                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
                        if (o1 == o2) {
                            return 0;
                        }

                        // Compare the anatomy columns.
                        if (o1 != null) {
                            return (o2 != null) ? o1.getExperimentType().compareTo(o2.getExperimentType()) : 1;
                        }
                        return -1;
                    }
                });
        this.addColumnSortHandler(columnSortHandler6);

        this.getColumnSortList().push(this.getColumn(4));


    }
//
//    // The advanced results table
//    public void setHeadersAdvanced(ListDataProvider<KUPKBExpData> dataProvider) {
//        // setting headers
//        // lots of hard coded urls here, these are only really temporary until we point to our own gene report/ experiment report pages...
//        Column<KUPKBExpData, SafeHtml> geneColumn = new Column<KUPKBExpData, SafeHtml>(new SafeHtmlCell()) {
//            @Override
//            public SafeHtml getValue(KUPKBExpData kupkbExpData) {
//                return new SafeHtmlBuilder().appendHtmlConstant(kupkbExpData.getGeneSymbol()).toSafeHtml();
//            }
//        };
//
//        Column<KUPKBExpData, SafeHtml> experimentNameColumn = new Column<KUPKBExpData, SafeHtml>(new SafeHtmlCell()) {
//            @Override
//            public SafeHtml getValue(KUPKBExpData data) {
//                return new SafeHtmlBuilder().appendHtmlConstant(data.getExperimentID()).toSafeHtml();
//            }
//
//        };
//
//        TextColumn<KUPKBExpData> speciesColumn = new TextColumn<KUPKBExpData>() {
//            @Override
//            public String getValue(KUPKBExpData data) {
//                if (data.getMaturity() != null) {
//                    return data.getSpecies() + " (" + data.getMaturity() + ")";
//                }
//                return data.getSpecies();
//            }
//
//            @Override
//            public boolean isSortable() {
//                return true;
//            }
//        };
//
//        TextColumn<KUPKBExpData> anatomyColumn = new TextColumn<KUPKBExpData>() {
//            @Override
//            public String getValue(KUPKBExpData data) {
//                return data.getAnalyteAnatomy();
//            }
//        };
//
//        TextColumn<KUPKBExpData> diseaseColumn = new TextColumn<KUPKBExpData>() {
//            @Override
//            public String getValue(KUPKBExpData data) {
//                if (data.getSeverity() != null) {
//                    return data.getAnalyteDisease() + " (" + data.getSeverity() + ")";
//                }
//                return data.getAnalyteDisease();
//            }
//        };
//
//
//        Column<KUPKBExpData, KUPKBExpData> expressionColumn = new Column<KUPKBExpData, KUPKBExpData>(new AbstractCell<KUPKBExpData>() {
//            @Override
//            public void render(Context context, KUPKBExpData o, SafeHtmlBuilder safeHtmlBuilder) {
//                if (o == null) {
//                    return;
//                }
//                // Append some HTML that sets the text color.
//                if (o.getExpression().equals("Up")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:red;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Down")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#008F29;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("down")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:green;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Unmodified")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#FFFAA8;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Absent")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:white;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Possible")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#919191;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Present")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#515151;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Weak")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#D0E1F2;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Medium")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#67A2D8;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                else if (o.getExpression().equals("Strong")) {
//                    safeHtmlBuilder.appendHtmlConstant("<div style=\"background:#1D496E;border:1px solid black;width:30px;height:30px\"></div>" + o.getExpression());
//                }
//                safeHtmlBuilder.appendHtmlConstant("&nbsp;");
//                safeHtmlBuilder.appendHtmlConstant("</div>");
//
//            }
//        }) {
//
//            @Override
//            public KUPKBExpData getValue(KUPKBExpData data) {
//                return data;
//            }
//        };
//
//        geneColumn.setSortable(true);
//        experimentNameColumn.setSortable(true);
////        experimentTypeColumn.setSortable(true);
//        speciesColumn.setSortable(true);
//        anatomyColumn.setSortable(true);
//        diseaseColumn.setSortable(true);
//        expressionColumn.setSortable(true);
//
//        this.addColumn(geneColumn, "Entity id");
//        this.addColumn(speciesColumn, "Species");
//        this.addColumn(anatomyColumn, "Anatomy");
//        this.addColumn(diseaseColumn, "Disease/Model");
//        this.addColumn(expressionColumn, "Expression");
//        this.addColumn(experimentNameColumn, "Experiment");
////        this.addColumn(experimentTypeColumn, "Type");
//
//        // set up sorters
//        List<KUPKBExpData> list = dataProvider.getList();
//
//        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler0 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
//                list);
//        columnSortHandler0.setComparator(this.getColumn(0),
//                new Comparator<KUPKBExpData>() {
//
//                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
//                        if (o1 == o2) {
//                            return 0;
//                        }
//
//                        // Compare the genename columns.
//                        if (o1 != null) {
//                            return (o2 != null) ? o1.getGeneSymbol().toLowerCase().compareTo(o2.getGeneSymbol().toLowerCase()) : 1;
//                        }
//                        return -1;
//                    }
//                });
//        this.addColumnSortHandler(columnSortHandler0);
//
//
//        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler2 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
//                list);
//        columnSortHandler2.setComparator(this.getColumn(2),
//                new Comparator<KUPKBExpData>() {
//
//                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
//                        if (o1 == o2) {
//                            return 0;
//                        }
//
//                        // Compare the anatomy columns.
//                        if (o1 != null) {
//                            return (o2 != null) ? o1.getAnalyteAnatomy().compareTo(o2.getAnalyteAnatomy()) : 1;
//                        }
//                        return -1;
//                    }
//                });
//        this.addColumnSortHandler(columnSortHandler2);
//
//        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler3 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
//                list);
//        columnSortHandler3.setComparator(this.getColumn(3),
//                new Comparator<KUPKBExpData>() {
//
//                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
//                        if (o1 == o2) {
//                            return 0;
//                        }
//
//                        // Compare the anatomy columns.
//                        if (o1 != null) {
//                            return (o2 != null) ? o1.getAnalyteDisease().compareTo(o2.getAnalyteDisease()) : 1;
//                        }
//                        return -1;
//                    }
//                });
//        this.addColumnSortHandler(columnSortHandler3);
//
//        final List<String> sortOrder = new ArrayList<String>();
//        sortOrder.add("Up");
//        sortOrder.add("Unmodified");
//        sortOrder.add("Down");
//        sortOrder.add("down");
//        sortOrder.add("Strong");
//        sortOrder.add("Medium");
//        sortOrder.add("Weak");
//        sortOrder.add("Present");
//        sortOrder.add("Possible");
//        sortOrder.add("Absent");
//
//        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler4 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
//                list);
//        columnSortHandler4.setComparator(this.getColumn(4),
//                new Comparator<KUPKBExpData>() {
//
//                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
//                        // Compare the name columns.
//                        if (o1 != null) {
//                            return sortOrder.indexOf(o1.getExpression()) - sortOrder.indexOf(o2.getExpression());
//                        }
//                        return -1;
//                    }
//                });
//        this.addColumnSortHandler(columnSortHandler4);
//
//        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler5 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
//                list);
//        columnSortHandler5.setComparator(this.getColumn(5),
//                new Comparator<KUPKBExpData>() {
//
//                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
//                        if (o1 == o2) {
//                            return 0;
//                        }
//
//                        // Compare the anatomy columns.
//                        if (o1 != null) {
//                            return (o2 != null) ? o1.getExperimentID().compareTo(o2.getExperimentID()) : 1;
//                        }
//                        return -1;
//                    }
//                });
//        this.addColumnSortHandler(columnSortHandler5);
//
////        ColumnSortEvent.ListHandler<KUPKBExpData> columnSortHandler6 = new ColumnSortEvent.ListHandler<KUPKBExpData>(
////                list);
////        columnSortHandler6.setComparator(this.getColumn(6),
////                new Comparator<KUPKBExpData>() {
////
////                    public int compare(KUPKBExpData o1, KUPKBExpData o2) {
////                        if (o1 == o2) {
////                            return 0;
////                        }
////
////                        // Compare the anatomy columns.
////                        if (o1 != null) {
////                            return (o2 != null) ? o1.getExperimentType().compareTo(o2.getExperimentType()) : 1;
////                        }
////                        return -1;
////                    }
////                });
////        this.addColumnSortHandler(columnSortHandler6);
//
//        this.getColumnSortList().push(this.getColumn(4));
//
//
//    }


}
