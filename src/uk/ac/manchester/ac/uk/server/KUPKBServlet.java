package uk.ac.manchester.ac.uk.server;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import uk.ac.manchester.ac.uk.server.queries.QueryManager;
import uk.ac.manchester.ac.uk.server.repository.KUPKBManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;/*
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
 * Date: Sep 21, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class KUPKBServlet extends HttpServlet {

    private String sparql = "";
    private String format = "xml";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        if (req.getParameter("format") !=null) {
            format = req.getParameter("format");
        }


        if (req.getParameter("sparql") !=null) {
            sparql = URLDecoder.decode(req.getParameter("sparql"), "UTF-8");
            query(req, resp);
        }
        else if (req.getParameter("template") !=null) {
            sparql = QueryManager.getQuery(req.getParameter("template"));
//            System.err.println("got sparql: " + sparql);
            query(req, resp);
        }
        else {
            errorNoSparql(req, resp, "Please provide a SPARQL query");
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getParameter("format") !=null) {
            format = req.getParameter("format");
        }

        if (req.getParameter("sparql") !=null) {
            sparql = URLDecoder.decode(req.getParameter("sparql"), "UTF-8");
            query(req, resp);
        }
        else if (req.getParameter("template") !=null) {
            sparql = QueryManager.getQuery(req.getParameter("template"));
            query(req, resp);
        }
        else {
            errorNoSparql(req, resp, "Please provide a SPARQL query");
        }
    }


    private void query(HttpServletRequest req, HttpServletResponse resp) {

        try {
            KUPKBManager man = new KUPKBManager();

            PrintWriter pw  = resp.getWriter();
            man.executeSingleQuery(sparql, format, pw);


        } catch (RepositoryConfigException e) {
            errorNoSparql(req, resp, "Can't connect to respository, repository config exception");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            errorNoSparql(req, resp, "Can't connect to respository, repository exception");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            errorNoSparql(req, resp, "IO exception getting print writer");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void errorNoSparql(HttpServletRequest req, HttpServletResponse resp, String message) {

        PrintWriter out = null;
        try {
            out = resp.getWriter();
            out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
            out.println("<error>");
            out.println(message);
            out.println("</error>");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}
