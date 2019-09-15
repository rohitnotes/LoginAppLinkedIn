package com.login.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private ImageView linkedInAccountProfilePicImageView;
    private TextView linkedInAccountHolderNameTextView, linkedInAccountUsernameTextView;
    private Button linkedInAccountSignOutButton,linkedInAccountRevokeAccessButton;
    private RequestQueue requestQueue;

    private static final String PROFILE_URL = "https://api.linkedin.com/v2/me";
    private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        initObjects();

        String accessToken =  AppSharedPreferences.getInstance(ProfileActivity.this).getAccessToken();
        if(!accessToken.equals("not found"))
        {
            String profileUrl = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,emailAddress,profilePicture(displayImage~:playableStreams))";
            POST_REQUEST_METHOD(profileUrl,accessToken);
        }

        linkedInAccountSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void initView()
    {
        linkedInAccountProfilePicImageView = findViewById(R.id.google_account_profile_image);
        linkedInAccountHolderNameTextView = findViewById(R.id.google_account_full_name);
        linkedInAccountUsernameTextView = findViewById(R.id.google_account_username);
        linkedInAccountSignOutButton=findViewById(R.id.sign_out_from_google_account);
        linkedInAccountSignOutButton=findViewById(R.id.sign_out_from_google_account);
    }

    private void initObjects()
    {
        /**
         * Get a RequestQueue
         */
        requestQueue = Volley.newRequestQueue(ProfileActivity.this);
    }

    /**
     * method to do google sign out
     * This code clears which account is connected to the app. To sign in again, the user must choose their account again.
     */
    private void signOut()
    {

    }

    private static final String getProfileUrl(String accessToken){
        return PROFILE_URL
                +QUESTION_MARK
                +OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;

        /*
        below url created
        https://api.linkedin.com/v2/me?oauth2_access_token=AQXTO6cTExTt_6clblZCiFw1u36wMw9bz2KSKILdXZTDcb2HVKF8SJhYHJL6oeAQVUXUQNGlTXRshR8uvtFaxoQoGu46dFFL2ElklyXKSsjZ2YewLhUlmSuluzN0cFaSFcea9_VmhZK3cH1CaSWyqbcabQLYQHOeLHmITb6bbucnutOrUYjBGm4vqzZslUMm-Vswc4LUJzQDeMe7JkpzXoOhmIsNuPJsaFWVQNdgD8h6Z5NZ9_hCTL6va8itaari6SKboyxgaGPwe_kntn2lA8-WYCSQT1b1N2uw44iyfNRKG3hUbUYvEYFUwpTUgIN9cASm8228YUogo7YXcHD_R_7bML2gBw
         */
    }

    private void POST_REQUEST_METHOD(final String callThisUrl,final String accessToken)
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
                                    JSONObject rootJsonObject;
                                    int expiresIn;
                                    String accessToken;
                                    try
                                    {
                                        rootJsonObject = new JSONObject(response);

                                        /**
                                         * First Name
                                         */
                                        JSONObject firstName = rootJsonObject.getJSONObject("firstName");
                                        JSONObject localizedFirstName = firstName.getJSONObject("localized");
                                        String firstNameString = localizedFirstName.getString("en_US");

                                        /**
                                         * Last Name
                                         */
                                        JSONObject lastName = rootJsonObject.getJSONObject("lastName");
                                        JSONObject localizedLastName = lastName.getJSONObject("localized");
                                        String lastNameString = localizedLastName.getString("en_US");
                                        linkedInAccountHolderNameTextView.setText(firstNameString+" "+lastNameString);

                                        /**
                                         * Profile image
                                         */
                                        JSONObject profilePicture = rootJsonObject.getJSONObject("profilePicture");
                                        JSONObject displayImage = profilePicture.getJSONObject("displayImage~");

                                        JSONArray elementsArray = displayImage.getJSONArray("elements");
                                        JSONArray identifiersArray = elementsArray.getJSONObject(0).getJSONArray("identifiers");

                                        JSONObject identifier = identifiersArray.getJSONObject(0);
                                        String profilePictureURLArrayString = identifier.getString("identifier");

                                        Picasso.with(ProfileActivity.this).load(profilePictureURLArrayString).error(R.drawable.placeholder).into(linkedInAccountProfilePicImageView, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d(TAG, "onSuccess");
                                            }

                                            @Override
                                            public void onError() {
                                                Toast.makeText(getApplicationContext(), "Image Loading Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        requestQueue.stop();
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
                            }
                        }
                )
        {
            @Override
            public int getMethod() {
                return Method.GET;
            }

            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+accessToken);
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}