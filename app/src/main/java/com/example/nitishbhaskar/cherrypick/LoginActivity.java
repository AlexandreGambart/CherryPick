package com.example.nitishbhaskar.cherrypick;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.auth.core.AuthProviderType;
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity;
import com.firebase.ui.auth.core.FirebaseLoginError;

import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends FirebaseLoginBaseActivity {

    Firebase firebaseRef;
    EditText userNameET;
    EditText passwordET;
    FancyButton signUp;
    String mName;
    ImageView logoName;
    TextView alreadyAccount;
    FancyButton login;
    FancyButton loginButton2;
    RelativeLayout signUpLayout;

    /* String Constants */
    private static final String FIREBASEREF = "https://cherrypick.firebaseio.com/";
    private static final String FIREBASE_ERROR = "Firebase Error";
    private static final String USER_ERROR = "User Error";
    private static final String LOGIN_SUCCESS = "Login Success";
    private static final String USER_CREATION_SUCCESS =  "Successfully created user";
    private static final String USER_CREATION_ERROR =  "User creation error";
    private static final String EMAIL_INVALID =  "email is invalid :";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(FIREBASEREF);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        loginButton2 = (FancyButton)findViewById(R.id.loginButton2);
        signUp = (FancyButton)findViewById(R.id.signUp);
        signUpLayout = (RelativeLayout)findViewById(R.id.signUplayout);
        alreadyAccount = (TextView)findViewById(R.id.loginTV);
        login = (FancyButton) findViewById(R.id.login);
        logoName = (ImageView)findViewById(R.id.logoName);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                signUp.setVisibility(View.GONE);
                alreadyAccount.setVisibility(View.GONE);
                signUpLayout.setVisibility(View.VISIBLE);
                logoName.setVisibility(View.GONE);
            }
        });
        userNameET = (EditText)findViewById(R.id.edit_text_email);
        passwordET = (EditText)findViewById(R.id.edit_text_password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.showFirebaseLoginPrompt();
            }
        });
        loginButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.showFirebaseLoginPrompt();
            }
        });

        FancyButton createButton = (FancyButton) findViewById(R.id.createUser);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    @Override
    protected void onFirebaseLoginProviderError(FirebaseLoginError firebaseLoginError) {
        Snackbar snackbar = Snackbar.
                make(userNameET, FIREBASE_ERROR + firebaseLoginError.message, Snackbar.LENGTH_SHORT);
        snackbar.show();
        resetFirebaseLoginPrompt();
    }

    @Override
    protected void onFirebaseLoginUserError(FirebaseLoginError firebaseLoginError) {
        Snackbar snackbar = Snackbar
                .make(userNameET, USER_ERROR + firebaseLoginError.message, Snackbar.LENGTH_SHORT);
        snackbar.show();
        resetFirebaseLoginPrompt();
    }

    @Override
    public Firebase getFirebaseRef() {
        return firebaseRef;
    }

    @Override
    public void onFirebaseLoggedIn(AuthData authData) {
        switch (authData.getProvider()) {
            case "password":
                mName = (String) authData.getProviderData().get("email");
                break;
            default:
                mName = (String) authData.getProviderData().get("displayName");
                break;
        }
        Toast.makeText(getApplicationContext(), LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    @Override
    public void onFirebaseLoggedOut() {
        Toast.makeText(getApplicationContext(), LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // All providers are optional! Remove any you don't want.
        setEnabledAuthProvider(AuthProviderType.PASSWORD);
        setEnabledAuthProvider(AuthProviderType.GOOGLE);
        // setEnabledAuthProvider(AuthProviderType.FACEBOOK);
        //  setEnabledAuthProvider(AuthProviderType.TWITTER);
    }

    // Validate email address for new accounts.
    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            userNameET.setError(EMAIL_INVALID + email);
            return false;
        }
        return true;
    }

    // create a new user in Firebase
    public void createUser() {
        if(userNameET.getText() == null ||  !isEmailValid(userNameET.getText().toString())) {
            return;
        }
        firebaseRef.createUser(userNameET.getText().toString(), passwordET.getText().toString(),
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_SUCCESS, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        signUpLayout.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        signUp.setVisibility(View.INVISIBLE);
                        alreadyAccount.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_ERROR, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }
}