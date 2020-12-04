package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EditGameInfo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button choose, update, back;
    private ImageView img;
    private EditText name, description, date, company;
    private CheckBox action, action_adventure, adventure, rolePlay, simulation, strategy, sport, puzzle, idle;
    private CheckBox pPlay5, pPlay4, pNS, pN3DS, pXbox, pPC, pIOS, pAnd;
    private ProgressBar mProgressBar;
    private String m_type_game, m_type_platform, TAG;

    private Uri imageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private String key, pGame_name, pDescription, pDate, pCompany, pType, pPlatform , pImgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game_info);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("gameInfo");

        choose = (Button) findViewById(R.id.button_choose);
        update = (Button) findViewById(R.id.button_update);
        back = (Button) findViewById(R.id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        img = (ImageView) findViewById(R.id.imageView);


        name = (EditText) findViewById(R.id.edit_name);
        description = (EditText) findViewById(R.id.edit_Description);
        date = (EditText) findViewById(R.id.edit_date);
        company = (EditText) findViewById(R.id.edit_Company);

        date.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date.setText(current);
                    date.setSelection(sel < current.length() ? sel : current.length());

                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //for type of games
        action = (CheckBox) findViewById(R.id.c_action);
        action_adventure = (CheckBox) findViewById(R.id.c_action_ad);
        adventure = (CheckBox) findViewById(R.id.c_adventure);
        rolePlay = (CheckBox) findViewById(R.id.c_role_play);
        simulation = (CheckBox) findViewById(R.id.c_simulation);
        strategy = (CheckBox) findViewById(R.id.c_strategy);
        sport = (CheckBox) findViewById(R.id.c_sport);
        puzzle = (CheckBox) findViewById(R.id.c_puzzle);
        idle = (CheckBox) findViewById(R.id.c_idle);

        //for type of platform
        pPlay5 = (CheckBox) findViewById(R.id.c_pPlaystation5);
        pPlay4 = (CheckBox) findViewById(R.id.c_pPlaystation4);
        pNS = (CheckBox) findViewById(R.id.c_pNintendoSwitch);
        pN3DS  = (CheckBox) findViewById(R.id.c_pNintendo3DS);
        pXbox = (CheckBox) findViewById(R.id.c_pXbox);
        pPC = (CheckBox) findViewById(R.id.c_pPC);
        pIOS = (CheckBox) findViewById(R.id.c_pIOS);
        pAnd = (CheckBox) findViewById(R.id.c_pAndroid);

        key = getIntent().getStringExtra("key");
        pGame_name = getIntent().getStringExtra("name");
        pDescription = getIntent().getStringExtra("desc");
        pDate = getIntent().getStringExtra("date");
        pCompany = getIntent().getStringExtra("company");
        pType = getIntent().getStringExtra("type");
        pPlatform = getIntent().getStringExtra("platform");
        pImgUrl = getIntent().getStringExtra("imgUrl");

        name.setText(pGame_name);
        description.setText(pDescription);
        date.setText(pDate);
        company.setText(pCompany);

        Picasso.get()
                .load(pImgUrl)
                .into(img);


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress())
                {
                    Toast.makeText(EditGameInfo.this, "Update in progress !", Toast.LENGTH_SHORT).show();
                }
                else{

                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditGameInfo.this);
                    builder.setTitle("Are you sure you want to update this game info ?");

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkEmpty();
                            FileUploader();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(EditGameInfo.this, "Update process have been cancel !" , Toast.LENGTH_LONG).show();
                        }
                    });

                    dialog = builder.create();
                    dialog.show();

                }
            }
        });
    }

    //combine the type game check box text into a string
    private String typeGame(){
        String type_game = "";
        String a,b,c,d,e,f,g,h,i;

        if(action.isChecked())
        {
            a = action.getText().toString().trim() + ", ";
        }
        else{
            a = "";
        }


        if(action_adventure.isChecked())
        {
            b = action_adventure.getText().toString().trim()  + ", ";
        }
        else{
            b = "";
        }

        if(adventure.isChecked())
        {
            c = adventure.getText().toString().trim() + ", ";
        }
        else{
            c ="";
        }

        if(rolePlay.isChecked())
        {
            d = rolePlay.getText().toString().trim() + ", ";
        }
        else{
            d = "";
        }

        if(simulation.isChecked())
        {
            e = simulation.getText().toString().trim() + ", ";
        }
        else{
            e = "";
        }

        if(strategy.isChecked())
        {
            f = strategy.getText().toString().trim() + ", ";
        }
        else{
            f = "";
        }

        if(sport.isChecked())
        {
            g = sport.getText().toString().trim() + ", ";
        }
        else{
            g = "";
        }

        if(puzzle.isChecked())
        {
            h = puzzle.getText().toString().trim() + ", ";
        }
        else{
            h = "";
        }


        if(idle.isChecked())
        {
            i = idle.getText().toString().trim();
        }
        else{
            i = "";
        }

        type_game = a + b + c + d + e + f + g + h + i;

        return type_game;
    }

    //combine the type platform check box text into a string
    private String typePlatform(){
        String type_platform = "";
        String a , b , c, d, e, f, g, h;


        if(pPlay5.isChecked())
        {
            a = pPlay5.getText().toString() + ", ";
        }
        else{
            a = "";
        }

        if(pPlay4.isChecked())
        {
            b = pPlay4.getText().toString() + ", ";
        }
        else{
            b = "";
        }

        if(pNS.isChecked())
        {
            c = pNS.getText().toString() + ", ";
        }
        else{
            c = "";
        }

        if(pN3DS.isChecked())
        {
            d = pN3DS.getText().toString() + ", ";
        }
        else{
            d = "";
        }

        if(pXbox.isChecked())
        {
            e = pXbox.getText().toString() + ", ";
        }
        else{
            e = "";
        }

        if(pPC.isChecked())
        {
            f = pPC.getText().toString() + ", ";
        }
        else{
            f = "";
        }

        if(pIOS.isChecked())
        {
            g = pIOS.getText().toString() + ", ";
        }
        else{
            g = "";
        }

        if(pAnd.isChecked())
        {
            h = pAnd.getText().toString().trim();
        }
        else{
            h = "";
        }

        type_platform = a + b + c + d + e + f + g + h;

        return type_platform;
    }

    //check if there is name, desc, date, company input is empty
    private void checkEmpty(){

        final String mName = name.getText().toString().trim();
        final String mDescription = description.getText().toString().trim();
        final String mDate = date.getText().toString().trim();
        final String mCompany = company.getText().toString().trim();

        if(mName.isEmpty()){
            name.setError("Full Game name is required!");
            name.requestFocus();
            return;
        }

        if(mDescription.isEmpty()){
            description.setError("Description is required!");
            description.requestFocus();
            return;
        }

        if(mDate.isEmpty()){
            date.setError("Date for game publish is required!");
            date.requestFocus();
            return;
        }

        if(mCompany.isEmpty()){
            company.setError("Full name is required!");
            company.requestFocus();
            return;
        }

    }

    //function for choosing img (manifest)
    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //display the img choose with img
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(img);
        }
    }


    //to get file extension from image
    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    //update the game info
    private void FileUploader() {
        if(imageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(imageUri));

            mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    m_type_game = typeGame();
                    m_type_platform = typePlatform();

                    //check if type game is empty, if empty it will assign original type game string else it will assign updated type game string
                    if(m_type_game == ""){
                        m_type_game = pType;
                    }

                    //check if type platform is empty, if empty it will assign original type platform  string else it will assign updated type platform  string
                    if(m_type_platform == ""){
                        m_type_platform = pPlatform;
                    }


                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUrl = uri;
                            Insert insert = new Insert(name.getText().toString().trim(), description.getText().toString().trim(), date.getText().toString().trim(),
                                    company.getText().toString().trim(), m_type_game, m_type_platform, downloadUrl.toString());

                            //insert data to firebase realtime database
                            mDatabaseRef.child(key).setValue(insert);
                        }
                    });

                    //for progress bar
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    }, 3000);


                    Toast.makeText(EditGameInfo.this, "Update successful !" , Toast.LENGTH_SHORT).show();

                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditGameInfo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 *snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            m_type_game = typeGame();
            m_type_platform = typePlatform();

            //check if type game is empty, if empty it will assign original type game string else it will assign updated type game string
            if(m_type_game == ""){
                m_type_game = pType;
            }

            //check if type platform is empty, if empty it will assign original type platform  string else it will assign updated type platform  string
            if(m_type_platform == ""){
                m_type_platform = pPlatform;
            }

            Insert insert = new Insert(name.getText().toString().trim(), description.getText().toString().trim(), date.getText().toString().trim(),
                    company.getText().toString().trim(), m_type_game, m_type_platform, pImgUrl);

            //insert data to firebase realtime database
            mDatabaseRef.child(key).setValue(insert);

            Toast.makeText(EditGameInfo.this, "Update successful !" , Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

