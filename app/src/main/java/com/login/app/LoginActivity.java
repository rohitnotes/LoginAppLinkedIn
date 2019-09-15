package com.login.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout linkedInAccountSignInButton;
    private WebView webView;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initObjects();
        initListener();
    }

    /**
     * init the views by finding the ID of all views
     */
    private void initViews()
    {

        linkedInAccountSignInButton = findViewById(R.id.sign_in_using_linked_in_account);
        webView = findViewById(R.id.fragment_web_view);
        progressBar = findViewById(R.id.page_loading_progress_bar);
    }

    private void initObjects()
    {
        /**
         * Get a RequestQueue
         */
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
    }

    /**
     * init the event
     */
    private void initListener()
    {
        linkedInAccountSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                webView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                linkedInAccountSignInButton.setVisibility(View.GONE);
                signInWithLinkedIn();
            }
        });
    }

    /**
     * on Sign In button do LinkedIn Authentication
     */
    public void signInWithLinkedIn()
    {
        String authorizationCodeUrl = LinkedInConstants.getRequestForAuthorizationCode();
        createWebView(authorizationCodeUrl);
    }

    private void createWebView(String authorizationUrl)
    {

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        /*
         * Hides the scroll bars
         */
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setWebViewClient(createWebViewClient());
        webView.setWebChromeClient(createWebChromeClient());
        webView.loadUrl(authorizationUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack())
                    {
                        webView.goBack();
                    }
                    else
                    {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private WebViewClient createWebViewClient()
    {
        return new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl)
            {
                if(authorizationUrl.startsWith(LinkedInConstants.REDIRECT_URI))
                {
                    webView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    String stateToken = uri.getQueryParameter("state");

                    if(stateToken==null || !stateToken.equals(LinkedInConstants.STATE))
                    {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    String authorizationToken = uri.getQueryParameter("code");

                    if(authorizationToken==null)
                    {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: "+authorizationToken);

                    String accessTokenUrl = LinkedInConstants.postRequestAccessTokenApiUrl(authorizationToken);
                    POST_REQUEST_METHOD(accessTokenUrl);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.i("Authorize","Redirecting to: "+authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                System.out.println("CALL WHEN PAGE LOADING FINISHED : onPageStarted(WebView view, String url, Bitmap favicon)");
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                progressBar.setVisibility(View.GONE);
                System.out.println("CALL WHEN PAGE LOADING FINISHED : onPageFinished(WebView view, String url)");
                super.onPageFinished(view, url);
            }

            /*
             * api<23
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String url)
            {
                /*
                 * webView.loadUrl("file:///android_asset/error/error.html");
                 */
                Toast.makeText(getApplicationContext(),"Error Code : "+errorCode,Toast.LENGTH_SHORT).show();
            }

            /*
             * api> 23
             */
            @Override
            @TargetApi(android.os.Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
            {
                /*
                 * webView.loadUrl("file:///android_asset/error/error.html");
                 */
                Toast.makeText(getApplicationContext(),"Error Code : "+error.getErrorCode(),Toast.LENGTH_SHORT).show();
                super.onReceivedError(view, request, error);
            }
        };
    }

    private WebChromeClient createWebChromeClient()
    {
        return new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
                super.onProgressChanged(view, newProgress);
                System.out.println("CALL WHEN PAGE LOADING PROGRESS "+newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
                super.onReceivedTitle(view, title);
                System.out.println("RECEIVED DOCUMENT PAGE TITTLE : "+title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon)
            {
                System.out.println("RECEIVED DOCUMENT PAGE TITTLE : "+icon);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() call");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart() call");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() call");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume call");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() call");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() call");
    }

    private void POST_REQUEST_METHOD(final String callThisUrl)
    {
        final StringRequest stringRequest = new StringRequest
                (
                        callThisUrl,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                if (response != null && response.length() > 0)
                                {
                                    JSONObject jsonObject;
                                    int expiresIn;
                                    String accessToken;
                                    try
                                    {
                                        jsonObject = new JSONObject(response);

                                        expiresIn = jsonObject.has("expires_in") ? jsonObject.getInt("expires_in") : 0;
                                        accessToken = jsonObject.has("access_token") ? jsonObject.getString("access_token") : null;

                                        /*
                                         * Calculate date of expiration
                                         */
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.add(Calendar.SECOND, expiresIn);
                                        long expireDate = calendar.getTimeInMillis();

                                        AppSharedPreferences.getInstance(LoginActivity.this).setAccessTokenAndExpiresDate(accessToken,expireDate);
                                        Intent goToHomeScreenActivity = new Intent(LoginActivity.this, ProfileActivity.class);
                                        startActivity(goToHomeScreenActivity);
                                        LoginActivity.this.finish();
                                        requestQueue.stop();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "INSIDE onResponse NULL");
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                String errorMessage;
                                if (error instanceof NetworkError)
                                {
                                    errorMessage = "Cannot connect to Internet...Please check your connection!";
                                }
                                else if (error instanceof ServerError)
                                {
                                    errorMessage = "The server could not be found. Please try again after some time!!";
                                }
                                else if (error instanceof AuthFailureError)
                                {
                                    errorMessage = "Authentication Error!!";
                                }
                                else if (error instanceof ParseError)
                                {
                                    errorMessage = "Parsing error! Please try again after some time!!";
                                }
                                else if (error instanceof TimeoutError)
                                {
                                    errorMessage = "Connection TimeOut! Please check your internet connection.";
                                }
                                else
                                {
                                    errorMessage = "Something went wrong. Please try again later";
                                    Log.d(TAG, "******MAIN ERROR****** : "+error.getMessage());
                                }
                                Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();
                                requestQueue.stop();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                )
        {
            @Override
            public int getMethod() {
                return Method.GET;
            }
        };

        requestQueue.add(stringRequest);
    }
}
