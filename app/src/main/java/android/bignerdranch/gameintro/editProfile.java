package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class editProfile extends AppCompatActivity {

    private static final String TAG = "editProfile";

    private EditText edt_edit;
    private Button update;

    private DatabaseReference userRef;
    String fullName, mUserID;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        edt_edit = (EditText) findViewById(R.id.username);

        update = (Button) findViewById(R.id.edit);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        mUserID = user.getUid();

        if(user != null){
            userRef.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile != null){

                        fullName = userProfile.fullName;
                        edt_edit.setText(fullName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(editProfile.this, "Something when wrong!" , Toast.LENGTH_SHORT).show();
                }
            });

        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(editProfile.this);
                builder.setTitle("Are you sure you want to edit the profile ?");

                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateProfile();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(editProfile.this, "Edit process have been cancel !" , Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void updateProfile(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(!fullName.equals(edt_edit.getText().toString())){
            userRef.child(mUserID).child("fullName").setValue(edt_edit.getText().toString());
            Toast.makeText(editProfile.this, "Profile username have been updated successfully !" , Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            edt_edit.setError("Please enter new username");
            edt_edit.requestFocus();
            return;
        }

    }
}
