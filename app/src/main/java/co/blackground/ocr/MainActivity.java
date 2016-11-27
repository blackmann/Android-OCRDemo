package co.blackground.ocr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;


public class MainActivity extends Activity {

    private static final int RC_CHOOSE_PHOTO = 1;
    private static final String TAG = "MainActivity";
    TextView tvResult;

    TessUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new TessUtils(getAssets());

        Button btnChoose = (Button) findViewById(R.id.chooseImage);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        tvResult = (TextView) findViewById(R.id.resultTxt);
    }

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_CHOOSE_PHOTO);
    }

    private void setResult(String result) {
        if (result == null || result.isEmpty()) {
            tvResult.setText(R.string.no_result);
        } else {
            tvResult.setText(result);
        }
    }

    private void extractText(Intent data) {
        try {
            Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            new DoOCR(image).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                extractText(data);
            }
        }
    }

    private class DoOCR extends AsyncTask<Void, Void, Void> {

        private final Bitmap file;
        private String result;
        private TessBaseAPI api;

        DoOCR(Bitmap file) {
            this.file = file;
            api = utils.getTess();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            api.setImage(file);
            result = api.getUTF8Text();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setResult(result);
        }
    }
}
