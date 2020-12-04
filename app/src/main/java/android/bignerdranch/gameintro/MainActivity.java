package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DrawerLayout drawer;
    private boolean logInStatus;
    private TextView email, fullName, logInMessage;
    private String mFullName, mUserID, mEmail;
    private Menu nav_menu;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        email = headerView.findViewById(R.id.text_email);
        fullName = headerView.findViewById(R.id.text_name);
        logInMessage = headerView.findViewById(R.id.text_loginMessage);

        nav_menu = navigationView.getMenu();

        nav_menu.findItem(R.id.nav_edit).setVisible(false);
        nav_menu.findItem(R.id.nav_profile).setVisible(false);
        nav_menu.findItem(R.id.nav_log).setVisible(true);
        headerView.findViewById(R.id.text_email).setVisibility(View.GONE);
        headerView.findViewById(R.id.text_name).setVisibility(View.GONE);
        headerView.findViewById(R.id.text_loginMessage).setVisibility(View.VISIBLE);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            logInStatus = extras.getBoolean("logInStatus");
            mEmail = extras.getString("email");
        }
        else{
            nav_menu.findItem(R.id.nav_edit).setVisible(false);
            nav_menu.findItem(R.id.nav_profile).setVisible(false);
            nav_menu.findItem(R.id.nav_log).setVisible(true);
            headerView.findViewById(R.id.text_email).setVisibility(View.GONE);
            headerView.findViewById(R.id.text_name).setVisibility(View.GONE);
            headerView.findViewById(R.id.text_loginMessage).setVisibility(View.VISIBLE);
        }





        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomepageFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_view);
        }


    }

    //code inside onStart will run when onStart() run (act as update data)
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");

        if(user != null){
            mUserID = user.getUid();
            nav_menu.findItem(R.id.nav_edit).setVisible(false);
            nav_menu.findItem(R.id.nav_profile).setVisible(true);
            nav_menu.findItem(R.id.nav_log).setVisible(false);
            headerView.findViewById(R.id.text_email).setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.text_name).setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.text_loginMessage).setVisibility(View.GONE);

            userRef.child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile != null){

                        mFullName = userProfile.fullName;
                        mEmail = userProfile.email;

                        fullName.setText(mFullName);
                        email.setText(mEmail);

                        if(mEmail.equals("connectfirebase2077@gmail.com")){
                            nav_menu.findItem(R.id.nav_edit).setVisible(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Something when wrong!" , Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    //side nav
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomepageFragment()).commit();
                break;

            case R.id.nav_gameInfo:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GameInfoFragment()).commit();
                break;

            case R.id.nav_edit:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EditFragment()).commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;

            case R.id.nav_log:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LogFragment()).commit();
                break;

            case R.id.nav_faq:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FAQFragment()).commit();
                break;

            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }


}
