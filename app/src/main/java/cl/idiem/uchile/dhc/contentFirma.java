package cl.idiem.uchile.dhc;

/**
 * Created by Usuario on 24-05-2016.
 */
import android.net.Uri;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

import cl.idiem.uchile.dhc.Assist.Utilitys;

public class contentFirma extends Activity {
    Button b1;
    ImageView signImage;
    String MEDIA_DIRECTORY = "DCIM/Camera";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b1.setOnClickListener(onButtonClick);
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent i = new Intent(contentFirma.this, CaptureSignature.class);
            startActivityForResult(i, 0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        Globals globals = (Globals) getApplicationContext();
        if (resultCode == 1) {
            File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            file.mkdirs();

            String path = Environment.getExternalStorageDirectory() + File.separator
                    + MEDIA_DIRECTORY + File.separator + "aaa.jpg";

            File newFile = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            Bitmap b = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("byteArray"), 0,
                    data.getByteArrayExtra("byteArray").length);
            signImage.setImageBitmap(b);
            globals.getMuestra().setFirma(Utilitys.encodeToBase64(b, Bitmap.CompressFormat.JPEG, 50));
        }
    }
}
