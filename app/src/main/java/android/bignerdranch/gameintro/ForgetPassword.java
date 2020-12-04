package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {

    private EditText email;
    private Button btn_reset;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = (EditText) findViewById(R.id.edit_email);
        btn_reset = (Button) findViewById(R.id.button_reset);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });



    }

    private void resetPassword() {
        String mEmail = email.getText().toString().trim();

        if(mEmail.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()){
            email.setError("Please provide a valid email!");
            email.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressBar.setVisibility(View.GONE);
                    finish();
                    Toast.makeText(ForgetPassword.this, "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(ForgetPassword.this, "Try again!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }
}
