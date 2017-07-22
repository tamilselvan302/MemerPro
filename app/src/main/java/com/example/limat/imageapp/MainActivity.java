package com.example.limat.imageapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends Activity {

    Button create_new;
    //ImageView picture;
    final private static int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        create_new = (Button)findViewById(R.id.create_new);
        //picture = (ImageView)findViewById(R.id.picture);



        create_new.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        open_image_chooser();
                    }
                }

        );


    }

    protected void open_image_chooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select a template"),SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode,int resultcode,Intent data)
    {
        if(resultcode==RESULT_OK)
        {
            if(requestCode==SELECT_PICTURE)
            {
                Uri selectedImageUri = data.getData();
                if(null != selectedImageUri)
                {
                    //String path = getPathFromURI(selectedImageUri);
                    String path = selectedImageUri.toString();
                    //String path = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                    Log.i(TAG,"image:"+path);
                    //picture.setImageURI(selectedImageUri);
                    Intent in = new Intent(getApplicationContext(),MemeCreate.class);
                    in.putExtra("path",path);
                    startActivity(in);

                }
            }
        }
    }

}
