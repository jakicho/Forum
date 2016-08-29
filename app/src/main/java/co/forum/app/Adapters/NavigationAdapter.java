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

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.NavigationHolder>{

    private LayoutInflater inflater;

    private Context context;

    public List<ParseTagsSubscription> user_subscriptions = Collections.emptyList();

    public List<ParseUser> authorsList = Collections.emptyList();

    private int page;

    public int selected_row_index;

    private boolean no_subscription_yet = false;


    public NavigationAdapter(Context context, List<ParseTagsSubscription> user_subscriptions, List<ParseUser> authorsList, int page) {

        inflater = LayoutInflater.from(context);

        this.context = context;

        this.user_subscriptions = user_subscriptions;

        this.authorsList = authorsList;

        this.page = page;

        if(page == 0) {

            if(user_subscriptions.isEmpty()) no_subscription_yet = true;

            this.user_subscriptions.add(0, new ParseTagsSubscription());

            if(no_subscription_yet) this.user_subscriptions.add(1, new ParseTagsSubscription()); // explainer indicator

            selected_row_index = 0;

        } else selected_row_index = -1;

    }


    @Override
    public NavigationAdapter.NavigationHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if(page == 0 || page == 1) view = inflater.inflate(R.layout.item_subscription, parent, false);

        else view = inflater.inflate(R.layout.item_author_quote, parent, false);

        NavigationHolder holder = new NavigationHolder(view);

        return holder;
    }




    @Override
    public void onBindViewHolder(final NavigationAdapter.NavigationHolder holder, final int position) {

        populate_row(holder, position);

        if(position == selected_row_index) holder.row.setBackground(ContextCompat.getDrawable(context, R.drawable.button_nav_selected));

        else holder.row.setBackground(ContextCompat.getDrawable(context, R.drawable.button_sl_nav_menu));


        // ouverture de stack

        if(page == 0) {

            if (position == 0 || !no_subscription_yet ) {

                holder.row.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        select_subscription(holder.row, position);
                    }
                });

                holder.tags_row.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        select_subscription(holder.row, position);
                    }
                });

            }

        } else if(page == 1) {

            holder.row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    select_subscription(holder.row, position);
                }
            });

            holder.tags_row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    select_subscription(holder.row, position);
                }
            });

        } else if(page == 2) {

            holder.row.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    select_subscription(holder.row, position);
                }
            });

        }


        // hide show btn delete

        if(page == 0) {

            holder.btn_row_icon.setOnClickListener(new View.OnClickListener() {

                boolean open = false;

                @Override
                public void onClick(View v) {

                    /*
                    - position == 0 => null
                    - position == 1 & no subscripition => null
                     */

                    if (!(position == 0 || (position == 1 && no_subscription_yet ))) {

                        if(open) {

                            holder.btn_delete_row.animate()
                                    .translationY(-150)
                                    .setInterpolator(new AccelerateInterpolator(20f))
                                    .setInterpolator(new DecelerateInterpolator(1f))
                                    .start();

                            open = false;

                        } else {

                            holder.btn_delete_row.animate()
                                    .translationY(0)
                                    .setInterpolator(new AccelerateInterpolator(20f))
                                    .setInterpolator(new DecelerateInterpolator(1f))
                                    .start();

                            open = true;
                        }

                    }

                    /*
                    if(position != 0 && (position == 1 && no_subscription_yet ) ) {

                        if(open) {

                            holder.btn_delete_row.animate()
                                    .translationY(-150)
                                    .setInterpolator(new AccelerateInterpolator(20f))
                                    .setInterpolator(new DecelerateInterpolator(1f))
                                    .start();

                            open = false;

                        } else {

                            holder.btn_delete_row.animate()
                                    .translationY(0)
                                    .setInterpolator(new AccelerateInterpolator(20f))
                                    .setInterpolator(new DecelerateInterpolator(1f))
                                    .start();

                            open = true;
                        }
                    }
                    */
                }
            });
        }
    }


    @Override
    public int getItemCount() {

        if(page == 0 || page == 1) return user_subscriptions.size();

        else return authorsList.size();

    }


    private void populate_row(final NavigationAdapter.NavigationHolder holder, final int position) {

        holder.btn_delete_row.setTranslationY(-150);

        if(page == 0) {

            if(position == 0) {

                holder.tags_row.setVisibility(View.GONE);

                holder.global_feed.setVisibility(View.VISIBLE);

                holder.row_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_earth_white_48dp));

                holder.btn_row_icon.setOnClickListener(null);

                holder.btn_row_icon.setBackground(null);

                holder.tv_nb_followers.setText(context.getResources().getString(R.string.cards_chronological_order));

            } else if(position == 1 && no_subscription_yet) {

                holder.tags_row.setVisibility(View.GONE);

                holder.global_feed.setVisibility(View.VISIBLE);

                holder.tv_nb_followers.setVisibility(View.GONE);

                holder.row_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pound_white_48dp));

                holder.btn_row_icon.setOnClickListener(null);

                holder.btn_row_icon.setBackground(null);

                holder.global_feed.setText(context.getResources().getString(R.string.click_to_add));

                holder.global_feed.setTextColor(ContextCompat.getColor(context, R.color.colorProfil_Tabs_Off));


            } else {

                holder.tags_row.setVisibility(View.VISIBLE);

                holder.global_feed.setVisibility(View.GONE);

                holder.row_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pound_white_48dp));

                holder.btn_row_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.button_sl_nav_menu));

                Tags.populateTagsRowDark(context, holder.tags_row, (ArrayList<String>) user_subscriptions.get(position).getTags());

                holder.tv_nb_followers.setVisibility(View.VISIBLE);

                if( user_subscriptions.get(position).getNb_followers() == 1)

                    holder.tv_nb_followers.setText(user_subscriptions.get(position).getNb_followers() + " follower");

                else holder.tv_nb_followers.setText(user_subscriptions.get(position).getNb_followers() + " followers");

            }

        } else if(page == 1) {

            holder.global_feed.setVisibility(View.GONE);

            holder.row_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pound_white_48dp));

            holder.tags_row.setVisibility(View.VISIBLE);

            Tags.populateTagsRowDark(context, holder.tags_row, (ArrayList<String>) user_subscriptions.get(position).getTags());

            holder.tv_nb_followers.setText(user_subscriptions.get(position).get_description());

            holder.btn_row_icon.setOnClickListener(null);

            holder.btn_row_icon.setBackground(null);

        } else if(page == 2) {

            final String fullname = authorsList.get(position).getString("name") + " " + authorsList.get(position).getString("surname");

            final String profil_url = authorsList.get(position).getParseFile("profil_pict").getUrl();

            holder.tags_row.setVisibility(View.GONE);

            holder.global_feed.setVisibility(View.VISIBLE);

            holder.global_feed.setText(fullname);

            if(!profil_url.equals("NULL")) Picasso.with(MainActivity.context).load(profil_url).into(holder.row_icon2);

            else holder.row_icon.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

            holder.row_icon2.setBorderColor(ContextCompat.getColor(MainActivity.context, R.color.curated));

            holder.row_icon2.setBorderWidth(5);

            holder.global_feed.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.curated));

            holder.tv_nb_followers.setText(authorsList.get(position).getString("curated_tagline"));

            holder.btn_row_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SubActivity.openUserProfil(
                            context,
                            fullname,
                            authorsList.get(position).getObjectId(),
                            profil_url,
                            true);

                }
            });
        }
    }

    public static void deselect_other_row(int page) {

        if(page == 0) {

            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.notifyDataSetChanged();
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.notifyDataSetChanged();

            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.selected_row_index = -1;
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.selected_row_index = -1;

        } else if(page == 1) {

            ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.notifyDataSetChanged();
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.notifyDataSetChanged();

            ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.selected_row_index = -1;
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.selected_row_index = -1;

        } else if(page == 2) {

            ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.notifyDataSetChanged();
            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.notifyDataSetChanged();

            ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.selected_row_index = -1;
            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.selected_row_index = -1;

        } else if(page == -1) {

            if( ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter != null )
                ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.notifyDataSetChanged();

            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.notifyDataSetChanged();
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.notifyDataSetChanged();

            ((MainActivity)MainActivity.context).navigationSelector.page00.navigationAdapter.selected_row_index = -1;
            ((MainActivity)MainActivity.context).navigationSelector.page01.navigationAdapter.selected_row_index = -1;
            ((MainActivity)MainActivity.context).navigationSelector.page02.navigationAdapter.selected_row_index = -1;

        }
    }

    private void close_drawer_hide_icon(int delay, final boolean hide_icon) {

        Handler handler0 = new Handler();
        handler0.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(hide_icon) {

                    MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

                    MainActivity.action_rankResult.setVisible(false);

                }

                ((MainActivity) MainActivity.context).navigationSelector.mDrawerLayout.closeDrawer(Gravity.LEFT);

            }
        }, delay);

    }

    private void empty_deck_before_reload() {

        ((MainActivity)MainActivity.context).deckFragment.cardStackAdapter.offset = 0;

        ((MainActivity)MainActivity.context).deckFragment.card_stack_view.removeAllViews();

        ((MainActivity)MainActivity.context).deckFragment.cardDataset.clear();

    }

    private void reset_search_engine(int delay) {

        Handler handler2 = new Handler();

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {

                ((MainActivity)MainActivity.context).deckFragment.searchFragment.remove_TagFromHeader(null, null);

                ((MainActivity)MainActivity.context).deckFragment.searchFragment.reset_SearchEngine();

            }
        }, delay);

    }

    private void select_subscription(LinearLayout row, final int position) {

        notifyDataSetChanged();

        deselect_other_row(page);

        row.setBackground(ContextCompat.getDrawable(context, R.drawable.button_nav_selected));

        selected_row_index = position;

        if(page == 0) {

            if(position == 0) {

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        empty_deck_before_reload();

                        ((MainActivity) MainActivity.context).deckFragment.reset_toGlobalDeck();

                    }
                }, 600);

                ((MainActivity) MainActivity.context).deckFragment.reload_home.setVisibility(View.GONE);

                close_drawer_hide_icon(300, true);

                reset_search_engine(900);

            } else {

                ((MainActivity) MainActivity.context).deckFragment.reload_home.setVisibility(View.GONE);

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        empty_deck_before_reload();

                        //((MainActivity) MainActivity.context).deckFragment.reset_toGlobalDeck();

                        ParseRequest.get_subscriptionCards(context, (ArrayList<String>) user_subscriptions.get(position).getTags(), "createdAt", 0);

                        ((MainActivity) MainActivity.context).deckFragment.loading_deck.setVisibility(View.VISIBLE);

                        ((MainActivity) MainActivity.context).deckFragment.order = DeckFrag.TIMESTAMP;

                        ((MainActivity) MainActivity.context).deckFragment.loaderDeck.setVisibility(View.GONE);

                        MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

                        MainActivity.action_rankResult.setVisible(true);

                    }
                }, 600);

                close_drawer_hide_icon(300, true);

                reset_search_engine(900);

            }

        } else if(page == 1) {

            ((MainActivity) MainActivity.context).deckFragment.reload_home.setVisibility(View.GONE);

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    empty_deck_before_reload();

                    //((MainActivity) MainActivity.context).deckFragment.reset_toGlobalDeck();

                    ParseRequest.get_subscriptionCards(context, (ArrayList<String>) user_subscriptions.get(position).getTags(), "createdAt", 0);

                    ((MainActivity) MainActivity.context).deckFragment.loading_deck.setVisibility(View.VISIBLE);

                    ((MainActivity) MainActivity.context).deckFragment.order = DeckFrag.TIMESTAMP;

                    ((MainActivity) MainActivity.context).deckFragment.loaderDeck.setVisibility(View.GONE);

                    MainActivity.action_rankResult.setIcon(R.drawable.ic_rank_recent);

                    MainActivity.action_rankResult.setVisible(true);

                }
            }, 600);

            close_drawer_hide_icon(300, true);

            reset_search_engine(900);


        } else if(page == 2) {

            final String UserID = authorsList.get(position).getObjectId();

            empty_deck_before_reload();

            ((MainActivity) MainActivity.context).deckFragment.display_authorNavCards(context, UserID);

            close_drawer_hide_icon(300, true);

            reset_search_engine(900);

        }
    }


    public void add_subscription(ParseTagsSubscription subscription) {

        if(no_subscription_yet) {

            no_subscription_yet = false;

            user_subscriptions.remove(1);

            user_subscriptions.add(1, subscription);

            notifyDataSetChanged();

        } else {

            user_subscriptions.add(1, subscription);

            notifyItemInserted(1);

            notifyItemRangeChanged(1, getItemCount());

        }
    }


    class NavigationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.row) LinearLayout row;
        @Bind(R.id.btn_delete_row) LinearLayout btn_delete_row;
        @Bind(R.id.global_feed)TextView global_feed;
        @Bind(R.id.btn_row_icon)FrameLayout btn_row_icon;
        @Bind(R.id.tags_follow_row) RowLayout tags_row;
        @Bind(R.id.tv_nb_followers) TextView tv_nb_followers;

        ImageView row_icon;
        CircleImageView row_icon2;

        public NavigationHolder(View itemView) {

            super(itemView);

            ButterKnife.bind(this, itemView);

            if(page == 0 || page == 1) row_icon = (ImageView) itemView.findViewById(R.id.row_icon);

            else if(page == 2 ) row_icon2 = (CircleImageView) itemView.findViewById(R.id.row_icon);

            global_feed.setTypeface(MainActivity.tfSemiBold);

            tv_nb_followers.setTypeface(MainActivity.tf);

            if(page == 0 ) btn_delete_row.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            if(v == btn_delete_row) {

                Message.message(context, context.getResources().getString(R.string.subscription_deleted));

                ParseTagsSubscription currentSelection;

                if(selected_row_index == -1)  currentSelection = user_subscriptions.get(0);

                else currentSelection = user_subscriptions.get(selected_row_index);

                //if(getAdapterPosition() == selected_row_index) selected_row_index = -1;

                ParseRequest.delete_subscription(context, user_subscriptions.get(getAdapterPosition()),

                        (ParseTagsNbFollowers) user_subscriptions.get(getAdapterPosition()).get(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT));

                user_subscriptions.remove(getAdapterPosition());

                notifyItemRemoved(getAdapterPosition());

                notifyItemRangeChanged(getAdapterPosition(), getItemCount());

                selected_row_index = user_subscriptions.indexOf(currentSelection);

            }
        }
    }
}
