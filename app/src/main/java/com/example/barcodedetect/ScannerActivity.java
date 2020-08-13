package com.example.barcodedetect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private Button buttonConfirm;
    private Button buttonCancel;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    //This class provides methods to play DTMF tones
    private ToneGenerator toneGen1;
    private TextView barcodeText;
    private String barcodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

//        // Get the Intent that started this activity and extract the string
//        Intent intent = getIntent();

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC,100);
        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonCancel = findViewById(R.id.buttonCancel);
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {

        Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.UPC_A | Barcode.UPC_E | Barcode.EAN_8 | Barcode.EAN_13 |
                        Barcode.DATA_MATRIX | Barcode.QR_CODE).build();

        if(!barcodeDetector.isOperational()){
            Toast.makeText(getApplicationContext(), "Could not set up the detector!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannerActivity.this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(holder); //surfaceView.getHolder()
                    } else {
                        ActivityCompat.requestPermissions(ScannerActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeText.post(new Runnable() {

                        @Override
                        public void run() {
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                            barcodeData = barcodes.valueAt(0).displayValue;
                            barcodeText.setText(barcodeData.toString());
                            buttonConfirm.setVisibility(View.VISIBLE);
                            buttonCancel.setVisibility(View.VISIBLE);
                            barcodeDetector.release();
                        }
                    });
                }
            }
        });
    }
//                if (barcodes.size() != 0) {
//
//                    barcodeText.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            if (barcodes.valueAt(0).email != null) {
//                                barcodeText.removeCallbacks(null);
//                                barcodeData = barcodes.valueAt(0).email.address;
//                                barcodeText.setText(barcodeData);
//                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
//                            } else {
//                                barcodeData = barcodes.valueAt(0).displayValue;
//                                barcodeText.setText(barcodeData);
//                                toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
//                            }
//                            barcodeText.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    finish();
//                                }
//                            });
//                        }
//                    });
//                }



    @Override
    protected void onPause() {
        super.onPause();
        getSupportActionBar().hide();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        initialiseDetectorsAndSources();
    }

    public void confirmCodebar(View v) {
        Intent data = new Intent();
        data.putExtra("barCode", barcodeData.toString());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        finish();
    }

    public void cancelCodebar(View v) {
        buttonConfirm.setVisibility(View.INVISIBLE);
        buttonCancel.setVisibility(View.INVISIBLE);
        initialiseDetectorsAndSources();
    }
}

