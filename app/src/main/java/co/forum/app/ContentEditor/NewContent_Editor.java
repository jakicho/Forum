package co.forum.app.ContentEditor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

//import com.google.android.gms.analytics.Tracker; todo later

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.Data.CardData;
import co.forum.app.Data.ParseRequest;
import co.forum.app.MainActivity;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.cardStackView.CardStackView;
import co.forum.app.cardStackView.SwipeTouchListener;
import co.forum.app.tools.TagView;
import co.forum.app.tools.TextSize;
import co.forum.app.tools.Message;
import co.forum.app.tools.RowLayout;
import co.forum.app.tools.Tags;

public class NewContent_Editor extends Fragment {

    // ------------------------------------------------------------------------------------------ //

    public static final int RESULT_NEW_CARD_PICT = 123;

    public static final int CARD_TEXT = 0;
    public static final int CARD_PICT = 1;
    public static final int CARD_POLL = 2;

    private int card_content_type = CARD_TEXT;

    public View layout;

    private InputMethodManager imm;

    // USERNAME EDITOR -------------------------------------------------------------------------- //
    @Bind(R.id.et_update_family_surname) public EditText et_update_family_surname;
    @Bind(R.id.et_update_first_name) public EditText et_update_first_name;
    @Bind(R.id.btn_update_username) public Button btn_update_username;

    // BIO EDITOR ------------------------------------------------------------------------------- //
    @Bind(R.id.bio_edit_text) public EditText bio_edit_text;
    @Bind(R.id.optional_link) public TextView optional_link;
    @Bind(R.id.tv_count_bio_chars) public TextView tv_count_bio_chars;
    @Bind(R.id.btn_optional_link) public LinearLayout btn_optional_link;


    // CONTENT EDITOR --------------------------------------------------------------------------- //

    public int countRemaining;
    public int currentContentLenght = 0;
    private String hint_content;

    @Bind(R.id.content_card) RelativeLayout content_card;
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


    // TAG EDITOR ------------------------------------------------------------------------------- //

    public int tag_editor_offscreen_position = 150;
    public int card_of_tags_offscreen_position = 1500;
    public boolean tag_helper_is_visible = false;

    public final int ADD_TAG_FROM_KEYBOARD_SPACE = 0;
    public final int ADD_TAG_FROM_BTN_SEND = 1;

    private String hint_tags;

    public ArrayList<String> tagsArrayList;

    @Bind(R.id.tag_helper) RelativeLayout tag_helper;
    @Bind(R.id.card_of_tags) CardView card_of_tags;
    @Bind(R.id.tag_editor) LinearLayout tag_editor;
    @Bind(R.id.header_tags_row) public RowLayout tags_row;
    @Bind(R.id.fr_separator) public FrameLayout fr_separator;

    @Bind(R.id.tv_countTags) TextView tv_countTags;
    @Bind(R.id.et_Tags) public EditText et_Tags;
    @Bind(R.id.tv_select_tag) public TextView tv_select_tag;


    // -- BOTTOM BUTTONS ------------------------------------------------------------------------ //

    Drawable tag_icon_grey_state;
    Drawable tag_icon_orange_state;
    Drawable tag_icon_passive_state;

    //Drawable tag_icon_grey_state = ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_grey600_48dp);
    //Drawable tag_icon_orange_state = ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_orange_48dp);
    //Drawable tag_icon_passive_state = tag_icon_grey_state;

    @Bind(R.id.editor_bottom_menu1) LinearLayout editor_bottom_menu1;
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
    @Bind(R.id.btn_preview) LinearLayout btn_preview;
    @Bind(R.id.btn_icon_preview) ImageView btn_icon_preview;
    @Bind(R.id.btn_text_post) TextView btn_text_preview;

    public int bottom_menu_offscreen_position = 180;

    public float speed_factor = 0.9f;


    // -- CONFIRMATION SCREEN ------------------------------------------------------------------

    @Bind(R.id.preview_card_include) public RelativeLayout preview_card_include;
    @Bind(R.id.card_stack_preview) CardStackView card_stack_preview;

    public static CardStackAdapter cardStackAdapter;
    private List<CardData> swipeStack;

    public static Uri selectedImage;
    private CardData newCard;

