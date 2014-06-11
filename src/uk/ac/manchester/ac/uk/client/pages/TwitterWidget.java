package uk.ac.manchester.ac.uk.client.pages;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author Simon Jupp
 * @date 14/02/2012
 * Functional Genomics Group EMBL-EBI
 */
public class TwitterWidget extends Composite {

    private JavaScriptObject widgetJsObj = null;
    private final FlowPanel twPanel;
    private final boolean destroyOnUnload;

    public TwitterWidget() {
        this(true);
    }

    public TwitterWidget(boolean destroyOnUnload) {
        this.destroyOnUnload = destroyOnUnload;
        twPanel = new FlowPanel();
        twPanel.getElement().setId(DOM.createUniqueId());
        initWidget(twPanel);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Callback<Void, Exception> callback = new Callback<Void, Exception>() {

            @Override
            public void onSuccess(Void result) {
                if (nativeEnsureTwitterWidgetJsLoadedAndSetToWnd()) {
                    renderAndStart();
                } else {
                    GWT.log("even though success has been called, the twitter widget js is still not available");
                    // some logic maybe keep checking every second for 1 minute
                }
            }

            @Override
            public void onFailure(Exception reason) {
                // TODO Auto-generated method stub
                GWT.log("exception loading the twitter widget javascript", reason);
            }


        };
        ScriptInjector.fromString(
         "!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");"
        )
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
//        boolean isTwitterWidgetAvailable = nativeEnsureTwitterWidgetJsLoadedAndSetToWnd();
//        if (isTwitterWidgetAvailable) {
//            renderAndStart();
//        } else {
////            ScriptInjector.fromUrl("http://widgets.twimg.com/j/2/widget.js")
////                    .setWindow(ScriptInjector.TOP_WINDOW)
////                    .setCallback(callback)
////                    .inject();
//
//        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        if (widgetJsObj!=null) {
            // need to manually destroy so that attached events get removed
            if (destroyOnUnload) {
                nativeDestroyTwitterWidget(widgetJsObj);
            } else {
                nativeStopTwitterWidget(widgetJsObj);
            }
        }
    }

//    private native JavaScriptObject nativeRenderStartTwitterWidget(String domId) /*-{
//        var twObj = new $wnd.TWTR.Widget({
//            version: 2,
//            id:domId,
//            type: 'profile',
//            rpp: 5,
//            title: 'KUPKB news',
//            subject: 'KUPKB news',
//            interval: 30000,
//            width: 300,
//            height: 300,
//            theme: {
//                shell: {
//                    background: 'transparent',
//                    color: '#000000'
//                },
//                tweets: {
//                    background: 'transparent',
//                    color: '#000000',
//                    links: '#1128bf'
//                }
//            },
//            features: {
//                scrollbar: true,
//                loop: false,
//                live: false,
//                avatars: false,
//                timestamp: true,
//                behavior: 'all'
//            }
//        }).render().setUser("KUPKB_team").start();
//        return twObj;
//    }-*/;

    private native JavaScriptObject nativeRenderStartTwitterWidget(String domId) /*-{
        var twObj = new $wnd.TWTR.Widget(
        !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs"));
        return twObj;
    }-*/;

    private native boolean nativeEnsureTwitterWidgetJsLoadedAndSetToWnd() /*-{
        // this only works when TWTR has been properly loaded to $wnd directly
        if (!(typeof $wnd.TWTR === "undefined") && !(null===$wnd.TWTR)) {
            return true;
        }
        return false;
    }-*/;

    private native JavaScriptObject nativeStopTwitterWidget(JavaScriptObject twObj) /*-{
        return twObj.stop();
    }-*/;

    private native JavaScriptObject nativeDestroyTwitterWidget(JavaScriptObject twObj) /*-{
        return twObj.destroy();
    }-*/;

    private void renderAndStart() {
        widgetJsObj = nativeRenderStartTwitterWidget(twPanel.getElement().getId());
        // you can call other native javascript functions
        // on twitWidgetJsObj such as stop() and destroy()
    }

}