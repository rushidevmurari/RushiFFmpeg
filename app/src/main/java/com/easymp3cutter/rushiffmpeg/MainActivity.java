package com.easymp3cutter.rushiffmpeg;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.exoplayer2.C;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.io.File;
import java.security.Permission;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

public class MainActivity extends AppCompatActivity {

    private ImageButton reverse,slow,fast,videoToAudio,RemoveAudio,ExtractImage,CompressVideo,LowResolution,HighResolution,VideoToGif,TrimVideo,AddVintage,AddBlackWhite,AddFadeInOut,VerticalFlip,HFlip,Rotation90,Rotation180,Rotation270,Blur4by3,Blur3by2,Blur5by4;
    private Button selectVideo,selectImage;
    private TextView tvLeft,tvRight;
    private ProgressDialog progressDialog;
    private int duration;
    private String video_url;
    private String audio_url;
    private String image_url;
    static MediaPlayer mediaPlayer;
    private VideoView videoView;
    private ImageView imageView;
    private static final int SELECT_AUDIO = 1;

    private Runnable r;
    private RangeSeekBar rangeSeekBar;
    private static final String root= Environment.getExternalStorageDirectory().toString();
    private static final String app_folder=root+"/DCIM/";

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }
        setContentView(R.layout.activity_main);

        Toast.makeText(MainActivity.this,"On_create_Method_Called",Toast.LENGTH_SHORT).show();

        rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeSeekBar);
        tvLeft = (TextView) findViewById(R.id.textleft);
        tvRight = (TextView) findViewById(R.id.textright);
        slow = (ImageButton) findViewById(R.id.slow);
        reverse = (ImageButton) findViewById(R.id.reverse);
        fast = (ImageButton) findViewById(R.id.fast);
        selectVideo = (Button) findViewById(R.id.select);
        fast = (ImageButton) findViewById(R.id.fast);
        videoToAudio = (ImageButton) findViewById(R.id.videotomp3);
        RemoveAudio = (ImageButton) findViewById(R.id.audiodisa);
        ExtractImage = (ImageButton) findViewById(R.id.extract);
        CompressVideo = (ImageButton) findViewById(R.id.compressvideo);
        LowResolution = (ImageButton) findViewById(R.id.lowreso);
        HighResolution = (ImageButton) findViewById(R.id.highreso);
        VideoToGif = (ImageButton) findViewById(R.id.videotogif);
        TrimVideo = (ImageButton) findViewById(R.id.trimVideo);
        AddVintage = (ImageButton) findViewById(R.id.vintage);
        AddBlackWhite = (ImageButton) findViewById(R.id.blackwhite);
        AddFadeInOut = (ImageButton) findViewById(R.id.fadeinout);
        VerticalFlip = (ImageButton) findViewById(R.id.vflip);
        HFlip = (ImageButton) findViewById(R.id.hflip);
        Rotation90 = (ImageButton) findViewById(R.id.rotate);
        Rotation180 = (ImageButton) findViewById(R.id.rotatee);
        Rotation270 = (ImageButton) findViewById(R.id.rotateee);
        Blur4by3 = (ImageButton) findViewById(R.id.blurr);
        Blur3by2 = (ImageButton) findViewById(R.id.blurrr);
        Blur5by4 = (ImageButton) findViewById(R.id.blurrrr);
        videoView = (VideoView) findViewById(R.id.layout_movie_wrapper);
        imageView = (ImageView) findViewById(R.id.overlayimage);

        Toast.makeText(MainActivity.this,"DialogBox_Called",Toast.LENGTH_SHORT).show();

        //creating the progress dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(true);

        //set up the onClickListeners
        selectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an intent to retrieve the video file from the device storage
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, 123);
            }
        });

