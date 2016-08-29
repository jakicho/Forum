package co.forum.app.Data;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.forum.app.Adapters.CardStackAdapter;
import co.forum.app.Adapters.CommentRecyclerAdapter;
import co.forum.app.CropActivity;
import co.forum.app.ForumApp;
import co.forum.app.MainActivity;
import co.forum.app.MainSections.ProfilPages.ChatOFrag;
import co.forum.app.R;
import co.forum.app.SharedPref;
import co.forum.app.SubActivity.SubActivity;
import co.forum.app.OnBoardingActivity.OnBoardingActivity;
import co.forum.app.tools.Message;
import co.forum.app.tools.SquareRelativeLayout;
import co.forum.app.tools.Tags;
import co.forum.app.tools.tools;

public class ParseRequest {

    private static int NB_CARDS_TO_LOAD = 24; // 6 avant

    public static void signup(final Context context, final ParseUser parseUser) {

        parseUser.signUpInBackground(new SignUpCallback() {

            public void done(ParseException e) {

                if (e == null) {

                    ((MainActivity) MainActivity.context).localData.setUserConnectedStatus(true);

                    ((MainActivity) MainActivity.context).localData.storeUserData(parseUser);

                    OnBoardingActivity.goBack_toMain(OnBoardingActivity.SIGNUP_FORM);

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.put("listening", false);
                    installation.saveInBackground();

                } else {

                    Message.message(context, "signup failed: " + e);

                    ((OnBoardingActivity) OnBoardingActivity.context).show_Loader(OnBoardingActivity.SIGNUP_FORM, false);
                }
            }
        });
    }

    public static void login(final Context context, String email, String password, final int apiVersion) {

        ParseUser.logInInBackground(email, password, new LogInCallback() {

            private void updateUpvoteCount(final ParseUser currentUser) {

                ParseQuery<ParseUpvote> query = ParseQuery.getQuery("ParseUpvote");

                query.whereEqualTo(ParseUpvote.JOIN_RECEIVER, currentUser);

                query.countInBackground(new CountCallback() {

                    @Override
                    public void done(int count, ParseException e) {

                        if (e == null) {

                            currentUser.put("upvotes_count", count);

                            ((MainActivity) MainActivity.context).localData.setUserData(SharedPref.KEY_UPVOTES_COUNT, count);

                            updateRepliesCount(currentUser);

                        } else
                            Message.message(MainActivity.context, "error - update upvotes count");
                    }
                });

            }

            private void updateRepliesCount(final ParseUser currentUser) {

                ParseQuery<ParseCard> query = ParseQuery.getQuery(ParseCard.class);

                query.whereEqualTo(ParseCard.JOIN_REPLY_PARENT_USER, currentUser);

                query.whereNotEqualTo(ParseCard.JOIN_CONTENT_USER, currentUser);

                query.countInBackground(new CountCallback() {

                    @Override
                    public void done(int count, ParseException e) {

                        if (e == null) {

                            currentUser.put("replies_count", count);

                            currentUser.saveInBackground();

                            ((MainActivity) MainActivity.context).localData.setUserData(SharedPref.KEY_REPLIES_COUNT, count);

                            ((MainActivity) MainActivity.context).profilFragment.update_MainProfilHeader(SharedPref.KEY_UPVOTES_COUNT);

                            ((MainActivity) MainActivity.context).profilFragment.update_MainProfilHeader(SharedPref.KEY_REPLIES_COUNT);

                        } else
                            Message.message(MainActivity.context, "error - update replies count");

                    }
                });
            }

            public void done(final ParseUser currentUser, ParseException e) {

                if (currentUser != null && e == null) {

                    ((MainActivity) MainActivity.context).localData.storeUserData(currentUser);

                    ((MainActivity) MainActivity.context).localData.setUserConnectedStatus(true);

                    OnBoardingActivity.goBack_toMain(OnBoardingActivity.LOGIN_FORM);

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("user", ParseUser.getCurrentUser());
                    installation.put("listening", false);
                    installation.saveInBackground();

                    currentUser.increment("RunCount");

                    currentUser.put("api", apiVersion);

                    currentUser.put("appVersion", context.getResources().getString(R.string.app_version)); //MainActivity.APP_VERSION

                    updateUpvoteCount(currentUser);

                } else {

                    Message.message(context, context.getResources().getString(R.string.message_wrong_login));

                    ((OnBoardingActivity) OnBoardingActivity.context).show_Loader(OnBoardingActivity.LOGIN_FORM, false);
                }
            }

        });
    }

