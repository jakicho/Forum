package co.forum.app.Adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.Data.ParseTagsNbFollowers;
import co.forum.app.Data.ParseTagsSubscription;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.DeckFrag;
import co.forum.app.R;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFollowAdapter extends RecyclerView.Adapter<SearchFollowAdapter.FollowHolder>{

    private LayoutInflater inflater;

    private Context context;

    public List<ParseTagsNbFollowers> tagsNbFollowersList = Collections.emptyList();


    public SearchFollowAdapter(Context context, List<ParseTagsNbFollowers> tagsNbFollowersList) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.tagsNbFollowersList = tagsNbFollowersList;

    }


    @Override
    public FollowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        view = inflater.inflate(R.layout.item_follow_row, parent, false);

        FollowHolder holder = new FollowHolder(view);

        return holder;
    }




    @Override
    public void onBindViewHolder(final FollowHolder holder, final int position) {

        populate_row(holder, position);

    }


    @Override
    public int getItemCount() {

        return tagsNbFollowersList.size();

    }


    private void populate_row(final SearchFollowAdapter.FollowHolder holder, final int position) {

        holder.tags_follow_row.setVisibility(View.VISIBLE);

        Tags.populateTagsRowFollow(context, holder.tags_follow_row, (ArrayList<String>) tagsNbFollowersList.get(position).getTags());

        if(tagsNbFollowersList.get(position).get_nb_followers() == 1)

            holder.tv_nb_followers.setText(tagsNbFollowersList.get(position).get_nb_followers() + " follower");

        else holder.tv_nb_followers.setText(tagsNbFollowersList.get(position).get_nb_followers() + " followers");

        holder.btn_add_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseRequest.insert_tags_subscription(context, (ArrayList<String>) tagsNbFollowersList.get(position).getTags());

            }
        });
    }




    public void add_subscription(ParseTagsNbFollowers tagsFollowers) {

        tagsNbFollowersList.add(1, tagsFollowers);

        notifyItemInserted(1);

        notifyItemRangeChanged(1, getItemCount());

    }


    class FollowHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.row) LinearLayout row;
        @Bind(R.id.btn_delete_row) LinearLayout btn_delete_row;

        @Bind(R.id.btn_add_subscription) FrameLayout btn_add_subscription;
        @Bind(R.id.tags_follow_row) RowLayout tags_follow_row;
        @Bind(R.id.tv_nb_followers) TextView tv_nb_followers;

        public FollowHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            tv_nb_followers.setTypeface(MainActivity.tf);

            btn_delete_row.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if(v == btn_delete_row) {



            }
        }
    }
}
