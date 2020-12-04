package android.bignerdranch.gameintro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class GameInfoDetail extends AppCompatActivity {

    private TextView name, name2, company, date2, type, platform, desc;
    private ImageView imageView;
    private ImageButton send;
    private EditText comment;
    private String uName;
    private ImageButton like;
    private TextView tv_like;
    private String uid;
    private int n;
    private boolean likeCheck = false;

    private String key, pGame_name, pDescription, pDate, pCompany, pType, pPlatform, pImgUrl;
    String temp1, temp2;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference userRef, likeRef, bookmarkRef;

    RecyclerView mRecyclerViewComment;
    CommentAdapter mCommentAdapter;

    List<Comment> mComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info_detail);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //connect to the layout in activity game info detail xml
        name = (TextView) findViewById(R.id.subtitle_on_appbarGameInfo);
        name2 = (TextView) findViewById(R.id.textView_name);
        company = (TextView) findViewById(R.id.textView_company);
        date2 = (TextView) findViewById(R.id.textView_date);
        type = (TextView) findViewById(R.id.textView_type);
        platform = (TextView) findViewById(R.id.textView_platform);
        desc = (TextView) findViewById(R.id.textView_des);
        imageView = (ImageView) findViewById(R.id.backdropGameInfo);

        send = (ImageButton) findViewById(R.id.imageButton_send);
        comment = (EditText) findViewById(R.id.editText_comment);

        like = (ImageButton) findViewById(R.id.imageButton_like);
        tv_like = (TextView) findViewById(R.id.textView_like);


        mRecyclerViewComment = (RecyclerView) findViewById(R.id.recyclerViewGameComment);
        mRecyclerViewComment.setHasFixedSize(true);
        mRecyclerViewComment.setLayoutManager(new LinearLayoutManager(GameInfoDetail.this));
        mCommentAdapter = new CommentAdapter(this, mComments);
        mCommentAdapter.notifyDataSetChanged();
        mRecyclerViewComment.setAdapter(mCommentAdapter);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        likeRef = FirebaseDatabase.getInstance().getReference("likes");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        bookmarkRef = FirebaseDatabase.getInstance().getReference("Bookmark");

        if(mFirebaseUser != null) {
            uid = mFirebaseUser.getUid();

            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if (userProfile != null) {
                        uName = userProfile.fullName;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(GameInfoDetail.this, "Something when wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }


        //assign all data get from game info fragment to string variable
        key = getIntent().getStringExtra("keyGameInfo");
        pGame_name = getIntent().getStringExtra("nameGameInfo");
        pDescription = getIntent().getStringExtra("descGameInfo");
        pDate = getIntent().getStringExtra("dateGameInfo");
        pCompany = getIntent().getStringExtra("companyGameInfo");
        pType = getIntent().getStringExtra("typeGameInfo");
        pPlatform = getIntent().getStringExtra("platformGameInfo");
        pImgUrl = getIntent().getStringExtra("imgUrlGameInfo");

        Insert insert = new Insert(pGame_name, pDescription, pDate, pCompany, pType, pPlatform, pImgUrl);

        temp1 = pType.replaceAll(", $", "");
        temp2 = pPlatform.replaceAll(", $", "");

        //set the data get from game info fragment to activity game info detail xml
        name.setText(pGame_name);
        name2.setText(pGame_name);
        company.setText(pCompany);
        date2.setText(pDate);
        type.setText(temp1);
        platform.setText(temp2);
        desc.setText(pDescription);
        Picasso.get().load(pImgUrl).into(imageView);



        //add Comment click listener
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mFirebaseUser != null) {
                    String check = comment.getText().toString();
                    if (!check.isEmpty()) {
                        send.setVisibility(View.INVISIBLE);
                        DatabaseReference commentReference = mFirebaseDatabase.getReference("Comment").child(key).push();
                        String comment_content = comment.getText().toString();

                        Comment mComment = new Comment(comment_content, uid, uName, getCurTime());

                        commentReference.setValue(mComment).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(GameInfoDetail.this, "Comment added", Toast.LENGTH_SHORT).show();
                                comment.setText("");
                                send.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GameInfoDetail.this, "Fail to add comment : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        comment.setError("Enter a comment !");
                        comment.requestFocus();
                        return;
                    }
                }
                else{
                    Toast.makeText(GameInfoDetail.this,"Please log in first to be able to comment.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        String sLike = " likes";
        if(mFirebaseUser != null) {
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(key).hasChild(uid)) {
                        n = (int) dataSnapshot.child(key).getChildrenCount();
                        like.setImageResource(R.drawable.like);
                        tv_like.setText(Integer.toString(n) + sLike);
                    } else {
                        n = (int) dataSnapshot.child(key).getChildrenCount();
                        like.setImageResource(R.drawable.no_like);
                        tv_like.setText(Integer.toString(n) + sLike);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    n = (int) dataSnapshot.child(key).getChildrenCount();
                    like.setImageResource(R.drawable.no_like);
                    tv_like.setText(Integer.toString(n) + sLike);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeCheck = true;

                if(mFirebaseUser != null) {
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (likeCheck == true) {

                                if (dataSnapshot.child(key).hasChild(uid)) {
                                    likeRef.child(key).child(uid).removeValue();
                                    likeCheck = false;
                                } else {
                                    likeRef.child(key).child(uid).setValue(true);
                                    likeCheck = false;
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(GameInfoDetail.this,"Please log in first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //init recycler view comment
        iniRecyclerViewComment();


    }

    private void iniRecyclerViewComment() {

        mRecyclerViewComment.setLayoutManager(new LinearLayoutManager(GameInfoDetail.this));

        DatabaseReference commentRef = mFirebaseDatabase.getReference("Comment").child(key);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mComments = new ArrayList<>();
                for (DataSnapshot CommentSnapshot : dataSnapshot.getChildren()){
                    Comment comment  = CommentSnapshot.getValue(Comment.class);
                    mComments.add(comment);
                }

                mCommentAdapter = new CommentAdapter(GameInfoDetail.this, mComments);
                mRecyclerViewComment.setAdapter(mCommentAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    public String getCurTime() {
        String fromTimeZone = "GMT+8";
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        Date date = new Date();
        format.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        String chinaDate = format.format(date);
        return chinaDate;
    }

}
