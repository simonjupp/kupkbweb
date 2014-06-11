package uk.ac.manchester.ac.uk.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Namespace;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import uk.ac.manchester.ac.uk.client.KUPKBExpData;
import uk.ac.manchester.ac.uk.client.KUPKBOWLClass;
import uk.ac.manchester.ac.uk.client.KUPKBQueryObject;
import uk.ac.manchester.ac.uk.client.KUPKBQueryService;
import uk.ac.manchester.ac.uk.server.queries.QueryManager;
import uk.ac.manchester.ac.uk.client.pages.SPARQLQueryBuilder;
import uk.ac.manchester.ac.uk.server.repository.DevKUPKBConfig;
import uk.ac.manchester.ac.uk.server.repository.KUPKBManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
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

/**
 * Author: Simon Jupp<br>
 * Date: May 2, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class KUPKBQueryServiceImpl extends RemoteServiceServlet implements KUPKBQueryService {

    KUPKBManager manager;
    QueryManager queryManager = new QueryManager();
    static String sparqlquery = QueryManager.getQuery("generate_results_table");

    static String bio2RdfBaseUri = "http://bio2rdf.org/geneid:";

    static String miRnaBaseUri = "http://www.mirbase.org/cgi-bin/mirna_entry.pl?acc=";

    static String hmdbBaseUri = "http://bio2rdf.org/hmdb:";


    // constructor, sets up a connection to a default local repository

    public KUPKBQueryServiceImpl() {
        try {
            manager = new KUPKBManager();
        } catch (RepositoryConfigException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    // methods

    public KUPKBExpData[] query (String[] query) {

        List<KUPKBExpData> result = new ArrayList<KUPKBExpData>();
        for (String q : query) {
            result.addAll(Arrays.asList(query(q)));
        }
        return result.toArray(new KUPKBExpData[result.size()]);
    }

    private String advancedQuerySparql (String[] geneIds, String[] expressionIds, String[] locationIds, String[] conditionIds, String[] processIds) {
        SPARQLQueryBuilder sb = new SPARQLQueryBuilder( Arrays.asList(geneIds), Arrays.asList(expressionIds),
                Arrays.asList(locationIds), Arrays.asList(conditionIds), Arrays.asList(processIds));
        return sb.getSparqlQuery();

    }

    @Override
    public KUPKBExpData[] advancedQuery(String[] geneIds, String[] expressionIds, String[] locationIds, String[] conditionIds, String[] processIds, boolean intersecting) {

        String sparql = advancedQuerySparql(geneIds, expressionIds, locationIds, conditionIds, processIds);

        List<KUPKBExpData> resultsList = new ArrayList<KUPKBExpData>();
        try {
            TupleQuery tq = manager.prepareTupleQuery(sparql);
            TupleQueryResult queryResult = tq.evaluate();
            ValueFactory factory = manager.getValueFactory();

            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();

                Value geneid = bindingSet.getValue("geneId");
//                System.out.println("geneid: " + geneid.stringValue());
                Value genesymbol = bindingSet.getValue("geneSymbol");
//                System.out.println("genesymbol: " + genesymbol.stringValue());
                Value expName = bindingSet.getValue("experimentId");
//                System.out.println("expName: " + expName.stringValue());
                Value species = bindingSet.getValue("species");
//                System.out.println("species: " + species.stringValue());
                Value maturity = bindingSet.getValue("maturity");
                Value anatomy = bindingSet.getValue("analyteanatomy");
                Value anatomyURI = bindingSet.getValue("analyteBioMaterialURI");

//                System.out.println("anatomy: " + anatomy.stringValue());
                Value disease = bindingSet.getValue("analytedisease");


//                System.out.println("disease: " + disease.stringValue());
                Value severity = bindingSet.getValue("severity");
//                System.out.println("severity: " + severity.stringValue());
                Value deviationFromNormal = bindingSet.getValue("deviationFromNormal");
//                System.out.println("deviationFromNormal: " + deviationFromNormal.stringValue());
                Value expressionURI = bindingSet.getValue("expressionURI");
                Value expressionLabel = bindingSet.getValue("expressionLabel");
//                System.out.println("expressionLabel: " + expressionLabel.stringValue());
//                Value pvalue = bindingSet.getValue("pvalue");
//                System.out.println("pvalue: " + pvalue.stringValue());
//                Value foldChange = bindingSet.getValue("foldChange");
//                System.out.println("foldChange: " + foldChange.stringValue());

                Value expType = bindingSet.getValue("experimentTypeLabel");
                Value expLabel = bindingSet.getValue("experimentDisplayName");
                Value expDesc = bindingSet.getValue("expDesc");


                KUPKBExpData exp = new KUPKBExpData();

                exp.setGeneid(pretty(geneid));
                exp.setGeneSymbol(pretty(genesymbol));
                exp.setExperimentID(pretty(expName));
                exp.setSpecies(pretty(species));
                exp.setMaturity(pretty(maturity));
                exp.setAnalyteAnatomy(pretty(anatomy));
                exp.setAnalyteAnatomyURI(anatomyURI.stringValue());
                exp.setAnalyteDisease(pretty(disease));
                exp.setSeverity(pretty(severity));
                exp.setDeviationFromNormal(pretty(deviationFromNormal));
                exp.setExpressionURI(expressionURI.stringValue());
                exp.setExpression(pretty(expressionLabel));
//                exp.setPValue(pretty(pvalue));
//                exp.setFoldChange(pretty(foldChange));
                exp.setExperimentType(pretty(expType));
                exp.setExperimentDisplayLabel(pretty(expLabel));
                exp.setExperimentDesc(pretty(expDesc));

                if (bindingSet.getValue("pmid") != null) {
                    Value pmid = bindingSet.getValue("pmid");
                    exp.setPmid(BigDecimal.valueOf(new Double(pmid.stringValue())).toPlainString());
                }

                if (bindingSet.getValue("analyteDiseaseURI") != null) {
                    Value diseaseURI = bindingSet.getValue("analyteDiseaseURI");
                    exp.setAnalyteDiseaseURI(diseaseURI.stringValue());
                    exp.setAnalyteDisease(pretty(disease));
                }
                else {
                    exp.setAnalyteDiseaseURI("http://www.kupkb.org/data/kupo/KUPO_0300007");
                    exp.setAnalyteDisease("Healthy");
                }

                if (bindingSet.getValue("seeAlsoLink") != null) {
                    Value seeAlso = bindingSet.getValue("seeAlsoLink");
                    exp.setSeeAlso(seeAlso.stringValue());
                }

                // get the GO annotations
                TupleQuery tq2 = manager.prepareTupleQuery(QueryManager.getQuery("get_go_annotations"));
                HashSet<String> goAnnotations = new HashSet<String>();
                tq2.setBinding("geneid", factory.createURI(bio2RdfBaseUri + exp.getGeneid()));

                TupleQueryResult goQueryResult = tq2.evaluate();
                while (goQueryResult.hasNext()) {
                    BindingSet bindingSet2 = goQueryResult.next();
                    Value goid = bindingSet2.getValue("goid");
                    if (goid != null) {
                        goAnnotations.add(goid.stringValue());
                    }
                }
                exp.setGoAnnotations(goAnnotations.toArray(new String[goAnnotations.size()]));

                String [] ologs = getOrthologs(geneid);
                if (ologs.length > 0 ) {
                    exp.setOrthologs(ologs);
                }

                resultsList.add(exp);
            }



        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return resultsList.toArray(new KUPKBExpData[resultsList.size()]);
    }

    private String[] getOrthologs(Value geneid) {


        Set<String> ologs = new HashSet<String>();

        TupleQuery tq2 = null;
        try {
            tq2 = manager.prepareTupleQuery(QueryManager.getQuery("get_orthologs"));
            tq2.setBinding("geneid", geneid);

            TupleQueryResult goQueryResult = tq2.evaluate();
            while (goQueryResult.hasNext()) {
                BindingSet bindingSet2 = goQueryResult.next();
                Value homolog = bindingSet2.getValue("homolog");
                if (homolog != null) {
                    ologs.add(homolog.stringValue());
                }
            }

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return ologs.toArray(new String[ologs.size()]);

    }

    public String getNameSpaces() {

        StringBuffer prefix = new StringBuffer();
        try {
            RepositoryResult<Namespace> namepaces = manager.getNameSpaces();
            while (namepaces.hasNext()) {
                Namespace ns = namepaces.next();
                if (!ns.getName().equals("")) {
                    prefix.append("PREFIX " + ns.getPrefix() + ":<" + ns.getName() + ">\n");
                }
            }
        } catch (RepositoryException e) {
            prefix.append("");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return prefix.toString();
    }


    public String executeTemplateQuery(String templateId, String format) {
        return evaluateSparqlQuery(QueryManager.getQuery(templateId), format);
    }

    @Override
    public String evaluateSparqlQuery(String query, String format) {

        StringWriter stringWriter = new StringWriter();
        try {
            manager.executeSingleQuery(query, format, new PrintWriter(stringWriter));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return stringWriter.toString();
    }

    public Set<Value> getObjectsByProperty (String subjectUri, String predicateUri) {


        ValueFactory factory = manager.getValueFactory();

        Set<Value> values = new HashSet<Value>();

        try {
            TupleQuery tq = manager.prepareTupleQuery("SELECT ?o WHERE {?s ?p ?o}");
            tq.setBinding("s", factory.createURI(subjectUri));
            tq.setBinding("p", factory.createURI(predicateUri));

            TupleQueryResult queryResult = tq.evaluate();
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();
                Value object = bindingSet.getValue("o");
                values.add(object);
            }


        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return values;

    }


    public KUPKBExpData[] query(String queryString) {

        ValueFactory factory = manager.getValueFactory();

        List<KUPKBExpData> phosphoproteins = new ArrayList<KUPKBExpData>();

        List<KUPKBExpData> resultsList = new ArrayList<KUPKBExpData>();
        System.out.println("query string: " + queryString);
        System.out.println(sparqlquery);
        try {

            TupleQuery tq = manager.prepareTupleQuery(sparqlquery);
            if (queryString.startsWith("MIMAT")) {
                tq.setBinding("geneid", factory.createURI(miRnaBaseUri + queryString));
            }
            else if (queryString.startsWith("HMDB")) {
                tq.setBinding("geneid", factory.createURI(hmdbBaseUri + queryString));
            }
            else {
                tq.setBinding("geneid", factory.createURI(bio2RdfBaseUri + queryString));
            }

            // We then evaluate the prepared query and can process the result:

            TupleQueryResult queryResult = tq.evaluate();


            while (queryResult.hasNext()) {

                BindingSet bindingSet = queryResult.next();

                Value geneid = bindingSet.getValue("geneid");

                Value genesymbol = bindingSet.getValue("genesymbol");
                Value expName = bindingSet.getValue("experiment");
                Value expType = bindingSet.getValue("experimentTypeLabel");
                Value species = bindingSet.getValue("species");
                Value anatomy = bindingSet.getValue("analyteanatomy");
                Value anatomyURI = bindingSet.getValue("analyteBioMaterial");
                Value disease = bindingSet.getValue("analytedisease");
                Value expLabel = bindingSet.getValue("experimentDisplayName");
                Value expresssion = bindingSet.getValue("expLabel");
                Value expDesc = bindingSet.getValue("expDesc");
                Value annotationID = bindingSet.getValue("annotation");

                Set<Value> qualities = getObjectsByProperty(annotationID.stringValue(), "http://www.kupkb.org/data/kupkb/hasQuality");



                KUPKBExpData texp = new KUPKBExpData();
                texp.setGeneid(pretty(geneid));
                texp.setGeneSymbol(pretty(genesymbol));
                String prettyExpname = pretty(expName);
                // now make it prettier, this is temporary until we get proper labels in the KUPKB
                prettyExpname = prettyExpname.replaceAll("_", " ");

                texp.setExperimentID(StringUtils.capitalize(prettyExpname));
                texp.setExperimentType(pretty(expType));
                texp.setSpecies(pretty(species));
                texp.setAnalyteAnatomy(StringUtils.capitalize(pretty(anatomy)));
                texp.setAnalyteAnatomyURI(anatomyURI.stringValue());

                if (expDesc != null) {
                    texp.setExperimentDesc(expDesc.stringValue());
                }

                if (!expLabel.stringValue().equals("")) {
                    texp.setExperimentDisplayLabel(expLabel.stringValue());
                }
                else {
                    texp.setExperimentDisplayLabel(prettyExpname);
                }


                /* need to do a bit of tweaking to the results because of some sparql limitations */
                if (bindingSet.getValue("analyteDiseaseURI") != null) {
                    Value diseaseURI = bindingSet.getValue("analyteDiseaseURI");
                    texp.setAnalyteDiseaseURI(diseaseURI.stringValue());
                    texp.setAnalyteDisease(pretty(disease));
                }
                else {
                    texp.setAnalyteDiseaseURI("http://www.kupkb.org/data/kupo/KUPO_0300007");
                    texp.setAnalyteDisease("Healthy");
                }

                if (bindingSet.getValue("pmid") != null) {
                    Value pmid = bindingSet.getValue("pmid");
                    texp.setPmid(BigDecimal.valueOf(new Double(pmid.stringValue())).toPlainString());
                }

                if (bindingSet.getValue("seeAlsoLink") != null) {
                    Value seeAlso = bindingSet.getValue("seeAlsoLink");
                    texp.setSeeAlso(seeAlso.stringValue());
                }

                // get the GO annotations
                TupleQuery tq2 = manager.prepareTupleQuery(QueryManager.getQuery("get_go_annotations"));
                HashSet<String> goAnnotations = new HashSet<String>();
