package co.forum.app.SubActivity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.SearchFollowAdapter;
import co.forum.app.Data.ParseRequest;
import co.forum.app.Data.ParseTagsNbFollowers;
import co.forum.app.R;
import co.forum.app.tools.HorizontalScrollView2;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.TagView;


public class SearchFollowFrag extends Fragment {

    @Bind(R.id.tv_nbFollowers) public  TextView tv_nbFollowers;
    @Bind(R.id.btn_add_subscription_header) FrameLayout btn_add_subscription_header;

    @Bind(R.id.followed_recycler_view)
    RecyclerView followed_recycler_view;
    @Bind(R.id.tags_scroller)
    HorizontalScrollView2 tags_scroller;
    @Bind(R.id.tags_follow_row)
    RowLayout view_tags_follow_row;


    private SearchFollowAdapter followAdapter;



    public ArrayList<String> tags_in_header;

    private List<ParseTagsNbFollowers> most_followed;

    private List<ParseTagsNbFollowers> current_list = new ArrayList<>();


    public SearchFollowFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_follow, container, false);

        ButterKnife.bind(this, view);

        tags_in_header = new ArrayList<>();

        ParseRequest.get_mostFollowedTags();

        btn_add_subscription_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseRequest.insert_tags_subscription(getContext(), tags_in_header);
            }
        });

        return view;
    }



    public void add_TagInHeader(final String tag, TagView tagView) {

        if (!tags_in_header.contains(tag)) {

            tags_in_header.add(tag);

            view_tags_follow_row.addView(tagView);

            tags_scroller.fullScroll(View.FOCUS_RIGHT);

            btn_add_subscription_header.setVisibility(View.VISIBLE);

            get_suggestions("");

            /*
            if((((SubActivity)SubActivity.context).searchBar.getQuery().toString()).equals("") ) {// rajout par tag depuis click

                get_suggestions("");

            } else ((SubActivity)SubActivity.context).searchBar.setQuery("", false); // rajout par tag depuis search

            */
            ParseRequest.get_nb_followers(tags_in_header);



            //get_suggestions("");

            //deactivate_btn_follow();

            //load_related_tags(true);

        } else Message.message(getContext(), getResources().getString(R.string.tag_already_selected));
    }

    public void remove_TagFromHeader(final String tag, TagView tagView) {

        if (tag == null && tagView == null) {

            Message.message(getContext(), "lol ?");

            //tags_in_header.clear();

            //view_tags_follow_row.removeAllViews();

        } else {

            tags_in_header.remove(tag);

            view_tags_follow_row.removeView(tagView);

            if(tags_in_header.isEmpty()) {

                tv_nbFollowers.setText("");

                btn_add_subscription_header.setVisibility(View.INVISIBLE);
            }

            else ParseRequest.get_nb_followers(tags_in_header);

            ((SubActivity)SubActivity.context).searchBar.setQuery("", false);

            get_suggestions("");

        }
    }


    public void get_suggestions(String search_tag) {

        if(tags_in_header.isEmpty() && search_tag.isEmpty()) {

            current_list.clear();

            current_list.addAll(most_followed);
            followAdapter.notifyDataSetChanged();

            //Message.message(getContext(), "wassup ? " + current_list.size());


        } else {

            //Message.message(getContext(), "lenght : " + tags_in_header.size());

            ParseRequest.get_follow_suggestions(tags_in_header, search_tag);

        }
    }




    public void display_Most_Followed_Tags(List<ParseTagsNbFollowers> list) {

        followed_recycler_view.setVisibility(View.VISIBLE);

        most_followed = list;

        current_list.addAll(most_followed);

        followAdapter = new SearchFollowAdapter(getActivity(), current_list);

        followed_recycler_view.setAdapter(followAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        followed_recycler_view.setLayoutManager(mLayoutManager);

    }


    public void display_Suggested_Tags(List<ParseTagsNbFollowers> list, ArrayList<String> tagsHeader, String tagTyping) {

        ArrayList<ParseTagsNbFollowers> reducedList = new ArrayList<>();

        //Message.message(getContext(), "list size " + list.size());

        for (ParseTagsNbFollowers row : list) {

            ArrayList<String> toCheck = new ArrayList<>();

            toCheck.addAll(row.getTags());

            toCheck.removeAll(tagsHeader);

            for (String string : toCheck)

                if (string.startsWith(tagTyping)) {

                    //toCheck.addAll(tagsHeader);

                    if(!reducedList.contains(row)) reducedList.add(row);
                }
        }

        //Message.message(getContext(), "ya ?");

        current_list.clear();

        current_list.addAll(reducedList);

        followAdapter.notifyDataSetChanged();

    }
}
