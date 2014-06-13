package uk.ac.manchester.ac.uk.client;

import com.allen_sauer.gwt.log.client.Log;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import uk.ac.manchester.ac.uk.client.pages.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class KUPKBEntryPoint implements EntryPoint {


    private TabPanel mainTabPanel = new TabPanel();
//    private TabPanel searchTabPanel = new TabPanel();

    private String cookie_id = "kupkb_analytics";

    // custom loader for cell tree css
    public interface MyCellTreeResources extends CellTree.Resources {

        public MyCellTreeResources INSTANCE =
                GWT.create(MyCellTreeResources.class);

        @Source("tree.css")
        CellTree.Style cellTreeStyle();

    }

    public void onModuleLoad() {

        /*
        * Install an UncaughtExceptionHandler which will produce <code>FATAL</code> log messages
        */
        Log.setUncaughtExceptionHandler();

        // use deferred command to catch initialization exceptions in onModuleLoad2
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad2() {


//        HorizontalPanel surveyPanel = new HorizontalPanel();
//        HTMLPanel panel = new HTMLPanel("<p class=\"survey-text\">Help us improve the KUPKB!</p>");
//        Anchor surveyPopupButton = new Anchor(SafeHtmlUtils.fromString("Click here for more info"));
//        Anchor surveyButton = new Anchor(SafeHtmlUtils.fromString("take the KUPKB survey now"));
//        surveyPopupButton.setStyleName("survey-anchor");
//        final SurveyPopupPanel surveyPopupPanel = new SurveyPopupPanel();
//        surveyPopupPanel.setStyleName("surveyPopUpPanel");
//        surveyPopupButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                surveyPopupPanel.show();
//            }
//        });
//
//        surveyButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent clickEvent) {
//                surveyPopupPanel.setCookie();
//                Window.open("iKUPsurvey.htm", "_blank", "");
//            }
//        });
//
//        surveyPopupButton.addStyleName("surveyButton");
//        surveyButton.addStyleName("surveyButton");
//
//        surveyPanel.add(panel);
//        surveyPanel.add(surveyPopupButton);
//        surveyPanel.add(new HTML("<p class=\"survey-text\">&nbsp or &nbsp</p>"));
//        surveyPanel.add(surveyButton);
//
//        surveyPanel.addStyleName("surveyPanel");
//
//        if (Window.Location.getParameter("survey") != null) {
//            surveyPopupPanel.show();
//        }

        mainTabPanel.removeStyleName("gwt-TabPanelBottom");

        mainTabPanel.add(new HomeWidget(), "Home");
//        mainTabPanel.add(new AboutWidget(), "About");
//        mainTabPanel.add(new SubmitWidget(), "Submit Data");
//        mainTabPanel.add(new ContactWidget(), "Contact");
//        mainTabPanel.add(new AknowledgeWidget(), "Acknowledgements");
//        mainTabPanel.add(new FAQWidget(), "FAQ");


        mainTabPanel.addSelectionHandler(new SelectionHandler<Integer> () {
            @Override
            public void onSelection(SelectionEvent<Integer> integerSelectionEvent) {
                if (integerSelectionEvent.getSelectedItem() > 0) {
                    History.newItem("tab" + integerSelectionEvent.getSelectedItem());
                }
                else if (History.getToken().startsWith("queryids=") && integerSelectionEvent.getSelectedItem() == 0) {

                }
                else {
                    History.newItem("tab" + integerSelectionEvent.getSelectedItem());
                }
            }
        });

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                String historyToken = stringValueChangeEvent.getValue();

                if (historyToken.startsWith("queryids=")) {
                    try {

                        if (historyToken.substring(0, 9).equals("queryids=")) {
                            String tabIndexToken = historyToken.substring(9);
                            Set<String> idSet = new HashSet<String>();
                            for (String s : tabIndexToken.split(",")) {
                                if (!s.isEmpty()) {
                                    idSet.add(s);
                                }
                            }
                            // Select the specified tab panel
//                            moleculeSearchWidget.generateResultsTable(idSet);
                            mainTabPanel.selectTab(0);

                        } else {
                            mainTabPanel.selectTab(0);
                        }

                    } catch (IndexOutOfBoundsException e) {
                        mainTabPanel.selectTab(0);
                    }

                }
                else if (historyToken.startsWith("tab")) {
                    // Parse the history token
                    try {
                        if (historyToken.substring(0, 3).equals("tab")) {
                            String tabIndexToken = historyToken.substring(3, 4);
                            int tabIndex = Integer.parseInt(tabIndexToken);
                            // Select the specified tab panel
                            mainTabPanel.selectTab(tabIndex);
                        } else {
                            mainTabPanel.selectTab(0);
                        }

                    } catch (IndexOutOfBoundsException e) {
                        mainTabPanel.selectTab(0);
                    }
                }
            }
        });

        mainTabPanel.selectTab(0);
        mainTabPanel.setAnimationEnabled(true);