//                System.out.println("setting gene id for GO: " + texp.getGeneid());
                tq2.setBinding("geneid", factory.createURI(bio2RdfBaseUri + texp.getGeneid()));

                TupleQueryResult goQueryResult = tq2.evaluate();
                while (goQueryResult.hasNext()) {
                    BindingSet bindingSet2 = goQueryResult.next();
//                    Value protein = bindingSet2.getValue("protein");
                    Value goid = bindingSet2.getValue("goid");
                    if (goid != null) {
//                        System.out.println(protein.stringValue() + " classifiedWith " + goid.stringValue());
                        goAnnotations.add(goid.stringValue());
                    }
                }
                texp.setGoAnnotations(goAnnotations.toArray(new String[goAnnotations.size()]));
                // check the qualities

                for (Value v : qualities) {

                    if (manager.getRepositoryConnection().hasStatement(factory.createURI(v.stringValue()), factory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"), factory.createURI("http://purl.org/obo/owl/PATO#PATO_0000261"), false)) {
                        texp.setMaturity(getLabel(v.stringValue()));

                    }
                    else if (manager.getRepositoryConnection().hasStatement(factory.createURI(v.stringValue()), factory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"), factory.createURI("http://purl.org/obo/owl/PATO#PATO_0000069"), false)) {
                        texp.setSeverity(getLabel(v.stringValue()));
                    }
                    else if (manager.getRepositoryConnection().hasStatement(factory.createURI(v.stringValue()), factory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"), factory.createURI("http://purl.org/obo/owl/PATO#PATO_0000460"), false)) {
                        texp.setSeverity(getLabel(v.stringValue()));
                    }
                    else if (manager.getRepositoryConnection().hasStatement(factory.createURI(v.stringValue()), factory.createURI("http://www.w3.org/2000/01/rdf-schema#subClassOf"), factory.createURI("http://www.kupkb.org/data/kupo/KUPO_0300001"), false)) {
                        texp.setSeverity(getLabel(v.stringValue()));
                    }

                }

                texp.setExpression(pretty(expresssion));

                if (expType.stringValue().toLowerCase().contains("phosphoprotein")) {
                    phosphoproteins.add(texp);
                }

                boolean add = true;

                for (KUPKBExpData data : phosphoproteins) {

                    if (texp.getExperimentID().equals(data.getExperimentID())
                            && texp.getGeneid().equals(data.getGeneid())
                            && texp.getExperimentType().startsWith("Protein")) {

                        add = false;

                    }

                }
                if (add) {
                    resultsList.add(texp);
                }

            }


            queryResult.close();

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("number of hits: " + resultsList.size());
        return resultsList.toArray(new KUPKBExpData[resultsList.size()]);

    }

    public String getLabel (String uri) {


        for (Value v : getObjectsByProperty(uri, "http://www.w3.org/2000/01/rdf-schema#label")) {
            return pretty(v);
        }

        return null;
    }

    /* There is some bug using sesame and owlims lucene queries. For some reason you can't
       prepare a query and set a binding value for the query. Or at least I can't work out how to do it.
       Instead I have to do this horrible concat of two queries for now...
       I noticed someone else had the same problem http://sourceforge.net/mailarchive/message.php?msg_id=28017233
       so I'm waiting to see if he gets and answer.
     */

    /*
    static String lucenequery3 = "PREFIX luc: <http://www.ontotext.com/owlim/lucene#>\n" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>\n" +
            "SELECT DISTINCT ?s " +
            "WHERE { ?s luc:myIndex ";

    static String lucenequery4 = " .\n" +
            /*"{\n" +
            " {?subject rdfs:comment ?s} UNION {?subject <http://www.w3.org/2004/02/skos/core#notation> ?s} UNION " +
            "{?subject rdfs:label ?s} UNION {?subject <http://bio2rdf.org/ns/bio2rdf:symbol> ?s}\n" +
            "} .\n" +
            "?subject ?p ?s ." +
            "\n" +
            "?subject rdf:type ?type .\n" +
            "filter (?type = <http://bio2rdf.org/ns/geneid:Gene> || ?type = <http://bio2rdf.org/core:Protein> || ?type = kupkb:KUPKB_1000056) .\n" +
            "}" +
            "ORDER BY ?s LIMIT 20";

 * this was the workaround query for the OWLIM 4.X bug...
    static String lucenequery1 = "PREFIX luc: <http://www.ontotext.com/owlim/lucene#>\n" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>\n" +
            "SELECT DISTINCT ?subject ?type ?s ?symbol ?species ?geneid ?comment\n" +
            "WHERE { ?s luc:myIndex ";
    static String lucenequery2 = " . \n" +
            "?subject ?p ?s .\n" +
            "?subject rdf:type ?type .\n" +
            "OPTIONAL {\n" +
            "?subject rdfs:comment ?comment .\n" +
            "}\n" +
            "{\n" +
            "{?subject <http://bio2rdf.org/ns/bio2rdf:symbol> ?symbol} UNION {?subject rdfs:label ?symbol}\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "{\n" +
            "\t{?geneid <http://bio2rdf.org/ns/uniprot:xProtein> ?subject}  UNION {?subject <http://www.w3.org/2004/02/skos/core#notation> ?geneid }\n" +
            "}\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?subject <http://www.w3.org/2004/02/skos/core#notation> ?geneid .\n" +
            "}\n" +
            "?subject <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
            "?taxon rdfs:label ?species\n" +
            "filter (?type = <http://bio2rdf.org/ns/geneid:Gene> || ?type = <http://bio2rdf.org/core:Protein> || ?type = kupkb:KUPKB_1000056) .\n" +
            "}\n" +
            "ORDER BY ?s LIMIT 20";
  */
    static String lucenequery3 = "PREFIX luc: <http://www.ontotext.com/owlim/lucene#>\n" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>\n" +
            "SELECT DISTINCT ?s " +
            "WHERE { ?s luc:myIndex ";

    static String lucenequery4 = " .\n" +
            "{\n" +
            " {?subject rdfs:comment ?s} UNION {?subject <http://www.w3.org/2004/02/skos/core#notation> ?s} UNION " +
            "{?subject rdfs:label ?s} UNION {?subject <http://bio2rdf.org/ns/bio2rdf:symbol> ?s}\n" +
            "} .\n" +
            "\n" +
            "?subject rdf:type ?type .\n" +
            "filter (?type = <http://bio2rdf.org/ns/geneid:Gene> || ?type = <http://bio2rdf.org/ns/hmdb:Compound>  || ?type = <http://bio2rdf.org/core:Protein> || ?type = kupkb:KUPKB_1000056) .\n" +
            "}" +
            "ORDER BY ?s LIMIT 20";

    static String lucenequery1 = "PREFIX luc: <http://www.ontotext.com/owlim/lucene#>\n" +
            "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>\n" +
            "SELECT DISTINCT ?subject ?type ?s ?symbol ?species ?geneid ?comment\n" +
            "WHERE { ?s luc:myIndex ";
    static String lucenequery2 = " . \n" +
            "{\n" +
            "{?subject rdfs:comment ?s} UNION {?subject <http://www.w3.org/2004/02/skos/core#notation> ?s} UNION \n" +
            "{?subject rdfs:label ?s} UNION {?subject <http://bio2rdf.org/ns/bio2rdf:symbol> ?s}\n" +
            "}\n" +
            "?subject rdf:type ?type .\n" +
            "OPTIONAL {\n" +
            "?subject rdfs:comment ?comment .\n" +
            "?subject <http://bio2rdf.org/ns/bio2rdf:symbol> ?symbol .\n" +
            "?subject <http://www.w3.org/2004/02/skos/core#notation> ?geneid .\n" +
            "?subject <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
            "?taxon rdfs:label ?species\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?subject rdfs:label ?symbol .\n" +
            "?subject <http://www.w3.org/2004/02/skos/core#notation> ?geneid .\n" +
            "?subject <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
            "?taxon rdfs:label ?species\n" +
            "}\n" +
            "OPTIONAL {\n" +
            "?geneid <http://bio2rdf.org/ns/uniprot:xProtein> ?subject . \n" +
            "?subject rdfs:comment ?comment .\n" +
            "?subject rdfs:label ?symbol .\n" +
            "?subject <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
            "?taxon rdfs:label ?species\n" +
            "}\n" +
            "filter (?type = <http://bio2rdf.org/ns/geneid:Gene> || ?type = <http://bio2rdf.org/ns/hmdb:Compound> || ?type = <http://bio2rdf.org/core:Protein> || ?type = kupkb:KUPKB_1000056) .\n" +
            "\n" +
            "}\n" +
            "ORDER BY ?s LIMIT 20";



    public KUPKBQueryObject[] suggest(String input) {

        List<KUPKBQueryObject> suggestion = new ArrayList<KUPKBQueryObject>();

        try {
            String fullQuery = lucenequery3 + "\"" + input.replace(" ", "+") + "\"" + lucenequery4;

            //System.out.println("querying: " + fullQuery);
            TupleQuery tq = manager.prepareTupleQuery(fullQuery);

            TupleQueryResult queryResult = tq.evaluate();
            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();

                Value symbol = bindingSet.getValue("s");

                KUPKBQueryObject texp = new KUPKBQueryObject();
                texp.setSymbol(pretty(symbol));
                suggestion.add(texp);


            }


            queryResult.close();

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return suggestion.toArray(new KUPKBQueryObject[suggestion.size()]);
    }

    public KUPKBQueryObject[] suggest(String[] input) {

        List<KUPKBQueryObject> suggestion = new ArrayList<KUPKBQueryObject>();

        Set<String> geneids = new HashSet<String>();

        for (String s : input) {
            try {
                String fullQuery = lucenequery1 + "\"\\\"" + s + "\\\"\"" + lucenequery2;
                //System.out.println("querying: " + fullQuery);

                TupleQuery tq = manager.prepareTupleQuery(fullQuery);

                // We then evaluate the prepared query and can process the result:
                TupleQueryResult queryResult = tq.evaluate();
                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();

                    Value subject = bindingSet.getValue("geneid");
                    if (subject == null) {
                        continue;
                    }
                    else if (geneids.contains(subject.stringValue())) {
                        continue;
                    }
                    geneids.add(subject.stringValue());
                    Value type = bindingSet.getValue("type");
                    Value symbol = bindingSet.getValue("symbol");
                    Value species = bindingSet.getValue("species");
                    Value comment = bindingSet.getValue("comment");

                    KUPKBQueryObject texp = new KUPKBQueryObject();
                    texp.setGeneid(pretty(subject));
                    texp.setType(pretty(type));
                    texp.setSymbol(pretty(symbol));
                    texp.setSpecies(pretty(species));
                    texp.setComment(pretty(comment));

                    suggestion.add(texp);

                }


                queryResult.close();

            } catch (RepositoryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (MalformedQueryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueryEvaluationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        return suggestion.toArray(new KUPKBQueryObject[suggestion.size()]);
    }


    @Override
    public KUPKBOWLClass[] suggestClass(String input) {

        List<KUPKBOWLClass> suggestion = new ArrayList<KUPKBOWLClass>();

        String query1 = QueryManager.getQuery("get_location_leucene_p1");
        String query2 = QueryManager.getQuery("get_location_leucene_p2");

        String fullQuery = query1 + "*" + input + "*" + query2;

        try {

            TupleQuery tq = manager.prepareTupleQuery(fullQuery);

//            System.out.println("input: "+ input + ", query:" + tq.toString());

            TupleQueryResult queryResult = tq.evaluate();

            while (queryResult.hasNext()) {
                BindingSet bindingSet = queryResult.next();

                Value uri = bindingSet.getValue("subject");
                Value label = bindingSet.getValue("s");


                KUPKBOWLClass cls = new KUPKBOWLClass();
                cls.setIRI(uri.stringValue());
                cls.setLabel(label.stringValue());
                if (cls.getDisplayString().contains(input)) {
                    suggestion.add(cls);
                }
            }

            queryResult.close();

        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return suggestion.toArray(new KUPKBOWLClass[suggestion.size()]);
    }

    @Override
    public KUPKBOWLClass[] suggestClass(String[] input) {

        List<KUPKBOWLClass> suggestion = new ArrayList<KUPKBOWLClass>();

        ValueFactory factory = manager.getValueFactory();

        String query1 = QueryManager.getQuery("get_term_and_parts_by_label");

        for (String s : input) {


            try {

                TupleQuery tq = manager.prepareTupleQuery(query1);

                Literal v1 = factory.createLiteral(s);
                Literal v2 = factory.createLiteral(s, "en");
//                System.out.println("input: "+ s + ", query:" + tq.toString());

                tq.setBinding("value", v1);
                tq.setBinding("value_lang", v2);
                TupleQueryResult queryResult = tq.evaluate();

                while (queryResult.hasNext()) {
                    BindingSet bindingSet = queryResult.next();

                    Value uri = bindingSet.getValue("children");
                    Value label = bindingSet.getValue("label");

                    KUPKBOWLClass cls = new KUPKBOWLClass();
                    cls.setIRI(uri.stringValue());
                    cls.setLabel(label.stringValue());
                    suggestion.add(cls);
                }

                queryResult.close();

            } catch (RepositoryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (MalformedQueryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (QueryEvaluationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }

        return suggestion.toArray(new KUPKBOWLClass[suggestion.size()]);
    }


    private String pretty (Value v) {

        if (v == null) {
            return "";
        }
        String s = v.stringValue();
        if (s.startsWith("http")) {
            s = s.replaceAll(">", "");
            s = s.substring(s.lastIndexOf("/") + 1);
            if (s.contains(":")) {
                s = s.substring(s.lastIndexOf(":") + 1);
            }

        }
        else {
            s = s.replaceAll("@en", "");

            s = s.replaceAll("\"", "");
        }

        // special case for miRNA
        if (s.equals("KUPKB_1000056")) {
            s = "miRNA";
        }
        return s;

    }

    public static void main(String[] args) {
        KUPKBQueryServiceImpl i = new KUPKBQueryServiceImpl();
        for (KUPKBExpData kd : i.query("23564"))  {

            System.out.print(kd.getExperimentID());
            System.out.print("\t");
            System.out.print(kd.getSpecies());
            System.out.print("\t");
            System.out.print(kd.getAnalyteAnatomy());
            System.out.print("\t");
            System.out.print(kd.getAnalyteDisease());
            System.out.print("\t");
            System.out.print(kd.getExpression());
            System.out.println();
        }

        for (KUPKBQueryObject q : i.suggest("transforming")) {

            System.out.println(q.getSymbol());

        }

        System.out.println(i.evaluateSparqlQuery("select * where {?s ?p ?o} limit 10", "html"));


    }


}
