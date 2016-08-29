package co.forum.app.tools;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.forum.app.Data.ParseCard;
import co.forum.app.ContentEditor.NewContent_Editor;
import co.forum.app.ContentEditor.Comment_Editor;
import co.forum.app.MainActivity;
import co.forum.app.R;

public class Tags {

    public static ArrayList<String> Spliter(String hashtags) {
        String[] hashtagsArray;

        // traitement des tags saisis --------------------- //
        // todo - gestion de multiples espaces (exception)

        if (hashtags.length() == 0) return new ArrayList<>();

        else {
            if (hashtags.contains(" ")) {
                hashtagsArray = hashtags.split(" ");
            } else {
                hashtagsArray = new String[]{hashtags};
            }

            // 2 - on supprime le '#' s'il y en a un
            ArrayList<String> tagsList = new ArrayList<>();
            for (String tag : hashtagsArray) {
                if (String.valueOf(tag.charAt(0)).equals("#")) {
                    tagsList.add(tag.substring(1));
                } else {
                    tagsList.add(tag);
                }
            }

            return tagsList;
        }

    }


    public final static int TAG = 0;
    public final static int PROFIL_TAG = 8;
    public final static int TAG_MINI_ADAPTER = 2;
    public final static int TAG_MINI_ADAPTER_BOLD = 4;
    public final static int TAG_SEARCH_BOLD = 5;
    public final static int TAG_EDITOR = 7;
    public final static int AUTHOR_SEARCH_SMALL = 6;
    public final static int TAG_MINI_HEADER = 3;


    public static String TagsString(ArrayList<String> tagList) {

        String tagsString = "";

        for (String tag : tagList)

            tagsString = tagsString + "#" + tag + " ";

        return tagsString;

    }


