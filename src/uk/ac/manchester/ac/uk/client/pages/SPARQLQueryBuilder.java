package uk.ac.manchester.ac.uk.client.pages;

import java.util.ArrayList;
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
 * Date: Sep 28, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class SPARQLQueryBuilder {

    private static String NEW_LINE = "\n";

    private static String SPACE = " ";

    private static String DOT = ".";

    private static String FILTER = "filter";

    private static String LT = "<";
    private static String GT = ">";
    private static String BAR = "|";

    private static String OPEN_B = "(";
    private static String CLOSE_B = ")";
    private static String OPEN_P = "{";
    private static String CLOSE_P = "}";
    private static String EQUALS = "=";

    private static String PREFIXES = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>" + NEW_LINE +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>" + NEW_LINE +
            "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>" + NEW_LINE +
            "PREFIX PATO:<http://purl.org/obo/owl/PATO#>" + NEW_LINE +
            "PREFIX core:<http://purl.uniprot.org/core/>" + NEW_LINE +
            "PREFIX kupo:<http://www.kupkb.org/data/kupo/>" + NEW_LINE + NEW_LINE;

//    private String SELECT = "SELECT DISTINCT ?geneId? geneSymbol ?experimentId ?species ?analyteBioMaterialURI ?analyteanatomy ?analyteDiseaseURI ?analytedisease ?laterality ?severity ?deviationFromNormal ?maturity ?expressionLabel ?pvalue ?foldChange" + NEW_LINE;
    private String SELECT = "SELECT DISTINCT ?geneId ?geneSymbol ?experimentId ?annotation ?expDesc ?experimentDisplayName ?species " +
        "?experimentTypeLabel ?maturity ?laterality ?analyteBioMaterialURI ?analyteanatomy ?analyteDiseaseURI ?analytedisease ?severity ?deviationFromNormal ?expressionURI ?expressionLabel  ?expDesc ?pmid ?seeAlsoLink" + NEW_LINE;

    private String WHERE_START = "WHERE {" + NEW_LINE;

    private String STATIC_BODY_GENES = "{\n" +
            "  {?geneId rdf:type <http://bio2rdf.org/ns/geneid:Gene>}\n" +
            "  UNION\n" +
            "  {?geneId rdf:type kupkb:KUPKB_1000056}\n" +
            "  UNION\n" +
            "  {?geneId rdf:type <http://bio2rdf.org/ns/hmdb:Compound>}\n" +
            "} .\n" +
            "{\n" +
            "  {?geneId <http://bio2rdf.org/ns/bio2rdf:symbol> ?geneSymbol}\n" +
            "  UNION\n" +
            "  {?geneId rdfs:label ?geneSymbol}\n" +
            "}\n" +
            "?geneId <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
            "?taxon rdfs:label ?species .\n";

    private String STATIC_BODY_LISTMEMBER =
            "\n" +
                    "{\n" +
                    "  {?listmember kupkb:hasDatabaseRef ?geneId}\n" +
                    "  UNION\n" +
                    "  { ?geneId <http://bio2rdf.org/ns/uniprot:xProtein> ?uniprot .\n" +
                    "    ?listmember kupkb:hasDatabaseRef ?uniprot\n" +
                    "  }\n" +
                    "}\n" +
                    "\n";


    private String STAIC_BODY_ANNOTATIONS =
            "OPTIONAL {\n" +
                    "?listmember kupkb:pValue ?pvalue \n" +
                    "}\n" +
                    "OPTIONAL {\n" +
                    "?listmember kupkb:foldChange ?foldChange \n" +
                    "}\n" +
                    "?compoundList kupkb:hasMember ?listmember .\n" +
                    "?compoundList rdf:type ?experimentType .\n" +
                    "?experimentType rdfs:label ?experimentTypeLabel .\n" +
                    "filter (?experimentType = kupkb:KUPKB_1000029 || ?experimentType = kupkb:KUPKB_1000027 ||\n" +
                    "        ?experimentType = kupkb:KUPKB_1000073 || ?experimentType = kupkb:KUPKB_1000030 || ?experimentType = kupkb:KUPKB_1000028 || ?experimentType = kupkb:KUPKB_1000077) .\n" +
                    "?analysis kupkb:produces ?compoundList .\n" +
                    "?analysis kupkb:annotatedWith ?annotation .\n" +
                    "?analysis kupkb:analysisOf  ?experimentId  .\n" +
                    "OPTIONAL { ?experimentId rdfs:comment  ?expDesc  .}\n" +
                    "OPTIONAL { ?experimentId rdfs:label  ?experimentDisplayName  .}\n" +
                    "OPTIONAL { ?experimentId <http://www.kupkb.org/data/kupkb/experiment/pmid> ?pmid . }\n" +
                    "OPTIONAL { ?experimentId rdfs:seeAlso ?seeAlsoLink . }\n" +
                    "?annotation kupkb:hasAnnotationRole kupo:KUPO_0300008 .\n";


    private String OPTIONAL_CONDITIONS = "OPTIONAL {\n" +
            "?annotation kupkb:hasQuality ?lateralityURI .\n" +
            "?lateralityURI rdfs:subClassOf kupo:KUPO_0300002 .\n" +
            "?lateralityURI rdfs:label ?laterality\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?annotation kupkb:hasQuality ?severityURI .\n" +
            "?severityURI rdfs:subClassOf kupo:KUPO_0300001 .\n" +
            "?severityURI rdfs:label ?severity\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?annotation kupkb:hasQuality ?deviationURI .\n" +
            "?deviationURI rdfs:subClassOf PATO:PATO_0000069 .\n" +
            "?deviationURI rdfs:label ?deviationFromNormal\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?annotation kupkb:hasQuality ?maturityURI .\n" +
            "?maturityURI rdfs:subClassOf PATO:PATO_0000261 .\n" +
            "?maturityURI rdfs:label ?maturity\n" +
            "FILTER(langMatches(lang(?maturity), \"en\"))\n" +
            "}\n";

//    private String CONDITIONS = "?annotation kupkb:hasDisease ?analyteDiseaseURI .\n" +
//            "?analyteDiseaseURI rdfs:label ?analytedisease . \n";

    private String WHERE_END = CLOSE_P + NEW_LINE;
//    private String LIMIT = "LIMIT 1000" + NEW_LINE;
    private String LIMIT = NEW_LINE;

    private String sparqlQuery = "";

    public SPARQLQueryBuilder(List<String> geneIds,
                              List<String> expressionIds, List<String> locationIds,
                              List<String> conditionIds, List<String> processIds)  {


        // if we have gene ids we need to make a filter

        String geneIdFilterString = generateFilterString(geneIds, "?geneId");

        String expressionFilterString = generateExpressionUnionString(expressionIds);

        String anatomyFilterString = generateAnatomyFilterString(locationIds);

        String conditionsFilterString = generateConditionFilterString(conditionIds);

        String processFilterString = generateProcessFilterString(processIds);

        StringBuilder sb = new StringBuilder();

        sb.append(PREFIXES);
        sb.append(SELECT);
        sb.append(WHERE_START);
        sb.append(STATIC_BODY_GENES);
        sb.append(geneIdFilterString);
        sb.append(STATIC_BODY_LISTMEMBER);
        sb.append(expressionFilterString);
        sb.append(STAIC_BODY_ANNOTATIONS);
        sb.append(anatomyFilterString);

        sb.append(OPTIONAL_CONDITIONS);
        if (!conditionsFilterString.equals("")) {

            sb.append(conditionsFilterString);

        }
        if (!processFilterString.equals("")) {
            sb.append(processFilterString);
        }
        sb.append(WHERE_END);
        sb.append(LIMIT);

        this.sparqlQuery = sb.toString();

    }


    public String getSparqlQuery() {
        return sparqlQuery;
    }

    private String generateAnatomyFilterString(List<String> ids) {

        String annotations = "?annotation kupkb:bioMaterial ?analyteBioMaterialURI";
        String annoLabel = "?analyteBioMaterialURI rdfs:label ?analyteanatomy";

        if (ids == null) {
            return annotations +  SPACE + DOT + NEW_LINE + annoLabel + SPACE + DOT + NEW_LINE;
        }
        else if (ids.size() == 0) {
            return annotations +  SPACE + DOT + NEW_LINE + annoLabel + SPACE + DOT + NEW_LINE;
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(annotations);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);

            sb.append(OPEN_P);
            sb.append(NEW_LINE);
            for (int x = 0; x < ids.size(); x++) {

                sb.append(SPACE);
                sb.append(OPEN_P);
                sb.append("?analyteBioMaterialURI rdfs:subClassOf ");
                sb.append(LT);
                sb.append(ids.get(x));
                sb.append(GT);
                sb.append(CLOSE_P);

                if (x == ids.size() -1) {
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }
                else {
                    sb.append(SPACE);
                    sb.append("UNION");
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }


            }
            sb.append(CLOSE_P);
            sb.append(NEW_LINE);
            sb.append(annoLabel);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);
            return sb.toString();
        }

    }

    private String generateProcessFilterString(List<String> ids) {

        String uniprot = "?geneId <http://bio2rdf.org/ns/uniprot:xProtein> ?uniprot";
        String classifiedWith = "?uniprot core:classifiedWith ?goid";
        String goLabel = "?goid rdfs:label ?biological_process";

        if (ids == null) {
            return "";
        }
        else if (ids.size() == 0) {
            return "";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(uniprot);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);
            sb.append(classifiedWith);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);

            sb.append(OPEN_P);
            sb.append(NEW_LINE);
            for (int x = 0; x < ids.size(); x++) {

                sb.append(SPACE);
                sb.append(OPEN_P);
                sb.append("?goid rdfs:subClassOf ");
                sb.append(LT);
                sb.append(ids.get(x));
                sb.append(GT);
                sb.append(CLOSE_P);

                if (x == ids.size() -1) {
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }
                else {
                    sb.append(SPACE);
                    sb.append("UNION");
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }


            }
            sb.append(CLOSE_P);
            sb.append(NEW_LINE);
            sb.append(goLabel);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);
            return sb.toString();
        }

    }
    

    private String generateConditionFilterString(List<String> ids) {

        String annotations = "?annotation kupkb:hasDisease ?analyteDiseaseURI";
        String annoLabel = "?analyteDiseaseURI rdfs:label ?analytedisease";

        if (ids == null) {
            return "OPTIONAL {\n" + annotations + " . \n" + annoLabel + " . \n}";
        }
        else if (ids.size() == 0) {
            return "OPTIONAL {\n" + annotations + " . \n" + annoLabel + " . \n}";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(annotations);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);

            sb.append(OPEN_P);
            sb.append(NEW_LINE);
            for (int x = 0; x < ids.size(); x++) {

                sb.append(SPACE);
                sb.append(OPEN_P);
                sb.append("?analyteDiseaseURI rdfs:subClassOf ");
                sb.append(LT);
                sb.append(ids.get(x));
                sb.append(GT);
                sb.append(CLOSE_P);

                if (x == ids.size() -1) {
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }
                else {
                    sb.append(SPACE);
                    sb.append("UNION");
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }


            }
            sb.append(CLOSE_P);
            sb.append(NEW_LINE);
            sb.append(annoLabel);
            sb.append(SPACE);
            sb.append(DOT);
            sb.append(NEW_LINE);
            return sb.toString();
        }

    }


