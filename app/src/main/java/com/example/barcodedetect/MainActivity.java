package com.example.barcodedetect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {

    private Button buttonRead;
    private Button buttonProcess;
    private TextView txtView;
    private ImageView imageView;
    private Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = findViewById(R.id.txtContent);
        imageView = findViewById(R.id.imgview);
        buttonRead = findViewById(R.id.buttonRead);
        buttonProcess = findViewById(R.id.buttonProcess);

        buttonRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // LOAD THE IMAGE
                myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.barcode_wiki);
                imageView.setImageBitmap(myBitmap);
            }
        });

        buttonProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SET UP THE BARCODE DETECTOR
                BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE | Barcode.UPC_A)
                                .build();
                if(!detector.isOperational()){
                    txtView.setText("Could not set up the detector!");
                    return;
                }

                // DETECT THE BARCODE
                try {
                    Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                    SparseArray<Barcode> barcodes = detector.detect(frame);

                    // DECODE THE BARCODE
                    Barcode thisCode = barcodes.valueAt(0);
                    txtView.setText(thisCode.rawValue);
                } catch (Exception e){
                    txtView.setText("The barcode format is not recognised");
                }

            }
        });
    }

}