    public static void resetPassword(final String email_adress) {

        ParseUser.requestPasswordResetInBackground(email_adress,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {

                            Message.message(MainActivity.context, (MainActivity.context).getResources().getString(R.string.message_reset_password));

                            ((OnBoardingActivity) OnBoardingActivity.context).tab_login.loginFormPager.setCurrentItem(0, true);


                        } else Message.message(MainActivity.context, (MainActivity.context).getResources().getString(R.string.message_reset_retry));
                    }
                });


        /*


        final ParseQuery<ParseUser> queryUser= ParseQuery.getQuery("_User");

        queryUser.whereEqualTo("email", email_adress);

        queryUser.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e== null) {

                    Message.message(MainActivity.context, "Check your inbox and spam, we've send you an email!");


                    // si l'email exist, generer un nouveau mot de passe a 7caracteres et l'envoyer
                    String passwd = tools.getRandomString(8);

                    parseUser.setPassword(passwd);
                    parseUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                Message.message(MainActivity.context, "savec new password");
                            } else {
                                Message.message(MainActivity.context, "error: " + e);
                            }
                        }
                    });



                    //LoginFragment.et_email_recovery.setText("");
                    //LoginFragment.login_section.setVisibility(View.VISIBLE);
                    //LoginFragment.reset_section.setVisibility(View.GONE);




                } else {

                    Message.message(MainActivity.context,  "email doesn't exist");
                }
            }
        });

        */

    }

    // caller
    public static final int GLOBAL_DECK = 0;
    public static final int DEMO_DECK = 777;

    public static final int MAIN_USER_PROFIL_DECK = 1;
    public static final int OTHER_USER_PROFIL_DECK = 11;

    public static final int MAIN_USER_CARD_GRID = 2;
    public static final int OTHER_USER_CARD_GRID = 22;

    public static final int MAIN_USER_COMMENT_GRID = 3;
    public static final int OTHER_USER_COMMENT_GRID = 33;

    public static final int MAIN_USER_CHAT_LIST = 6;
    public static final int OTHER_USER_CHAT_LIST = 66;


    public static final int CARD_REPLIES = 4;
    public static final int CARD_CHAT = 5;
    public static final int CARD_STORY = 55;




    // SUBSCRIPTION SEARCH ----------------


    public static void get_mostFollowedTags() {

        ParseQuery<ParseTagsNbFollowers> tagQuery = ParseQuery.getQuery("ParseTagsNbFollowers");

        tagQuery.orderByDescending("nb_followers")
                .whereNotEqualTo("nb_followers", 0)
                .setLimit(50)
                .findInBackground(new FindCallback<ParseTagsNbFollowers>() {

                    @Override
                    public void done(List<ParseTagsNbFollowers> list, ParseException e) {

                        if (e == null)
                            ((SubActivity) SubActivity.context).followFragment.display_Most_Followed_Tags(list);

                        else Message.message(MainActivity.context, "error \n" + e);
                    }
                });
    }

    public static void get_nb_followers(final ArrayList<String> tagsHeader) {

        ParseQuery<ParseTagsNbFollowers> query = ParseQuery.getQuery("ParseTagsNbFollowers");

        query
                .whereContainsAll("tags", tagsHeader)
                .whereEqualTo("nb_tags", tagsHeader.size())
                .getFirstInBackground(new GetCallback<ParseTagsNbFollowers>() {
                    @Override
                    public void done(ParseTagsNbFollowers result, ParseException e) {

                        int nb_follower;

                        if (e == null) nb_follower = result.get_nb_followers();

                        else nb_follower = 0;

                        String follower;

                        if (nb_follower == 0) follower = " follower - Be the first to follow this topic :-)";

                        else if (nb_follower == 1) follower = " follower";

                        else follower = " followers";

                        ((SubActivity) SubActivity.context).followFragment.tv_nbFollowers.setText(nb_follower + follower);
                    }
                });
    }

    public static void get_follow_suggestions(final ArrayList<String> tagsHeader, final String tagTyping) {

        ParseQuery<ParseTagsNbFollowers> tagQuery = ParseQuery.getQuery("ParseTagsNbFollowers");

        if (!tagsHeader.isEmpty()) {

            tagQuery.orderByDescending("nb_followers")
                    .whereNotEqualTo("nb_followers", 0)
                    .whereContainsAll("tags", tagsHeader)
                    .setLimit(100)
                    .findInBackground(new FindCallback<ParseTagsNbFollowers>() {

                        @Override
                        public void done(List<ParseTagsNbFollowers> list, ParseException e) {

                            if (e == null)
                                ((SubActivity) SubActivity.context).followFragment.display_Suggested_Tags(list, tagsHeader, tagTyping);

                            else Message.message(MainActivity.context, "error \n" + e);
                        }
                    });

        } else {

            tagQuery.orderByDescending("nb_followers")
                    .whereNotEqualTo("nb_followers", 0)
                    .setLimit(100)
                    .findInBackground(new FindCallback<ParseTagsNbFollowers>() {

                        @Override
                        public void done(List<ParseTagsNbFollowers> list, ParseException e) {

                            if (e == null)
                                ((SubActivity) SubActivity.context).followFragment.display_Suggested_Tags(list, tagsHeader, tagTyping);

                            else Message.message(MainActivity.context, "error \n" + e);
                        }
                    });

        }
    }


    // SEARCH -------------------------

    public static void get_MostUsedTags(final ArrayList<String> defaultList) {

        ParseQuery<ParseTag> tagQuery = ParseQuery.getQuery("ParseTag");

        tagQuery.orderByDescending("counter")
                .setLimit(50)
                .findInBackground(new FindCallback<ParseTag>() {

                    @Override
                    public void done(List<ParseTag> list, ParseException e) {

                        if (e == null) {

                            for (ParseTag tag : list) defaultList.add(tag.getTag());

                            ((MainActivity) MainActivity.context).deckFragment.searchFragment.display_Most_Used_Tags(defaultList);

                            //if(SearchFrag.showToSoon) { }
                        } else {
                            Message.message(MainActivity.context, "error \n" + e);
                        }

                    }
                });
    }


    public static void get_Cards_by_ranking(final ArrayList<String> tagsChosen, String order) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query
                .whereContainsAll("tags", tagsChosen)

                .orderByDescending(order)
                .include(ParseCard.JOIN_CONTENT_USER)

                .findInBackground(new FindCallback<ParseCard>() {

                    @Override
                    public void done(List<ParseCard> parseCardList, ParseException e) {

                        if (parseCardList.size() != 0) {

                            for (ParseCard parseCard : parseCardList) {

                                final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                                parseCard.set_Curated(user.getBoolean("curated"));

                                if (user.getParseFile("profil_pict") != null)

                                    parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                                else parseCard.set_ProfilpictUrl("NULL");

                            }


                            ArrayList<CardData> cardsfounded = CardData.getCardsList(parseCardList, "", false);

                            ((MainActivity) MainActivity.context).deckFragment.display_AllCardsFromSearch(cardsfounded);

                        }
                    }
                });
    }


    public static void get_subscriptionCards(final Context context, final ArrayList<String> tagsChosen, final String order, final int offset) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        if (tagsChosen.contains("how_it_works") || tagsChosen.contains("update")) {

            ParseUser parseUser = ParseUser.createWithoutData(ParseUser.class, MainActivity.ADMIN_ID);

            query
                    .whereContainsAll("tags", tagsChosen)

                    .whereEqualTo("content_to_user", parseUser)

                    .orderByDescending(order) //ResultPage.order

                    .setLimit(30)

                    .setSkip(30 * offset)

                    .include(ParseCard.JOIN_CONTENT_USER)

                    .findInBackground(new FindCallback<ParseCard>() {

                        @Override
                        public void done(List<ParseCard> parseCardList, ParseException e) {

                            if (e == null && parseCardList.size() != 0) {

                                for (ParseCard parseCard : parseCardList) {

                                    final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                                    parseCard.set_Curated(user.getBoolean("curated"));

                                    if (user.getParseFile("profil_pict") != null)

                                        parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                                    else parseCard.set_ProfilpictUrl("NULL");

                                }

                                ArrayList<CardData> cardsfounded = CardData.getCardsList(parseCardList, "", false);

                                ((MainActivity) MainActivity.context).deckFragment.display_CardsFromFollow(context, cardsfounded, tagsChosen, offset);

                            } else {

                                ((MainActivity) MainActivity.context).deckFragment.loading_deck.setVisibility(View.GONE); //loaderDeck.setVisibility(View.GONE);

                                ((MainActivity) MainActivity.context).deckFragment.reload_home.setVisibility(View.VISIBLE);

                                if(offset == 0) Message.message(context, context.getResources().getString(R.string.message_no_card));

                            }
                        }
                    });

        } else {

            query
                    .whereContainsAll("tags", tagsChosen)

                    .orderByDescending(order) //ResultPage.order

                    .setLimit(30)

                    .setSkip(30 * offset)

                    .include(ParseCard.JOIN_CONTENT_USER)

                    .findInBackground(new FindCallback<ParseCard>() {

                        @Override
                        public void done(List<ParseCard> parseCardList, ParseException e) {

                            if (parseCardList.size() != 0) {

                                for (ParseCard parseCard : parseCardList) {

                                    final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                                    parseCard.set_Curated(user.getBoolean("curated"));

                                    if (user.getParseFile("profil_pict") != null)

                                        parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                                    else parseCard.set_ProfilpictUrl("NULL");

                                }

                                ArrayList<CardData> cardsfounded = CardData.getCardsList(parseCardList, "", false);

                                ((MainActivity) MainActivity.context).deckFragment.display_CardsFromFollow(context, cardsfounded, tagsChosen, offset);

                            } else {

                                ((MainActivity) MainActivity.context).deckFragment.loading_deck.setVisibility(View.GONE); //loaderDeck.setVisibility(View.GONE);

                                ((MainActivity) MainActivity.context).deckFragment.reload_home.setVisibility(View.VISIBLE);

                                if(offset == 0) {

                                    Message.message(context, context.getResources().getString(R.string.message_no_card2));

                                }

                            }
                        }
                    });
        }
    }


    public static void get_RelatedTags(final ArrayList<String> tagsChosen) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query
                .whereContainsAll("tags", tagsChosen)

                .orderByDescending("createdAt") //ResultPage.order

                .include(ParseCard.JOIN_CONTENT_USER)

                .findInBackground(new FindCallback<ParseCard>() {

                    @Override
                    public void done(List<ParseCard> parseCardList, ParseException e) {

                        if (parseCardList.size() != 0) {

                            for (ParseCard parseCard : parseCardList) {

                                final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                                parseCard.set_Curated(user.getBoolean("curated"));

                                if (user.getParseFile("profil_pict") != null)

                                    parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                                else parseCard.set_ProfilpictUrl("NULL");

                            }

                            ((MainActivity) MainActivity.context).deckFragment

                                    .searchFragment.set_RelatedTags(parseCardList, tagsChosen);
                        }

                    }
                });
    }


    public static void getFirstTagsList(String tagBegin) {

        ParseQuery<ParseTag> tagQuery = ParseQuery.getQuery("ParseTag");

        tagQuery.whereStartsWith("tag", tagBegin);

        tagQuery.orderByDescending("counter");

        tagQuery.findInBackground(new FindCallback<ParseTag>() {
            @Override
            public void done(List<ParseTag> list, ParseException e) {

                ArrayList<String> firstTagsList = new ArrayList<>();

                for (ParseTag tag : list) firstTagsList.add(tag.getTag());

                ((MainActivity) MainActivity.context).deckFragment.searchFragment.display_tags_beginning_with(firstTagsList);

            }
        });
    }


    public static void get_CardsFromTags(final ArrayList<String> tagsChosen, final int offset) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query
                .whereContainsAll("tags", tagsChosen)
                .orderByDescending("createdAt") //ResultPage.order
                .setLimit(50)
                .setSkip(50 * offset)
                .include(ParseCard.JOIN_CONTENT_USER)


                .findInBackground(new FindCallback<ParseCard>() {

                    @Override
                    public void done(List<ParseCard> parseCardList, ParseException e) {

                        if (parseCardList.size() != 0) {

                            for (ParseCard parseCard : parseCardList) {

                                final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                                parseCard.set_Curated(user.getBoolean("curated"));

                                if (user.getParseFile("profil_pict") != null)

                                    parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                                else parseCard.set_ProfilpictUrl("NULL");

                            }

                            ((MainActivity) MainActivity.context).deckFragment

                                    .searchFragment.set_RelatedTags(parseCardList, tagsChosen);
                        }

                    }
                });
    }


    // CARD LISTS ----------------------

    public static void get_GlobalDeck(final Context context, final int offset, final boolean isCuratedDeck) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_CARD);
        query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_USER);
        query.whereNotEqualTo(ParseCard.JOIN_CONTENT_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(ParseCard.CURATED, isCuratedDeck);

        if (isCuratedDeck) query.orderByDescending("objectId");

        else query.orderByDescending("createdAt");

        query.setLimit(NB_CARDS_TO_LOAD);

        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.findInBackground(new FindCallback<ParseCard>() {

            @Override
            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    if (parseCardList.size() > 0) {

                        for (ParseCard parseCard : parseCardList) {

                            final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                            parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                            parseCard.set_Curated(user.getBoolean("curated"));

                            if (user.getParseFile("profil_pict") != null)

                                parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                            else parseCard.set_ProfilpictUrl("NULL");

                        }

                        display_CardsInAdapter(context, GLOBAL_DECK, parseCardList, offset, "");

                    } else if (!isCuratedDeck) {

                        ((MainActivity) MainActivity.context).deckFragment.isCuratedDeck = true;

                        if (offset == 0) {

                            // juste pour moi

                            ((MainActivity) MainActivity.context).deckFragment.createEmptyAdapter();

                            get_GlobalDeck(MainActivity.context, offset, true);

                        } else {

                            ((MainActivity) MainActivity.context).deckFragment.cardStackAdapter.reset_offset_for_curation();
                        }
                    }

                } else {
                    Message.message(context, "error - get_GlobalDeck :\n" + e);
                    Message.message(context, "error - get_GlobalDeck :\n" + e);
                    Message.message(context, "error - get_GlobalDeck :\n" + e);
                    Message.message(context, "error - get_GlobalDeck :\n" + e);
                }
            }
        });
    }


    public static void get_UserCards(final Context context, final String userID, final int offset, final int caller) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        ParseUser parseUser = ParseUser.createWithoutData(ParseUser.class, userID);

        query.whereEqualTo(ParseCard.JOIN_CONTENT_USER, parseUser);

        if (caller == MAIN_USER_CARD_GRID || caller == OTHER_USER_CARD_GRID) {

            query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_USER);
            query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_CARD);

        } else if (caller == MAIN_USER_COMMENT_GRID || caller == OTHER_USER_COMMENT_GRID) {

            query.whereExists(ParseCard.JOIN_REPLY_PARENT_USER);
            query.whereExists(ParseCard.JOIN_REPLY_PARENT_CARD);

            query.include(ParseCard.JOIN_REPLY_PARENT_USER);
        }

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.orderByDescending("createdAt");

        query.setLimit(NB_CARDS_TO_LOAD);

        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseCard>() {

            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    for (ParseCard parseCard : parseCardList) {

                        final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                        parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                        parseCard.set_Curated(user.getBoolean("curated"));

                        if (user.getParseFile("profil_pict") != null)

                            parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseCard.set_ProfilpictUrl("NULL");

                        if (caller == MAIN_USER_COMMENT_GRID || caller == OTHER_USER_COMMENT_GRID) {

                            ParseUser parentUser = parseCard.getParseUser(ParseCard.JOIN_REPLY_PARENT_USER);

                            parseCard.setParentAuthorFullName(parentUser.getString("name") + " " + parentUser.getString("surname"));
                        }
                    }

                    display_CardsInAdapter(context, caller, parseCardList, offset, userID);

                } else {

                    // TODO ERROR

                    //Message.message(context, "error - get_UserCards : " + userID);
                }
            }
        });

    }


    public static void get_AuthorCards(final Context context, final String userID, final int offset) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        ParseUser parseUser = ParseUser.createWithoutData(ParseUser.class, userID);

        query.whereEqualTo(ParseCard.JOIN_CONTENT_USER, parseUser);

        query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_USER);

        query.whereDoesNotExist(ParseCard.JOIN_REPLY_PARENT_CARD);

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.orderByDescending("createdAt");

        query.setLimit(NB_CARDS_TO_LOAD);

        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseCard>() {

            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    for (ParseCard parseCard : parseCardList) {

                        final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                        parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                        parseCard.set_Curated(user.getBoolean("curated"));

                        if (user.getParseFile("profil_pict") != null)

                            parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseCard.set_ProfilpictUrl("NULL");

                    }

                    display_CardsInAdapter(context, CardStackAdapter.GLOBAL_DECK, parseCardList, offset, userID);

                } else Message.message(context, "error - get_AuthorCards : " + userID);
            }
        });

    }


    public static void get_subComments(final Context context,
                                       final String parentCardID,
                                       final CommentRecyclerAdapter.CommentViewHolder viewHolder,
                                       final int nb_subcomments) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        final ParseCard parentCard = ParseCard.createWithoutData(ParseCard.class, parentCardID);

        query.whereEqualTo(ParseCard.JOIN_REPLY_PARENT_CARD, parentCard);

        query.orderByAscending("createdAt");

        query.setLimit(2);

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.findInBackground(new FindCallback<ParseCard>() {

            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    for (ParseCard parseCard : parseCardList) {

                        final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                        parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));


                        if (user.getParseFile("profil_pict") != null)

                            parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseCard.set_ProfilpictUrl("NULL");

                    }

                    display_subComments(context, parseCardList, viewHolder, nb_subcomments);
                } else Message.message(context, "error - get_CardReplies : " + parentCardID);
            }
        });
    }


    public static void get_CardReplies(final Context context, final String parentCardID) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        final ParseCard parentCard = ParseCard.createWithoutData(ParseCard.class, parentCardID);

        query.whereEqualTo(ParseCard.JOIN_REPLY_PARENT_CARD, parentCard);

        query.orderByAscending("createdAt");

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.include(ParseCard.JOIN_CHAT);

        query.findInBackground(new FindCallback<ParseCard>() {

            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    for (ParseCard parseCard : parseCardList) {

                        final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                        final ParseChat chat = (ParseChat) parseCard.get(ParseCard.JOIN_CHAT);

                        parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                        if (chat != null){

                            parseCard.setChatCount(chat.getCountComments());

                            parseCard.set_isStory(chat.isStory());

                        } else {

                            parseCard.setChatCount(0);

                            parseCard.set_isStory(false);
                        }

                        if (user.getParseFile("profil_pict") != null)

                            parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseCard.set_ProfilpictUrl("NULL");
                    }

                    display_CardsInAdapter(context, CARD_REPLIES, parseCardList, -1, "");

                } else Message.message(context, "error - get_CardReplies : " + parentCardID);
            }
        });
    }


    private static void display_subComments(
            final Context context,
            List<ParseCard> parseCardList,
            final CommentRecyclerAdapter.CommentViewHolder viewHolder,
            int nb_subcomments) {

        viewHolder.loader_subcomments.setVisibility(View.GONE);
        viewHolder.subcomments_frame.setVisibility(View.VISIBLE);
        viewHolder.btn_mini_show_comments.setVisibility(View.VISIBLE);
        ((TextView) viewHolder.btn_mini_show_comments.getChildAt(0)).setText("hide comments");


        if (nb_subcomments > 2)
            viewHolder.subcomments_frame.getChildAt(2).setVisibility(View.VISIBLE);
        else viewHolder.subcomments_frame.getChildAt(2).setVisibility(View.GONE);

        if (nb_subcomments == 1)
            viewHolder.subcomments_frame.getChildAt(1).setVisibility(View.GONE);

        int i = 0;


        for (final ParseCard subcardParse : parseCardList) {

            LinearLayout btn_subPict;
            ImageView sub_author_Pict;
            TextView sub_AuthorName;
            SquareRelativeLayout sub_square_placeholder;
            ImageView sub_pict;
            TextView sub_Comment;
            TextView sub_Link;
            TextView sub_Nb_Upvotes;
            TextView sub_Nb_Comments;
            TextView sub_locatisation;
            LinearLayout button;

            if (i < 2) {

                btn_subPict =
                        ((LinearLayout)
                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                        .getChildAt(0));

                sub_author_Pict =
                        (ImageView)
                                ((LinearLayout)
                                        ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                .getChildAt(0))
                                        .getChildAt(0);

                button = ((LinearLayout)
                        ((CardView)
                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                        .getChildAt(1))
                                .getChildAt(0));


                sub_AuthorName =
                        ((TextView)
                                ((LinearLayout)
                                        ((LinearLayout)
                                                ((CardView)
                                                        ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                                .getChildAt(1))
                                                        .getChildAt(0))
                                                .getChildAt(0))
                                        .getChildAt(0));

                sub_locatisation =
                        ((TextView)
                                ((LinearLayout)
                                        ((LinearLayout)
                                                ((LinearLayout)
                                                        ((CardView)
                                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                                        .getChildAt(1))
                                                                .getChildAt(0))
                                                        .getChildAt(0))
                                                .getChildAt(1))
                                        .getChildAt(1));

                sub_square_placeholder =
                        ((SquareRelativeLayout)
                                ((LinearLayout)
                                        ((CardView)
                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                        .getChildAt(1))
                                                .getChildAt(0))
                                        .getChildAt(1));

                sub_pict =
                        ((ImageView)
                                ((SquareRelativeLayout)
                                         ((LinearLayout)
                                                 ((CardView)
                                                         ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                                 .getChildAt(1))
                                                         .getChildAt(0))
                                                 .getChildAt(1))
                                        .getChildAt(0));


                sub_Comment =
                        ((TextView)
                                ((LinearLayout)
                                        ((CardView)
                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                        .getChildAt(1))
                                                .getChildAt(0))
                                        .getChildAt(2));

                sub_Link =
                        ((TextView)
                                ((LinearLayout)
                                        ((CardView)
                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                        .getChildAt(1))
                                                .getChildAt(0))
                                        .getChildAt(3));

                sub_Nb_Upvotes =
                        ((TextView)
                                ((LinearLayout)
                                        ((LinearLayout)

                                                ((LinearLayout)
                                                        ((CardView)
                                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                                        .getChildAt(1))
                                                                .getChildAt(0))

                                                        .getChildAt(4))
                                                .getChildAt(0))
                                        .getChildAt(0));

                sub_Nb_Comments =
                        ((TextView)
                                ((LinearLayout)
                                        ((LinearLayout)
                                                ((LinearLayout)
                                                        ((CardView)
                                                                ((LinearLayout) viewHolder.subcomments_frame.getChildAt(i))
                                                                        .getChildAt(1))
                                                                .getChildAt(0))
                                                        .getChildAt(4))
                                                .getChildAt(1))
                                        .getChildAt(0));



                if (!subcardParse.get_ProfilpictUrl().equals("NULL"))
                    Picasso.with(MainActivity.context).load(subcardParse.get_ProfilpictUrl()).into(sub_author_Pict);

                else sub_author_Pict.setImageDrawable(ContextCompat.getDrawable(MainActivity.context, R.drawable.profil_pict));

                sub_AuthorName.setTypeface(MainActivity.tfSemiBold);

                sub_Comment.setTypeface(MainActivity.tf_serif);

                final CardData subCard = CardData.getCard(subcardParse);

                if (!(subCard.getAuthor_id()).equals(
                        ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID))) {

                    btn_subPict.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SubActivity.openUserProfil(
                                    context,
                                    subCard.getAuthor_full_name(),
                                    subCard.getAuthor_id(),
                                    subCard.get_user_pict_url(),
                                    false);
                        }
                    });

                }

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SubActivity.openCard(context, subCard, true, false);
                    }
                });

                sub_AuthorName.setText(subcardParse.getAuthorFullName());

                sub_locatisation.setText(CardData.get_localisation_from_gps(subcardParse.getLatitude(), subcardParse.getLongitude()));

                if(subcardParse.getParseFile("pict") != null) {

                    sub_square_placeholder.setVisibility(View.VISIBLE);
                    Picasso.with(SubActivity.context)

                            .load(subcardParse.getParseFile("pict").getUrl())
                            .fit().centerCrop()
                            .into(sub_pict);

                    if(subcardParse.getContent().isEmpty()) sub_Comment.setVisibility(View.GONE);

                    else sub_Comment.setText(subcardParse.getContent());

                } else {
                    sub_square_placeholder.setVisibility(View.GONE);
                    sub_Comment.setText(subcardParse.getContent());
                }

                // link

                if(subcardParse.getLinkUrl() != null  && !subcardParse.getLinkUrl().isEmpty() ) {

                    final String link = subcardParse.getLinkUrl();

                    sub_Link.setText(link);
                    sub_Link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {

                                Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                SubActivity.context.startActivity(intent);

                            } catch (ActivityNotFoundException e) {

                            }
                        }
                    });

                } else sub_Link.setVisibility(View.GONE);



                sub_Nb_Upvotes.setText("" + subcardParse.getUpvoteCount());

                sub_Nb_Comments.setText("" + subcardParse.getReplyCount());
                i++;

            }
        }
    }

    private static void display_CardsInAdapter(Context context, int caller, List<ParseCard> parseCardList, int offset, String mainUserID) {

        // deck fragment - card fragment - profilCardFrag

        if (caller == GLOBAL_DECK) {

            ((MainActivity) context).deckFragment.loading_deck.setVisibility(View.GONE);

            ((MainActivity) context).deckFragment.displayCards(

                    context, CardData.getCardsList(parseCardList, "", false), offset);


        } else if (caller == MAIN_USER_CARD_GRID) {

            ((MainActivity) context).profilFragment.userCardsTab.displayCards(

                    context, CardData.getCardsList(parseCardList, mainUserID, false), offset);


        } else if (caller == MAIN_USER_COMMENT_GRID) {

            //((MainActivity) context).profilFragment.userCommentsTab.displayCards(context, CardData.getCardsList(parseCardList, mainUserID, false), offset);


        } else if (caller == MAIN_USER_PROFIL_DECK) {

            ((SubActivity) context).deckFragment.displayCards(

                    context, CardData.getCardsList(parseCardList, mainUserID, false), offset);


        } else if (caller == OTHER_USER_CARD_GRID) {

            //Message.message(context, "other user card grid \noffset : " + offset);

            ((SubActivity) context).profilFragment.userCardsTab.displayCards(

                    context, CardData.getCardsList(parseCardList, "", false), offset);


        } else if (caller == OTHER_USER_COMMENT_GRID) {

            //Message.message(context, "other user comment grid \noffset : " + offset);

            //((SubActivity) context).profilFragment.userCommentsTab.displayCards(context, CardData.getCardsList(parseCardList, "", false), offset);


        } else if (caller == OTHER_USER_PROFIL_DECK) {

            ((SubActivity) context).deckFragment.displayCards(

                    context, CardData.getCardsList(parseCardList, "", false), offset);


        } else if (caller == CARD_REPLIES) {

            ((SubActivity) context).cardFragment.displayReplies(

                    context, CardData.getCardsList(parseCardList, "", true));


        } else if (caller == CARD_CHAT) {

            ((SubActivity) context).chatFragment.displayReplies(

                    context, CardData.getCardsList(parseCardList, "", true));

        } else if (caller == CARD_STORY) {

            ((SubActivity) context).storyFragment.displayCards(

                    context, CardData.getCardsList(parseCardList, "", true));
        }
    }


    // for parent card (ouverture depuis notification)
    public static void get_Card(final Context context, String cardID) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query.whereEqualTo("objectId", cardID);

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.include(ParseCard.JOIN_REPLY_PARENT_USER);

        query.getFirstInBackground(new GetCallback<ParseCard>() {
            @Override
            public void done(ParseCard parseCard, ParseException e) {

                if (e == null) {

                    final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                    parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                    if (user.getParseFile("profil_pict") != null)

                        parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                    else parseCard.set_ProfilpictUrl("NULL");

                    if (parseCard.getParseUser(ParseCard.JOIN_REPLY_PARENT_USER) != null) {

                        final ParseUser parentUser = parseCard.getParseUser(ParseCard.JOIN_REPLY_PARENT_USER);

                        parseCard.setParentAuthorFullName(parentUser.getString("name") + " " + parentUser.getString("surname"));

                    }

                    ((SubActivity) context).cardFragment.set_Delayed_Header(CardData.getCard(parseCard));

                }
            }
        });
    }


    // CHATS -------------------------

    public static void get_ChatElements(final Context context, final String chatID, final boolean isStory) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery("ParseChat");

        query.whereEqualTo("objectId", chatID);

        query.include(ParseChat.JOIN_FIRST_CARD);

        query.getFirstInBackground(new GetCallback<ParseChat>() {
            @Override
            public void done(ParseChat parseChat, ParseException e) {

                if (e == null) {

                    ParseCard parsefirstCard = (ParseCard) parseChat.get(ParseChat.JOIN_FIRST_CARD);

                    CardData firstCard = CardData.getCard(parsefirstCard); // TODO conversion qui prend du temps

                    String starterID = ((ParseUser) parseChat.get(ParseChat.JOIN_STARTER_USER)).getObjectId();

                    String replierID = ((ParseUser) parseChat.get(ParseChat.JOIN_REPLIER_USER)).getObjectId();

                    ((SubActivity) context).setChat_next(firstCard, chatID, starterID, replierID, isStory);

                }
            }
        });
    }


    public static void get_ChatThread(final Context context, final String chatID, final boolean isStory) {

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        final ParseChat parseChat = ParseChat.createWithoutData(ParseChat.class, chatID);

        query.whereEqualTo(ParseCard.JOIN_CHAT, parseChat);

        query.orderByAscending("createdAt");

        query.include(ParseCard.JOIN_CONTENT_USER);

        query.findInBackground(new FindCallback<ParseCard>() {

            @Override
            public void done(List<ParseCard> parseCardList, ParseException e) {

                if (e == null) {

                    for (ParseCard parseCard : parseCardList) {

                        final ParseUser user = parseCard.getParseUser(ParseCard.JOIN_CONTENT_USER);

                        parseCard.setAuthorFullName(user.getString("name") + " " + user.getString("surname"));

                        //final ParseChat chat = (ParseChat) parseCard.get(ParseCard.JOIN_CHAT);

                        //if (chat != null) parseCard.setChatCount(chat.getCountComments());

                        //else parseCard.setChatCount(0);

                        if (user.getParseFile("profil_pict") != null)

                            parseCard.set_ProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseCard.set_ProfilpictUrl("NULL");
                    }

                    if(isStory) display_CardsInAdapter(context, CARD_STORY, parseCardList, -1, "");

                    else display_CardsInAdapter(context, CARD_CHAT, parseCardList, -1, "");

                }
            }
        });
    }


    public static void get_UserChats(final Context context, final String userID, final int chat_type, final int offset) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery("ParseChat");
        query.whereEqualTo("userIDs", userID);
        query.whereNotEqualTo("is_story", true);
        query.whereGreaterThan("count_comments", 2); // TODO A VIRER
        query.include(ParseChat.JOIN_STARTER_USER);
        query.include(ParseChat.JOIN_REPLIER_USER);

        query.orderByDescending("updatedAt");
        query.setLimit(NB_CARDS_TO_LOAD);
        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseChat>() {

            public void done(List<ParseChat> parseChatsList, ParseException e) {

                if (e == null) {

                    if (chat_type == ChatOFrag.CONV_U_CHAT) {

                        ((MainActivity) context).convFragment.userChats.display_rows(

                                context, ChatOverviewData.getChatsList(parseChatsList, userID), chat_type, offset);

                    } else if (chat_type == ChatOFrag.PROFIL_O_CHAT) {

                        ((SubActivity) context).profilFragment.otherUserChatsTab.display_rows(

                                context, ChatOverviewData.getChatsList(parseChatsList, userID), chat_type, offset);

                    }

                } else Message.message(context, "error - get_UserCards : " + userID);
            }
        });
    }


    public static void get_UserStories(final Context context, final String userID, final int chat_type, final int offset) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery("ParseChat");

        query.whereEqualTo("userIDs", userID);
        query.whereEqualTo("is_story", true);

        //query.whereGreaterThan("count_comments", 2);
        query.include(ParseChat.JOIN_STARTER_USER);
        query.include(ParseChat.JOIN_FIRST_CARD);

        query.orderByDescending("createdAt");
        query.setLimit(NB_CARDS_TO_LOAD);
        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseChat>() {

            public void done(List<ParseChat> parseChatsList, ParseException e) {

                if (e == null) {

                    if (chat_type == ChatOFrag.PROFIL_U_STORIES) {

                        ((MainActivity) context).profilFragment.userStoriesTab.display_rows(

                                context, ChatOverviewData.getStoriesList(parseChatsList), chat_type, offset);

                    } else if (chat_type == ChatOFrag.PROFIL_O_STORIES) {

                        ((SubActivity) context).profilFragment.userStoriesTab.display_rows(

                                context, ChatOverviewData.getStoriesList(parseChatsList), chat_type, offset);

                    }

                } else Message.message(context, "error - get_UserCards : " + userID);
            }
        });

    }


    public static void get_CommunityChats(final Context context, final String userID, final int chat_type, final int offset) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery("ParseChat");

        query.whereNotEqualTo("userIDs", userID);
        query.whereNotEqualTo("is_story", true);

        query.whereGreaterThan("count_comments", 2); //TODO a virer
        query.include(ParseChat.JOIN_STARTER_USER);
        query.include(ParseChat.JOIN_REPLIER_USER);

        query.orderByDescending("updatedAt");
        query.setLimit(NB_CARDS_TO_LOAD);
        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseChat>() {

            public void done(List<ParseChat> parseChatsList, ParseException e) {

                if (e == null) {

                    if (chat_type == ChatOFrag.CONV_O_CHAT) {

                        ((MainActivity) context).convFragment.otherChats.display_rows(

                                context, ChatOverviewData.getCommunityChatsList(parseChatsList, userID), chat_type, offset);

                    }

                } else Message.message(context, "error - get_UserCards : " + userID);
            }
        });

    }


    public static void get_CommunityStories(final Context context, final String userID, final int chat_type, final String topic, final int offset) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery("ParseChat");

        query.whereNotEqualTo("userIDs", userID);
        query.whereEqualTo("is_story", true);

        query.include(ParseChat.JOIN_STARTER_USER);
        query.include(ParseChat.JOIN_FIRST_CARD);

        if(!topic.equals(ChatOFrag.ALL_STORIES)) query.whereEqualTo("first_tag", topic);

        query.orderByDescending("createdAt");
        query.setLimit(NB_CARDS_TO_LOAD);
        query.setSkip(NB_CARDS_TO_LOAD * offset);

        query.findInBackground(new FindCallback<ParseChat>() {

            public void done(List<ParseChat> parseChatsList, ParseException e) {

                if (e == null) {

                    if (chat_type == ChatOFrag.STORIES_O) {

                        ((MainActivity) context).storyFragment.display_rows(

                                context, ChatOverviewData.getStoriesList(parseChatsList), chat_type, offset);

                    }

                } else Message.message(context, "error - get_CommunityStories");
            }
        });

    }


    // PROFILS -------------------------

    public static void get_OtherUserProfil(final Context context, String userID) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("objectId", userID);

        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (e == null) {

                    ((SubActivity) context).profilFragment.otherUser = user;

                    ((SubActivity) context).profilFragment.update_OtherProfilHeader();

                } else Message.message(MainActivity.context, "error - get_OtherUserProfil");
            }
        });
    }


    public static void get_UpvotesCount(final String userCountID, final TextView tv_upvotes) {

        ParseUser user = ParseUser.createWithoutData(ParseUser.class, userCountID);

        ParseQuery<ParseUpvote> query = ParseQuery.getQuery("ParseUpvote");

        query.whereEqualTo(ParseUpvote.JOIN_RECEIVER, user);

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {

                if (e == null) {

                    tv_upvotes.setText(String.valueOf(count));

                }
            }
        });

    }


    public static void get_RepliesCount(final String userCountID, final TextView tv_replies) {

        ParseUser user = ParseUser.createWithoutData(ParseUser.class, userCountID);

        ParseQuery<ParseCard> query = ParseQuery.getQuery("ParseCard");

        query.whereEqualTo(ParseCard.JOIN_REPLY_PARENT_USER, user);

        query.whereNotEqualTo(ParseCard.JOIN_CONTENT_USER, user);

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {

                if (e == null) {

                    tv_replies.setText(String.valueOf(count));

                }
            }
        });

    }


    public static void get_UserTopics(final Context context, final String userID, final boolean isMainUser) {

        ParseQuery<ParseUserTagCount> query = ParseQuery.getQuery("ParseUserTagCount");

        ParseUser parseUser = ParseUser.createWithoutData(ParseUser.class, userID);

        query.whereEqualTo(ParseUserTagCount.JOIN_USER_ID, parseUser);

        query.orderByDescending("counter");

        query.setLimit(12);

        query.findInBackground(new FindCallback<ParseUserTagCount>() {

            public void done(List<ParseUserTagCount> parseTagsList, ParseException e) {

                if (e == null) {

                    ArrayList<String> tagsList = new ArrayList<>();

                    for (ParseUserTagCount parseTags : parseTagsList)
                        tagsList.add(parseTags.getTag());

                    if (isMainUser) {

                        if (tagsList.size() != 0)
                            ((MainActivity) context).profilFragment.update_profil_topics(tagsList);

                        else
                            ((MainActivity) context).profilFragment.header_tags_row.setVisibility(View.GONE);

                    } else {

                        if (tagsList.size() != 0)
                            ((SubActivity) context).profilFragment.update_profil_topics(tagsList);

                        else
                            ((SubActivity) context).profilFragment.header_tags_row.setVisibility(View.GONE);

                    }

                } else Message.message(context, "error - get_UserTopics : " + e);

            }
        });
    }

    public static void update_UserName(String name, String surname) {

        ParseUser.getCurrentUser().put("name", name);

        ParseUser.getCurrentUser().put("surname", surname);

        ParseUser.getCurrentUser().saveInBackground();

    }

    public static void update_UserBio(String bio) {

        ParseUser.getCurrentUser().put("bio", bio);

        ParseUser.getCurrentUser().saveInBackground();

    }

    public static void update_UserBioUrl(String url) {

        ParseUser.getCurrentUser().put("bio_link", url);

        ParseUser.getCurrentUser().saveInBackground();

    }





    public static void save_parse_image(final Context context, Bitmap cropImage) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cropImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        final ParseFile parsePhoto = new ParseFile("profil_photo.png", byteArray);

        parsePhoto.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {

                ParseUser mainUser = ParseUser.createWithoutData(
                        ParseUser.class,
                        ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID));

                mainUser.put("profil_pict", parsePhoto);

                mainUser.saveInBackground();

                ((MainActivity) MainActivity.context).localData.store_newProfilPict(parsePhoto.getUrl());

                Intent intentBack = new Intent();

                ((CropActivity) context).setResult(CropActivity.RESULT_CROP_IMAGE, intentBack);
                ((CropActivity) context).finish();
                ((CropActivity) context).overridePendingTransition(0, R.anim.exit_right);


            }
        });
    }


    // INSERT CARDS & UPDATE USER/CARDS/TAGS COUNTS -------------------------

    public static void update_chat_checked(final String chatID, String role) {

        ParseChat parseChat = ParseChat.createWithoutData(ParseChat.class, chatID);

        if (role.equals(ChatOverviewData.STARTER)) {

            parseChat.setStarterChecked(true);
            parseChat.saveInBackground();

        } else if (role.equals(ChatOverviewData.REPLIER)) {

            parseChat.setReplierChecked(true);
            parseChat.saveInBackground();
        }
    }


    public static void update_story_title(String storyID, String title) {

        ParseChat parseChat = ParseChat.createWithoutData(ParseChat.class, storyID);

        parseChat.setStoryTitle(title);
        parseChat.saveInBackground();


    }

    private static void send_pushnot_comment(ParseCard parseCard) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("channels", "master_" + ((ParseCard) parseCard.get(ParseCard.JOIN_REPLY_PARENT_CARD)).getObjectId());
        params.put("listening", true);

        params.put("data", "comment");
        params.put("alert", parseCard.getAuthorFullName() + " replied to your card");

        ParseCloud.callFunctionInBackground("sendPushCommentToUser", params, new FunctionCallback<String>() {
            @Override
            public void done(String success, ParseException e) {
                if (e == null) {
                    // Push sent successfully
                }
            }
        });

        /*
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("channels", "master_" + ((ParseCard) parseCard.get(ParseCard.JOIN_REPLY_PARENT_CARD)).getObjectId()); // Set the channel
        pushQuery.whereEqualTo("listening", true);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        try {
            JSONObject data = new JSONObject();
            data.put("data","comment");
            data.put("alert", parseCard.getAuthorFullName() + " replied to your card");
            push.setData(data);

            //push.setMessage(parseCard.getAuthorFullName() + " replied to your card");
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private static void send_pushnot_chat(ParseChat parseChat, ParseCard parseCard) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("channels", "chat_" + parseChat.getObjectId());
        params.put("recipientIdExclude", ParseUser.getCurrentUser().getObjectId());
        params.put("listening", true);

        params.put("data", "message");
        params.put("alert", parseCard.getAuthorFullName() + " replied to your message");

        ParseCloud.callFunctionInBackground("sendPushChatToUser", params, new FunctionCallback<String>() {
            @Override
            public void done(String success, ParseException e) {
                if (e == null) {
                    // Push sent successfully
                }
            }
        });

        /*
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("channels", "chat_" + parseChat.getObjectId()); // Set the channel
        pushQuery.whereNotEqualTo("user", ParseUser.getCurrentUser());
        pushQuery.whereEqualTo("listening", true);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);

        try {

            JSONObject data = new JSONObject();
            data.put("data","message");
            data.put("alert", parseCard.getAuthorFullName() + " replied to your message");
            push.setData(data);

            //push.setMessage(parseCard.getAuthorFullName() + " replied to your message");
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
    }

    private static void update_chat(ParseChat parseChat, ParseCard parseCard, CardData cardData) {

        parseChat.setLastCommenterID(cardData.getAuthor_id());
        parseChat.setLastComment(cardData.getMain_content());
        parseChat.increment("count_comments", 1);

        // si pas monologue
        if (!(parseChat.getUsersID().get(0)).equals(parseChat.getUsersID().get(1))) { // TODO BUGGGGG

            if ((cardData.getAuthor_id()).equals(parseChat.getUsersID().get(0))) {

                parseChat.setReplierChecked(false);

                parseChat.subscribe_commenter(ParseChat.STARTER_IS_LISTENING); // starter subscribe

            } else if ((cardData.getAuthor_id()).equals(parseChat.getUsersID().get(1))) {

                parseChat.setStarterChecked(false);

                parseChat.subscribe_commenter(ParseChat.REPLIER_IS_LISTENING); // replier subscribe
            }
        }

        parseChat.saveInBackground();

        send_pushnot_chat(parseChat, parseCard);

        ParsePush.subscribeInBackground("chat_" + parseChat.getObjectId());

    }



    private static void insert_chat_comment_pict(final Context context,
                                                 final CardData cardData, final ParseCard parseCard, final ParseChat parseChat) {

        try {

            Bitmap btm = tools.decodeUri(context, cardData.getRepliedImage(), 700);

            final ParseFile parsePict = get_PictFile_FromBtm(btm);

            parsePict.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {

                    parseCard.set_Pict(parsePict);

                    cardData.set_pict_url(parseCard.getParseFile("pict").getUrl());

                    insert_chat_comment_text(context, cardData, parseCard, parseChat);

                }
            });

        } catch (FileNotFoundException e) {

            Message.message(context, context.getResources().getString(R.string.message_picture_error));
        }

    }


    private static void insert_chat_comment_text(final Context context,
                                                 final CardData cardData, final ParseCard parseCard, final ParseChat parseChat) {

        parseCard.saveInBackground(new SaveCallback() {

            private void set_cardData() {

                cardData.setCard_id(parseCard.getObjectId());

                cardData.setTimestamp(DateFormat.getDateInstance().format((parseCard.getCreatedAt()))); // TODO BUG

                cardData.setChat_id(parseChat.getObjectId());

                cardData.set_user_pict_url(
                        ((MainActivity) MainActivity.context)
                                .localData.getUserData(SharedPref.KEY_PICT_URL));

                cardData.set_localisation_from_gps(parseCard);
            }

            private void update_mainUser_card_count() {

                ParseUser.getCurrentUser().increment("cards_reply_count", 1);

                ParseUser.getCurrentUser().saveInBackground();

            }

            private void update_parentCard_count() {

                // compteur de la carte
                ParseCard parentCard = ParseCard.createWithoutData(ParseCard.class, cardData.getParent_card_id());

                parentCard.increment("reply_count", 1);

                parentCard.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            //Message.message(MainActivity.context, "incrementation");

                        } else Message.message(MainActivity.context, "counter error");
                    }
                });
            }

            @Override
            public void done(ParseException e) {

                Message.message(context, context.getResources().getString(R.string.message_comment_posted));

                set_cardData();

                update_TagsCounts(parseCard);

                ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_NEW_COMMENT);

                //((MainActivity) MainActivity.context).profilFragment.userCommentsTab.addNewCardinGrid(cardData);

                //((MainActivity) MainActivity.context).profilFragment.otherUserChatsTab.reload(MainActivity.context, ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID));

                update_mainUser_card_count();

                update_parentCard_count();

                update_chat(parseChat, parseCard, cardData);

                ParsePush.subscribeInBackground("master_" + parseCard.getObjectId());


                ((SubActivity) context).displayReplyCard_inChat(cardData); // chat ou story
                //((SubActivity) context).storyFragment.comment_editor.displayReplyCard_inChat(cardData);
                //((SubActivity) context).chatFragment.comment_editor.displayReplyCard_inChat(cardData);

            }
        });

    }


    private static void insert_chat_comment(final Context context,
                                            final CardData cardData, final ParseCard parseCard, final ParseChat parseChat) {

        if(cardData.getRepliedImage() == null)

            insert_chat_comment_text(context, cardData, parseCard, parseChat);

        else insert_chat_comment_pict(context, cardData, parseCard, parseChat);
    }


    private static void insert_comment_text(final Context context,
                                            final CardData cardData, final ParseCard parseCard, final boolean newChat,
                                            final ParseChat parseChat, final String parent_card_content, final String headerCardID) {

        parseCard.setChat(parseChat);

        parseCard.saveInBackground(new SaveCallback() {

            private void set_cardData() {

                cardData.setCard_id(parseCard.getObjectId());

                if(cardData.getRepliedImage() != null) cardData.set_pict_url(parseCard.getParseFile("pict").getUrl());

                else cardData.set_pict_url("");

                cardData.setTimestamp(DateFormat.getDateInstance().format((parseCard.getCreatedAt())));

                cardData.setChat_id(parseChat.getObjectId());

                cardData.set_user_pict_url(
                        ((MainActivity) MainActivity.context)
                                .localData.getUserData(SharedPref.KEY_PICT_URL));

                cardData.set_localisation_from_gps(parseCard);
            }

            private void update_mainUser_card_count() {

                ParseUser.getCurrentUser().increment("cards_reply_count", 1);

                ParseUser.getCurrentUser().saveInBackground();

            }

            private void update_parentCard_count() {

                // compteur de la carte
                // todo verifier le reply count de la carte mere
                ParseCard parentCard = ParseCard.createWithoutData(ParseCard.class, cardData.getParent_card_id());

                parentCard.increment("reply_count", 1);

                parentCard.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {

                            //Message.message(MainActivity.context, "incrementation");

                        } else Message.message(MainActivity.context, "counter error");
                    }
                });
            }

            @Override
            public void done(ParseException e) {

                if (e == null) {

                    set_cardData();

                    update_TagsCounts(parseCard);

                    ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_NEW_COMMENT);

                    //((MainActivity) MainActivity.context).profilFragment.userCommentsTab.addNewCardinGrid(cardData); desactivé, plus de panneaux commentaire

                    update_mainUser_card_count();

                    update_parentCard_count();

                    if (newChat) {

                        ((SubActivity) context).cardFragment.comment_editor.displayReplyCard_inComment(cardData);

                        Message.message(context, context.getResources().getString(R.string.message_comment_posted));

                        if (!((ParseUser.getCurrentUser()).getObjectId()).equals(cardData.getParent_author_id()))

                            send_pushnot_comment(parseCard);


                    } else {

                        Message.message(context, context.getResources().getString(R.string.message_comment_posted));

                        Message.message(context, context.getResources().getString(R.string.message_comment_posted_in_thread));

                        // desactive
                        //((MainActivity) MainActivity.context).profilFragment.otherUserChatsTab.reload(MainActivity.context,
                                //((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID));

                        if ((cardData.getParent_card_id()).equals(headerCardID))

                            ((SubActivity) context).cardFragment.comment_editor.displayReplyCard_inComment(cardData);

                        update_chat(parseChat, parseCard, cardData);
                    }

                    // notification

                    ParsePush.subscribeInBackground("master_" + parseCard.getObjectId());

                    if (!cardData.getParent_author_id().equals(((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID))) {

                        //Message.message(context, "notif : parent_card_Content : \n" + parent_card_content);

                        if (newChat) {

                            insert_Notification(
                                    cardData.getParent_author_id(),
                                    cardData.getAuthor_id(),
                                    cardData.getParent_card_id(),
                                    ParseNotification.TYPE_COMMENT,
                                    cardData.getAuthor_full_name(),
                                    parent_card_content,
                                    cardData.getLatitude(),
                                    cardData.getLongitude());

                        }
                    }
                }
            }
        });

    }


    private static void insert_comment_pict(final Context context,
                                            final CardData cardData, final ParseCard parseCard, final boolean newChat,
                                            final ParseChat parseChat, final String parent_card_content, final String headerCardID) {


        try {

            Bitmap btm = tools.decodeUri(context, cardData.getRepliedImage(), 700);

            final ParseFile parsePict = get_PictFile_FromBtm(btm);

            parsePict.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {

                    parseCard.set_Pict(parsePict);

                    insert_comment_text(context, cardData, parseCard, newChat, parseChat, parent_card_content, headerCardID);

                }
            });

        } catch (FileNotFoundException e) {

            Message.message(context, context.getResources().getString(R.string.message_picture_error));
        }
    }

    private static void insert_comment(final Context context,
                                       final CardData cardData, final ParseCard parseCard, final boolean newChat,
                                       final ParseChat parseChat, final String parent_card_content, final String headerCardID) {

        if(cardData.getRepliedImage() == null)

            insert_comment_text(context, cardData, parseCard, newChat, parseChat, parent_card_content, headerCardID);

        else insert_comment_pict(context, cardData, parseCard, newChat, parseChat, parent_card_content, headerCardID);

    }

    private static void insert_chat(final Context context,
                                    final CardData cardData,
                                    final ParseCard parseCard,
                                    final String parent_card_content,
                                    final String parent_card_first_tag) {

        //Message.message(context, ">> " + parseCard.getTags().get(0));

        final ParseChat parseChat = new ParseChat(
                parseCard.get(ParseCard.JOIN_REPLY_PARENT_CARD),
                parseCard.get(ParseCard.JOIN_REPLY_PARENT_USER), // user 1
                parseCard.get(ParseCard.JOIN_CONTENT_USER), // user 2
                parent_card_first_tag,
                2);

        parseChat.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {

                if (e == null) {

                    cardData.setChat_id(parseChat.getObjectId());

                    //if(!(ParseUser.getCurrentUser().getObjectId()).equals(cardData.getAuthor_id()))

                    ParsePush.subscribeInBackground("chat_" + parseChat.getObjectId());

                    insert_comment(context, cardData, parseCard, true, parseChat, parent_card_content, cardData.getParent_card_id());

                } else Message.message(context, "insert_chat - error");
            }
        });
    }


    private static void get_last_comment_from_chat(final Context context,
                                                   final ParseChat parseChat, final CardData repliedCardData,
                                                   final String parent_card_content, final boolean chatEditor) {

         /*
        - le parent_card_ID du commentaire sera égale à
            chercher dernier card_id ayant le chat_id
        - incrémentation du chat
        - notification a l'autre user, redirection vers chat screen
         */

        ParseQuery<ParseCard> query = ParseQuery.getQuery(ParseCard.class);

        query.whereEqualTo("chat", parseChat);

        query.orderByDescending("createdAt");

        query.getFirstInBackground(new GetCallback<ParseCard>() {

            private String change_parentCard(CardData cardData, ParseCard lastCardFromChat) {

                String current_headerCardID = cardData.getParent_card_id();

                cardData.setParent_card_id(lastCardFromChat.getObjectId());
                cardData.setParent_author_id(((ParseUser) lastCardFromChat.get(ParseCard.JOIN_CONTENT_USER)).getObjectId());
                cardData.setParent_author_full_name(lastCardFromChat.getAuthorFullName());

                return current_headerCardID;

            }

            @Override
            public void done(ParseCard lastCardFromChat, ParseException e) {

                if (e == null) {

                    if (!chatEditor) {

                        String headerCardID = change_parentCard(repliedCardData, lastCardFromChat);

                        final ParseCard parseRepliedCard = new ParseCard(repliedCardData, true);

                        insert_comment(context, repliedCardData, parseRepliedCard, false, parseChat, parent_card_content, headerCardID);

                    } else {

                        change_parentCard(repliedCardData, lastCardFromChat);

                        final ParseCard parseRepliedCard = new ParseCard(repliedCardData, true);

                        parseRepliedCard.setChat(parseChat);

                        insert_chat_comment(context, repliedCardData, parseRepliedCard, parseChat);

                    }
                }
            }
        });
    }

    private static void check_chat_exists(final Context context,
                                          String firstCardID,
                                          String parentUserID,
                                          final String parent_card_first_tag,
                                          final CardData cardData,
                                          final String parent_card_content) {

        /*
        - voir si chat existe avec
            - parent_card_id as first_card
            - parent_author_id as user_1
            - current_user_id as user_2

        - si chat existe :
            - le parent_card_ID du commentaire sera égale à
                chercher dernier card_id ayant le chat_id
            - incrémentation du chat
            - notification a l'autre user, redirection vers chat screen TODO

        - si pas de chat :
            - insertion d'un chat
            - insertion du commentaire

         */

        ParseQuery<ParseChat> query = ParseQuery.getQuery(ParseChat.class);

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, firstCardID);
        ParseUser starter = ParseUser.createWithoutData(ParseUser.class, parentUserID);
        ParseUser replier = ParseUser.getCurrentUser();

        query.whereEqualTo(ParseChat.JOIN_FIRST_CARD, card);
        query.whereEqualTo(ParseChat.JOIN_STARTER_USER, starter);
        query.whereEqualTo(ParseChat.JOIN_REPLIER_USER, replier);

        query.getFirstInBackground(new GetCallback<ParseChat>() {
            @Override
            public void done(ParseChat parseChat, ParseException e) {

                if (e == null) {

                    //Message.message(context, "chat exist");

                    get_last_comment_from_chat(context, parseChat, cardData, parent_card_content, false);

                } else {

                    //Message.message(context, "chat doesn't exist");

                    final ParseCard parseCard = new ParseCard(cardData, true);

                    //Message.message(context, ">>" + parent_card_first_tag);

                    insert_chat(context, cardData, parseCard, parent_card_content, parent_card_first_tag);

                }
            }

        });
    }


    private static void get_parent_chat_thread(final Context context, final String chatID, final CardData repliedCardData, final String parent_card_content) {

        ParseQuery<ParseChat> query = ParseQuery.getQuery(ParseChat.class);

        query.whereEqualTo("objectId", chatID);

        query.getFirstInBackground(new GetCallback<ParseChat>() {

            private boolean isChatting(ParseChat parseChat, String replierUserID) {

                if (((((ParseUser) parseChat.get(ParseChat.JOIN_STARTER_USER)).getObjectId()).equals(replierUserID))
                        || (((ParseUser) parseChat.get(ParseChat.JOIN_REPLIER_USER)).getObjectId()).equals(replierUserID)) {

                    return true;

                } else return false;
            }

            @Override
            public void done(ParseChat parseChat, ParseException e) {

                if (e == null) {

                    if (isChatting(parseChat, repliedCardData.getAuthor_id())) {

                        // inserer a la fin du thread
                        get_last_comment_from_chat(context, parseChat,
                                repliedCardData, parent_card_content, false);

                    } else {

                        // inserer nouveau chat
                        /*
                        Message.message(context, "no chat yet\n"
                                + "replier : " + repliedCardData.getAuthor_id()
                                + "\n chat_replier : " + (((ParseUser)parseChat.get(ParseChat.JOIN_REPLIER_USER)).getObjectId())
                                + "\n chat_starter : " + (((ParseUser)parseChat.get(ParseChat.JOIN_STARTER_USER)).getObjectId())
                        );*/

                        String parent_tag;
                        if(repliedCardData.getTags_list().isEmpty()) parent_tag = "";
                        else parent_tag = repliedCardData.getTags_list().get(0);


                        check_chat_exists(context,
                                repliedCardData.getParent_card_id(),
                                repliedCardData.getParent_author_id(),
                                parent_tag,
                                repliedCardData, parent_card_content);

                        /*
                        final ParseCard parseCard = new ParseCard(repliedCardData, true);

                        insert_chat(context, repliedCardData, parseCard, parent_card_content);
                        */

                    }
                } else {

                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        //Message.message(context, "error - get_parent_chat_thread : " + chatID + "\n" + e);

                        String parent_tag;
                        if(repliedCardData.getTags_list().isEmpty()) parent_tag = "";
                        else parent_tag = repliedCardData.getTags_list().get(0);

                        check_chat_exists(context,
                                repliedCardData.getParent_card_id(),
                                repliedCardData.getParent_author_id(),
                                parent_tag,
                                repliedCardData, parent_card_content);

                    }
                }
            }
        });
    }


    public static void insert_comment_chat_inBG(final Context context, final CardData repliedCardData) {

        String chatID = repliedCardData.getChat_id();

        //ParseChat parseChat = ParseChat.createWithoutData(ParseChat.class, chatID);

        ParseQuery<ParseChat> query = ParseQuery.getQuery(ParseChat.class);

        query.whereEqualTo("objectId", chatID);

        query.getFirstInBackground(new GetCallback<ParseChat>() {
            @Override
            public void done(ParseChat parseChat, ParseException e) {

                get_last_comment_from_chat(context, parseChat, repliedCardData, "TODO", true);

            }
        });
    }


    public static void insert_comment_in_BG(Context context, CardData parentCardData,
                                            CardData repliedCardData, String parent_card_content) {

        if (parentCardData.getParent_card_id().isEmpty()) {

            // parent card == new card

            check_chat_exists(context,
                    parentCardData.getCard_id(),
                    parentCardData.getAuthor_id(),
                    parentCardData.getTags_list().get(0),
                    repliedCardData,
                    parent_card_content);

        } else {

            // parent card == comment card

            get_parent_chat_thread(context, parentCardData.getChat_id(), repliedCardData, parent_card_content);

        }
    }




    public static void insert_card_text(final Context context, final CardData cardData) {

        final ParseCard parseCard = new ParseCard(cardData, false);

        parseCard.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    cardData.setCard_id(parseCard.getObjectId());

                    cardData.setTimestamp(DateFormat.getDateInstance().format((parseCard.getCreatedAt())));

                    update_TagsCounts(parseCard);

                    ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_NEW_CARD);

                    ((MainActivity) MainActivity.context).profilFragment.userCardsTab.addNewCardinGrid(cardData);

                    Message.message(context, context.getResources().getString(R.string.message_card_sent));

                    ParseUser.getCurrentUser().increment("cards_count", 1);

                    ParseUser.getCurrentUser().saveInBackground();

                    ParsePush.subscribeInBackground("master_" + parseCard.getObjectId());

                } else Message.message(context, context.getResources().getString(R.string.message_card_not_sent));
            }
        });
    }

    private static ParseFile get_PictFile_FromBtm(Bitmap btm) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        btm.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        byte[] byteArray = stream.toByteArray();

        return new ParseFile("card_pict.jpeg", byteArray);
    }

    public static void insert_card_pict(final Context context, final CardData cardData, Uri image) {

        final ParseCard parseCard = new ParseCard(cardData, false);

        try {

            Bitmap btm = tools.decodeUri(context, image, 700);

            final ParseFile parsePict = get_PictFile_FromBtm(btm);

            parsePict.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {

                    parseCard.set_Pict(parsePict);

                    parseCard.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                cardData.setCard_id(parseCard.getObjectId());

                                cardData.setTimestamp(DateFormat.getDateInstance().format((parseCard.getCreatedAt())));

                                cardData.set_pict_url(parseCard.getParseFile("pict").getUrl());

                                update_TagsCounts(parseCard);

                                ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_NEW_CARD);

                                ((MainActivity) MainActivity.context).profilFragment.userCardsTab.addNewCardinGrid(cardData);

                                Message.message(context, context.getResources().getString(R.string.message_card_sent));

                                ParseUser.getCurrentUser().increment("cards_count", 1);

                                ParseUser.getCurrentUser().saveInBackground();

                                ParsePush.subscribeInBackground("master_" + parseCard.getObjectId());

                            } else Message.message(context, context.getResources().getString(R.string.message_card_not_sent));
                        }
                    });


                }
            });

        } catch (FileNotFoundException e) {

            Message.message(context, context.getResources().getString(R.string.message_picture_error));
        }
    }

    public static void increment_tags_nb_followers(final Context context, final ArrayList<String> tags, final ParseTagsSubscription subscription) {

        ParseQuery<ParseTagsNbFollowers> query = ParseQuery.getQuery(ParseTagsNbFollowers.class);

        query.whereContainsAll(ParseTagsNbFollowers.TAGS, tags);

        query.whereEqualTo(ParseTagsNbFollowers.NB_TAGS, tags.size());

        query.getFirstInBackground(new GetCallback<ParseTagsNbFollowers>() {
            @Override
            public void done(ParseTagsNbFollowers result, ParseException e) {

                if (e == null) {

                    result.increment(ParseTagsNbFollowers.NB_FOLLOWERS, 1);

                    result.saveInBackground();

                    subscription.put(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT, result);

                    subscription.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) get_NewSubscription(context, tags);

                            // TODOOOOO

                        }
                    });

                } else {

                    final ParseTagsNbFollowers tagsNbFollowers = new ParseTagsNbFollowers(tags);

                    tagsNbFollowers.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                subscription.put(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT, tagsNbFollowers);

                                subscription.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if (e == null) get_NewSubscription(context, tags);

                                        // TODDDOOOOO

                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }


    public static void insert_tags_subscription(final Context context, final ArrayList<String> tags) {

        ParseQuery<ParseTagsSubscription> query = ParseQuery.getQuery(ParseTagsSubscription.class);

        ParseUser subscriber = ParseUser.getCurrentUser();

        query.whereEqualTo(ParseTagsSubscription.JOIN_USER, subscriber);

        query.whereContainsAll(ParseTagsSubscription.TAGS, tags);

        query.whereEqualTo(ParseTagsSubscription.NB_TAGS, tags.size());

        query.getFirstInBackground(new GetCallback<ParseTagsSubscription>() {

            @Override
            public void done(ParseTagsSubscription result, ParseException e) {

                if (e == null) Message.message(context, context.getResources().getString(R.string.message_tag_already_subscribed));

                else {

                    final ParseTagsSubscription subscription = new ParseTagsSubscription(tags);

                    subscription.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                String result = "";

                                for (String tag : tags) result += "#" + tag + " ";

                                Message.message(context, context.getResources().getString(R.string.message_tag_subscribed_to) + result);

                                increment_tags_nb_followers(context, tags, subscription);

                            }
                        }
                    });
                }
            }
        });
    }


    private static void update_TagsCounts(ParseCard parseCard) {

        for (String tag : parseCard.getTags()) {

            incrementOrInsert_Tag(tag);

            incrementOrInsert_UserTagCount(tag, parseCard, ParseUser.getCurrentUser());

        }
    }


    private static void incrementOrInsert_Tag(final String tagName) {

        ParseQuery<ParseTag> query = ParseQuery.getQuery("ParseTag");

        query.whereEqualTo("tag", tagName);

        query.getFirstInBackground(new GetCallback<ParseTag>() {
            @Override
            public void done(ParseTag parseTag, ParseException e) {

                if (e == null) {

                    parseTag.increment("counter", 1);
                    parseTag.saveInBackground();

                } else (new ParseTag(tagName)).saveInBackground();

            }
        });
    }


    private static void incrementOrInsert_UserTagCount(final String tagName, final Object tagID, final Object userID) {

        ParseQuery<ParseUserTagCount> query = ParseQuery.getQuery("ParseUserTagCount");

        query.whereEqualTo("tag", tagName);
        query.whereEqualTo("user_id", userID);

        query.getFirstInBackground(new GetCallback<ParseUserTagCount>() {
            @Override
            public void done(ParseUserTagCount tagUserCount, ParseException e) {

                if (e == null) {

                    tagUserCount.increment("counter", 1);

                    tagUserCount.saveInBackground();

                } else (new ParseUserTagCount(tagName, tagID, userID)).saveInBackground();

            }
        });
    }


    // INSERT & GET UPVOTES -------------------------


    public static void check_and_insert_upvote(final Context context,
                                               final String cardID, final String upvoterID, final String receiverID,
                                               final String upvoterFullName, final String cardContent) {

        ParseQuery<ParseUpvote> query = ParseQuery.getQuery(ParseUpvote.class);

        //Message.message(context, "1 - check_and_insert_upvote called");

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);
        ParseUser user = ParseUser.createWithoutData(ParseUser.class, upvoterID);
        ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);

        query.whereEqualTo(ParseUpvote.JOIN_CARD, card);
        query.whereEqualTo(ParseUpvote.JOIN_UPVOTER, user);
        query.whereEqualTo(ParseUpvote.JOIN_RECEIVER, receiver);

        query.getFirstInBackground(new GetCallback<ParseUpvote>() {
            @Override
            public void done(ParseUpvote object, ParseException e) {

                //Message.message(context, ">> " + cardContent);

                if (e == null) {

                    Message.message(context, context.getResources().getString(R.string.message_already_upvoted));

                } else
                    insert_upvote(context, cardID, upvoterID, receiverID, upvoterFullName, cardContent,
                            ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LATITUDE),
                            ((MainActivity) MainActivity.context).localData.getGPS(SharedPref.KEY_LONGITUDE));

            }
        });
    }

    private static void insert_upvote(final Context context,
                                      final String cardID, final String upvoterID, final String receiverID,
                                      final String upvoterFullName, final String cardContent,
                                      final float latitude, final float longitude) {

        final ParseUpvote parseUpvote = new ParseUpvote(cardID, upvoterID, receiverID, latitude, longitude);

        parseUpvote.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Message.message(context, context.getResources().getString(R.string.message_upvoted));

                    ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_UPVOTE);

                    incrementCardUpvoteCount(cardID, 1);

                    insert_Notification(
                            receiverID, upvoterID, cardID,
                            ParseNotification.TYPE_UPVOTE, upvoterFullName, cardContent,
                            latitude, longitude);

                } else {
                    Message.message(context, context.getResources().getString(R.string.message_no_upvoted) + receiverID + "\n" + e);
                }
            }
        });

    }

    public static void insert_pass(final Context context, final String cardID, final String downswiperID, final String cardAuthorID,
                                   float latitude, float longitude) {

        if (!((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID).equals(MainActivity.ADMIN_ID)) {

            final ParseDownSwipe parseDownSwipe = new ParseDownSwipe(cardID, downswiperID, cardAuthorID, latitude, longitude);

            parseDownSwipe.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        // increment pass column
                        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);

                        ForumApp.trackAction(MainActivity.mTracker, ForumApp.A_PASS);

                        card.increment("pass_count", 1);

                        card.saveInBackground();

                    } else Message.message(context, "error - no downvote" + e);
                }
            });

        }

    }


    public static void delete_upvote(final Context context, final String cardID, final String voterID, final String receiverID) {

        ParseQuery<ParseUpvote> query = ParseQuery.getQuery(ParseUpvote.class);

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);
        ParseUser user = ParseUser.createWithoutData(ParseUser.class, voterID);
        final ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);

        query.whereEqualTo(ParseUpvote.JOIN_CARD, card);
        query.whereEqualTo(ParseUpvote.JOIN_UPVOTER, user);
        query.whereEqualTo(ParseUpvote.JOIN_RECEIVER, receiver);

        query.getFirstInBackground(new GetCallback<ParseUpvote>() {
            @Override
            public void done(ParseUpvote object, ParseException e) {

                if (e == null) {

                    object.deleteInBackground();

                    Message.message(context, context.getResources().getString(R.string.message_upvoted_deleted));

                    incrementCardUpvoteCount(cardID, -1);

                    delete_notification(
                            receiverID, voterID, cardID,
                            ParseNotification.TYPE_UPVOTE);

                } else Message.message(context, "error delete : " + e);
            }
        });


    }


    private static void incrementCardUpvoteCount(String cardID, final int value) {

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);

        card.increment("upvote_count", value);

        card.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    //Message.message(MainActivity.context, "increment :" + value);

                } else if (e != null) {

                    Message.message(MainActivity.context, "3 - error - incrementCardUpvoteCount \n" + e);
                }
            }
        });
    }


    public static void get_userVote(final Context context, String cardID, String receiverID, final CommentRecyclerAdapter.CommentViewHolder viewHolder) {

        final Drawable upvoteON = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_orange));

        final Drawable upvoteOFF = (ContextCompat.getDrawable(MainActivity.context, R.drawable.ic_thumb_up_border));

        ParseQuery<ParseUpvote> query = ParseQuery.getQuery(ParseUpvote.class);

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);

        ParseUser user = ParseUser.getCurrentUser();

        ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);

        query.whereEqualTo(ParseUpvote.JOIN_UPVOTER, user);

        query.whereEqualTo(ParseUpvote.JOIN_CARD, card);

        query.whereEqualTo(ParseUpvote.JOIN_RECEIVER, receiver);

        query.getFirstInBackground(new GetCallback<ParseUpvote>() {
            @Override
            public void done(ParseUpvote user, ParseException e) {

                if (e == null) {

                    ((SubActivity) context).cardFragment.commentAdapter.set_header_btn_upvote(true, viewHolder);

                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {

                    ((SubActivity) context).cardFragment.commentAdapter.set_header_btn_upvote(false, viewHolder);


                } else Message.message(MainActivity.context, "error - get_userVote : \n" + e);
            }
        });

    }

    public static void set_chatSubscription(Context context, String role, String chatID, boolean subscribe) {

        ParseChat parseChat = ParseObject.createWithoutData(ParseChat.class, chatID);

        if (subscribe) {

            if (role.equals(ChatOverviewData.STARTER)) {

                parseChat.subscribe_commenter(ParseChat.STARTER_IS_LISTENING);
                parseChat.saveInBackground();

            } else if (role.equals(ChatOverviewData.REPLIER)) {

                parseChat.subscribe_commenter(ParseChat.REPLIER_IS_LISTENING);
                parseChat.saveInBackground();
            }

            ParsePush.subscribeInBackground("chat_" + chatID);
            Message.message(context, "Push notification ON");

        } else {

            if (role.equals(ChatOverviewData.STARTER)) {

                parseChat.unsubscribe_commenter(ParseChat.STARTER_IS_LISTENING);
                parseChat.saveInBackground();

            } else if (role.equals(ChatOverviewData.REPLIER)) {

                parseChat.unsubscribe_commenter(ParseChat.REPLIER_IS_LISTENING);
                parseChat.saveInBackground();
            }

            ParsePush.unsubscribeInBackground("chat_" + chatID);
            Message.message(context, "Push notification OFF");
        }

    }

    public static void set_cardSubscription(Context context, String cardID, boolean subscribe) {

        ParseCard parseCard = ParseObject.createWithoutData(ParseCard.class, cardID);
        parseCard.set_listening_state(subscribe);
        parseCard.saveInBackground();

        if (subscribe) {

            ParsePush.subscribeInBackground("master_" + cardID);
            Message.message(context, "Push notification ON");

        } else {
            ParsePush.unsubscribeInBackground("master_" + cardID);
            Message.message(context, "Push notification OFF");
        }

    }

    // TODO a revoir
    public static void get_cardNotifState(final Context context, String cardID) {

        Message.message(context, "search > " + "master_" + cardID);

        ParseQuery<ParseInstallation> query = ParseQuery.getQuery(ParseInstallation.class);

        query.whereEqualTo("channels", "master_" + cardID);

        query.getFirstInBackground(new GetCallback<ParseInstallation>() {
            @Override
            public void done(ParseInstallation object, ParseException e) {

                if (e == null) {

                    Message.message(context, "search > " + "ON");

                    //((SubActivity) context).cardFragment.headerCardFrag.set_btn_notif(false);

                } else {

                    Message.message(context, "search > " + "OFF\n" + e);

                    //((SubActivity) context).cardFragment.headerCardFrag.set_btn_notif(true);
                }
            }
        });


    }


    // NOTIFICATION -------------------------
    private static void insert_error_message() {

    }

    private static void insert_Notification(final String receiverID,
                                            String senderID,
                                            final String cardID,
                                            final int type,
                                            String senderFullName,
                                            String cardContent,
                                            float latitude, float longitude) {

        //if (type == ParseNotification.TYPE_COMMENT) Message.message(MainActivity.context, "insert comment");

        final ParseNotification notification = new ParseNotification(
                receiverID, senderID, cardID,
                type, senderFullName, cardContent,
                latitude, longitude);

        notification.saveInBackground(new SaveCallback() {

            @Override
            public void done(ParseException e) {

                if (e == null) {

                    //Message.message(MainActivity.context, "insert notification");

                    //if (type == ParseNotification.TYPE_UPVOTE) incrementCardUpvoteCount(cardID, 1);

                } else
                    Message.message(MainActivity.context, "2 - error - insert_Notification \n" + e);
            }
        });

    }

    public static void delete_subscription(final Context context, ParseTagsSubscription user_subscription, final ParseTagsNbFollowers tags_subscription) {

        user_subscription.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {

                tags_subscription.increment(ParseTagsNbFollowers.NB_FOLLOWERS, -1);

                tags_subscription.saveInBackground();

            }
        });


    }

    public static void get_Curated_Accounts(final Context context, final int offset) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereEqualTo("curated", true);

        query.orderByAscending("surname");

        query.setLimit(24);

        query.setSkip(24 * offset);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> authorList, ParseException e) {

                if (e == null)

                    ((MainActivity) context).navigationSelector.page02.display_authors(context, authorList, offset);
            }
        });


    }

    private static void get_NewSubscription(final Context context, final ArrayList<String> tags) {

        String mainUserID = ((MainActivity) MainActivity.context).localData.getUserData(SharedPref.KEY_USER_ID);

        ParseQuery<ParseTagsSubscription> query = ParseQuery.getQuery("ParseTagsSubscription");

        final ParseUser mainUser = ParseUser.createWithoutData(ParseUser.class, mainUserID);

        query.whereEqualTo(ParseTagsSubscription.JOIN_USER, mainUser);

        query.whereContainsAll("tags", tags);

        query.whereEqualTo("nb_tags", tags.size());

        query.orderByDescending("createdAt");

        query.include(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT);

        query.getFirstInBackground(new GetCallback<ParseTagsSubscription>() {
            @Override
            public void done(ParseTagsSubscription subscription, ParseException e) {

                if (e == null) {

                    final ParseTagsNbFollowers parseNbFollowers = (ParseTagsNbFollowers) subscription.get(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT);

                    if (parseNbFollowers != null)
                        subscription.setNb_followers(parseNbFollowers.getInt(ParseTagsNbFollowers.NB_FOLLOWERS));

                    else subscription.setNb_followers(0);

                    ((MainActivity) MainActivity.context).navigationSelector

                            .page00.navigationAdapter.add_subscription(subscription);

                }

            }
        });

    }


    public static void insert_default_subscription() {

        //Message.message(MainActivity.context, "load default subscriptions");

        ParseUser.getCurrentUser().put("init_first_subscription", true);
        ParseUser.getCurrentUser().saveInBackground();

        String[] tags = (MainActivity.context).getResources().getStringArray(R.array.default_feeds);

        for (int i = 0; i < tags.length; i++)

            insert_tags_subscription(MainActivity.context, Tags.Spliter(tags[i]));
    }


    public static void get_UserSubscriptions(final Context context, final String mainUserID, final int offset) {

        ParseQuery<ParseTagsSubscription> query = ParseQuery.getQuery("ParseTagsSubscription");

        final ParseUser mainUser = ParseUser.createWithoutData(ParseUser.class, mainUserID);

        query.whereEqualTo(ParseTagsSubscription.JOIN_USER, mainUser);

        query.orderByDescending("createdAt");

        query.setLimit(24);

        query.setSkip(24 * offset);

        query.include(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT);

        query.findInBackground(new FindCallback<ParseTagsSubscription>() {

            public void done(final List<ParseTagsSubscription> parseTagsSubscriptionsList, ParseException e) {

                if (e == null) {

                    for (ParseTagsSubscription parseNotification : parseTagsSubscriptionsList) {

                        final ParseTagsNbFollowers parseNbFollowers = (ParseTagsNbFollowers) parseNotification.get(ParseTagsSubscription.JOIN_FOLLOWERS_COUNTER_OBJECT);

                        if (parseNbFollowers != null)

                            parseNotification.setNb_followers(parseNbFollowers.getInt(ParseTagsNbFollowers.NB_FOLLOWERS));

                        else parseNotification.setNb_followers(0);

                    }

                    ((MainActivity) context).navigationSelector.page00.display_subscriptions(context, parseTagsSubscriptionsList, offset);

                } else Message.message(context, "error - get_UserSubscriptions : " + mainUserID);
            }
        });


    }

    public static void get_UserNotifications(final Context context, final String mainUserID, final int offset) {

        ParseQuery<ParseNotification> query = ParseQuery.getQuery("ParseNotification");

        final ParseUser mainUser = ParseUser.createWithoutData(ParseUser.class, mainUserID);

        query.whereEqualTo(ParseNotification.JOIN_RECEIVER, mainUser);

        query.orderByDescending("createdAt");

        query.setLimit(24);

        query.setSkip(24 * offset);

        query.include("sender");

        query.findInBackground(new FindCallback<ParseNotification>() {

            public void done(final List<ParseNotification> parseNotificationList, ParseException e) {

                if (e == null) {

                    for (ParseNotification parseNotification : parseNotificationList) {

                        final ParseUser user = parseNotification.getParseUser("sender");

                        parseNotification.setSender_FullName(user.getString("name") + " " + user.getString("surname"));

                        if (user.getParseFile("profil_pict") != null)

                            parseNotification.setSenderProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseNotification.setSenderProfilpictUrl("NULL");

                    }


                    if (offset == 0 && parseNotificationList.size() != 0) {

                        ((MainActivity) MainActivity.context).localData.store_most_recent_timestamp_parse(parseNotificationList.get(0).getCreatedAt());

                        if (!parseNotificationList.get(0).getIs_opened())

                            ((MainActivity) MainActivity.context).change_tab_profil_icon(false);

                    }

                    ((MainActivity) context).profilFragment.notifFrag.display_notifications(parseNotificationList, offset);

                } else Message.message(context, "error - get_CardReplies : " + mainUserID);
            }
        });
    }


    public static void getUserNotificationsRefresh(final Context context, final String mainUserID, Date last_timestamp, final boolean fromResume) {

        ParseQuery<ParseNotification> query = ParseQuery.getQuery("ParseNotification");

        final ParseUser mainUser = ParseUser.createWithoutData(ParseUser.class, mainUserID);

        query.whereEqualTo(ParseNotification.JOIN_RECEIVER, mainUser);

        query.orderByDescending("createdAt");

        query.whereGreaterThan("createdAt", last_timestamp);

        query.include("sender");

        query.findInBackground(new FindCallback<ParseNotification>() {
            @Override
            public void done(List<ParseNotification> parseNotificationList, ParseException e) {

                if (e == null) {

                    for (final ParseNotification parseNotification : parseNotificationList) {

                        final ParseUser user = parseNotification.getParseUser("sender");

                        if (user.getParseFile("profil_pict") != null)

                            parseNotification.setSenderProfilpictUrl(user.getParseFile("profil_pict").getUrl());

                        else parseNotification.setSenderProfilpictUrl("NULL");

                    }

                    if (parseNotificationList.size() != 0) {

                        ((MainActivity) MainActivity.context).localData.store_most_recent_timestamp_parse(parseNotificationList.get(0).getCreatedAt());

                        ((MainActivity) context).profilFragment.notifFrag.displayCards(context, NotifData.getNotifList(parseNotificationList), -1);

                        //((MainActivity) context).notifFragment.displayCards(context, NotifData.getNotifList(parseNotificationList), -1);

                        if (fromResume) ((MainActivity) MainActivity.context).change_tab_profil_icon(false);

                    }

                } else Message.message(context, "error - getUserNotifications()");

            }
        });

    }


    public static void change_Notification_To_Opened(final String notifID) {

        ParseNotification notification = ParseNotification.createWithoutData(ParseNotification.class, notifID);

        notification.setIs_opened(true);

        notification.saveInBackground();

    }


    public static void delete_notification(String receiverID, String senderID, final String cardID, final int type) {

        ParseQuery<ParseNotification> query = ParseQuery.getQuery(ParseNotification.class);

        ParseCard card = ParseCard.createWithoutData(ParseCard.class, cardID);
        ParseUser sender = ParseUser.createWithoutData(ParseUser.class, senderID);
        ParseUser receiver = ParseUser.createWithoutData(ParseUser.class, receiverID);

        query.whereEqualTo(ParseNotification.JOIN_CARD, card);
        query.whereEqualTo(ParseNotification.JOIN_SENDER, sender);
        query.whereEqualTo(ParseNotification.JOIN_RECEIVER, receiver);
        query.whereEqualTo(ParseNotification.TYPE, type);

        query.getFirstInBackground(new GetCallback<ParseNotification>() {
            @Override
            public void done(ParseNotification object, ParseException e) {

                if (e == null) {

                    object.deleteInBackground();

                    //Message.message(MainActivity.context, "delete notification");

                    //if (type == ParseNotification.TYPE_UPVOTE) incrementCardUpvoteCount(cardID, -1);

                } else Message.message(MainActivity.context, "error delete notification : " + e);
            }
        });

    }

}
