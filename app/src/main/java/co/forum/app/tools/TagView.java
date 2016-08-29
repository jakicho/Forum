package co.forum.app.tools;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SubActivity.SubActivity;


public class TagView extends LinearLayout {

    // TODO A REFAIRE C'EST LE BORDEL LA


    public static String COLOR_NORMAL = "normal";
    public static String COLOR_SELECTED = "selected";
    public static String COLOR_DARK = "dark";

    public static boolean HEADER_TAG = true;
    public static boolean SUGGESTED_TAG = false;

    private boolean type;

    private String color_type;

    @Bind(R.id.tv_tag) TextView tv_tag;
    @Bind(R.id.tv_count) TextView tv_count;

    // tag subscribe panel()
    public TagView(final Context context,final String tag, String color_type) {
        super(context);

        if(color_type.equals(COLOR_DARK)) init(tag, color_type);

        else if(color_type.equals(COLOR_NORMAL)) {

            // tag pour le follow search

            inflate(getContext(), R.layout.tag_view, this);

            ButterKnife.bind(this);

            tv_tag.setTag("#" + tag);
            tv_tag.setTextSize(18f);
            tv_tag.setText("#" + tag);

            tv_count.setVisibility(GONE);

            tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off2));

            this.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.tag_bg4));

            this.setOnClickListener(new View.OnClickListener() {

                private void add_tag_to_header_subscribe() {

                    TagView tagView = new TagView(context, tag, TagView.HEADER_TAG, true);

                    ((SubActivity) SubActivity.context).followFragment.add_TagInHeader(tag, tagView);

                }

                @Override
                public void onClick(View v) {

                    add_tag_to_header_subscribe();

                }

            });

        }
    }


    // tag sans compteur pour la page follow (header)
    public TagView(Context context, String tag, boolean type, boolean forFollowPageOnly) {
        super(context);
        init(tag);
    }


    // tag sans compteur
    public TagView(Context context, String tag, boolean type) {
        super(context);
        init(tag, type);
    }

    // tag avec compteur
    public TagView(Context context, String tag, int count) {
        super(context);
        init(tag, count);
    }


    // tag pour editor new card + story
    public TagView(Context context, int type, String tag) {

        super(context);

        if(type<=5) init_for_editor(type, tag);

        else init_for_story(type, tag);
    }


    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void init(String tag, String color) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        if(color.equals(COLOR_NORMAL)) {

            tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off2));
            //this.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.tag_bg4));

        } else if (color.equals(COLOR_SELECTED)) {

            tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.colorAccent));
            //tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.tag_bg_selected_search));

        }

        if(color.equals(COLOR_DARK)) {

            if(tag.equals("FORUM")) {

                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_FORUM));

                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_activated_small_forum));

            } else tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_activated_small));

            tv_tag.setTag("#" + tag);
            tv_tag.setTextSize(16f);
            tv_tag.setText("#" + tag);
            tv_count.setVisibility(GONE);

            tv_tag.setPadding(13, 5, 13, 5);

        } else {

            tv_tag.setTag("#" + tag);
            tv_tag.setTextSize(13f);
            tv_tag.setText("#" + tag);
            tv_count.setVisibility(GONE);

            tv_tag.setPadding(13, 5, 13, 5);
        }



    }


    private void init_for_story(int tag_type, final String tag) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        this.type = HEADER_TAG;

        tv_tag.setTag("#" + tag);
        tv_tag.setTextSize(20f);
        tv_tag.setText("#" + tag);

        LayoutParams params = new AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.WRAP_CONTENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 4, 4, 4);
        tv_tag.setLayoutParams(params);

        tv_count.setVisibility(GONE);

        set_Layout_story(tag_type);

        final TagView view = this;

        this.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ((MainActivity)MainActivity.context).storyFragment.stories_appbar.setExpanded(false, true);

                ((MainActivity)MainActivity.context).storyFragment.load_Stories(tag);

            }
        });
    }

    private void init_for_editor(int tag_type, final String tag) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        this.type = HEADER_TAG;

        tv_tag.setTag("#" + tag);
        tv_tag.setTextSize(20f);
        tv_tag.setText("#" + tag);

        tv_count.setVisibility(GONE);

        set_Layout_editor(tag_type);

        final TagView view = this;

        this.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MainActivity.newContent_editor.addTagToContent(-1, tag);

                MainActivity.newContent_editor.et_Tags.requestFocus();

            }
        });
    }

    private void init(final String tag) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        this.type = HEADER_TAG;

        tv_tag.setTag("#" + tag);
        tv_tag.setTextSize(18f);
        tv_tag.setText("#" + tag);

        tv_count.setVisibility(GONE);

        set_Layout(type);

        // tags follow page header

        final TagView view = this;

        this.setOnClickListener(new View.OnClickListener() {

            private void remove_tag_from_header_subscribe() {

                ((SubActivity) SubActivity.context).followFragment.remove_TagFromHeader(tag, view);

            }

            @Override
            public void onClick(View v) {

                remove_tag_from_header_subscribe();

            }
        });
    }


    private void init(String tag, boolean type) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        this.type = type;

        tv_tag.setTag("#" + tag);
        tv_tag.setTextSize(18f);
        tv_tag.setText("#" + tag);

        tv_count.setVisibility(GONE);

        set_Layout(type);
        set_Tag_Click(type, this, tag);

    }


    private void init(String tag, int count) {

        inflate(getContext(), R.layout.tag_view, this);

        ButterKnife.bind(this);

        this.type = SUGGESTED_TAG;

        tv_tag.setText("#" + tag);
        tv_count.setText(String.valueOf(count));

        set_Layout(type);
        set_Tag_Click(type, this, tag);

    }


    private void set_Layout(boolean type) {

        if(type == SUGGESTED_TAG) {

            tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off2));
            this.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.tag_bg4));

        } else if(type == HEADER_TAG) {

            tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selected));
            tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.tag_bg_selected));
            tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_on));

        }
    }


    private void set_Layout_story(int type) {

        switch (type) {

            case 11:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector05));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_05));
                break;

            case 22:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector02));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_02));
                break;

            case 33:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector03));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_03));
                break;

            case 44:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector01));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_01));
                break;

            case 55:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector04));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_04));
                break;
        }

    }


    private void set_Layout_editor(int type) {

        switch (type) {

            case 1:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector01));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_01));
                break;

            case 2:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector02));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_02));
                break;

            case 3:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector03));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_03));
                break;

            case 4:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector04));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_04));
                break;

            case 5:
                tv_tag.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selector05));
                tv_tag.setBackground(ContextCompat.getDrawable(MainActivity.context, R.drawable.button_sl_tag_helper_05));
                break;
        }

    }


    public void set_Tag_Click(boolean type, final TagView view, final String tag) {

        if (type == SUGGESTED_TAG)

            view.setOnClickListener(new View.OnClickListener() {

                private void add_tag_to_header() {

                    TagView tagView = new TagView(MainActivity.context, tag, TagView.HEADER_TAG);

                    ((MainActivity)MainActivity.context).deckFragment

                            .searchFragment.add_TagInHeader(tag, tagView);

                }

                @Override
                public void onClick(View v) {

                    add_tag_to_header();

                }

            });


        if (type == HEADER_TAG)

            view.setOnClickListener(new View.OnClickListener() {

                private void remove_tag_from_header() {

                    ((MainActivity)MainActivity.context).deckFragment

                            .searchFragment.remove_TagFromHeader(tag, view);

                }

                @Override
                public void onClick(View v) {

                    remove_tag_from_header();

                }

            });
    }
}
