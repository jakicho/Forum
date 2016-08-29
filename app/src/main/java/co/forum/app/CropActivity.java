package co.forum.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.isseiaoki.simplecropview.CropImageView;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.forum.app.Data.ParseRequest;
import co.forum.app.tools.Message;
import co.forum.app.tools.tools;

public class CropActivity extends AppCompatActivity{

    public static final int RESULT_CROP_IMAGE = 1000;
    public static final int NO_CROP_IMAGE = 2000;

    public static final String KEY_IMAGE = "key_image";

    Context context;

    @Bind(R.id.cropImageView) CropImageView cropImageView;
    @Bind(R.id.croppedImageView) ImageView croppedImageView;
    @Bind(R.id.loader) LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.activity_crop);

        ButterKnife.bind(this);

        Uri pict_toCrop = getIntent().getData();

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        setTitle(getResources().getString(R.string.change_profil_pict));

        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_left_white_18dp));


        cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
        cropImageView.setMinFrameSizeInDp(100);
        cropImageView.setHandleSizeInDp(6);
        cropImageView.setTouchPaddingInDp(16);
        cropImageView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);


        try {

            // Set image for cropping
            //InputStream inputStream = getContentResolver().openInputStream(pict_toCrop);
            //drawable = Drawable.createFromStream(inputStream, pict_toCrop.toString());
            //cropImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), drawable.getInt()));
            //cropImageView.setImageBitmap(drawableToBitmap(drawable));

            Bitmap image = tools.decodeUri(context, pict_toCrop, 500);

            cropImageView.setImageBitmap(image);

            LinearLayout cropButton = (LinearLayout)findViewById(R.id.crop_button);

            final LinearLayout btn_validate = (LinearLayout)findViewById(R.id.btn_validate);


            cropButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get cropped image, and show result.
                    croppedImageView.setImageBitmap(cropImageView.getCroppedBitmap());

                    btn_validate.setVisibility(View.VISIBLE);
                }
            });

            btn_validate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cropImageView.setVisibility(View.GONE);

                    croppedImageView.setVisibility(View.GONE);

                    loader.setVisibility(View.VISIBLE);



                    ParseRequest.save_parse_image(context, cropImageView.getCroppedBitmap());

                }
            });

        } catch (FileNotFoundException e) {
            Message.message(MainActivity.context, getResources().getString(R.string.no_crop_image));
        }

        init_slide_to_exit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // top left button <-
        if (id == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        overridePendingTransition(0, R.anim.exit_right);

    }

    private void init_slide_to_exit() {

        final SlidrConfig configON = new SlidrConfig.Builder()
                .position(SlidrPosition.LEFT)
                .sensitivity(0.7f)
                .scrimColor(Color.BLACK)
                .scrimStartAlpha(0.8f)
                .scrimEndAlpha(0f)
                .velocityThreshold(2400)
                .distanceThreshold(0.4f)
                .edge(true)
                .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
                .build();

        Slidr.attach(this, configON);
    }






    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
