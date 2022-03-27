package com.example.bikerx.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.bikerx.MainActivity;
import com.example.bikerx.R;
import com.example.bikerx.databinding.ActivityEmailLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 *
 */
public class EmailLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ActivityEmailLoginBinding mBinding;

    /**
     * initialize register,Login and forgotPassword button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        mBinding = ActivityEmailLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.register.setOnClickListener(this);
        mBinding.signIn.setOnClickListener(this);
        mBinding.forgotPassword.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * brings user to different activities depending on which button user clicks on in the login page
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        /**
         * if user click on register button, bring user to Register user activity
         * if user click on signIn button, call userLogin() method to log user into account
         * if user clicks forgotpassword button, bring user to forgotpassword page to reset password via email
         */
        switch (v.getId()) {
            //take user to RegisterUser page when click register on Email Login
            case R.id.register:
                startActivity(new Intent(EmailLoginActivity.this, RegisterUserActivity.class));
                break;
            //if user clicks login button on EmailLogin page, call userLogin() function below
            case R.id.signIn:
                userLogin();
                break;
            //if user clicks "forgotPassword" TextView on Email login page, lead user to ForgotPassword Page to reset password
            case R.id.forgotPassword:
                startActivity(new Intent(EmailLoginActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }

    /**
     * executes when user clicks "login" button on EmailLoginActivity after filling up their email and password
     * checks for valid input for email and password
     * if valid check if email and password matches data in firebase.
     * If it matches, check if email is verified (first time users who logs in for the first time needs to go to their mailbox to verify email address)
     * If verified, log user into account and bring them to homepage
     */

    private void userLogin() {
        String email = mBinding.email.getText().toString().trim();
        String password = mBinding.password.getText().toString().trim();

        /**
         * these if conditions checks for valid input of email and password by user
         * check if email input by user is empty
         */
        if (email.isEmpty()) {
            mBinding.email.setError("Email required");
            mBinding.email.requestFocus();
            return;

        }

        /**
         * check if email is a valid one
         */

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mBinding.email.setError("Please provide valid email");
            mBinding.email.requestFocus();
            return;
        }

        /**
         * check if user key in a password
         */
        if (password.isEmpty()) {
            mBinding.password.setError("Password required");
            mBinding.password.requestFocus();
            return;
        }

        /**
         * check if password entered is valid. firebase do not allow password that is <6 characters
         */

        if (password.length() < 6) {
            mBinding.password.setError("Minimum password length is 6 characters");
            mBinding.password.requestFocus();
            return;
        }

        /**
         * display a progress dialog box to indicate to user that the app is logging him into account
         */
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //Without this user can hide loader by tapping outside screen
        dialog.setCancelable(false);
        dialog.setMessage("Logging you in...");
        dialog.show();
/**
 * log user into account by checking if their email and password matches data in firebase
 */
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                /**
                 * check if email address has been verified by the user or not
                 */
                if (task.isSuccessful()) {


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    /**
                     * if user email is verified, direct user to main activity once user logs in successfully with correct email and password
                     * else send email verification link to user
                     */

                    if (user.isEmailVerified()) {

                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));

                    } else {

                        dialog.dismiss();
                        user.sendEmailVerification();
                        Toast.makeText(EmailLoginActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                    }


                    /**
                     * if email or password do not match data in firebase, do not log user in and prompt an error message to user that their login credential is wrong
                     */
                } else {
                    dialog.dismiss();
                    Toast.makeText(EmailLoginActivity.this, "Failed to login. check credentials", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}