/*       selectImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(
                       Intent.ACTION_PICK,
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               intent.setType("image/*");
               startActivityForResult(intent, 123);

           }
       });

 */

    //    Toast.makeText(MainActivity.this,"Slow_Method_Called",Toast.LENGTH_SHORT).show();
        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    check if the user has selected any video or not
                    In case a user hasen't selected any video and press the button,
                    we will show an warning, stating "Please upload the video"
                 */
                if (video_url != null) {
                    //a try-catch block to handle all necessary exceptions like File not found, IOException
                    try {
                        slowmotion(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

 //       Toast.makeText(MainActivity.this,"Fast_Method_Called",Toast.LENGTH_SHORT).show();
        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null) {

                    try {
                        fastforward(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

     //   Toast.makeText(MainActivity.this,"Revers_Method_Called",Toast.LENGTH_SHORT).show();
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null) {
                    try {
                        reverse(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

        videoToAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null){

                    try {
                        videoToAudio(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });
        RemoveAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null){
                    try{
                        RemoveAudio(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

        ExtractImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try{
                        ExtractImage(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

        CompressVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null)
                {
                    try {
                        CompressVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

        LowResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null)
                {
                    try {
                        LowResolution(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this, "Please upload video", Toast.LENGTH_SHORT).show();
            }
        });

        HighResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null)
                {
                    try {
                        HighResolution(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this, "Please upload video", Toast.LENGTH_SHORT).show();
            }
        });

        VideoToGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url != null)
                {
                    try {
                        VideoToGif(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(), Toast.LENGTH_SHORT).show();
                     }
                }else
                    Toast.makeText(MainActivity.this, "Please_Upload_Video", Toast.LENGTH_SHORT).show();
            }
        });

        TrimVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        TrimVideo(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        AddVintage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try{
                        AddVintage(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        AddBlackWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        AddBlackWhite(rangeSeekBar.getSelectedMinValue().intValue() * 1000, rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        AddFadeInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        AddFadeInOut(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });
        VerticalFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        VerticalFlip(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        HFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        HFlip(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        Rotation90.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Rotation90(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        Rotation180.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Rotation180(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        Rotation270.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Rotation270(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        Blur4by3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Blur4by3(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_Video",Toast.LENGTH_SHORT).show();
            }
        });

        Blur3by2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Blur3by2(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_video", Toast.LENGTH_SHORT).show();
            }
        });

        Blur5by4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video_url != null)
                {
                    try {
                        Blur5by4(rangeSeekBar.getSelectedMinValue().intValue() * 1000,rangeSeekBar.getSelectedMaxValue().intValue() * 1000);

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(MainActivity.this,"Please_Upload_video", Toast.LENGTH_SHORT).show();
            }
        });



        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                //get the durtion of the video
                duration = mp.getDuration() / 1000;
                //initially set the left TextView to "00:00:00"
                tvLeft.setText("00:00:00");
                //initially set the right Text-View to the video length
                //the getTime() method returns a formatted string in hh:mm:ss
                tvRight.setText(getTime(mp.getDuration() / 1000));
                //this will run he video in loop i.e. the video won't stop
                //when it reaches its duration

                mp.setLooping(true);

                //set up the initial values of rangeSeekbar
                rangeSeekBar.setRangeValues(0, duration);
                rangeSeekBar.setSelectedMinValue(0);
                rangeSeekBar.setSelectedMaxValue(duration);
                rangeSeekBar.setEnabled(true);

       //         Toast.makeText(MainActivity.this,"RangeSeekbar_Called",Toast.LENGTH_SHORT).show();

                rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        //we seek through the video when the user drags and adjusts the seekbar
                        videoView.seekTo((int) minValue * 1000);

                        //changing the left and right TextView according to the minValue and maxValue
                        tvLeft.setText(getTime((int) bar.getSelectedMinValue()));
                        tvRight.setText(getTime((int) bar.getSelectedMaxValue()));

                    }
                });

                //this method changes the right TextView every 1 second as the video is being played
                //It works same as a time counter we see in any Video Player

                Toast.makeText(MainActivity.this,"Handler_Called",Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(r = new Runnable() {
                    @Override
                    public void run() {

                        if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                            videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                        handler.postDelayed(r, 1000);
                    }
                }, 1000);

            }
        });
    }
    private void Blur5by4(int startMs, int endMs) throws Exception {
        progressDialog.show();
        final String filePath;
        String filePrefix = "Blur5by4";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);

            File file = FileUtils.getFileFromUri(this, uri);
            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

        String cmd;

        cmd="-y -i " +video_url+" -vf 'split[original][copy];[copy]scale=ih*5/4:-1,crop=h=iw*4/5,gblur=sigma=20[blurred];[blurred][original]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2' "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " +filePath;

        //   String[] Command = {"-ss", "" + startMs / 1000, "-y", "-i", video_url, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG, "Async Command Execution Cancel By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Cancel By returnCode=%d.",returnCode));
                }
            }
        });

    }

    private void Blur3by2(int startMs, int endMs) throws Exception {
        progressDialog.show();
        final String filePath;
        String filePrefix = "Blur3by2";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);

            File file = FileUtils.getFileFromUri(this, uri);
            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

        String cmd;

        cmd="-y -i " +video_url+" -vf 'split[original][copy];[copy]scale=ih*3/2:-1,crop=h=iw*2/3,gblur=sigma=20[blurred];[blurred][original]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2' "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " +filePath;

        //   String[] Command = {"-ss", "" + startMs / 1000, "-y", "-i", video_url, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {


                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG, "Async Command Execution Cancel By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Cancel By returnCode=%d.",returnCode));
                }
            }
        });

    }

    private void Blur4by3(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "Blur4by3";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd="-y -i " +video_url+" -vf 'split[original][copy];[copy]scale=ih*4/3:-1,crop=h=iw*3/4,gblur=sigma=20[blurred];[blurred][original]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2' "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " +filePath;
        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void Rotation270(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "Rotation270";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd="-y -i " +video_url+" -vf transpose=1,transpose=1,transpose=1 -b:v 2097k -b:a 128k -ac 2 -ar 22050"+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void Rotation180(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "Rotation180";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd="-y -i " +video_url+" -vf transpose=2,transpose=2 -b:v 2097k -b:a 128k -ac 2 -ar 22050"+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void Rotation90(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "Rotation90";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd="-y -i " +video_url+" -vf transpose=1 -b:v 2097k -b:a 128k -ac 2 -ar 22050"+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void VerticalFlip(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "VerticalFlip";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;
        cmd="-y -i " +video_url+" -vf vflip -b:v 2097k -b:a 128k -ac 2 -ar 22050 "+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void HFlip(int startMs,int endMs) throws Exception{
        progressDialog.show();
        final String filePath;
        String filePrefix = "HFlip";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;
        cmd="-y -i " +video_url+" -vf hflip -b:v 2097k -b:a 128k -ac 2 -ar 22050 "+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d",returnCode));
                }
            }
        });

    }

    private void AddFadeInOut(int startMs,int endMs) throws Exception{

        progressDialog.show();
        final String filePath;
        String filePrefix = "AddFadeInOut";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String[] cmd = {"-y", "-i", video_url, "-acodec", "copy", "-vf", "fade=t=in:st=0:d=5,fade=t=out:st=" + (duration - 5) + ":d=5","-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();
                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Cancel By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d", returnCode));
                }
            }
        });
    }

    private  void AddBlackWhite(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "AddBlackWhite";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

        String cmd;

        cmd="-y -i " +video_url+" -vf format=gray -b:v 2097k -b:a 128k -ac 2 -ar 22050 "+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG,"Async Command Execution Canceled By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed By returnCode=%d", returnCode));
                }
            }
        });

    }

    private void AddVintage(int startMs, int endMs) throws Exception {
        progressDialog.show();
        final String filePath;
        String filePrefix = "AddVintage";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis() +fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);
            File file = FileUtils.getFileFromUri(this,uri);

            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

        String cmd;

        cmd="-y -i " +video_url+" -vf curves=vintage -b:v 2097k -b:a 128k -ac 2 -ar 22050"+" -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG, "Async Command Execution Cancel By User");
                }else
                {
                    Log.i(Config.TAG,String.format("Async Command Execution Failed With returnCode=%d",returnCode));
                }
            }
        });
    }

    private void TrimVideo(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "TrimVideo";
        String fileExtn = ".mp4";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

            File file = FileUtils.getFileFromUri(this, uri);
            filePath = file.getAbsolutePath();
        }else {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);

            }
            filePath = dest.getAbsolutePath();

        }

            String[] Command = {"-ss", "" + startMs / 1000, "-y", "-i", video_url, "-t", "" + (endMs - startMs) / 1000,"-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};

          //  cmd = "-y -i" +video_url+ " -ss -ac 2 -ar 22050 " +startMs/1000+ "-to" +(endMs - 1000) / 1000+ "-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast" +filePath;

            long executionId = FFmpeg.executeAsync(Command, new ExecuteCallback() {
                @Override
                public void apply(long executionId, int returnCode) {
                    if (returnCode == RETURN_CODE_SUCCESS)
                    {
                        videoView.setVideoURI(Uri.parse(filePath));

                        video_url = filePath;

                        videoView.start();

                        progressDialog.dismiss();
                    }else if (returnCode == RETURN_CODE_CANCEL)
                    {
                        Log.i(Config.TAG,"Async Command Execution Cancel By User");
                    }else
                    {
                        Log.i(Config.TAG,String.format("Async Command Execution Cancel By returnCode=%d.",returnCode));
                    }
                }
            });
        }

    private void VideoToGif(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "VideoToGif";
        String fileExtn = ".gif";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Folder");
            contentValues.put(MediaStore.Images.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            File file = FileUtils.getFileFromUri(this,uri);
            filePath = file.getAbsolutePath();
        }else {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
            String cmd;


            cmd ="-y -i  " +video_url+" -vf scale=512:-1 -an -ss 00:00:03 -to 00:00:05 "+" -vcodec gif -crf 0 -preset superfast " +filePath;


            long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
                @Override
                public void apply(long executionId, int returnCode) {
                    if (returnCode == RETURN_CODE_SUCCESS)
                    {
                        imageView.setImageURI(Uri.parse(filePath));

                        image_url = filePath;

                        videoView.start();

                        progressDialog.dismiss();
                    }else if (returnCode == RETURN_CODE_CANCEL)
                    {
                        Log.i(Config.TAG,"Async Command Execution Cancel By User");
                    } else
                    {
                        Log.i(Config.TAG, String.format("Async Command Execution Failed With returnCode=%d.",returnCode));
                    }
                }
            });
        }

    private void HighResolution(int startsMs, int ensMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "HighResolution";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

            File file = FileUtils.getFileFromUri(this,uri);
            filePath = file.getAbsolutePath();
        }else {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;
        cmd = " -y -i " +video_url+ " -r 60 -s 640*1136"+" -b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();
                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    private void LowResolution(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "LowResolution";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);

            File file = FileUtils.getFileFromUri(this,uri);
            filePath = file.getAbsolutePath();

        }else {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;

            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo +fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd = " -y -i " +video_url+ " -vf scale=360:-1 -c:v "+"-b:v -vcodec mpeg4 -crf 0 -preset superfast "+filePath;

        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {

                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url =filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }

            }
        });

    }

    private void ExtractImage(int startMs, int endMs) throws Exception {


        progressDialog.show();
        final String filePath;
        String filePrefix = "ExtractImage";
        String fileExtn = ".jpg";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "Folder");
            contentValues.put(MediaStore.Images.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+ "%03d" +fileExtn);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

            File file = FileUtils.getFileFromUri(this,uri);
            filePath=file.getAbsolutePath();




        }else {
            //This else statement will work for devices with Android version lower than 10
            //Here, "app_folder" is the path to your app's root directory in device storage
            File dest = new File(new File(app_folder), filePrefix + "%03d" + fileExtn);
            int fileNo = 0;
            //check if the file name previously exist. Since we don't want to oerwrite the video files
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            //Get the filePath once the file is successfully created.
            filePath = dest.getAbsolutePath();

        }
       String cmd;

       // cmd = "-y -i  " +video_url+ " -vframes:v 1 -ss 01:25:45 "+" outout.jpg -crf 0 -preset superfast "+filePath;
        String[] Command = {"-y", "-i", video_url, "-an", "-r", "1", "-ss", "" + startMs / 1000, "-t", "" + (endMs - startMs) / 1000, filePath};





        long executionId = FFmpeg.executeAsync(Command, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {

                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();

                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    private void CompressVideo(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "CompressVideo";
        String fileExtn = ".Mp4";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);

            File file = FileUtils.getFileFromUri(this,uri);
            filePath = file.getAbsolutePath();
        }else
        {
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists())
            {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }

        String cmd;

        //cmd = " -y -i " +video_url+ " -vf scale=1280:-1 -c:v "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast "+filePath;
        String[] complexCommand = {"-y", "-i", video_url, "-s", "160x120", "-r", "25", "-vcodec", "mpeg4", "-b:v", "150k", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};


        long executionId = FFmpeg.executeAsync(complexCommand, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();
                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });

    }

    private void fastforward(int startMs, int endMs) throws Exception {
          /* startMs is the starting time, from where we have to apply the effect.
  	         endMs is the ending time, till where we have to apply effect.
   	         For example, we have a video of 5min and we only want to fast forward a part of video
  	         say, from 1:00 min to 2:00min, then our startMs will be 1000ms and endMs will be 2000ms.
		 */

        progressDialog.show();

        //creating a new file in storage
        final String filePath;
        String filePrefix = "fastforward";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            /*
            With introduction of scoped storage in Android Q the primitive method gives error
            So, it is recommended to use the below method to create a video file in storage.
             */
            ContentValues valuesvideos = new ContentValues();
            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);

            //get the path of the video file created in the storage.
            File file=FileUtils.getFileFromUri(this,uri);
            filePath=file.getAbsolutePath();

        }else {
            //This else statement will work for devices with Android version lower than 10
            //Here, "app_folder" is the path to your app's root directory in device storage
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            //check if the file name previously exist. Since we don't want to oerwrite the video files
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            //Get the filePath once the file is successfully created.
            filePath = dest.getAbsolutePath();
        }

   //     Toast.makeText(MainActivity.this,"Execute_Command", Toast.LENGTH_SHORT).show();
        String exe;
        //the "exe" string contains the command to process video.The details of command are discussed later in this post.
        // "video_url" is the url of video which you want to edit. You can get this url from intent by selecting any video from gallery.
        exe="-y -i " +video_url+" -filter_complex [0:v]trim=0:"+startMs/1000+",setpts=PTS-STARTPTS[v1];[0:v]trim="+startMs/1000+":"+endMs/1000+",setpts=0.5*(PTS-STARTPTS)[v2];[0:v]trim="+(endMs/1000)+",setpts=PTS-STARTPTS[v3];[0:a]atrim=0:"+(startMs/1000)+",asetpts=PTS-STARTPTS[a1];[0:a]atrim="+(startMs/1000)+":"+(endMs/1000)+",asetpts=PTS-STARTPTS,atempo=2[a2];[0:a]atrim="+(endMs/1000)+",asetpts=PTS-STARTPTS[a3];[v1][a1][v2][a2][v3][a3]concat=n=3:v=1:a=1 "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast "+filePath;


        long executionId = FFmpeg.executeAsync(exe, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    //after successful execution of ffmpeg command,
                    //again set up the video Uri in VideoView

                    videoView.setVideoURI(Uri.parse(filePath));
                    //change the video_url to filePath, so that we could do more manipulations in the
                    //resultant video. By this we can apply as many effects as we want in a single video.
                    //Actually there are multiple videos being formed in storage but while using app it
                    //feels like we are doing manipulations in only one video
                    video_url = filePath;
                    //play the result video in VideoView
                    videoView.start();
                    //remove the progress dialog
                    progressDialog.dismiss();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    private void slowmotion(int startMs, int endMs) throws Exception {

  // Toast.makeText(MainActivity.this,"slow_motion_Method_Called",Toast.LENGTH_SHORT).show();
        progressDialog.show();

        final String filePath;
        String filePrefix = "slowmotion";
        String fileExtn = ".mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues valuesvideos = new ContentValues();
            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);
            File file=FileUtils.getFileFromUri(this,uri);
            filePath=file.getAbsolutePath();

        }else {

            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        String exe;
        exe="-y -i " +video_url+" -filter_complex [0:v]trim=0:"+startMs/1000+",setpts=PTS-STARTPTS[v1];[0:v]trim="+startMs/1000+":"+endMs/1000+",setpts=2*(PTS-STARTPTS)[v2];[0:v]trim="+(endMs/1000)+",setpts=PTS-STARTPTS[v3];[0:a]atrim=0:"+(startMs/1000)+",asetpts=PTS-STARTPTS[a1];[0:a]atrim="+(startMs/1000)+":"+(endMs/1000)+",asetpts=PTS-STARTPTS,atempo=0.5[a2];[0:a]atrim="+(endMs/1000)+",asetpts=PTS-STARTPTS[a3];[v1][a1][v2][a2][v3][a3]concat=n=3:v=1:a=1 "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " +filePath;

        //Toast.makeText(MainActivity.this,"loadFFmpeg",Toast.LENGTH_SHORT).show();

        long executionId = FFmpeg.executeAsync(exe, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {

                    videoView.setVideoURI(Uri.parse(filePath));
                    video_url = filePath;
                    videoView.start();
                    progressDialog.dismiss();

                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    private void videoToAudio(int startMs, int endMs) throws Exception {

        progressDialog.show();
        final String filePath;
        String filePrefix = "videoToAudio";
        String fileExtn = ".mp3";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){

            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/" + "Folder");
            values.put(MediaStore.Audio.Media.TITLE, filePrefix+System.currentTimeMillis());
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() /1000);
            values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            File file = FileUtils.getFileFromUri(this, uri);
            filePath = file.getAbsolutePath();
        }
        else {
            //This else statement will work for devices with Android version lower than 10
            //Here, "app_folder" is the path to your app's root directory in device storage
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            //check if the file name previously exist. Since we don't want to oerwrite the video files
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            //Get the filePath once the file is successfully created.
            filePath = dest.getAbsolutePath();
        }

        String exe;

       // exe ="-y -i  " +video_url+" -vn -ar 44100 -ac 2 -ab 320k "+"-b:a 128k -acodec mp3 -crf 0 -preset superfast "+filePath;

        String[] complexCommand = {"-y", "-i", video_url, "-vn", "-ar", "44100", "-ac", "2", "-b:a", "256k", "-f", "mp3", filePath};

        long executionId = FFmpeg.executeAsync(complexCommand, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url= filePath;

                    videoView.start();

                    progressDialog.dismiss();
                } else if (returnCode == RETURN_CODE_CANCEL)
                {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                }
                else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    private void RemoveAudio(int startMs, int endMs) throws Exception{

        progressDialog.show();

        final String filePath;
        String filePrefix = "RemoveAudio";
        String fileExtn = ".mp4";



        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
       ContentValues contentValues = new ContentValues();
       contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
       contentValues.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
       contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
       contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
       contentValues.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
       contentValues.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
       Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
       File file = FileUtils.getFileFromUri(this,uri);
       filePath = file.getAbsolutePath();
        }
        else {
            //This else statement will work for devices with Android version lower than 10
            //Here, "app_folder" is the path to your app's root directory in device storage
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            //check if the file name previously exist. Since we don't want to OverWrite the video files
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            //Get the filePath once the file is successfully created.
            filePath = dest.getAbsolutePath();
        }
        String cmd;

        cmd = " -y -i " +video_url+ " -an  "+"-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast "+filePath;


        long executionId = FFmpeg.executeAsync(cmd, new ExecuteCallback() {
            @Override
            public void apply(long executionId, int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS)
                {
                    videoView.setVideoURI(Uri.parse(filePath));

                    video_url = filePath;

                    videoView.start();
                    progressDialog.dismiss();
                }else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });

    }

    private void reverse(int startMs, int endMs) throws Exception {


        progressDialog.show();
        final String filePath;
        String filePrefix = "reverse";
        String fileExtn = ".mp4";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentValues valuesvideos = new ContentValues();
            valuesvideos.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/" + "Folder");
            valuesvideos.put(MediaStore.Video.Media.TITLE, filePrefix+System.currentTimeMillis());
            valuesvideos.put(MediaStore.Video.Media.DISPLAY_NAME, filePrefix+System.currentTimeMillis()+fileExtn);
            valuesvideos.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            valuesvideos.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            valuesvideos.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, valuesvideos);
            File file=FileUtils.getFileFromUri(this,uri);
            filePath=file.getAbsolutePath();

        }else{
            filePrefix = "reverse";
            fileExtn = ".mp4";
            File dest = new File(new File(app_folder), filePrefix + fileExtn);
            int fileNo = 0;
            while (dest.exists()) {
                fileNo++;
                dest = new File(new File(app_folder), filePrefix + fileNo + fileExtn);
            }
            filePath = dest.getAbsolutePath();
        }
        long executionId = FFmpeg.executeAsync("-y -i " + video_url + " -filter_complex [0:v]trim=0:" + endMs / 1000 + ",setpts=PTS-STARTPTS[v1];[0:v]trim=" + startMs / 1000 + ":" + endMs / 1000 + ",reverse,setpts=PTS-STARTPTS[v2];[0:v]trim=" + (startMs / 1000) + ",setpts=PTS-STARTPTS[v3];[v1][v2][v3]concat=n=3:v=1 " + "-b:v 2097k -vcodec mpeg4 -crf 0 -preset superfast " + filePath, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    videoView.setVideoURI(Uri.parse(filePath));
                    video_url = filePath;
                    videoView.start();
                    progressDialog.dismiss();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
    }

    //Overriding the method onActivityResult() to get the video Uri form intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(MainActivity.this,"On_activity_Method_Called",Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_OK) {

            if (requestCode == 123) {

                if (data != null) {

                    Uri uri = data.getData();
                    try {
                        File video_file = FileUtils.getFileFromUri(this, uri);
                        File image_file = FileUtils.getFileFromUri(this,uri);

                        videoView.setVideoURI(uri);
                        imageView.setImageURI(uri);

                        videoView.start();

                        video_url=video_file.getAbsolutePath();
                        image_url=image_file.getAbsolutePath();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }


                }
            }
        }
    }

    //This method returns the seconds in hh:mm:ss time format

    private String getTime(int seconds) {

        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }
}