//    private String generateTaxonomyUnionString(List<String> ids) {
//
//        String taxons = "?geneId <http://bio2rdf.org/ns/bio2rdf:xTaxonomy>";
//        String taxonl = "?taxon rdfs:label ?species";
//        String taxonfull =taxons + SPACE + "?taxon" + SPACE + DOT + NEW_LINE + taxonl + SPACE + DOT + NEW_LINE;
//        if (ids == null) {
//            return taxonfull;
//        }
//        else if (ids.size() == 0) {
//            return taxonfull;
//        }
//        else {
//            StringBuilder sb = new StringBuilder();
//
//            sb.append(taxonfull);
//            sb.append(OPEN_P);
//            sb.append(NEW_LINE);
//            for (int x = 0; x < ids.size(); x++) {
//
//                sb.append(OPEN_P);
//                sb.append(taxons);
//                sb.append(SPACE);
//                sb.append(LT);
//                sb.append(ids.get(x));
//                sb.append(GT);
//                sb.append(SPACE);
//                sb.append(CLOSE_P);
//
//                if (x == ids.size() -1) {
//                    sb.append(SPACE);
//                    sb.append(NEW_LINE);
//                }
//                else {
//                    sb.append(SPACE);
//                    sb.append("UNION");
//                    sb.append(NEW_LINE);
//                }
//            }
//            sb.append(CLOSE_P);
//            sb.append(NEW_LINE);
//            return sb.toString();
//
//        }
//
//    }

    private String  generateExpressionUnionString(List<String> ids) {

        String listmember = "?listmember kupkb:hasExpression";
        String listlabel = "?expressionURI rdfs:label ?expressionLabel";

        String listmemberfull = listmember + SPACE + "?expressionURI" + SPACE + DOT + NEW_LINE + listlabel + SPACE + DOT + NEW_LINE;
        if (ids == null) {
            return listmemberfull;
        }
        else if (ids.size() == 0) {
            return listmemberfull;
        }
        else {
            StringBuilder sb = new StringBuilder();


            sb.append(listmemberfull);
            sb.append(OPEN_P);
            sb.append(NEW_LINE);
            for (int x = 0; x < ids.size(); x++) {

                sb.append(OPEN_P);
                sb.append(listmember);
                sb.append(SPACE);
                sb.append(LT);
                sb.append(ids.get(x));
                sb.append(GT);
                sb.append(SPACE);
                sb.append(CLOSE_P);

                if (x == ids.size() -1) {
                    sb.append(SPACE);
                    sb.append(NEW_LINE);
                }
                else {
                    sb.append(SPACE);
                    sb.append("UNION");
                    sb.append(NEW_LINE);
                }
            }
            sb.append(CLOSE_P);
            sb.append(NEW_LINE);
            return sb.toString();

        }

    }

    private String generateFilterString(List<String> ids, String variableName) {

        if (ids == null) return "";
        if (ids.size() == 0) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(FILTER);
        sb.append(OPEN_B);
        sb.append(SPACE);
        for (int x = 0; x < ids.size(); x++) {

            sb.append(variableName);
            sb.append(SPACE);
            sb.append(EQUALS);
            sb.append(SPACE);
            sb.append(LT);
            sb.append(ids.get(x));
            sb.append(GT);
            sb.append(SPACE);

            if (x == ids.size() -1) {
                sb.append(SPACE);
                sb.append(CLOSE_B);
            }
            else {
                sb.append(BAR);
                sb.append(BAR);
                sb.append(SPACE);
            }
        }
        sb.append(DOT);
        sb.append(NEW_LINE);
        return sb.toString();
    }

    public static void main(String[] args) {

        ArrayList<String> genelist = new ArrayList<String>();
        genelist.add("http://www.kupkb.org/data/kupo/KUPO_0200108");
        genelist.add("http://www.kupkb.org/data/kupo/KUPO_0100018");
        genelist.add("http://www.kupkb.org/data/kupo/KUPO_0100005");

        SPARQLQueryBuilder sb = new SPARQLQueryBuilder(null, null, null, genelist, null);

        System.out.println(sb.getSparqlQuery());

    }


}
