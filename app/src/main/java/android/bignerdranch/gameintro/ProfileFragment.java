package android.bignerdranch.gameintro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ProfileFragment extends Fragment {

    private FirebaseUser mUser;
    private DatabaseReference mReference;
    private String mUserID;
    private ProgressBar mProgressBar;

    private Button logout, edit;
    private TextView emailTextView, fullNameTextView, welcome;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        edit = (Button) rootView.findViewById(R.id.button_edit);


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference("Users");
        mUserID = mUser.getUid();

        emailTextView = (TextView) rootView.findViewById(R.id.emailAddress);
        fullNameTextView = (TextView) rootView.findViewById(R.id.fullName);
        welcome = (TextView) rootView.findViewById(R.id.text_welcome);

        mProgressBar.setVisibility(View.VISIBLE);



        //start activity and go to edit profile page when click the edit button
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),editProfile.class);
                startActivity(intent);
            }
        });

        logout = (Button) rootView.findViewById(R.id.button_signOut);

        //for log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(),MainActivity.class);
                intent.putExtra("logInStatus",false);
                startActivity(intent);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomepageFragment()).commit();
            }
        });



        return rootView;
    }

    //code inside onStart will run when onStart() run (act as update data)
    @Override
    public void onStart() {
        super.onStart();

        mReference.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;

                    welcome.setText("Welcome " + fullName + " to Game Intro app");
                    fullNameTextView.setText(fullName);
                    emailTextView.setText(email);

                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something when wrong!" , Toast.LENGTH_SHORT).show();
            }
        });

    }



}
