package co.forum.app.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalScrollView2 extends HorizontalScrollView{
    public HorizontalScrollView2(Context context) {
        super(context);
    }

    public HorizontalScrollView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //Message.message(MainActivity.context, "touch");
        return super.onTouchEvent(ev);

    }

    /*
    private float SWIPE_VERT_MIN_DISTANCE = 100;
    private float startlocation = 0;
    private float diffY = 0;
    private boolean vertScroll = false;

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev){
        boolean ret = super.onInterceptTouchEvent(ev);
        Message.message(MainActivity.context,"" + ev );

        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startlocation = ev.getY();
                vertScroll = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Message.message(MainActivity.context, "MOVE");
                break;
            case MotionEvent.ACTION_UP:
                diffY = Math.abs(startlocation - ev.getY());
                if(diffY > SWIPE_VERT_MIN_DISTANCE){
                    vertScroll = true;
                    Message.message(MainActivity.context, "vert true");
                }
                Message.message(MainActivity.context, "start : " + startlocation);
                Message.message(MainActivity.context, "end : " + ev.getY());
                break;
        }

        if(ret){
            if(vertScroll)
                return false;
            else
                return true;
        }

        return ret;
    }
    */
}
