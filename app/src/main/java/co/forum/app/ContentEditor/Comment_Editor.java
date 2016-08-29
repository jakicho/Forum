package co.forum.app.ContentEditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.tools.TextSize;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;

public class Comment_Editor extends Fragment {

    public static final int RESULT_REPLY_CARD_PICT = 456;
    public static final int RESULT_REPLY_CHAT_PICT = 789;
    public static final int RESULT_REPLY_STORY_PICT = 901;

    public static final int CARD_TEXT = 0;
    public static final int CARD_PICT = 1;
    public static final int CARD_POLL = 2;

    private int card_content_type = CARD_TEXT;

    public View layout;

    @Bind(R.id.header_reply) public LinearLayout header_reply;


    private InputMethodManager imm;

    // CONTENT EDITOR --------------------------------------------------------------------------- //

    public int countRemaining;
    public int currentContentLenght = 0;
    private String hint_content;
    private String reply_to_who;

    private CardData parent_cardData;
    private String chatID;

    // bordel a remplacer par 0, 1, 2
    private boolean editor_type;
    public static final boolean CARD_COMMENT_EDITOR = true;
    public static final boolean CHAT_COMMENT_EDITOR = false;
    private boolean is_story;

    @Bind(R.id.blank_frame) FrameLayout blank_frame;
    @Bind(R.id.blank_frame2) FrameLayout blank_frame2;

    // - Text Editor
    @Bind(R.id.text_editor_scroller) ScrollView text_editor_scroller;
    @Bind(R.id.et_Content) public EditText et_Content;

    @Bind(R.id.link_preview1) LinearLayout link_preview1;
    @Bind(R.id.tv_preview1) TextView tv_preview1;
    @Bind(R.id.tv_count_thought_chars) TextView tv_count_thought_chars;

    // - Image Editor
    @Bind(R.id.image_editor_scroller) ScrollView image_editor_scroller;
    @Bind(R.id.pict_placeholder) public ImageView pict_placeholder;
    @Bind(R.id.et_Legend) EditText et_Legend;
    @Bind(R.id.close_image_editor) ImageView close_image_editor;

    @Bind(R.id.link_preview2) LinearLayout link_preview2;
    @Bind(R.id.tv_preview2) TextView tv_preview2;
    @Bind(R.id.tv_count_legend_chars) TextView tv_count_legend_chars;

    // - Add Link / Pict
    @Bind(R.id.btn_add_link) FrameLayout btn_add_link;
    @Bind(R.id.btn_add_pict) FrameLayout btn_add_pict;

    @Bind(R.id.reply_hint_bar) public TextView reply_hint_bar;


    // TAG EDITOR ------------------------------------------------------------------------------- //

    public int tag_editor_offscreen_position = 150;

    public final int ADD_TAG_FROM_KEYBOARD_SPACE = 0;
    public final int ADD_TAG_FROM_BTN_SEND = 1;

    private String hint_tags;

    public ArrayList<String> tagsArrayList;

    @Bind(R.id.tag_editor) LinearLayout tag_editor;
    @Bind(R.id.header_tags_row) public RowLayout tags_row;
    @Bind(R.id.tv_countTags) TextView tv_countTags;
    @Bind(R.id.et_Tags) public EditText et_Tags;


    // -- BOTTOM BUTTONS ------------------------------------------------------------------------ //

