package uk.ac.manchester.ac.uk.client.pages;

import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.*;

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
public class HomeWidget extends Composite {


    public HomeWidget () {

        VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.add(
                new HTML("<p class=\"ikupHomeText\">The KUPKB is a collection of omics datasets that have been extracted from scientific publications " +
                        " and other related renal databases. The iKUP browser provides a single point of entry for you to query and browse these datasets.</p>"));

//        mainPanel.add(
//                new HTML("<p style=\"width: 50%; margin-left:10px; padding-left:5px; font-weight:bold; font-size:1.1em; background-color:#FFFBD0; border-style:solid; border-width:1px; border-color:#878787; border-radius:5px; -webkit-border-radius:5px; -moz-border-radius: 5px;\">The <span style=\"color:#E21A30\">KUP</span><span style=\"color:#000000\">KB </span><span style=\"color:#E21A30;\">Net</span><span style=\"color:#000000\">work </span><span style=\"color:#E21A30\">Vis</span><span style=\"color:#000000\">ualizer</span> beta version is now online! You can try it <a href=\"http://www.kupkb.org/vis/index.php\" target=\"_blank\">here!</a></p>"));

//        mainPanel.setStyleName("mainPanel");

        HorizontalPanel searchPanel = new HorizontalPanel();
        final MoleculeSearchWidget moleculeSearchWidget = new MoleculeSearchWidget();

        TabPanel searchTabPanel = new TabPanel();
        searchTabPanel.removeStyleName("gwt-TabPanelBottom");

        searchTabPanel.add(moleculeSearchWidget, "Molecule Search");
        searchTabPanel.add(new SPARQLWidget(), "Advanced Search");
        Frame vizFrame = new Frame("http://www.kupkb.org/vis/index.php?remove_template=1");

        vizFrame.setSize("1700","1150");
        VerticalPanel vizPanel = new VerticalPanel();
        vizPanel.add(vizFrame);
        searchTabPanel.add(vizPanel, "KUPKB Network Visualizer");

        searchPanel.add(searchTabPanel);

//        VerticalPanel twitterPanel = new VerticalPanel();
//        twitterPanel.add(new HTMLPanel("<span style=\"margin-left: 10px; font-size: 1.3em; font-weight: bold;\">KUPKB News</span>"));
//        twitterPanel.add(
//                new HTMLPanel("<a class=\"twitter-timeline\" href=\"https://twitter.com/KUPKB_team\" data-widget-id=\"348003163430854656\">Tweets by @KUPKB_team</a>\n" +
//                        "<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");</script>\n")
//                  );


//        Document doc = Document.get();
//        ScriptElement script = doc.createScriptElement();
//        script.setText("!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");");
//        script.setType("text/javascript");
//        script.setLang("javascript");
//        HTML twp = new HTML(
//
//                "<a class=\"twitter-timeline\" href=\"https://twitter.com/KUPKB_team\" data-widget-id=\"3480031634308546<a class=\"twitter-timeline\" href=\"https://twitter.com/KUPKB_team\" data-widget-id=\"348003163430854656\">Tweets by @KUPKB_team</a>"
//        +    script.toString()
//        );

//        doc.getBody().appendChild(script);
//        twitterPanel.add(twp);

//        searchPanel.add(twitterPanel);

        searchTabPanel.selectTab(0);
        searchTabPanel.setAnimationEnabled(true);

        mainPanel.add(searchPanel);

        initWidget(mainPanel);


    }
}