    @Bind(R.id.btn_back_to_editor) LinearLayout btn_back_to_editor;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        tag_icon_grey_state = ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_grey600_48dp);
        tag_icon_orange_state = ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_orange_48dp);
        tag_icon_passive_state = tag_icon_grey_state;

        layout = inflater.inflate(R.layout.panel_editor_card, container, false);

        ButterKnife.bind(this, layout);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        init_username_editor();

        init_bio_editor();

        init_editor_layouts();

        //init_preview();

        init_click_listeners();

        init_listener_et_tags();


        init_btn_tag();


        disable_btn_preview();

        init_btn_back_to_editor();

        //init_btn_send_card();



        init_loginAndTracker();

        return layout;

    }



    private void init_username_editor() {

        btn_update_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_update_family_surname.getText().length() > 3 && et_update_first_name.getText().length() > 2) {

                    ((MainActivity) getContext()).closeEditorPanel();

                    String name = et_update_first_name.getText().toString();
                    String surname = et_update_family_surname.getText().toString();
                    String fullname = name + " " + surname;

                    ((MainActivity) getActivity()).localData.setUserData(SharedPref.KEY_NAME, name);
                    ((MainActivity) getActivity()).localData.setUserData(SharedPref.KEY_SURNAME, surname);
                    ((MainActivity) getActivity()).localData.setUserData(SharedPref.KEY_FULL_NAME, fullname);

                    getActivity().setTitle(fullname.toUpperCase());
                    ((MainActivity) getActivity()).tabTitle[2] = fullname.toUpperCase();


                    ParseRequest.update_UserName(name, surname);

                    et_update_family_surname.clearFocus();
                    et_update_first_name.clearFocus();

                } else Message.message(MainActivity.context, "too short");
            }
        });
    }




    private void init_bio_editor() {

        bio_edit_text.setHorizontallyScrolling(false);

        bio_edit_text.setMaxLines(10);

        bio_edit_text.setTypeface(MainActivity.tf);

        bio_edit_text.addTextChangedListener(new TextWatcher() {

            private void updateCount(Editable s) {

                currentContentLenght = s.toString().length();

                countRemaining = 140 - currentContentLenght;

                tv_count_bio_chars.setText(String.valueOf(countRemaining));

                if (countRemaining < 0) {

                    // todo
                    //tv_count_thought_chars.setTextColor(getResources().getColor(R.color.warning_red));

                    tv_count_bio_chars.setTextSize(12);

                } else {

                    //todo
                    //tv_count_thought_chars.setTextColor(getResources().getColor(R.color.timestamp));

                    tv_count_bio_chars.setTextSize(10);

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int aft) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                updateCount(s); // update du compteur a chaque fois que l'utilisateur tape sur le clavier

                //if (tag_editor.getTranslationY() != tag_editor_offscreen_position) hide_tags_editor();
                if (countRemaining < 0) {
                    bio_edit_text.setText("");
                    bio_edit_text.append(s.subSequence(0, 139));
                }


            }
        });

        bio_edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                ((MainActivity) getContext()).closeEditorPanel();

                if (bio_edit_text.getText().length() != 0) {

                    ((MainActivity) getContext()).profilFragment.update_bio(bio_edit_text.getText().toString());

                } else {

                    ((MainActivity) getContext()).profilFragment.update_bio("");

                }

                ((MainActivity) getActivity()).localData.setUserData(SharedPref.KEY_BIO, bio_edit_text.getText().toString());
                ParseRequest.update_UserBio(bio_edit_text.getText().toString());

                bio_edit_text.clearFocus();

                return false;
            }
        });


        final String link = ((MainActivity) getActivity()).localData.getUserData(SharedPref.KEY_BIO_URL);

        if(link.isEmpty()) optional_link.setText("add link");

        else optional_link.setText(link);

        btn_optional_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "Paste a link";
                String yes_button = "Add";
                String no_button = "Cancel";

                new MaterialDialog.Builder(getContext())
                        .content(title)
                        .inputType(InputType.TYPE_TEXT_VARIATION_URI)
                        .positiveText(yes_button)
                        .negativeText(no_button)

                        .input("http://", link, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {

                                if (input.length() != 0) {

                                    ((MainActivity) getContext()).profilFragment.update_bio_url(input.toString());

                                    optional_link.setText(input.toString());

                                } else {

                                    ((MainActivity) getContext()).profilFragment.update_bio_url("");

                                    optional_link.setText("add link");

                                }

                                ((MainActivity) getActivity()).localData.setUserData(SharedPref.KEY_BIO_URL, input.toString());
                                ParseRequest.update_UserBioUrl(input.toString());

                            }
                        })

                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
        });
    }



    private void init_editor_layouts() {

        tagsIndicator = new ArrayList<>();

        tagsIndicator.add(tag_nb_1);
        tagsIndicator.add(tag_nb_2);
        tagsIndicator.add(tag_nb_3);
        tagsIndicator.add(tag_nb_4);
        tagsIndicator.add(tag_nb_5);
        tagsIndicator.add(tag_nb_6);
        tagsIndicator.add(tag_nb_7);

        preview_card_include.setAlpha(0);

        // -- THOUGHT edit text --------------------------------------------------------------------

        et_Content.setHint(hint_content);

        et_Content.setTypeface(MainActivity.tf_serif);

        tv_count_thought_chars.setText(String.valueOf(CardData.MAX_THOUGHT_LENGTH));

        tv_count_thought_chars.setVisibility(View.INVISIBLE);

        text_editor_scroller.setVisibility(View.VISIBLE);

        image_editor_scroller.setVisibility(View.GONE);

        et_Legend.setTypeface(MainActivity.tf_serif);


        // -- TAGS edit text -----------------------------------------------------------------------

        Display display = ((MainActivity)MainActivity.context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        card_of_tags_offscreen_position = size.y;

        tag_editor.setTranslationY(tag_editor_offscreen_position);
        card_of_tags.setTranslationY(card_of_tags_offscreen_position);

        for(String tag : tagsArrayList) Tags.displaySelected_EditTag(tag, true, MainActivity.newContent_editor);


        et_Tags.setHint(hint_tags);

        et_Tags.setHorizontallyScrolling(false); // permet d'avoir un edit text d'une ligne

        et_Tags.setMaxLines(Integer.MAX_VALUE);

        display_Tags_Suggestion(1, getActivity().getResources().getStringArray(R.array.tags_suggestion01));
        display_Tags_Suggestion(2, getActivity().getResources().getStringArray(R.array.tags_suggestion02));
        display_Tags_Suggestion(3, getActivity().getResources().getStringArray(R.array.tags_suggestion03));
        display_Tags_Suggestion(4, getActivity().getResources().getStringArray(R.array.tags_suggestion04));
        display_Tags_Suggestion(5, getActivity().getResources().getStringArray(R.array.tags_suggestion05));


        // -- CONFIRMATION SCREEN ------------------------------------------------------------------

        btn_back_to_editor.setTranslationY(bottom_menu_offscreen_position);

    }

    private void init_preview() {

        swipeStack = new ArrayList<>();

        cardStackAdapter = new CardStackAdapter(getActivity(), swipeStack, CardStackAdapter.SEND_DECK);

        card_stack_preview.setTypeOfDeck(CardStackAdapter.SEND_DECK);

        card_stack_preview.setOrientation(SwipeTouchListener.Orientation.Vertical);

        card_stack_preview.setAdapter(cardStackAdapter);
    }


    private void init_loginAndTracker() {

            /*
            todo later
            ((MainActivity)MainActivity.context).setLoginPage(MainActivity.LOGIN_FROM_EDITOR);

            MentorApp application = (MentorApp) ((MainActivity)MainActivity.context).getApplication();
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

    private void set_popup_url(String link, final int card_type) {

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

                            link_preview1.setVisibility(View.GONE);

                            tv_preview2.setText("");

                            link_preview2.setVisibility(View.GONE);
                        }

                        if(card_content_type == CARD_TEXT) et_Content.requestFocus();

                        else if(card_content_type == CARD_PICT) et_Legend.requestFocus();

                        text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        //image_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        set_btn_preview_click();
                    }
                })

                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        link_preview1.setVisibility(View.GONE);

                        link_preview2.setVisibility(View.GONE);

                        tv_preview1.setText("");

                        tv_preview2.setText("");

                        //btn_add_link.setVisibility(View.VISIBLE);

                        check_linkOk = true;

                        if(card_content_type == CARD_TEXT) et_Content.requestFocus();

                        else if(card_content_type == CARD_PICT) et_Legend.requestFocus();

                        //text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        image_editor_scroller.fullScroll(View.FOCUS_DOWN);

                        set_btn_preview_click();
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

                et_Content.setTextSize(TextSize.scale(countRemaining, false));

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

                //text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                updateCount(s); // update du compteur a chaque fois que l'utilisateur tape sur le clavier

                check_contentExist = currentContentLenght > 0;

                check_contentLengthIsOk = currentContentLenght <= CardData.MAX_THOUGHT_LENGTH;

                set_btn_preview_click();


                /*
                if(s.toString().contains("\n")) {

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

                if (hasFocus && !tag_helper_is_visible) {

                    hide_tags_editor();
                }
            }
        });

        link_preview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_popup_url(tv_preview1.getText().toString(), card_content_type);

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

                set_btn_preview_click();

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

                set_popup_url(tv_preview2.getText().toString(), card_content_type);

                hide_tags_editor();
            }
        });

        close_image_editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choose_text_editor();
            }
        });

        // ---

        btn_add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_popup_url(tv_preview2.getText().toString(), card_content_type);

            }
        });

        btn_add_pict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = Intent.createChooser(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), "Upload a picture with:");

                ((MainActivity) MainActivity.context).startActivityForResult(intent, RESULT_NEW_CARD_PICT);

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

                    if(card_content_type == CARD_TEXT) {

                        String current = et_Content.getText().toString();

                        et_Content.setText("");
                        et_Content.requestFocus();
                        et_Content.append(current);

                    } else if(card_content_type == CARD_PICT) {

                        String current = et_Legend.getText().toString();

                        et_Legend.setText("");
                        et_Legend.requestFocus();
                        et_Legend.append(current);
                    }
                }

                else if (s.toString().endsWith(" ")) {

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

                if (hasFocus) {

                    btn_icon_tags.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_pound_blue_48dp));

                } else {

                    if (et_Tags.length() != 0) et_Tags.setText("");

                    setBtnTags_PassiveState();

                    displayTagPassiveIcon(); //btn_icon_tags.setImageDrawable(tag_icon_grey_state);

                }

            }
        });

    }



    // BOTTOM BUTTONS ------------------------------------------------------------------------------



    private void hide_tag_edit_text(boolean hide) {

        if(hide) {

            tv_select_tag.setVisibility(View.VISIBLE);

            fr_separator.setVisibility(View.GONE);

            et_Tags.setVisibility(View.GONE); //et_Tags.setAlpha(0);

            tv_countTags.setVisibility(View.GONE);

        } else {

            tv_select_tag.setVisibility(View.GONE);

            fr_separator.setVisibility(View.VISIBLE);

            et_Tags.setVisibility(View.VISIBLE);

            tv_countTags.setVisibility(View.VISIBLE);
        }

    }

    public void show_tag_helper(boolean show) {

        tag_helper_is_visible = show;

        if(show) {

            tag_helper.animate().alpha(1)
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            card_of_tags.animate().translationY(0).alpha(1)
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            MainActivity.hideSoftKeyboard(et_Tags);

            hide_tag_edit_text(true);

        } else {

            tag_helper.animate().alpha(0)
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            card_of_tags.animate().translationY(card_of_tags_offscreen_position).alpha(0)
                    .setInterpolator(new AccelerateInterpolator(1.5f))
                    .setInterpolator(new DecelerateInterpolator(1.5f))
                    .start();

            hide_tag_edit_text(false);
        }
    }

    public void close_tag_editor() {

        hide_tags_editor();

        if (card_content_type == CARD_TEXT) et_Content.requestFocus();

        else if (card_content_type == CARD_PICT) et_Legend.requestFocus();

        btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

    }

    private void init_btn_tag() {

        btn_tag.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (et_Tags.hasFocus() || tagsArrayList.isEmpty() && tag_helper_is_visible) {

                    close_tag_editor();

                } else if (tagsArrayList.size() == CardData.MAX_TAGS && tag_editor.getTranslationY() != tag_editor_offscreen_position) {

                    hide_tags_editor();

                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                /*} else if (tagsArrayList.isEmpty() && tag_helper_is_visible){

                    show_tag_helper(false);

                    hide_tags_editor();

                    if (card_content_type == CARD_TEXT) et_Content.requestFocus();

                    else if (card_content_type == CARD_PICT) et_Legend.requestFocus();

                    */

                } else {



                    btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                    text_editor_scroller.fullScroll(View.FOCUS_DOWN);

                    tag_editor.animate().translationY(0)
                            .setInterpolator(new DecelerateInterpolator(1.5f))
                            .start();

                    if (tagsArrayList.isEmpty()) show_tag_helper(true);

                    else et_Tags.requestFocus();



                }
            }
        });
    }

    private void hide_tags_editor() {

        show_tag_helper(false);

        tag_editor.animate().translationY(tag_editor_offscreen_position)
                .setInterpolator(new AccelerateInterpolator(1.5f))
                .setInterpolator(new DecelerateInterpolator(1.5f))
                .start();

    }

    private void displayTagPassiveIcon() {

        if(tagsArrayList.size() >= CardData.MIN_TAGS) btn_icon_tags.setImageDrawable(tag_icon_grey_state);

        else btn_icon_tags.setImageDrawable(tag_icon_passive_state);

        btn_text_add_tags.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));
    }


    // TAGS SUGGESTION ------------------------------------------

    @Bind(R.id.tags_default_suggestion_01) public RowLayout tags_default_suggestion_01;
    @Bind(R.id.tags_default_suggestion_02) public RowLayout tags_default_suggestion_02;
    @Bind(R.id.tags_default_suggestion_03) public RowLayout tags_default_suggestion_03;
    @Bind(R.id.tags_default_suggestion_04) public RowLayout tags_default_suggestion_04;
    @Bind(R.id.tags_default_suggestion_05) public RowLayout tags_default_suggestion_05;

    private void display_Tags_Suggestion(int bundle, String[] TagsList) {

        switch (bundle) {

            case 1:
                tags_default_suggestion_01.removeAllViews();
                for (final String tag : TagsList) tags_default_suggestion_01.addView(new TagView(MainActivity.context, 1, tag));
                break;

            case 2:
                tags_default_suggestion_02.removeAllViews();
                for (final String tag : TagsList) tags_default_suggestion_02.addView(new TagView(MainActivity.context, 2, tag));
                break;

            case 3:
                tags_default_suggestion_03.removeAllViews();
                for (final String tag : TagsList) tags_default_suggestion_03.addView(new TagView(MainActivity.context, 3, tag));
                break;

            case 4:
                tags_default_suggestion_04.removeAllViews();
                for (final String tag : TagsList) tags_default_suggestion_04.addView(new TagView(MainActivity.context, 4, tag));
                break;

            case 5:
                tags_default_suggestion_05.removeAllViews();
                for (final String tag : TagsList) tags_default_suggestion_05.addView(new TagView(MainActivity.context, 5, tag));
                break;
        }
    }




    // ADD & REMOVE TAG ------------------------------------------------------------------------

    public void addTagToContent(int source, String tag) {

        if (!tagsArrayList.contains(tag) && (tagsArrayList.size() + 1) <= CardData.MAX_TAGS) {

            tagsArrayList.add(tag);

            Tags.displaySelected_EditTag(tag, true, MainActivity.newContent_editor);

            et_Tags.setText("");

            if (tagsArrayList.size() == CardData.MAX_TAGS) et_Tags.setVisibility(View.GONE);

            update_tag_editor_offscreen_position();

        } else {

            et_Tags.setText("");

            if(source == ADD_TAG_FROM_KEYBOARD_SPACE) et_Tags.append(tag);

            Message.message(MainActivity.context, "tag is already selected");

        }


        /*

        if(source == ADD_TAG_FROM_KEYBOARD_SPACE) {

            if (!tagsArrayList.contains(tag) && (tagsArrayList.size() + 1) <= CardData.MAX_TAGS) {

                tagsArrayList.add(tag);

                Tags.displaySelected_EditTag(tag, true, MainActivity.newContent_editor);

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

                Tags.displaySelected_EditTag(tag, true, MainActivity.newContent_editor);

                et_Tags.setText("");

                if (tagsArrayList.size() == CardData.MAX_TAGS) et_Tags.setVisibility(View.GONE);

                update_tag_editor_offscreen_position();

            } else {

                et_Tags.setText("");

                //et_Tags.append(tag);

                Message.message(MainActivity.context, "tag is already selected");

            }
        }
        */

        if(tagsArrayList.size() == 1) show_tag_helper(false);

        set_TagsCounter();

        setET_tags_Hint();

        setBtnTags_PassiveState();

        if(card_content_type == CARD_TEXT) check_minTagsRequired = tagsArrayList.size() >= CardData.MIN_TAGS;

        else if(card_content_type == CARD_PICT) check_minTagsRequired = tagsArrayList.size() >= CardData.MIN_TAGS;

        set_btn_preview_click();

    }

    public void deleteTagFromThought(CardView cardView, String tag) {

        et_Tags.requestFocus(); // necessaire quand on passe de 12 a 11 tags

        tags_row.removeView(cardView);

        et_Tags.setVisibility(View.VISIBLE);

        tagsArrayList.remove(tag);

        if(tagsArrayList.isEmpty()) {

            //Message.message(getContext(), "empty");

            et_Tags.setHint(hint_tags);

            show_tag_helper(true);
        }

        update_tag_editor_offscreen_position();

        set_TagsCounter();

        setBtnTags_PassiveState();

        setET_tags_Hint();



        if(card_content_type == CARD_TEXT) check_minTagsRequired = tagsArrayList.size() >= CardData.MIN_TAGS;

        else if(card_content_type == CARD_PICT) check_minTagsRequired = tagsArrayList.size() >= 2;

        set_btn_preview_click();

    }

    private void set_tags_indicator(int nb_active) {

        if(nb_active == 0) {

            for(int i = 0; i < CardData.MIN_TAGS; i++) {

                tagsIndicator.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tag_orange));
                tagsIndicator.get(i).setVisibility(View.VISIBLE);

            }

            for(int i = (CardData.MIN_TAGS + 1); i < 7; i++) tagsIndicator.get(i).setVisibility(View.INVISIBLE);

        } else {

            int i;

            for (i = 0; i < nb_active; i++) {

                tagsIndicator.get(i).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tag_on));
                tagsIndicator.get(i).setVisibility(View.VISIBLE);

            }

            if(i < CardData.MIN_TAGS) {

                int index;

                for (index = i; index <= CardData.MIN_TAGS; index++) {

                    tagsIndicator.get(index).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tag_orange));
                    tagsIndicator.get(index).setVisibility(View.VISIBLE);

                }

                i = index;
            }

            for (int index = i; index < 7; index++) {

                tagsIndicator.get(i).setVisibility(View.INVISIBLE);

            }
        }
    }


    private void set_TagsCounter() {

        tv_countTags.setText(tagsArrayList.size()+"/"+CardData.MAX_TAGS);

        set_tags_indicator(tagsArrayList.size());

    }

    private void setET_tags_Hint() {

        if(tagsArrayList.size() < CardData.MIN_TAGS) et_Tags.setHint(getActivity().getResources().getString(R.string.et_hint_tags));

        else et_Tags.setHint(getActivity().getResources().getString(R.string.et_short_hint_tags));
    }

    private void setBtnTags_PassiveState() {

        if (tagsArrayList.size() < CardData.MIN_TAGS) tag_icon_passive_state = tag_icon_orange_state;

        else tag_icon_passive_state = tag_icon_grey_state;
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

    public void setHint_tags(String hint_tags) {
        this.hint_tags = hint_tags;
    }

    public void setTagsArrayList(ArrayList<String> tagsArrayList) {
        this.tagsArrayList = tagsArrayList;
    }

    public void setHint_content(String hint_content) {
        this.hint_content = hint_content;
    }


    // PREVIEW CONTENT -------------------------------------------------------------------------- //



    private void populatePreviewLayout() {

        if(card_content_type == CARD_TEXT) {

            newCard = new CardData(
                    ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_PICT_URL),
                    ((MainActivity) getContext()).localData.getCurated(),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_CURATED_TAGLINE),
                    "", // no parent
                    "", // no image
                    et_Content.getText().toString(),
                    tv_preview1.getText().toString(),
                    tagsArrayList,
                    ((MainActivity) getContext()).localData.getGPS(SharedPref.KEY_LATITUDE),
                    ((MainActivity) getContext()).localData.getGPS(SharedPref.KEY_LONGITUDE),
                    0, 0, 0);

        } else if(card_content_type == CARD_PICT) {

            newCard = new CardData(
                    ((MainActivity)MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_FULL_NAME),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_PICT_URL),
                    ((MainActivity) getContext()).localData.getCurated(),
                    ((MainActivity) getContext()).localData.getUserData(SharedPref.KEY_CURATED_TAGLINE),
                    "", // no parent
                    CardData.PICT_NOT_UPLOADED_YET, // use URI instead
                    et_Legend.getText().toString(),
                    tv_preview2.getText().toString(),
                    tagsArrayList,
                    ((MainActivity) getContext()).localData.getGPS(SharedPref.KEY_LATITUDE),
                    ((MainActivity) getContext()).localData.getGPS(SharedPref.KEY_LONGITUDE),
                    0, 0, 0);

        }

        swipeStack = new ArrayList<>();

        swipeStack.add(newCard);

        cardStackAdapter = new CardStackAdapter(getActivity(), swipeStack, CardStackAdapter.SEND_DECK);

        card_stack_preview.setTypeOfDeck(CardStackAdapter.SEND_DECK);

        card_stack_preview.setOrientation(SwipeTouchListener.Orientation.Vertical);

        card_stack_preview.setAdapter(cardStackAdapter);

    }


    private void disable_btn_preview() {

        btn_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_preview_disable();
            }
        });
    }


    private void set_btn_preview_click() {

        if(card_content_type == CARD_TEXT) {

            if (check_contentExist && check_contentLengthIsOk && check_minTagsRequired && check_linkOk) {

                btn_icon_preview.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_send_white_48dp));

                btn_text_preview.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                btn_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (et_Tags.hasFocus() && et_Tags.getText().length() != 0)

                            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());

                        btn_preview_enable();
                    }
                });

            } else {

                btn_icon_preview.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_send_grey600_48dp));

                btn_text_preview.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                btn_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btn_preview_disable();
                    }
                });
            }

        } else if(card_content_type == CARD_PICT) {

            if (check_legendLengthIsOk && check_minTagsRequired && check_linkOk) {

                btn_icon_preview.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_send_white_48dp));

                btn_text_preview.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.white));

                btn_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (et_Tags.hasFocus() && et_Tags.getText().length() != 0)

                            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());

                        btn_preview_enable();
                    }
                });

            } else {

                btn_icon_preview.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_send_grey600_48dp));

                btn_text_preview.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.editor_bottom_text));

                btn_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        btn_preview_disable();
                    }
                });
            }
        }
    }


    private void btn_preview_disable() {

        if(et_Tags.hasFocus() && et_Tags.getText().length() != 0)

            addTagToContent(ADD_TAG_FROM_BTN_SEND, et_Tags.getText().toString());


        String message = "";

        if(card_content_type == CARD_TEXT) {

            if (tv_preview1.getText().length() != 0 &&
                    (!tv_preview1.getText().toString().startsWith("http://") || !tv_preview1.getText().toString().startsWith("https://")))

                message = "Your link must begin by 'http://' or 'https://'";

            if (!check_minTagsRequired) message = "Enter at least 1 tag";

            if (!check_contentLengthIsOk) message = "Your post is too long";

            if (!check_contentExist) {

                message = "Share something";

                et_Content.requestFocus();
            }

            if (message.equals("Enter at least 1 tag")) {

                tag_editor.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .start();

                et_Tags.requestFocus();

                show_tag_helper(true);
            }

        } else if(card_content_type == CARD_PICT) {

            if (tv_preview2.getText().length() != 0 &&
                    (!tv_preview2.getText().toString().startsWith("http://") || !tv_preview2.getText().toString().startsWith("https://")))

                message = "Your link must begin by 'http://' or 'https://'";

            if (!check_minTagsRequired) message = "Enter at least 1 tag";

            if (!check_legendLengthIsOk) message = "Your text is too long";


            if (message.equals("Enter at least 1 tag")) {

                tag_editor.animate().translationY(0)
                        .setInterpolator(new DecelerateInterpolator(1.5f))
                        .start();

                et_Tags.requestFocus();

                show_tag_helper(true);

            }
        }

        if (!message.isEmpty()) Message.message(MainActivity.context, message);

        else btn_preview_enable();
    }


    private void btn_preview_enable() {

        if(imm.isAcceptingText()) {

            if(card_content_type == CARD_TEXT) MainActivity.hideSoftKeyboard(et_Content);

            else if(card_content_type == CARD_PICT) MainActivity.hideSoftKeyboard(et_Legend);

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_preview();
                }
            }, 60);

        } else {

            show_preview();
        }


    }


    private void show_preview() {

        populatePreviewLayout();

        preview_card_include.setVisibility(View.VISIBLE);

        // switch menu
        slideBottomMenuTo(btn_back_to_editor);


        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                switchEditorTo(preview_card_include);

            }
        }, 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                enableEditor_Et(false);

            }
        }, 60);

    }


    // SEND THOUGHT -------------------------------------------------------------------------------

    private boolean check_linkOk = true;
    private boolean check_contentExist = false;
    private boolean check_contentLengthIsOk = false;
    private boolean check_minTagsRequired = false;

    private boolean check_legendLengthIsOk = true;

    // confirm screen

    public void onSwipedUp_SendCard() {

        if(!tagsArrayList.isEmpty() && card_content_type == CARD_TEXT )

            ParseRequest.insert_card_text(getActivity(), newCard);

        else if(!tagsArrayList.isEmpty() && card_content_type == CARD_PICT )

            ParseRequest.insert_card_pict(getActivity(), newCard, selectedImage);


        closeEditorPanel();

        reset_text_editor();
    }

    public void backToEditor() {

        enableEditor_Et(true);

        slideBottomMenuTo(editor_bottom_menu1);

        switchEditorTo(content_card);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                preview_card_include.setVisibility(View.INVISIBLE);

            }
        }, 50);

    }

    private void init_btn_back_to_editor() {

        btn_back_to_editor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                backToEditor();

            }

        });
    }


    private void reset_text_editor() {

        selectedImage = null;

        card_content_type = CARD_TEXT;

        text_editor_scroller.setVisibility(View.VISIBLE);
        image_editor_scroller.setVisibility(View.GONE);

        tags_row.removeViews(0, tagsArrayList.size());
        tagsArrayList = new ArrayList<>();
        et_Tags.setText("");
        et_Tags.setHint(hint_tags);

        link_preview1.setVisibility(View.INVISIBLE);
        tv_preview1.setText("");

        link_preview2.setVisibility(View.INVISIBLE);
        tv_preview2.setText("");

        //btn_add_link.setVisibility(View.VISIBLE);

        set_TagsCounter();

        setBtnTags_PassiveState();

        et_Content.setText("");
        et_Legend.setText("");

        tag_icon_passive_state = tag_icon_orange_state;

        btn_icon_tags.setImageDrawable(tag_icon_passive_state);

        check_linkOk = true;
        check_contentExist = false;
        check_contentLengthIsOk = false;
        check_minTagsRequired = false;


        content_card.setScaleX(1f);
        content_card.setScaleY(1f);
        content_card.setAlpha(1f);
        content_card.setTranslationY(1200f);


        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                preview_card_include.setVisibility(View.INVISIBLE);
                preview_card_include.setScaleX(1f);
                preview_card_include.setScaleY(1f);
                preview_card_include.setAlpha(0f);
                preview_card_include.setTranslationX(0);

                content_card.animate().translationY(0)
                        .setInterpolator(new AccelerateInterpolator(1.5f))
                        .setInterpolator(new DecelerateInterpolator(0.4f))
                        .start();

                enableEditor_Et(true);

                slideBottomMenuTo(editor_bottom_menu1);

            }
        }, 2000);
    }

    private void closeEditorPanel() {

        Runnable myRunnable;

        ((MainActivity)MainActivity.context).setMenuAndFabVisibility(MainActivity.SHOW_BOTTOM_MENU); // avec delay

        myRunnable = new Runnable(){

            @Override
            public void run() {

                btn_back_to_editor.animate().translationY(bottom_menu_offscreen_position)
                        .setInterpolator(new AccelerateInterpolator(speed_factor))
                        .start();

                ((MainActivity)getContext()).hideEditor_ShowMenu();

                ((MainActivity)MainActivity.context).new_content_panel_structure.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);


            }

        };

        Handler myHandler = new Handler();

        myHandler.postDelayed(myRunnable, 0);

        //Handler myHandler = new Handler();

        //myHandler.postDelayed(myRunnable, 350);

    }

    public void enableEditor_Et(boolean activate) {

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

    private void switchEditorTo(View cardToShow) {

        if(cardToShow == content_card) {

            preview_card_include.animate().scaleX(1.2f).scaleY(1.2f)
                    .alpha(0f)
                    .setInterpolator(new AccelerateInterpolator(0.1f))
                    .start();

            content_card.animate().scaleX(1f).scaleY(1f)
                    .alpha(1f)
                    .setInterpolator(new AccelerateInterpolator(0.05f))
                    .start();
        }

        if(cardToShow == preview_card_include) {

            content_card.animate().scaleX(0.7f).scaleY(0.7f)
                    .alpha(0f)
                    .setInterpolator(new AccelerateInterpolator(0.1f))
                    .start();

            preview_card_include.animate().scaleX(1f).scaleY(1f)
                    .alpha(1f)
                    .setInterpolator(new AccelerateInterpolator(0.1f))
                    .start();
        }

    }

    private void slideBottomMenuTo(LinearLayout menuToShow){

        if(menuToShow == editor_bottom_menu1) {

            editor_bottom_menu1.animate().translationY(0)
                    .setInterpolator(new AccelerateInterpolator(speed_factor))
                    .setInterpolator(new DecelerateInterpolator(speed_factor))
                    .start();

            btn_back_to_editor.animate().translationY(bottom_menu_offscreen_position)
                    .setInterpolator(new AccelerateInterpolator(speed_factor))
                    .setInterpolator(new DecelerateInterpolator(speed_factor))
                    .start();
        }

        if(menuToShow == btn_back_to_editor) {

            editor_bottom_menu1.animate().translationY(bottom_menu_offscreen_position)
                    .setInterpolator(new AccelerateInterpolator(speed_factor))
                    .setInterpolator(new DecelerateInterpolator(speed_factor))
                    .start();

            btn_back_to_editor.animate().translationY(0)
                    .setInterpolator(new AccelerateInterpolator(speed_factor))
                    .setInterpolator(new DecelerateInterpolator(speed_factor))
                    .start();

        }
    }


    //

    public void choose_pict_editor(Uri selectedImage) {

        this.selectedImage = selectedImage;

        card_content_type = CARD_PICT;

        text_editor_scroller.setVisibility(View.GONE);
        image_editor_scroller.setVisibility(View.VISIBLE);

        Picasso.with(MainActivity.context)

                .load(selectedImage)
                //.resize(200, 200)
                //.transform(new ResizeTransformation(1280))
                .fit().centerInside()
                //.fit().centerCrop()
                //.placeholder(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict))
                .into(pict_placeholder);

        et_Legend.setText("");
        et_Legend.requestFocus();

        //if(tv_preview2.getText().toString().isEmpty()) btn_add_link.setVisibility(View.VISIBLE);
        //else btn_add_link.setVisibility(View.INVISIBLE);
        tv_count_thought_chars.setVisibility(View.GONE);

        et_Tags.setHint(getActivity().getResources().getString(R.string.et_hint_tags));
    }

    private void choose_text_editor() {

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