    Drawable tag_icon_grey_state = ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_grey600_48dp);

    @Bind(R.id.btn_tag) LinearLayout btn_tag;
    @Bind(R.id.btn_text_add_tags) TextView btn_text_add_tags;

    @Bind(R.id.tag_nb_1) FrameLayout tag_nb_1;
    @Bind(R.id.tag_nb_2) FrameLayout tag_nb_2;
    @Bind(R.id.tag_nb_3) FrameLayout tag_nb_3;
    @Bind(R.id.tag_nb_4) FrameLayout tag_nb_4;
    @Bind(R.id.tag_nb_5) FrameLayout tag_nb_5;
    @Bind(R.id.tag_nb_6) FrameLayout tag_nb_6;
    @Bind(R.id.tag_nb_7) FrameLayout tag_nb_7;
    ArrayList<FrameLayout> tagsIndicator;

    @Bind(R.id.btn_icon_tags) ImageView btn_icon_tags;
    @Bind(R.id.btn_preview) LinearLayout btn_post;
    @Bind(R.id.btn_text_post) TextView btn_text_post;


    public boolean chat_editor_visible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.panel_editor_reply, container, false);

        ButterKnife.bind(this, layout);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        init_editor_layouts();

        init_click_listeners();

        init_listener_et_tags();



        init_btn_tag();

        disable_btn_post();

        init_loginAndTracker();

        // -----------------




        return layout;

    }


    public void setChat_editor_visible(boolean chat_editor_visible) {
        this.chat_editor_visible = chat_editor_visible;
    }

    public void setIs_story(boolean is_story) {
        this.is_story = is_story;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }}


    private void init_editor_layouts() {

        if(!editor_type) {

            ViewTreeObserver vto = header_reply.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    removeOnGlobalLayoutListener(header_reply, this); //header_reply.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    int header_editor_height = header_reply.getMeasuredHeight();

                    if (is_story) ((SubActivity) SubActivity.context)
                            .storyFragment.story_content_panel_structure.setPanelHeight(header_editor_height); //90

                    else ((SubActivity) SubActivity.context)
                            .chatFragment.chat_content_panel_structure.setPanelHeight(header_editor_height); //90
                }
            });

            if (is_story) {
                blank_frame.setBackground(null);
                blank_frame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
            }

        }

        if(chat_editor_visible) header_reply.setVisibility(View.VISIBLE);

        tagsIndicator = new ArrayList<>();

        tagsIndicator.add(tag_nb_1);
        tagsIndicator.add(tag_nb_2);
        tagsIndicator.add(tag_nb_3);
        tagsIndicator.add(tag_nb_4);
        tagsIndicator.add(tag_nb_5);
        tagsIndicator.add(tag_nb_6);
        tagsIndicator.add(tag_nb_7);

        reply_hint_bar.setText(reply_to_who);


        // -- THOUGHT edit text --------------------------------------------------------------------

        et_Content.setHint(hint_content);

        et_Content.setTypeface(MainActivity.tf_serif);

        tv_count_thought_chars.setText(String.valueOf(CardData.MAX_THOUGHT_LENGTH));

        tv_count_thought_chars.setVisibility(View.INVISIBLE);

        text_editor_scroller.setVisibility(View.VISIBLE);

        image_editor_scroller.setVisibility(View.GONE);

        et_Legend.setTypeface(MainActivity.tf_serif);


        // -- TAGS edit text -----------------------------------------------------------------------

        tag_editor.setTranslationY(tag_editor_offscreen_position);

        //for(String tag : tagsArrayList) Tags.displaySelected_EditTag(tag);


        et_Tags.setHint(hint_tags);

        et_Tags.setHorizontallyScrolling(false); // permet d'avoir un edit text d'une ligne

        et_Tags.setMaxLines(Integer.MAX_VALUE);

    }


    private void init_loginAndTracker() {

        /*
        todo later
        ((ThoughtActivity)ThoughtActivity.context).setLoginPage();

        MentorApp application = (MentorApp) ((ThoughtActivity)ThoughtActivity.context).getApplication();
        mTracker = application.getDefaultTracker();
        */

    }


    // EDIT TEXT LISTENERS ------------------------------------------------------------------------

    int currentLegendLenght;

    public final static int MAX_LEGEND = 100;

    private boolean check_link(String link) {

        if(link.startsWith("http://") || link.startsWith("https://")) return true;

        else return false;

    }

    private void set_popup_url(String link) {

        String title="";
        String yes_button="";
        String no_button="";

        if(link.isEmpty()) {

            title = "Paste a link";
            yes_button = "add";
            no_button = "cancel";

        } else {

            title = "Change your link";
            yes_button = "modify";
            no_button = "erase";
        }


        new MaterialDialog.Builder(getContext())
                .content(title)
                .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                .positiveText(yes_button)
                .negativeText(no_button)

                .input("http://", link, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        if (input.length() != 0) {

                            //btn_add_link.setVisibility(View.INVISIBLE);

                            tv_preview1.setText(input);

                            tv_preview2.setText(input);

                            link_preview1.setVisibility(View.VISIBLE);

                            link_preview2.setVisibility(View.VISIBLE);

                            check_linkOk = check_link(input.toString());

                        } else {

                            //btn_add_link.setVisibility(View.VISIBLE);

                            check_linkOk = true;

                            tv_preview1.setText("");

                            link_preview1.setVisibility(View.INVISIBLE);

                            tv_preview2.setText("");

                            link_preview2.setVisibility(View.INVISIBLE);
                        }

                        if (card_content_type == CARD_TEXT) et_Content.requestFocus();

                        else if (card_content_type == CARD_PICT) et_Legend.requestFocus();

                        text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        set_btn_post_click();

                    }
                })

                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        link_preview1.setVisibility(View.INVISIBLE);

                        link_preview2.setVisibility(View.INVISIBLE);

                        tv_preview1.setText("");

                        tv_preview2.setText("");

                        //btn_add_link.setVisibility(View.VISIBLE);

                        check_linkOk = true;

                        if (card_content_type == CARD_TEXT) et_Content.requestFocus();

                        else if (card_content_type == CARD_PICT) et_Legend.requestFocus();

                        text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        set_btn_post_click();
                    }
                })
                .show();
    }

    private void init_click_listeners() {

        blank_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Content.requestFocus();

                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

            }
        });

        blank_frame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_Content.requestFocus();

                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        });

        // CARD TEXT

        et_Content.addTextChangedListener(new TextWatcher() {

            private void updateCount(Editable s) {

                currentContentLenght = s.toString().length();

                if (currentContentLenght == 0) {

                    btn_add_pict.setVisibility(View.VISIBLE);
                    tv_count_thought_chars.setVisibility(View.INVISIBLE);

                } else {

                    btn_add_pict.setVisibility(View.INVISIBLE);
                    tv_count_thought_chars.setVisibility(View.VISIBLE);
                }

                countRemaining = CardData.MAX_THOUGHT_LENGTH - currentContentLenght;

                tv_count_thought_chars.setText(String.valueOf(countRemaining));

                if (countRemaining < 0) {

                    // todo
                    //tv_count_thought_chars.setTextColor(getResources().getColor(R.color.warning_red));

                    tv_count_thought_chars.setTextSize(12);

                } else {

                    //todo
                    //tv_count_thought_chars.setTextColor(getResources().getColor(R.color.timestamp));

                    tv_count_thought_chars.setTextSize(10);

                }

                et_Content.setTextSize(TextSize.scale(countRemaining, true));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //if (tag_editor.getTranslationY() != tag_editor_offscreen_position) hide_tags_editor();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                updateCount(s); // update du compteur a chaque fois que l'utilisateur tape sur le clavier

                check_contentExist = currentContentLenght > 0;

                check_contentLengthIsOk = currentContentLenght <= CardData.MAX_THOUGHT_LENGTH;

                set_btn_post_click();

                /*

                if (s.toString().contains("\n")) {

                    String text = s.toString().replace("\n", "");

                    et_Content.setText(text);

                    et_Tags.requestFocus();

                    tag_editor.animate().translationY(0)
                            .setInterpolator(new DecelerateInterpolator(1.5f))
                            .start();
                }

                */
            }
        });

        et_Content.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) hide_tags_editor();
            }
        });

        link_preview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_popup_url(tv_preview1.getText().toString());

                hide_tags_editor();
            }
        });

        // CARD PICT

        et_Legend.addTextChangedListener(new TextWatcher() {

            private void updateCount(Editable s) {

                //image_editor_scroller.fullScroll(View.FOCUS_DOWN);

                currentLegendLenght = s.toString().length();

                if (currentLegendLenght == 0) tv_count_legend_chars.setVisibility(View.INVISIBLE);

                else tv_count_legend_chars.setVisibility(View.VISIBLE);

                int remain = MAX_LEGEND - currentLegendLenght;

                tv_count_legend_chars.setText(String.valueOf(remain));

                if (remain < 0) tv_count_legend_chars.setTextSize(12);

                else tv_count_legend_chars.setTextSize(10);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                updateCount(s);

                if (s.toString().contains("\n")) {

                    String text = s.toString().replace("\n", "");

                    et_Legend.setText(text);

                    et_Tags.requestFocus();

                    tag_editor.animate().translationY(0)
                            .setInterpolator(new DecelerateInterpolator(1.5f))
                            .start();
                }

                check_legendLengthIsOk = currentContentLenght <= MAX_LEGEND;

                set_btn_post_click();

            }
        });

        et_Legend.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    image_editor_scroller.fullScroll(View.FOCUS_DOWN);

                    hide_tags_editor();
                }
            }
        });

        link_preview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_popup_url(tv_preview2.getText().toString());

                hide_tags_editor();
            }
        });

        close_image_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choose_text_editor();
            }
        });

        // --

        btn_add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_popup_url(tv_preview1.getText().toString());

            }
        });


        btn_add_pict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = Intent.createChooser(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), "Upload a picture with:");

                if(editor_type == CARD_COMMENT_EDITOR)

                    ((SubActivity) SubActivity.context).startActivityForResult(intent, RESULT_REPLY_CARD_PICT);

                else if(editor_type == CHAT_COMMENT_EDITOR) {

                    if(is_story) ((SubActivity) SubActivity.context).startActivityForResult(intent, RESULT_REPLY_STORY_PICT);

                    else ((SubActivity) SubActivity.context).startActivityForResult(intent, RESULT_REPLY_CHAT_PICT);

                }

            }
        });


    }

    private void init_listener_et_tags() {

        tag_editor.setVisibility(View.VISIBLE);

        et_Tags.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if ((s.toString().endsWith(" ") && s.toString().length() == 1)
                        || (s.toString().endsWith("\n") && s.toString().length() == 1)) {

                    et_Tags.setText("");

                    String current = et_Content.getText().toString();

                    et_Content.setText("");
                    et_Content.requestFocus();

                    et_Content.append(current);

                } else if (s.toString().endsWith(" ")) {

                    String tag = s.toString().replace(" ", "");

                    addTagToContent(ADD_TAG_FROM_KEYBOARD_SPACE, tag);

                } else if (s.toString().endsWith("\n")) {

                    String tag = s.toString().replace("\n", "");

                    addTagToContent(ADD_TAG_FROM_KEYBOARD_SPACE, tag);
                }
            }

        });

        et_Tags.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus)
                    btn_icon_tags.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_blue_48dp));

                else {

                    if (et_Tags.length() != 0) et_Tags.setText("");

                    btn_icon_tags.setImageDrawable(tag_icon_grey_state);

                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                }
            }
        });
    }



    // BOTTOM BUTTONS ------------------------------------------------------------------------------

    private void init_btn_tag() {

        btn_tag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (et_Tags.hasFocus()) {

                    hide_tags_editor();

                    if (card_content_type == CARD_TEXT) et_Content.requestFocus();

                    else if (card_content_type == CARD_PICT) et_Legend.requestFocus();

                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                } else if (tagsArrayList.size() == CardData.MAX_TAGS && tag_editor.getTranslationY() != tag_editor_offscreen_position) {

                    hide_tags_editor();

                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                } else {

                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                    //text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                    tag_editor.animate().translationY(0)
                            .setInterpolator(new DecelerateInterpolator(1.5f))
                            .start();

                    et_Tags.requestFocus();

                }
            }
        });
    }

    private void hide_tags_editor() {

        tag_editor.animate().translationY(tag_editor_offscreen_position)
                .setInterpolator(new AccelerateInterpolator(1.5f))
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();

    }


    // ADD & REMOVE TAG ------------------------------------------------------------------------

    public void addTagToContent(int source, String tag) {

        if(source == ADD_TAG_FROM_KEYBOARD_SPACE) {

            if (!tagsArrayList.contains(tag) && (tagsArrayList.size() + 1) <= CardData.MAX_TAGS) {

                tagsArrayList.add(tag);

                if(editor_type == CARD_COMMENT_EDITOR)

                    Tags.displaySelected_EditTag(tag, false, ((SubActivity)getActivity()).cardFragment.comment_editor);

                else {

                    if(is_story) Tags.displaySelected_EditTag(tag, false, ((SubActivity) getActivity()).storyFragment.comment_editor);

                    else Tags.displaySelected_EditTag(tag, false, ((SubActivity) getActivity()).chatFragment.comment_editor);

                }

                et_Tags.setText("");

                if (tagsArrayList.size() == CardData.MAX_TAGS) et_Tags.setVisibility(View.GONE);

                update_tag_editor_offscreen_position();

            } else {

                et_Tags.setText("");

                et_Tags.append(tag);

                Message.message(MainActivity.context, "tag is already selected");

            }
        }


        if(source == ADD_TAG_FROM_BTN_SEND) {

            if (!tagsArrayList.contains(tag) && (tagsArrayList.size() + 1) <= CardData.MAX_TAGS) {

                tagsArrayList.add(tag);

                if(editor_type == CARD_COMMENT_EDITOR)

                    Tags.displaySelected_EditTag(tag, false, ((SubActivity)getActivity()).cardFragment.comment_editor);

                else  {

                    if(is_story) Tags.displaySelected_EditTag(tag, false, ((SubActivity) getActivity()).storyFragment.comment_editor);

                    else Tags.displaySelected_EditTag(tag, false, ((SubActivity) getActivity()).chatFragment.comment_editor);

                }

                et_Tags.setText("");

                if (tagsArrayList.size() == CardData.MAX_TAGS) et_Tags.setVisibility(View.GONE);

                update_tag_editor_offscreen_position();

            } else {

                et_Tags.setText("");

                //et_Tags.append(tag);

                Message.message(MainActivity.context, "tag is already selected (btn send)");

            }
        }

        setTagsCounter();

        setET_tags_Hint();

        set_btn_post_click();

    }

    public void deleteTagFromThought(CardView cardView, String tag) {

        tags_row.removeView(cardView);

        et_Tags.setVisibility(View.VISIBLE);

        tagsArrayList.remove(tag);

        if(tagsArrayList.isEmpty()) et_Tags.setHint(hint_tags);

        update_tag_editor_offscreen_position();

        setTagsCounter();

        setET_tags_Hint();

        et_Tags.requestFocus(); // necessaire quand on passe de 12 a 11 tags

        set_btn_post_click();

    }


    private void set_tags_indicator(int nb_active) {

        if(nb_active == 0) {

            for(int i = 0; i < 7; i++) tagsIndicator.get(i).setVisibility(View.INVISIBLE);

        } else {

            int i;

            for (i = 0; i < nb_active; i++) {

                tagsIndicator.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tag_on));
                tagsIndicator.get(i).setVisibility(View.VISIBLE);

            }

            /*
            if(i <= 2) {

                int index;

                for (index = i; index < 3; index++) {

                    tagsIndicator.get(index).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tag_orange));
                    tagsIndicator.get(index).setVisibility(View.VISIBLE);

                }

                i = index;
            }
            */

            for (int index = i; index < 7; index++) {

                tagsIndicator.get(i).setVisibility(View.INVISIBLE);

            }
        }
    }

    private void setTagsCounter() {

        tv_countTags.setText(tagsArrayList.size() + "/" + CardData.MAX_TAGS);

        set_tags_indicator(tagsArrayList.size());

        check_tagsOk = true;

        /*
        if(card_content_type == CARD_TEXT && (tagsArrayList.size() == 0 || tagsArrayList.size() >= 3)) check_tagsOk = true;

        else if(card_content_type == CARD_PICT && (tagsArrayList.size() == 0 || tagsArrayList.size() >= 2)) check_tagsOk = true;

        else check_tagsOk = false;*/

    }

    private void setET_tags_Hint() {

        if(tagsArrayList.size() == 0) et_Tags.setHint(getActivity().getResources().getString(R.string.et_hint_tags_optional));

        else if(tagsArrayList.size() < CardData.MIN_TAGS) et_Tags.setHint(getActivity().getResources().getString(R.string.et_hint_tags));

        else et_Tags.setHint(getActivity().getResources().getString(R.string.et_short_hint_tags));
    }


    private void update_tag_editor_offscreen_position() {

        ViewTreeObserver observer = tag_editor.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                tag_editor_offscreen_position = tag_editor.getHeight();

            }
        });

    }



    // GETTERS & SETTERS --------------------------------------------------------------------------


    public void setEditor_type(boolean editor_type) {
        this.editor_type = editor_type;
    }

    public void setParent_card(CardData cardData) {
        this.parent_cardData = cardData;

    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public void setReply_to_who(String reply_to_who) {
        this.reply_to_who = reply_to_who;
    }

    public TextView getReply_hint_bar() {
        return reply_hint_bar;
    }

    public void setHint_tags(String hint_tags) {
        this.hint_tags = hint_tags;
    }


    public void setTagsArrayList(ArrayList<String> tagsArrayList) {
        this.tagsArrayList = tagsArrayList;
    }

    public void setHint_content(String hint_content) {
        this.hint_content = hint_content;
    }


    public LinearLayout getHeader_reply() {
        return header_reply;
    }




    private void disable_btn_post() {

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_post_disable();
            }
        });
    }


    private void set_btn_post_click() {

        if(card_content_type == CARD_TEXT) {

            if (check_contentExist && check_contentLengthIsOk && check_linkOk && check_tagsOk) {

                btn_text_post.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (et_Tags.hasFocus() && et_Tags.getText().length() != 0)

                            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());

                        btn_post_enable();
                    }
                });

            } else {

                btn_text_post.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btn_post_disable();
                    }
                });
            }

        } else if(card_content_type == CARD_PICT) {

            if (check_legendLengthIsOk && check_tagsOk && check_linkOk) {

                btn_text_post.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (et_Tags.hasFocus() && et_Tags.getText().length() != 0)

                            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());

                        btn_post_enable();
                    }
                });

            } else {

                btn_text_post.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                btn_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btn_post_disable();
                    }
                });
            }
        }
    }


    private void btn_post_disable() {

        if(et_Tags.hasFocus() && et_Tags.getText().length() != 0)

            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());


        String message = "";

        if(card_content_type == CARD_TEXT) {

            if(!check_linkOk) message = "Your link must begin by 'http://' or 'https://'";

            if(!check_contentLengthIsOk) message = "Your comment is too long";

            if(!check_contentExist) {

                message = "Write something";

                et_Content.requestFocus();
            }

            /*
            if(message.equals("Enter at least 3 tags")) {

                tag_editor.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .start();

                et_Tags.requestFocus();

            } */

        } else if(card_content_type == CARD_PICT) {

            if(!check_linkOk) message = "Your link must begin by 'http://' or 'https://'";

            //if (tv_preview2.getText().length() != 0 &&
                //(!tv_preview2.getText().toString().startsWith("http://") || !tv_preview2.getText().toString().startsWith("https://")))

                //message = "Your link must begin by 'http://' or 'https://' ds";

            if (!check_legendLengthIsOk) message = "Your comment is too long";

            /*
            if (message.equals("Enter at least 2 tags")) {

                tag_editor.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .start();

                et_Tags.requestFocus();
            }*/

        }

        if(!message.isEmpty()) Message.message(MainActivity.context, message);

        else btn_post_enable();

    }

    private void close_panel(final boolean isChatPanel) {

        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();

        if (view != null) MainActivity.hideSoftKeyboard(view);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if(!isChatPanel) ((SubActivity) getContext()).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                else if (is_story) ((SubActivity) getContext()).storyFragment.story_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                else ((SubActivity) getContext()).chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                reset_editor();

            }

        }, 300); // 5000ms delay
    }

    private void send_comment(boolean CARD_COMMENT_EDITOR) {

        if(CARD_COMMENT_EDITOR) {

            String content;

            if(selectedImage == null) content = et_Content.getText().toString();

            else content = et_Legend.getText().toString();

            CardData replyCard = new CardData(
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    parent_cardData.getCard_id(), // peut changer
                    parent_cardData.getAuthor_id(), // peut changer
                    parent_cardData.getAuthor_full_name(), // peut changer
                    "", // todo verifier si c'est utile
                    content, //et_Content.getText().toString(),
                    selectedImage,
                    tv_preview1.getText().toString(),
                    tagsArrayList,
                    ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LATITUDE),
                    ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LONGITUDE),
                    0, 0, 0);


            // TODO recuperer tag
            //Message.message(getContext(), "TAG >> " + parent_cardData.getTags_list().size());
            //Message.message(getContext(), "TAG >> " + parent_cardData.getTags_list().get(0));

            ParseRequest.insert_comment_in_BG(getContext(), parent_cardData, replyCard, parent_cardData.getMain_content());

            close_panel(false);

        } else {

            // TODO

            //CHAT_COMMENT_EDITOR
            String content;

            if(selectedImage == null) content = et_Content.getText().toString();

            else content = et_Legend.getText().toString();

            CardData replyCard = new CardData(
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    "",
                    "",
                    "",
                    chatID,
                    content,
                    selectedImage,
                    tv_preview1.getText().toString(),
                    tagsArrayList,
                    ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LATITUDE),
                    ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LONGITUDE),
                    0, 0, 0);

            ParseRequest.insert_comment_chat_inBG(getContext(), replyCard);

            close_panel(true);

        }
    }



    private void btn_post_enable() {

        send_comment(editor_type);

        /*

        if(card_content_type == CARD_TEXT && tagsArrayList.size() != 0 && tagsArrayList.size() < 3)

            Message.message(getContext(), "enter at least 3 tags or none");

        else if(card_content_type == CARD_PICT && tagsArrayList.size() != 0 && tagsArrayList.size() < 2)

            Message.message(getContext(), "enter at least 2 tags or none");

        else send_comment(editor_type);

        */
    }


    public void displayReplyCard_inChat(CardData replyCard) {


        final int lastPosition = ((SubActivity)getContext()).chatFragment.chatAdapter.getItemCount();

        ((SubActivity)getContext()).chatFragment.cardsList.add(lastPosition, replyCard);

        //((SubActivity)getContext()).cardFragment.commentAdapter.addItem(replyCard, lastPosition);

        ((SubActivity)getContext()).chatFragment.chatAdapter.notifyItemInserted(lastPosition + 1);

        if(((SubActivity)getContext()).chatFragment.chat_content_panel_structure.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)

            ((SubActivity)getContext()).chatFragment.chat_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        Runnable myRunnable2 = new Runnable() {
            @Override
            public void run() {

                ((SubActivity)getContext()).chatFragment.recycler_chat_message.smoothScrollToPosition(lastPosition+1);
            }
        };

        Handler myHandler2 = new Handler();
        myHandler2.postDelayed(myRunnable2, 550);

    }


    public void displayReplyCard_inComment(CardData replyCard) {

        final int lastPosition = ((SubActivity)getContext()).cardFragment.commentAdapter.getItemCount();

        ((SubActivity)getContext()).cardFragment.commentAdapter.repliesList.add(lastPosition, replyCard);

        ((SubActivity)getContext()).cardFragment.commentAdapter.notifyItemInserted(lastPosition + 1);

        if(((SubActivity)getContext()).cardFragment.reply_content_panel_structure.getPanelState() != SlidingUpPanelLayout.PanelState.COLLAPSED)

            ((SubActivity)getContext()).cardFragment.reply_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        Runnable myRunnable2 = new Runnable() {
            @Override
            public void run() {

                ((SubActivity)getContext()).cardFragment.repliesRecyclerView.smoothScrollToPosition(lastPosition+1);
            }
        };

        Handler myHandler2 = new Handler();
        myHandler2.postDelayed(myRunnable2, 550);

    }




    // POST -------------------------------------------------------------------------------


    private boolean check_linkOk = true;
    private boolean check_contentExist = false;
    private boolean check_contentLengthIsOk = false;
    private boolean check_tagsOk = true;
    private boolean check_legendLengthIsOk = true;





    private void reset_editor() {

        tags_row.removeViews(0, tagsArrayList.size());

        tagsArrayList = new ArrayList<>();

        image_editor_scroller.setVisibility(View.GONE);
        text_editor_scroller.setVisibility(View.VISIBLE);
        selectedImage = null;

        et_Legend.setText("");

        et_Tags.setText("");

        et_Tags.setHint(hint_tags);

        setTagsCounter();

        tag_editor.setTranslationY(tag_editor_offscreen_position);


        et_Content.setText("");

        tv_preview1.setText("");

        tv_preview2.setText("");
        //btn_add_link.setVisibility(View.VISIBLE);
        link_preview1.setVisibility(View.INVISIBLE);

        link_preview2.setVisibility(View.INVISIBLE);

        check_linkOk = true;
        check_contentExist = false;
        check_contentLengthIsOk = false;
        check_tagsOk = true;

        card_content_type = CARD_TEXT;

        disable_btn_post();

        set_btn_post_click();
        /*
        content_card.setScaleX(1f);
        content_card.setScaleY(1f);
        content_card.setAlpha(1f);
        content_card.setTranslationY(1200f);
        */

    }

    private void closeEditorPanel() {

        /*
        TODO

        // on cache le clavier puis le panel
        //imm.hideSoftInputFromWindow(et_Tags.getWindowToken(), 0);

        //imm.hideSoftInputFromWindow(et_Content.getWindowToken(), 0);

        Runnable myRunnable;


        myRunnable = new Runnable(){

            @Override
            public void run() {

                ((ThoughtActivity)getActivity()).thoughtFragment.thoughtEditor.hideEditorPanel(ContentEditor.EDITOR_TYPE_REPLY);

                btn_send_confirm_activate = true;

            }

        };

        //Handler myHandler = new Handler();

        //myHandler.postDelayed(myRunnable, 350);

        */

    }

    private void enableEditor_Et(boolean activate) {

        if(activate) {

            et_Content.setVisibility(View.VISIBLE);
            et_Legend.setVisibility(View.VISIBLE);
            et_Tags.setVisibility(View.VISIBLE);

        } else {

            et_Content.setVisibility(View.INVISIBLE);
            et_Legend.setVisibility(View.INVISIBLE);
            et_Tags.setVisibility(View.INVISIBLE);

        }
    }

    //

    public Uri selectedImage = null;

    public void choose_pict_editor(Uri selectedImage) {

        this.selectedImage = selectedImage;

        card_content_type = CARD_PICT;

        text_editor_scroller.setVisibility(View.GONE);
        image_editor_scroller.setVisibility(View.VISIBLE);

        Picasso.with(MainActivity.context)

                .load(selectedImage)
                .fit().centerInside()
                .into(pict_placeholder);

        et_Legend.setText("");
        et_Legend.requestFocus();

        //if(tv_preview2.getText().toString().isEmpty()) btn_add_link.setVisibility(View.VISIBLE);
        //else btn_add_link.setVisibility(View.INVISIBLE);
        tv_count_thought_chars.setVisibility(View.GONE);

        et_Tags.setHint(getActivity().getResources().getString(R.string.et_hint_tags_optional));
    }

    private void choose_text_editor() {

        selectedImage = null;

        card_content_type = CARD_TEXT;

        text_editor_scroller.setVisibility(View.VISIBLE);
        image_editor_scroller.setVisibility(View.GONE);

        et_Content.requestFocus();

        //if(tv_preview1.getText().toString().isEmpty()) btn_add_link.setVisibility(View.VISIBLE);
        //else btn_add_link.setVisibility(View.INVISIBLE);
        tv_count_legend_chars.setVisibility(View.GONE);

        et_Tags.setHint(hint_tags);
    }
}