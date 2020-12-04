package android.bignerdranch.gameintro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent,false);
        return new CommentViewHolder(v);
    }

    //set the text for comment
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.username.setText(mComments.get(position).getUsername());
        holder.userComment.setText(mComments.get(position).getContent());
        holder.date.setText(mComments.get(position).getTimestamp());
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView username, userComment, date;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.textView_name);
            userComment = itemView.findViewById(R.id.textView_comment);
            date = itemView.findViewById(R.id.textView_comment_date);

        }
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String date = df.format("hh:mm aa",calendar).toString();

        return date;
    }


}
