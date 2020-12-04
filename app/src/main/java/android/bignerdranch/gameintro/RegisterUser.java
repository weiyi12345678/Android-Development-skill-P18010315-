package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private Button mbtn_register, mbtn_back;
    private EditText edt_fullName, edt_email, edt_password, edt_rPassword;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        mAuth = FirebaseAuth.getInstance();

        mbtn_register = (Button) findViewById(R.id.button_register);
        mbtn_register.setOnClickListener(this);

        mbtn_back = (Button)findViewById(R.id.button_back);
        mbtn_back.setOnClickListener(this);

        edt_fullName = (EditText) findViewById(R.id.edit_fullName);
        edt_email = (EditText) findViewById(R.id.edit_email);
        edt_password = (EditText) findViewById(R.id.edit_password);
        edt_rPassword = (EditText) findViewById(R.id.edit_RPassword);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_back:
                finish();
                break;

            case R.id.button_register:
                if(checkEmail())
                    registerUser();
                break;
        }
    }

   public boolean checkEmail (){

       boolean a = true;
       final int[] n = {0};

        mAuth.fetchSignInMethodsForEmail(edt_email.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if(!check){
                            Toast.makeText(RegisterUser.this, "email can be use !", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            edt_email.setError("Email already exists! Please enter new email.");
                            edt_email.requestFocus();
                            n[0] =1;
                        }
                    }
                });

        if(n[0] == 1){
            a = false;
        }

        return a;
    }

    private void registerUser() {
        final String email = edt_email.getText().toString().trim();
        final String fullname = edt_fullName.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String rPassword = edt_rPassword.getText().toString().trim();

        if(fullname.isEmpty()){
            edt_fullName.setError("Full name is required!");
            edt_fullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            edt_email.setError("Email is required!");
            edt_email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Please provided valid email");
            edt_email.requestFocus();
            return;
        }

        if(password.isEmpty()){
            edt_password.setError("Password is required!");
            edt_password.requestFocus();
            return;
        }

        if(password.length() < 6){
            edt_password.setError("Minimum password length must be 6 character!");
            edt_password.requestFocus();
            return;
        }

        if(rPassword.isEmpty()){
            edt_rPassword.setError("Repeated Password is required!");
            edt_rPassword.requestFocus();
            return;
        }

        if(!password.equals(rPassword)){
            edt_rPassword.setError("Repeated Password must be same with password!");
            edt_rPassword.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User(fullname, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterUser.this, "User have been registered successfully!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                                //redirect to login fragment
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterUser.this, "Fail to registered! Try again!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(RegisterUser.this, "Fail to registered!", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
