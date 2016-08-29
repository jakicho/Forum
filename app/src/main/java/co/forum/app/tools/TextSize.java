package co.forum.app.tools;

import android.widget.TextView;

public class TextSize {

    public static void setHeaderVerticalPadding(TextView main_content, int remaining_char) {

        // todo a paramettrer
        int padding = 10;

        if(remaining_char >= 315) padding = 200; // < 85 char enter

        else if(remaining_char>=255 && remaining_char<315) padding = 150; // [145 - 85]

        else if(remaining_char>=150 && remaining_char<255) padding = 48; // [250 - 145]

        else if(remaining_char>=110 && remaining_char<150) padding = 15; // [290 - 250]

        else if(remaining_char>=70 && remaining_char<110) padding = 12; // [330 - 290]

        else if(remaining_char<70) padding = 10; // > 330 char enter

        main_content.setPadding(32, padding, 32, padding);

        //Message.message(MainActivity.context, ""+padding);

    }

    public static float scale(int remaining_char, boolean isReply) {

        float size = 29;

        if(!isReply) {

            if(remaining_char >= 315) size = 29; // < 85 char enter

            else if(remaining_char>=255 && remaining_char<315) size = 24; // [145 - 85]

            else if(remaining_char>=150 && remaining_char<255) size = 22; // [250 - 145]

            else if(remaining_char>=110 && remaining_char<150) size = 20; // [290 - 250]

            else if(remaining_char>=70 && remaining_char<110) size = 20; // [330 - 290]

            else if(remaining_char<70) size = 19; // > 330 char enter

        } else {

            if(remaining_char >= 315) size = 24; // < 85 char enter

            else if(remaining_char>=255 && remaining_char<315) size = 24; // [170 - 85]

            else if(remaining_char>=150 && remaining_char<255) size = 22; // [250 - 150]

            else if(remaining_char>=110 && remaining_char<150) size = 20; // [250 - 150]

            else if(remaining_char>=70 && remaining_char<110) size = 20; // [330 - 250]

            else if(remaining_char<70) size = 19; // > 330 char enter

        }
        return size;
    }


    public static float miniScale(int charCount) {

        float size = 10;

        if (charCount < 100) size = 18;

        else if (charCount >= 100) size = 16;

        return size;
    }

}
