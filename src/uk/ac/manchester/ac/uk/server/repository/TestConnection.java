package uk.ac.manchester.ac.uk.server.repository;

import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;/*

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;/*
 * Copyright (C) 2007, University of Manchester
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
import uk.ac.manchester.ac.uk.server.io.CSVQueryResultHandler;
import uk.ac.manchester.ac.uk.server.queries.QueryManager;

import java.util.List;

/**
 * Author: Simon Jupp<br>
 * Date: Apr 14, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class TestConnection {

    public static void main(String[] args) {

        KUPKBManager manager = null;
        try {
            manager = new KUPKBManager();


            String sparqlquery = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                    "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
                    "PREFIX kupkb:<http://www.kupkb.org/data/kupkb/>\n" +
                    "PREFIX kupo:<http://www.kupkb.org/data/kupo/>\n" +
                    "\n" +
                    "SELECT DISTINCT ?geneid ?genesymbol ?experiment ?experimentDisplayName ?experimentTypeLabel  ?annotation ?species ?analyteBioMaterial ?analyteanatomy ?analyteDiseaseURI ?analytedisease ?expLabel ?expDesc ?pmid ?seeAlsoLink\n" +
                    "WHERE {\n" +
                    "\n" +
                    "{\n" +
                    "  {?geneid rdf:type <http://bio2rdf.org/ns/geneid:Gene>}\n" +
                    "  UNION\n" +
                    "  {?geneid rdf:type kupkb:KUPKB_1000056}\n" +
                    "} .\n" +
                    "{\n" +
                    "  {?geneid <http://bio2rdf.org/ns/bio2rdf:symbol> ?genesymbol}\n" +
                    "  UNION\n" +
                    "  {?geneid rdfs:label ?genesymbol}\n" +
                    "}\n" +
                    "\n" +
                    "?geneid <http://bio2rdf.org/ns/bio2rdf:xTaxonomy> ?taxon .\n" +
                    "?taxon rdfs:label ?species .\n" +
                    "\n" +
                    "{\n" +
                    "  {?listmember kupkb:hasDatabaseRef ?geneid}\n" +
                    "  UNION\n" +
                    "  { ?geneid <http://bio2rdf.org/ns/uniprot:xProtein> ?uniprot .\n" +
                    "    ?listmember kupkb:hasDatabaseRef ?uniprot\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "?listmember kupkb:hasExpression ?expression .\n" +
                    "?expression rdfs:label ?expLabel .\n" +
                    "?compoundList kupkb:hasMember ?listmember .\n" +
                    "?compoundList rdf:type ?experimentType .\n" +
                    "?experimentType rdfs:label ?experimentTypeLabel .\n" +
                    "filter (?experimentType = kupkb:KUPKB_1000029 || ?experimentType = kupkb:KUPKB_1000027 ||\n" +
                    "        ?experimentType = kupkb:KUPKB_1000073 || ?experimentType = kupkb:KUPKB_1000030 || ?experimentType = kupkb:KUPKB_1000028 || ?experimentType = kupkb:KUPKB_1000077) .\n" +
                    "?analysis kupkb:produces ?compoundList .\n" +
                    "?analysis kupkb:annotatedWith ?annotation .\n" +
                    "?analysis kupkb:analysisOf  ?experiment  .\n" +
                    "?experiment rdfs:comment  ?expDesc  .\n" +
                    "?experiment rdfs:label  ?experimentDisplayName  .\n" +
                    "\n" +
                    "OPTIONAL { ?experiment <http://www.kupkb.org/data/kupkb/experiment/pmid> ?pmid . }\n" +
                    "OPTIONAL { ?experiment rdfs:seeAlso ?seeAlsoLink . }\n" +
                    "\n" +
                    "?annotation kupkb:hasAnnotationRole kupo:KUPO_0300008 .\n" +
                    "?annotation kupkb:bioMaterial ?analyteBioMaterial .\n" +
                    "?analyteBioMaterial rdfs:label ?analyteanatomy .\n" +
                    "\n" +
                    "OPTIONAL { ?annotation kupkb:hasDisease ?analyteDiseaseURI .\n" +
                    "           ?analyteDiseaseURI rdfs:label ?analytedisease }\n" +
                    "\n" +
                    "}";

            ValueFactory factory = manager.getValueFactory();
            String queryString = "http://bio2rdf.org/geneid:100041161";


            System.out.println("query string: " + queryString);
            System.out.println(sparqlquery);
            try {

                TupleQuery tq = manager.prepareTupleQuery(sparqlquery);
                tq.setBinding("geneid", factory.createURI(queryString));
                TupleQueryResult queryResult = tq.evaluate();

                System.out.println("done!: ");


            } catch (QueryEvaluationException e) {

                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (MalformedQueryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            manager.shutdown();



        } catch (RepositoryConfigException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
