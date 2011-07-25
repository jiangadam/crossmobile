/* Copyright (c) 2011 by crossmobile.org
 *
 * CrossMobile is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 * CrossMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jubler; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package org.xmlvm.iphone;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UIWebView extends UIView {

    private static final String JSTAG = "javascript:";
    private UIWebViewDelegate delegate = null;
    private int dataDetectorTypes = UIDataDetectorType.All;
    private boolean scalesPageToFit = false;

    public UIWebView() {
        this(new CGRect(0, 0, 0, 0));
    }

    public UIWebView(CGRect rect) {
        super(rect);
    }

    public void loadRequest(NSURLRequest request) {
        if (request != null && request.URL() != null && request.URL().absoluteString() != null
                && (delegate == null || delegate.shouldStartLoadWithRequest(this, request, UIWebViewNavigationType.LinkClicked)))
            ((WebView) __model()).loadUrl(request.URL().absoluteString());
    }

    public void loadHTMLString(String string, NSURL baseURL) {
        if (baseURL != null && baseURL.absoluteString() != null)
            ((WebView) __model()).loadDataWithBaseURL(baseURL.absoluteString(), string, "text/html", "utf-8", null);
        else
            ((WebView) __model()).loadData(string, "text/html", "utf-8");
    }

    public String stringByEvaluatingJavaScriptFromString(String script) {
        if (!script.startsWith(JSTAG))
            script = JSTAG + script;
        ((WebView) __model()).loadUrl(script);
        System.err.println("Call back string not implemented yet. Probably a addJavascriptInterface should be activated.");
        return null;
    }

    public UIWebViewDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(UIWebViewDelegate delegate) {
        this.delegate = delegate;
    }

    public void reload() {
        ((WebView) __model()).reload();
    }

    public void goBack() {
        WebView wv = (WebView) __model();
        if (wv.canGoBack())
            wv.goBack();
    }

    public void goForward() {
        WebView wv = (WebView) __model();
        if (wv.canGoForward())
            wv.goForward();
    }

    public int getDataDetectorTypes() {
        return dataDetectorTypes;
    }

    public void setDataDetectorTypes(int dataDetectorTypes) {
        this.dataDetectorTypes = dataDetectorTypes;
    }

    public boolean isScalesPageToFit() {
        return scalesPageToFit;
    }

    public void setScalesPageToFit(boolean scalesPageToFit) {
        this.scalesPageToFit = scalesPageToFit;
    }

    @Override
    View createModelObject(Activity activity) {
        WebView wv = new WebView(activity);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (delegate != null && !url.startsWith(JSTAG))
                    delegate.webViewDidStartLoad(UIWebView.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (delegate != null && !url.startsWith(JSTAG))
                    delegate.webViewDidFinishLoad(UIWebView.this);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (delegate != null)
                    delegate.didFailLoadWithError(UIWebView.this, NSError.error("NSURLErrorDomain", errorCode, null));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return delegate == null ? false
                        : !delegate.shouldStartLoadWithRequest(UIWebView.this, NSURLRequest.requestWithURL(NSURL.URLWithString(url)), UIWebViewNavigationType.LinkClicked);
            }
        });
        return wv;
    }
}