package android.bignerdranch.gameintro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LogFragment extends Fragment{

    private TextView register, forgetPassword;
    private EditText edt_email, edt_password;
    private Button btn_signIn;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_log, container, false);

        mAuth =  FirebaseAuth.getInstance();

        register = (TextView) rootView.findViewById(R.id.text_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RegisterUser.class);
                startActivity(intent);
            }
        });

        btn_signIn = (Button) rootView.findViewById(R.id.button_signIn);
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogIn();
            }

        });

        edt_email = (EditText) rootView.findViewById(R.id.edit_email);
        edt_password = (EditText) rootView.findViewById(R.id.edit_password);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        forgetPassword = (TextView) rootView.findViewById(R.id.text_forgetPassword);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgetPassword.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void userLogIn(){
        final String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        //check if email and password is empty or not
        if(email.isEmpty()){
            edt_email.setError("Email is required!");
            edt_email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edt_email.setError("Please enter a valid email!");
            edt_email.requestFocus();
            return;
        }

        if(password.isEmpty()){
            edt_password.setError("Password is required");
            edt_password.requestFocus();
            return;
        }

        if(password.length() < 6){
            edt_password.setError("Min password length is 6 character !");
            edt_password.requestFocus();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        //login function with email and password
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()) {

                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.putExtra("logInStatus",true);
                        startActivity(intent);

                        //redirect to user homepage
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomepageFragment()).commit();

                        mProgressBar.setVisibility(View.GONE);
                    }
                    else{
                        user.sendEmailVerification();
                        Toast.makeText(getActivity(), "Check your email to verify your account!", Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Fail to login ! Please check your credentials.", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }

}
