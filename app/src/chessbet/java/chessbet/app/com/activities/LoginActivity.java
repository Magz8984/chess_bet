package chessbet.app.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.utils.Util;
import es.dmoral.toasty.Toasty;

/**
 * @author Collins Magondu 6/6/2020
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.txtPhoneNumber) EditText txtPhoneNumber;
    @BindView(R.id.txtVerificationCode) EditText txtVerificationCode;
    @BindView(R.id.btn_verify_phone_number) Button btnVerifyPhoneNumber;
    @BindView(R.id.btn_verify_code) Button btnVerifyCode;
    @BindView(R.id.btn_resend_code) Button btnResendCode;
    @BindView(R.id.verificationInputLayout) TextInputLayout verificationInputLayout;
    @BindView(R.id.phoneNumberInputLayout) TextInputLayout phoneNumberInputLayout;

    private String verificationId;
    private String phoneNumber;
    private PhoneAuthProvider.ForceResendingToken token;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnVerifyPhoneNumber.setOnClickListener(this);
        btnVerifyCode.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);
        FirebaseAuth.getInstance().setLanguageCode("en");
        loading = new ProgressDialog(this);
        this.verificationStateChangedCallbacks = createVerificationCallBacks();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnVerifyPhoneNumber)) {
            verifyPhoneNumber();
        } else if (view.equals(btnVerifyCode)) {
            verifyToken();
        } else if (view.equals(btnResendCode)) {
            resendCode();
        }
    }

    private void verifyPhoneNumber() {
        if(Util.textViewHasText(txtPhoneNumber)) {
            String phoneNumber  = txtPhoneNumber.getText().toString();
            // TODO Support Other Countries
            if(phoneNumber.startsWith("+254")){
                loading.setMessage("Wait a moment...");
                loading.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, this.verificationStateChangedCallbacks);
            } else {
                Toasty.warning(this, "Phone Number must begin with +254", Toasty.LENGTH_LONG).show();
            }
        } else {
            Toasty.warning(this, "Phone Number Required", Toasty.LENGTH_LONG).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks createVerificationCallBacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loading.dismiss();
                Toasty.success(LoginActivity.this, "Successful Verification", Toasty.LENGTH_LONG).show();
                signInWithCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loading.dismiss();
                Crashlytics.logException(e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toasty.error(LoginActivity.this, "Invalid Credentials", Toasty.LENGTH_LONG).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toasty.error(LoginActivity.this, "SMS Quota Exceeded", Toasty.LENGTH_LONG).show();
                } else {
                    Log.e("ErrorVerify", Objects.requireNonNull(e.getMessage()));
                    Toasty.error(LoginActivity.this, "Verification Failed", Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                loading.dismiss();
                Toasty.success(LoginActivity.this, "Verification Code Has Been Sent Successfully", Toasty.LENGTH_LONG).show();
                LoginActivity.this.phoneNumber = txtPhoneNumber.getText().toString();
                verificationInputLayout.setVisibility(View.VISIBLE);
                phoneNumberInputLayout.setVisibility(View.GONE);
                btnVerifyPhoneNumber.setVisibility(View.GONE);
                btnVerifyCode.setVisibility(View.VISIBLE);
                btnResendCode.setVisibility(View.VISIBLE);
                LoginActivity.this.verificationId = verificationId;
                LoginActivity.this.token = forceResendingToken;
            }
        };
    }

    /**
     * Verify code if phone dose not have instant phone number verification
     */
    private void verifyToken() {
        try {
            if(Util.textViewHasText(txtVerificationCode)) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, txtVerificationCode.getText().toString());
                signInWithCredentials(credential);
            } else {
                Toast.makeText(this, "Verification Code Is Required", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toasty.error(this, ex.getMessage(), Toasty.LENGTH_LONG).show();
        }
    }

    private void resendCode() {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(this.phoneNumber, 60, TimeUnit.SECONDS,
                    this,  this.verificationStateChangedCallbacks, this.token);
            loading.setMessage("Wait a moment...");
            loading.show();
        } catch (Exception ex) {
            Toasty.error(this, ex.getMessage(), Toasty.LENGTH_LONG).show();
        }
    }


    private void signInWithCredentials(PhoneAuthCredential credential) {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    goToMainActivity(Objects.requireNonNull(task.getResult()).getUser());
                } else {
                    loading.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toasty.error(this, "Verification Code Entered Was Invalid", Toasty.LENGTH_LONG).show();
                    } else {
                        if(task.getException() != null){
                            String message = task.getException().getMessage();
                            Crashlytics.logException(new Exception(message));
                            Toasty.error(this, Objects.requireNonNull(task.getException().getMessage()), Toasty.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Toasty.error(this, ex.toString(), Toasty.LENGTH_LONG).show();
        }
    }

    /**
     * Wait for cloud functions to create account within 7 seconds
     * @param user Firebase User
     */
    private void goToMainActivity(FirebaseUser user) {
        loading.dismiss();
        loading.setMessage("Accounts Are Being Created");
        loading.show();

        new Handler().postDelayed((Runnable) () -> {
            loading.dismiss();
            AccountAPI.get().setUser(user);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }, 7000);
    }
}
