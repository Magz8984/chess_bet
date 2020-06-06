package chessbet.app.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.utils.Util;

/**
 * @author Collins Magondu 6/6/2020
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.txtPhoneNumber) EditText txtPhoneNumber;
    @BindView(R.id.txtVerificationCode) EditText txtVerificationCode;
    @BindView(R.id.btn_verify_phone_number) Button btnVerifyPhoneNumber;
    @BindView(R.id.btn_verify_code) Button btnVerifyCode;
    @BindView(R.id.verificationInputLayout) TextInputLayout verificationInputLayout;
    @BindView(R.id.phoneNumberInputLayout) TextInputLayout phoneNumberInputLayout;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnVerifyPhoneNumber.setOnClickListener(this);
        btnVerifyCode.setOnClickListener(this);
        FirebaseAuth.getInstance().setLanguageCode("en");
    }

    @Override
    public void onClick(View view) {
        if(view.equals(btnVerifyPhoneNumber)) {
            verifyPhoneNumber();
        } else if (view.equals(btnVerifyCode)) {
            verifyToken();
        }
    }

    private void verifyPhoneNumber() {
        if(Util.textViewHasText(txtPhoneNumber)) {
            String phoneNumber  = txtPhoneNumber.getText().toString();
            // TODO Support Other Countries
            if(phoneNumber.startsWith("+254")){
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(LoginActivity.this, "Successful Verification", Toast.LENGTH_LONG).show();
                        signInWithCredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Toast.makeText(LoginActivity.this, "SMS Quota Exceeded", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("ErrorVerify", Objects.requireNonNull(e.getMessage()));
                            Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Toast.makeText(LoginActivity.this, "Verification Code Has Been Sent Successfully", Toast.LENGTH_LONG).show();
                        verificationInputLayout.setVisibility(View.VISIBLE);
                        phoneNumberInputLayout.setVisibility(View.GONE);
                        btnVerifyPhoneNumber.setVisibility(View.GONE);
                        btnVerifyCode.setVisibility(View.VISIBLE);
                        LoginActivity.this.verificationId = verificationId;
                        LoginActivity.this.token = forceResendingToken;
                    }
                });
            } else {
                Toast.makeText(this, "Phone Number must begin with +254", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Phone Number Required", Toast.LENGTH_LONG).show();
        }
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
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void signInWithCredentials(PhoneAuthCredential credential) {
        try {
            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    AccountAPI.get().setUser(Objects.requireNonNull(task.getResult()).getUser());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Verification Code Entered Was Invalid", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "An error occurred while signing in", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
