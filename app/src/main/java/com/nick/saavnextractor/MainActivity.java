package com.nick.saavnextractor;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import java.io.File;

/**
 * Created by Nick on 12-Nov-16.
 */
public class MainActivity extends AppCompatActivity {
    public int STORAGE_PERMISSION_CODE =23 ;
    public static final String logtag = "Nick";
    public String extension = "";

    private boolean isPermissionAllowed(){
        int a = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (a == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Info");
            builder.setMessage("Storage Permission is required to write the song on your storage.\nPlease allow it.");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("i understand", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    // Ask Permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        else {
            // First run Permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Continue App
            }else{
                // Exit App
                // But show a message first.
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("Fatal");
                dlg.setMessage("App can't run without permissions.\nExiting.");
                dlg.setIcon(android.R.drawable.ic_dialog_alert);
                dlg.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                });
                AlertDialog d = dlg.create();
                d.setCanceledOnTouchOutside(false);
                d.show();
            }
        }
    }

    // Press BACK twice to exit.
    Boolean canExit = false;
    @Override
    public void onBackPressed() {
        if (canExit){
            super.onBackPressed();
        }
        else{
            canExit = true;
            Toast.makeText(getBaseContext(), "Press BACK again to exit.", Toast.LENGTH_SHORT).show();
        }
        mHandler.sendEmptyMessageDelayed(1,2000);
    }

    public Handler mHandler = new Handler(){
      public void handleMessage(android.os.Message msg){
          switch (msg.what){
              case 1 :
                  canExit = false;
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        // Initiate Material Drawer
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Nick").withEmail("nickk.2974@gmail.com").withIcon(R.drawable.usericon)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        Toast.makeText(MainActivity.this, "Hehe! You're tickling me ;)", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(GoogleMaterial.Icon.gmd_home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("About").withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Help").withIcon(GoogleMaterial.Icon.gmd_help).withSelectable(false);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Source Code").withIcon(FontAwesome.Icon.faw_github).withSelectable(false);



        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch (position){
                            case 1 :
                                toolbar.setTitle(R.string.app_name);
                                break;
                            case 2 :
                                showAbout();
                                toolbar.setTitle(R.string.app_name);
                                break;
                            case 3 :
                                toolbar.setTitle(R.string.app_name);
                                showHelp();
                                break;
                            case 4 :
                                // Add share button to action bar of Custom Tabs
                                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/*");
                                i.putExtra(Intent.EXTRA_TEXT, "Hello there." +
                                        " Check out an awesome app to save Saavn Songs by Nick (black.dragon74)" +
                                        " on GitHub:-\nhttps://github.com/black-dragon74/SaavnExtractor");
                                PendingIntent ii = PendingIntent.getActivity(MainActivity.this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
                                CustomTabsIntent intent = new CustomTabsIntent.Builder()
                                        .setToolbarColor(getResources().getColor(R.color.colorPrimary))
                                        .setSecondaryToolbarColor(getResources().getColor(R.color.colorPrimaryDark))
                                        .setActionButton(bmp, "Share App", ii, true)
                                        .build();
                                String url = "https://github.com/black-dragon74/SaavnExtractor";
                                intent.launchUrl(MainActivity.this, Uri.parse(url));
                                break;
                            default :
                                break;
                        }
                        return false;
                    }
                }).withDrawerWidthDp(290)
                .build();
        // Check permissions
        if (isPermissionAllowed()){
            // Continue
        }
        else{
            requestPermission();
        }
        Button mButton;
        final EditText mText;

        mButton = (Button) findViewById(R.id.savebtn);
        mText = (EditText) findViewById(R.id.edittext);
        final TextView logger = (TextView) findViewById(R.id.logview);

        // Check and populate logger
        String sdcard = Environment.getExternalStorageDirectory().toString();
        String saavndir = sdcard+"/Android/data/com.saavn.android";
        File saavnsong = new File (saavndir+"/songs/curr.mp3");
        File saavnsong2 = new File (saavndir+"/songs/curr.mp4");

        try {
            if (saavnsong.exists()){
                logger.setText("Saavn is installed. Song found. Good to go.");
                extension=".mp3";
            }
            else if (saavnsong2.exists()){
                logger.setText("Saavn is installed. Song found. Good to go.");
                extension=".mp4";
            }
            else{
                logger.setText("Song not found. You forgot to download, play & pause the song.");
                // Ask to open saavn. Only if App is Installed
                final String appDomain = "com.saavn.android";
                if (isAppInstalled(getBaseContext(), appDomain)){
                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setTitle("Open App");
                    b.setIcon(android.R.drawable.ic_menu_help);
                    b.setMessage("Saavn App is installed but paused song not found.\nDo you want me to open the app now?");
                    b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openApp(getBaseContext(), appDomain);
                        }
                    });
                    b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog d = b.create();
                    d.setCanceledOnTouchOutside(false);
                    d.show();
                }
                else{
                    // Tell user that App is not installed.
                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setTitle("Error");
                    b.setIcon(android.R.drawable.ic_dialog_alert);
                    b.setMessage("App not found. Install from store?");
                    // Open Store if user selects Yes.
                    b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent appInstall = new Intent(Intent.ACTION_VIEW);
                            appInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appInstall.setData(Uri.parse("http://play.google.com/store/apps/details?id="+appDomain));
                            startActivity(appInstall);
                        }
                    });
                    // Do nothing if user selects No.
                    b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dd = b.create();
                    dd.setCanceledOnTouchOutside(false);
                    dd.show();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v){
                        // Declare Variables
                        String sdcard = Environment.getExternalStorageDirectory().toString();
                        String saavndir = sdcard+"/Android/data/com.saavn.android";
                        File saavnsong = new File (saavndir+"/songs/curr.mp3");
                        File saavnsong2 = new File (saavndir+"/songs/curr.mp4");
                        final String outdir = sdcard + "/Music";
                        final File  songname = new File (outdir+"/"+mText.getText().toString()+".mp3");

                        try {
                            if (saavnsong.exists()) {
                                // Proceed
                                if (saavnsong.renameTo(songname)) {
                                    // Operation Successful
                                    logger.setText("File Mp3 Copied.");
                                    Log.v(logtag, "File Mp3 Copied.");
                                    Toast.makeText(MainActivity.this, "Song saved as:-"+"\n"+outdir+"/"+mText.getText().toString()+extension, Toast.LENGTH_LONG).show();
                                    // Notify Media Storage
                                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{songname.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){
                                        public void onScanCompleted(String path, Uri uri){
                                            Log.v(logtag, "Media Scanner Notified");
                                        }
                                    });
                                    // Ask if the song is to be played.
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Confirm");
                                    builder.setMessage("Play "+mText.getText().toString()+extension+"?");
                                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(songname), "audio/*");
                                            startActivity(intent);
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            mText.setText(null);
                                            mText.requestFocus();
                                            Toast.makeText(MainActivity.this, "Okay! No Problem.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    AlertDialog dlg = builder.create();
                                    dlg.setCanceledOnTouchOutside(false);
                                    dlg.show();
                                } else {
                                    // Operation Failed
                                    logger.setText("File Mp3 Not Copied.");
                                    Log.v(logtag, "File Mp3 Not Copied.");
                                }

                            }
                            else if (saavnsong2.exists()){
                                // Proceed
                                if (saavnsong2.renameTo(songname)){
                                    // Successful
                                    logger.setText("File MP4 copied.");
                                    Log.v(logtag, "File MP4 copied.");
                                    Toast.makeText(MainActivity.this, "Song saved as:-"+"\n"+outdir+"/"+mText.getText().toString()+extension, Toast.LENGTH_LONG).show();
                                    // Notify Media Storage
                                    MediaScannerConnection.scanFile(MainActivity.this, new String[]{songname.toString()}, null, new MediaScannerConnection.OnScanCompletedListener(){
                                        public void onScanCompleted(String path, Uri uri){
                                            Log.v(logtag, "Media Scanner Notified");
                                        }
                                    });
                                    // Ask if the song is to be played.
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Confirm");
                                    builder.setMessage("Play "+mText.getText().toString()+extension+"?");
                                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(songname), "video/*");
                                            startActivity(intent);
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            mText.setText(null);
                                            mText.requestFocus();
                                            Toast.makeText(MainActivity.this, "Okay! No Problem.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    AlertDialog dlg = builder.create();
                                    dlg.setCanceledOnTouchOutside(false);
                                    dlg.show();
                                }
                                else{
                                    // Failed
                                    logger.setText("File MP4 not copied.");
                                    Log.v(logtag, "File MP4 not copied.");
                                }
                            }
                            else {
                                // File is non existent
                                logger.setText("File Not Found!");
                                Log.v(logtag, "File Not Found");
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );
        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getBaseContext(), "Resetting program.", Toast.LENGTH_SHORT).show();
                MainActivity.this.finish();
                Intent res = new Intent(getBaseContext(), MainActivity.class);
                startActivity(res);
                return false;
            }
        });

    }

    public void showHelp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.dialog_title_help));
        builder.setMessage(getString(R.string.dialog_text_help));
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showAbout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.dialog_title_about));
        builder.setMessage((getString(R.string.dialog_text_about))+"Version: "+BuildConfig.VERSION_NAME);
        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static boolean openApp(Context context, String appName){
        PackageManager manager = context.getPackageManager();
        try{
            Intent i = manager.getLaunchIntentForPackage(appName);
            if (i == null){
                throw new PackageManager.NameNotFoundException();
            }
            else{
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                return true;
            }
        }
        catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    public boolean isAppInstalled(Context context, String appName){
        PackageManager mgr = context.getPackageManager();
        try{
            mgr.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }


}
