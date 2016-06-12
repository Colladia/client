package com.ia04nf28.colladia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.Observable;
import android.databinding.ObservableList;
import android.graphics.Color;
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
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * A login screen for Colladia
 */
public class LoginActivity extends AppCompatActivity {
    // UI references.
    private EditText mUserLoginView;
    private EditText mServerAddressView;
    private Button mColorPickerButton;
    private View mProgressView;
    private View mLoginFormView;

    private int color;
    private ColorMode mode = ColorMode.RGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserLoginView = (EditText) findViewById(R.id.user_login);
        mServerAddressView = (EditText) findViewById(R.id.server_address);
        mColorPickerButton = (Button) findViewById(R.id.color_picker);
        mColorPickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.validate_form_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        Manager.instance(this.getApplicationContext()).getLogged().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(Manager.instance(getApplicationContext()).getLogged().get()){
                    showProgress(false);
                    startDrawActivity();
                }
            }
        });

        Random rnd = new Random();
        int rndDefaultColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        // get last stored settings
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String lastLogin = settings.getString("login", "");
        String lastUrl = settings.getString("url", "");


        color = settings.getInt("color", rndDefaultColor);
        // inject in forms
        mUserLoginView.setText(lastLogin);
        mServerAddressView.setText(lastUrl);
        updateColorPickerButton(color);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // get user
        User user = Manager.instance(getApplicationContext()).getUser();
        String url = Manager.instance(getApplicationContext()).getUrl();

        // edit settings
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("login", user.getLogin());
        editor.putString("url", url);
        editor.putInt("color", user.getColor());

        // save edits
        editor.apply();
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
        String userLogin = mUserLoginView.getText().toString().trim();
        String address = mServerAddressView.getText().toString().trim();

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

        // Handle color selection
        // No white, please.
        // TODO

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Manager.instance(this.getApplicationContext()).login(new User(userLogin, color), address);
        }
        //startDrawActivity();
    }

    private boolean isUserLoginValid(String s) {
        //TODO: Replace this with your own logic
        return !s.isEmpty();
    }

    private boolean isAddressValid(String s) {
        return Pattern.matches("^(http://)?((?:\\d{1,3}.?){4}(:8182)?)/?$", s);
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

    private void startDrawActivity() {
        Intent intent = new Intent(this, WorkspacesListActivity.class);
        //Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }

    private void validationFail(VolleyError error) {
        //mServerAddressView.setError(getString(R.string.error_incorrect_address));
        mServerAddressView.setError(error.getMessage());
        mServerAddressView.requestFocus();
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showColorPickerDialog() {
        new ChromaDialog.Builder()
                .initialColor(color)
                .colorMode(ColorMode.RGB)
                .indicatorMode(IndicatorMode.DECIMAL) //HEX or DECIMAL;
                .onColorSelected(new OnColorSelectedListener() {
                    @Override public void onColorSelected(int newColor) {
                        updateColorPickerButton(newColor);
                        color = newColor;
                    }
                })
                .create()
                .show(getSupportFragmentManager(), "dialog");
    }

    private void updateColorPickerButton(int color){
        mColorPickerButton.setBackgroundColor(color);
    }
}