    public static void setCardView_Layout(int typeOfTag, CardView cardView, String tag) {

        /*
        if (typeOfTag == TAG_MINI_ADAPTER_BOLD) {
            int cardViewRadius = 5;
            ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(cvParams);
            cardView.setCardElevation(0);
            cardView.setRadius(cardViewRadius);
            TextView textView = (TextView) cardView.getChildAt(0);
            textView.setTag(tag);
            textView.setTextSize(13f);
            //textView.setTypeface(null, Typeface.BOLD);
            textView.setPadding(13, 5, 13, 5);
            textView.setText("#" + tag);
            textView.setBackgroundResource(co.mentor.app.R.color.tag_bg_off2);
            textView.setTextColor(context.getResources().getColor(co.mentor.app.R.color.tag_tx_off3));
        }
        */

        if (typeOfTag == PROFIL_TAG) {

            int cardViewRadius = 5;
            ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(cvParams);
            cardView.setCardElevation(2);
            cardView.setRadius(cardViewRadius);

            TextView textView = (TextView) cardView.getChildAt(0);

            textView.setTag(tag);
            textView.setTextSize(15f);
            textView.setPadding(13, 5, 13, 5);
            textView.setText("#" + tag);

            textView.setBackgroundResource(R.color.colorAccent);
            textView.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_selected));

        }

        if (typeOfTag == TAG) {

            int cardViewRadius = 5;
            ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(cvParams);
            cardView.setCardElevation(0);
            cardView.setRadius(cardViewRadius);

            TextView textView = new TextView(MainActivity.context);
            cardView.addView(textView);

            textView.setTag(tag);
            textView.setTextSize(22f);
            textView.setPadding(17, 10, 17, 10);
            textView.setText("#" + tag);

            textView.setBackgroundResource(R.color.tag_bg_off);
            textView.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off));

        }

        if (typeOfTag == TAG_EDITOR) {
            int cardViewRadius = 5;
            ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(cvParams);
            cardView.setCardElevation(0);
            cardView.setRadius(cardViewRadius);

            TextView textView = new TextView(MainActivity.context);
            cardView.addView(textView);

            textView.setTag(tag);
            textView.setTextSize(17f);
            textView.setPadding(13, 5, 13, 5);
            textView.setText("#" + tag);

            textView.setBackgroundResource(R.color.tag_bg_off);
            textView.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off));
        }

        if (typeOfTag == TAG_MINI_ADAPTER) {

            int cardViewRadius = 5;
            ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(cvParams);
            cardView.setCardElevation(0);
            cardView.setRadius(cardViewRadius);
            TextView textView = (TextView) cardView.getChildAt(0);
            textView.setTag(tag);
            textView.setTextSize(13f);
            textView.setPadding(13, 5, 13, 5);
            textView.setText("#" + tag);

            textView.setBackgroundResource(R.color.tag_bg_off);
            textView.setTextColor(ContextCompat.getColor(MainActivity.context, R.color.tag_tx_off));
        }
    }


    public static void populateTagsRowDark(Context context, RowLayout tags_row, ArrayList<String> tags_list) {

        tags_row.removeAllViews();

        for (String tag : tags_list) {

            TagView tagView = new TagView(context, tag, TagView.COLOR_DARK);

            tags_row.addView(tagView);

        }
    }



    public static void populateTagsRowFollow(Context context, RowLayout tags_row, ArrayList<String> tags_list) {

        tags_row.removeAllViews();

        for (String tag : tags_list) {

            TagView tagView = new TagView(context, tag, TagView.COLOR_NORMAL);

            tags_row.addView(tagView);

        }
    }


    public static void populateTagsRow(Context context, RowLayout tags_row, ArrayList<String> tags_list) {

        int i = 0;

        for (String tag : tags_list) {

            ((TagView) tags_row.getChildAt(i)).init(tag, TagView.COLOR_NORMAL);

            tags_row.getChildAt(i).setVisibility(View.VISIBLE);

            i++;

        }

        int position;
        if (tags_list.size() != 0) position = 11;
        else position = 10;


        while (i <= position) {
            tags_row.getChildAt(i).setVisibility(View.GONE);
            i++;
        }

        // pour une barre vide
        if (tags_list.size() == 0) {

            ((TagView) tags_row.getChildAt(0)).init(" ", TagView.COLOR_NORMAL);

            tags_row.getChildAt(0).setVisibility(View.INVISIBLE);

        }

    }


    public static void populateTagsRowSearch(Context context, RowLayout tags_row, ArrayList<String> tags_list, ArrayList<String> tagsSelected_Active) {

        int i = 0;

        if (tagsSelected_Active != null) {

            for (String tag : tagsSelected_Active) {

                ((TagView) tags_row.getChildAt(i)).init(tag, TagView.COLOR_SELECTED);

                tags_row.getChildAt(i).setVisibility(View.VISIBLE);

                i++;

            }


            for (String tag : tags_list) {

                if (!tagsSelected_Active.contains(tag)) {

                    ((TagView) tags_row.getChildAt(i)).init(tag, TagView.COLOR_NORMAL);

                    tags_row.getChildAt(i).setVisibility(View.VISIBLE);

                    i++;
                }

            }

            int position;
            if (tags_list.size() != 0) position = 11;
            else position = 10;


            while (i <= position) {
                tags_row.getChildAt(i).setVisibility(View.GONE);
                i++;
            }

        }
    }


    public static Map<String, Integer> tagsMap_from_list(List<ParseCard> parseCardList, final ArrayList<String> tagsChosen) {

        Map<String, Integer> tagsMap = new HashMap<>();

        for (ParseCard parseCard : parseCardList) {

            for (String tag : parseCard.getTags()) {

                if (!tagsChosen.contains(tag)) {

                    if (tagsMap.containsKey(tag))
                        tagsMap.put(tag, tagsMap.get(tag) + 1);

                    else tagsMap.put(tag, 1);
                }
            }
        }

        return tagsMap;
    }


    // EDITOR PANEL ------------------------------------------------------------------------------- //

    // tag_row (tag only)
    public static void displaySelected_EditTag(final String tag, boolean isNew, final Fragment editorFragment) {

        final CardView cardView = new CardView(MainActivity.context);

        setCardView_Layout(TAG_EDITOR, cardView, tag); //setCardView_Layout(TAG, cardView, tag);

        if (isNew) {

            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ((NewContent_Editor) editorFragment).deleteTagFromThought(cardView, tag);

                }
            });

            ((NewContent_Editor) editorFragment).tags_row.

                    addView(cardView, ((NewContent_Editor) editorFragment).tags_row.getChildCount() - 1);

        } else {


            cardView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    ((Comment_Editor) editorFragment).deleteTagFromThought(cardView, tag);

                }
            });

            ((Comment_Editor) editorFragment).tags_row.

                    addView(cardView, ((Comment_Editor) editorFragment).tags_row.getChildCount() - 1);
        }
    }
}
