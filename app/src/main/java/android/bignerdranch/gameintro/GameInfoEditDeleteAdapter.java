package android.bignerdranch.gameintro;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class GameInfoEditDeleteAdapter extends RecyclerView.Adapter<GameInfoEditDeleteAdapter.GameInfoEditDeleteViewHolder> {

    private Context mContext;
    private List<Insert> mInserts;
    private onItemCLickListener mListener;

    public GameInfoEditDeleteAdapter(Context context, List<Insert> inserts){
        mContext = context;
        mInserts = inserts;
    }

    @NonNull
    @Override
    public GameInfoEditDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.game_info_edit_delete_item, parent,false);
        return new GameInfoEditDeleteViewHolder(v);
    }

    //set the data that need to be display
    @Override
    public void onBindViewHolder(@NonNull GameInfoEditDeleteAdapter.GameInfoEditDeleteViewHolder holder, int position) {
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



    }

    @Override
    public int getItemCount() {
        return mInserts.size();
    }

    //link the variable with the component of game_info_edit_delete_item layout xml
    public class GameInfoEditDeleteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public ImageView mImageView;
        public ProgressBar mProgressBar;
        public TextView mDatePublish, mName;
        public ImageButton like, comment, bookmark;

        public GameInfoEditDeleteViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imgGameInfo);
            mProgressBar = itemView.findViewById(R.id.progress_load_game_photo);
            mDatePublish = itemView.findViewById(R.id.gamePublish);
            mName = itemView.findViewById(R.id.game_name);


            like = (ImageButton) itemView.findViewById(R.id.imageButton_like);
            comment = (ImageButton) itemView.findViewById(R.id.imageButton_comment);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v){
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        //for long click edit delete
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem edit = menu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            edit.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);

        }

        //for getting the position of which item is click
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch(item.getItemId()){

                        case 1:
                            mListener.onEditClick(position);
                            return true;

                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    //for item click listener
    public interface onItemCLickListener {
        void onItemClick(int position);

        void onEditClick(int position);

        void onDeleteClick(int position);

    }

    public void setOnItemClickListener(onItemCLickListener listener){
        mListener = listener;
    }
}
