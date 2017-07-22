package com.example.limat.imageapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MemeCreate extends Activity implements View.OnTouchListener{

    ImageView im;
    Button add_button;
    Button save_button;
    Button scale_button;
    private float dX,dY;
    private int lastAction;
    private FrameLayout frame;
    private long downtime;
    //TextView toptext,bottomtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_create);

        frame = (FrameLayout)findViewById(R.id.meme_frame);
        save_button = (Button)findViewById(R.id.save_button);
        add_button = (Button)findViewById(R.id.add_button);
        scale_button = (Button)findViewById(R.id.resize);

        add_button.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MemeCreate.this);
                        View dialogue = getLayoutInflater().inflate(R.layout.dialog_create_text,null);
                        final EditText text_entry = (EditText) dialogue.findViewById(R.id.text_entry);
                        Button submit_button = (Button)dialogue.findViewById(R.id.submit_button);

                        mBuilder.setView(dialogue);
                        final AlertDialog d = mBuilder.create();

                        submit_button.setOnClickListener(
                                new Button.OnClickListener()
                                {
                                    public void onClick(View v)
                                    {
                                        if(!text_entry.getText().toString().equals(""))
                                        {
                                            frame.addView(createNewTextView(text_entry.getText().toString()));
                                           // Toast.makeText(getApplicationContext(), "not empty", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(getApplicationContext(),"empty",Toast.LENGTH_SHORT).show();
                                        d.dismiss();
                                    }
                                }
                        );

                        d.show();

                    }
                }
        );

        save_button.setOnClickListener(
            new Button.OnClickListener()
            {
                public void onClick(View v)
                {
                    Bitmap meme = viewtoBitmap(frame);

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyymmddhhmmss");
                    String formatted_date = df.format(c.getTime());
                    String filename = "meme"+formatted_date+".jpg";

                    final File path =
                            Environment.getExternalStoragePublicDirectory
                                    (
                                            //Environment.DIRECTORY_PICTURES
                                            //Environment.DIRECTORY_DCIM
                                            Environment.DIRECTORY_DCIM + "/Memes/"
                                    );


                    // Make sure the Pictures directory exists.
                    if(!path.exists())
                    {
                        boolean a=path.mkdirs();
                        Toast.makeText(getApplicationContext(),Boolean.toString(a),Toast.LENGTH_SHORT);
                    }

                    final File file = new File(path, filename);

                    try
                    {
                        final FileOutputStream fos = new FileOutputStream(file);
                        final BufferedOutputStream bos = new BufferedOutputStream(fos, 8192);

                        //bmp.compress(CompressFormat.JPEG, 100, bos);
                        meme.compress(Bitmap.CompressFormat.JPEG, 85, bos);
                        bos.flush();
                        bos.close();
                        Toast.makeText(getApplicationContext(),"saved as "+filename+" under directory Memes in DCIM",Toast.LENGTH_SHORT).show();
                    }
                    catch (final IOException e)
                    {
                        Toast.makeText(getApplicationContext(),"couldn't save :( ",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                }
            }
        );

        scale_button.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {

                    }
                }
        );



        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path");

        Uri temp = Uri.parse(path);
        im = (ImageView)findViewById(R.id.picture);
        im.setImageURI(temp);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private TextView createNewTextView(String text) {
        final FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(text);
        textView.setOnTouchListener(this);
        return textView;
    }

    public Bitmap viewtoBitmap(View v)
    {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),v.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        v.draw(canvas);
        return b;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastAction = MotionEvent.ACTION_DOWN;
                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                downtime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                lastAction = MotionEvent.ACTION_MOVE;
                //Toast.makeText(getApplicationContext(),"touched",10).show();
                v.setX(event.getRawX() + dX);
                v.setY(event.getRawY() + dY);
                Log.v("touched and dragged","nothing");
                break;
            case MotionEvent.ACTION_UP:
                if(System.currentTimeMillis()-downtime<500)
                {
                    if(v instanceof TextView)
                    {
                        final TextView selected_text = (TextView)v;
                        final Dialog edit_box = new Dialog(this);
                        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.dialogue_edit_text,null);
                        edit_box.setContentView(layout);
////////////////////declarations
                        SeekBar text_size_specifier = (SeekBar)layout.findViewById(R.id.text_size_specifier);
                        Button edit_ok = (Button)layout.findViewById(R.id.submit_button_for_edit);
                        final TextView sample = (TextView)layout.findViewById(R.id.text_size_sample);
                        final TextView color_sample = (TextView)layout.findViewById(R.id.font_color_heading);
                        final EditText text_entry = (EditText)layout.findViewById(R.id.text_entry);
                        final Spinner color_selector = (Spinner)layout.findViewById(R.id.text_color_selector);
                        Button delete_button = (Button)layout.findViewById(R.id.delete_button);
////////////////////////////text color
                        final List<String> color_adapter = new ArrayList<String>();
                        color_adapter.add("default");
                        color_adapter.add("white");
                        color_adapter.add("black");
                        color_adapter.add("green");
                        color_adapter.add("red");
                        color_adapter.add("blue");
                        color_adapter.add("yellow");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,color_adapter);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        color_selector.setAdapter(adapter);
                        color_selector.setOnItemSelectedListener(
                            new Spinner.OnItemSelectedListener()
                            {
                                public void onItemSelected(AdapterView<?> adapterView,View view,int position,long id)
                                {
                                    Object item = adapterView.getItemAtPosition(position);
                                    if(item!=null)
                                    {
                                        //Toast.makeText(getApplicationContext(),item.toString(),Toast.LENGTH_SHORT).show();
                                        switch (item.toString())
                                        {
                                            case "white":color_sample.setTextColor(Color.WHITE);
                                                selected_text.setTextColor(Color.WHITE);
                                                break;
                                            case "default":
                                                break;
                                            case "black":color_sample.setTextColor(Color.BLACK);
                                                selected_text.setTextColor(Color.BLACK);
                                                break;
                                            case "green":color_sample.setTextColor(Color.GREEN);
                                                selected_text.setTextColor(Color.GREEN);
                                                break;
                                            case "red":color_sample.setTextColor(Color.RED);
                                                selected_text.setTextColor(Color.RED);
                                                break;
                                            case "blue":color_sample.setTextColor(Color.BLUE);
                                                selected_text.setTextColor(Color.BLUE);
                                                break;
                                            case "yellow":color_sample.setTextColor(Color.YELLOW);
                                                selected_text.setTextColor(Color.YELLOW);
                                                break;
                                        }
                                    }
                                }
                                    public  void onNothingSelected(AdapterView<?> adapterView)
                                    {}
                                }
                        );
//////////////////////////initialization
                        sample.setTextSize(selected_text.getTextSize());
                        text_size_specifier.setProgress((int)selected_text.getTextSize()/4);
                        text_entry.setText(selected_text.getText().toString());


                        SeekBar.OnSeekBarChangeListener text_size_specifier_listener = new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                selected_text.setTextSize(progress);
                                sample.setTextSize(progress);

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                        text_size_specifier.setOnSeekBarChangeListener(text_size_specifier_listener);

                        edit_ok.setOnClickListener(
                                new Button.OnClickListener()
                                {
                                    public void onClick(View v)
                                    {
                                        selected_text.setText(text_entry.getText().toString());
                                        edit_box.dismiss();
                                    }
                                }
                        );

                        delete_button.setOnClickListener(
                                new Button.OnClickListener()
                                {
                                    public void onClick(View v)
                                    {
                                        //delete selected text
                                        selected_text.setVisibility(View.GONE);
                                        edit_box.dismiss();
                                    }
                                }
                        );
                        edit_box.show();
                    }
                }
                break;
        }
        return true;
    }
}
