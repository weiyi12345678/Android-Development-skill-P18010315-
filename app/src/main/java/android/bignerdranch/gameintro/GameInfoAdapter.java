package android.bignerdranch.gameintro;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class GameInfoAdapter extends RecyclerView.Adapter<GameInfoAdapter.GameInfoViewHolder> {

    private Context mContext;
    private List<Insert> mInserts;
    private OnGameInfoListener mOnGameInfoListener;
    private String uid;

    int likeCount;
    private boolean likeCheck = false;

    public GameInfoAdapter(Context mContext, List<Insert> inserts){
        this.mContext = mContext;
        mInserts = inserts;
    }

    @NonNull
    @Override
    public GameInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.game_info_item, parent,false);
        return new GameInfoViewHolder(v, mOnGameInfoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GameInfoViewHolder holder, int position) {
        Insert insertCurrent = mInserts.get(position);

        holder.mName.setText(insertCurrent.getName());
        holder.mDatePublish.setText(insertCurrent.getDate());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(mContext)
                .load(insertCurrent.getImageUrl())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.mImageView);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference("likes");

        String like = " likes";
        if (user != null) {
            uid = user.getUid();
            //for checking the like status on how many people like and set the image of like
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(insertCurrent.getKey()).hasChild(uid)) {
                        likeCount = (int) dataSnapshot.child(insertCurrent.getKey()).getChildrenCount();
                        holder.like.setImageResource(R.drawable.like);
                        holder.mLikeCount.setText(Integer.toString(likeCount) + like);
                    } else {
                        likeCount = (int) dataSnapshot.child(insertCurrent.getKey()).getChildrenCount();
                        holder.like.setImageResource(R.drawable.no_like);
                        holder.mLikeCount.setText(Integer.toString(likeCount) + like);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            likeReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        likeCount = (int) dataSnapshot.child(insertCurrent.getKey()).getChildrenCount();
                        holder.like.setImageResource(R.drawable.no_like);
                        holder.mLikeCount.setText(Integer.toString(likeCount) + like);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null) {
                    likeCheck = true;

                    likeReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (likeCheck == true) {

                                if (dataSnapshot.child(insertCurrent.getKey()).hasChild(uid)) {
                                    likeReference.child(insertCurrent.getKey()).child(uid).removeValue();
                                    likeCheck = false;
                                } else {
                                    likeReference.child(insertCurrent.getKey()).child(uid).setValue(true);
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
                    Toast.makeText(v.getContext(),"Please log in first.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext.getApplicationContext(), GameInfoDetail.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("keyGameInfo", insertCurrent.getKey());
                intent.putExtra("nameGameInfo", insertCurrent.getName());
                intent.putExtra("descGameInfo", insertCurrent.getDescription());
                intent.putExtra("dateGameInfo", insertCurrent.getDate());
                intent.putExtra("companyGameInfo", insertCurrent.getCompany());
                intent.putExtra("typeGameInfo", insertCurrent.gettGame());
                intent.putExtra("platformGameInfo", insertCurrent.gettPlatform());
                intent.putExtra("imgUrlGameInfo", insertCurrent.getImageUrl());
                mContext.getApplicationContext().startActivity(intent);

            }
        });



    }
    @Override
    public int getItemCount() {
        return mInserts.size();
    }


    public class GameInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mImageView;
        public ProgressBar mProgressBar;
        public TextView mDatePublish, mName, mLikeCount;
        public ImageButton like, comment;
        OnGameInfoListener onGameInfoListener;

        public GameInfoViewHolder(@NonNull View itemView, OnGameInfoListener onGameInfoListener) {
            super(itemView);


            mImageView = itemView.findViewById(R.id.imgGameInfo);
            mProgressBar = itemView.findViewById(R.id.progress_load_game_photo);
            mDatePublish = itemView.findViewById(R.id.gamePublish);
            mName = itemView.findViewById(R.id.game_name);


            like = (ImageButton) itemView.findViewById(R.id.imageButton_like);
            mLikeCount = (TextView) itemView.findViewById(R.id.textView_likeInfo);
            comment = (ImageButton) itemView.findViewById(R.id.imageButton_comment);
            this.onGameInfoListener = onGameInfoListener;




            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mOnGameInfoListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mOnGameInfoListener.onDetailClick(position);
                }
            }
        }



    }

    public interface OnGameInfoListener{
        void onDetailClick(int position);
    }

    public void setOnGameInfoListener(OnGameInfoListener listener){
        mOnGameInfoListener = listener;
    }


}