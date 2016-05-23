package com.ia04nf28.colladia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ia04nf28.colladia.model.Manager;
import com.ia04nf28.colladia.model.User;

/**
 * A login screen for Colladia
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText mUserLoginView;
    private EditText mServerAddressView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserLoginView = (EditText) findViewById(R.id.user_login);
        mServerAddressView = (EditText) findViewById(R.id.server_address);

        Button mEmailSignInButton = (Button) findViewById(R.id.validate_form_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to connect.
     * If there are form errors (invalid fields, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUserLoginView.setError(null);
        mServerAddressView.setError(null);

        // Store values at the time of the login attempt.
        String userLogin = mUserLoginView.getText().toString();
        String address = mServerAddressView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid user login.
        if (TextUtils.isEmpty(userLogin)) {
            mUserLoginView.setError(getString(R.string.error_field_required));
            focusView = mUserLoginView;
            cancel = true;
        } else if (!isUserLoginValid(userLogin)) {
            mUserLoginView.setError(getString(R.string.error_invalid_user_login));
            focusView = mUserLoginView;
            cancel = true;
        }

        // Check for a valid server address.
        if (TextUtils.isEmpty(address)) {
            mServerAddressView.setError(getString(R.string.error_field_required));
            focusView = mServerAddressView;
            cancel = true;
        } else if (!isAddressValid(address)) {
            mServerAddressView.setError(getString(R.string.error_invalid_address));
            focusView = mServerAddressView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            sendSimpleRequest(address);
        }
    }

    private boolean isUserLoginValid(String s) {
        //TODO: Replace this with your own logic
        return !s.isEmpty();
    }

    private boolean isAddressValid(String s) {
        //TODO: Replace this with your own logic
        return !s.isEmpty();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void sendSimpleRequest(String address) {
        RequestQueue rq = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, address,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);
                        startDrawActivity();
                        //finish();
                    }
                }
                , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        validationFail(error);
                    }
                });

        // Add the request to the RequestQueue.
        rq.add(request);
    }

    private void startDrawActivity() {

        String userLogin = mUserLoginView.getText().toString();
        String address = mServerAddressView.getText().toString();

        Manager.instance(this.getApplicationContext()).login(new User(userLogin), address);

        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }

    private void validationFail(VolleyError error) {
        //mServerAddressView.setError(getString(R.string.error_incorrect_address));
        mServerAddressView.setError(error.getMessage());
        mServerAddressView.requestFocus();
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }
}