//        RootPanel.get("surveyPanel").add(surveyPanel);

        RootPanel.get("kupkbMain").add(mainTabPanel);

    }

    public class SurveyPopupPanel extends PopupPanel {

        private VerticalPanel poppupPanel = new VerticalPanel();
        private Button acceptButton = new Button("Tracking and survey!");
        private Button trackOnlyButton = new Button("Tracking only");
        private Button declineButton = new Button("No thanks");
        private Button removeButton = new Button("Remove my cookie");

        public SurveyPopupPanel() {
            super();
            setTitle("Set cookies");
            setStyleName("surveyPopUpPanel");
            setGlassEnabled(true);
            setPopupPosition(240, 40);
            setAutoHideEnabled(true);


            String text ="<h4>KUPKB Survey</h4><p class=\"surveyPopupText\">" +
                    "In addition to being a useful resource to the KUP community, " +
                    "the KUPKB and iKUP browser is being used as a platform for " +
                    "research into new web technologies. We want to know how you are " +
                    "interacting with the website so we can learn from this and improve " +
                    "the overall experience. In order to help us in our research and " +
                    "improve the KUPKB please click the \"I want to help\" button below. " +
                    "We will store a cookie on your computer that we will use for our analytics.</p> " +
                    "<p class=\"surveyPopupText\">We also have a short survey that we are using to better understand our users and " +
                    "how they use the KUPKB. It should take no longer than 3 minutes to complete. We would really appreciate" +
                    " your feedback and we will donate £5 to charity for every completed form.</p>" +
                    "<p style=\"font-size:smaller;\" class=\"surveyPopupText\">Please note, the resulting data will be confidential and we will not reveal in " +
                    "any form the nature of your searches without permission.  Please also bear in " +
                    "mind that the data will not contain any personal information and it will be " +
                    "used strictly for research purposes. The cookie is set to expire after 1 year." +
                    " If you have any questions please contact us at support[at]kupkb.org. Thank you for your participation and happy searching!</p>";



            poppupPanel.add(new HTMLPanel(text));
            HorizontalPanel buttonPanel = new HorizontalPanel();
            buttonPanel.add(acceptButton);
            buttonPanel.add(trackOnlyButton);
            buttonPanel.add(declineButton);
            buttonPanel.add(removeButton);

            acceptButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    setCookie();

                    Window.open("iKUPsurvey.htm", "_blank", "");
                }
            });

            trackOnlyButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    setCookie();
                }
            });

            declineButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    hide();
                }
            });

            removeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Cookies.removeCookie(cookie_id);
                    hide();
                }
            });

            poppupPanel.add(buttonPanel);
            add(poppupPanel);

        }

        private void setCookie() {
            if (Cookies.getCookie(cookie_id) == null) {
                Date now = new Date();
                long nowLong = now.getTime();
                nowLong = nowLong + (1000 * 60 * 60 * 24 * 365);//1 yr from now
                now.setTime(nowLong);
                Cookies.setCookie(cookie_id, "kupkb_" + Random.nextInt(), now);
                hide();
            }
        }

        public void hide() {
            setVisible(false);
        }

    }

}

