/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */

package com.nguyenhoanglam.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.SavePath;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/4/16.
 */
public class MainActivity extends AppCompatActivity {

    private Switch folderModeSwitch;
    private Switch multipleModeSwitch;
    private Switch cameraOnlySwitch;
    private Switch acceptImageSwitch;
    private Switch acceptVideoSwitch;

    private ImageAdapter adapter;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderModeSwitch = findViewById(R.id.switch_folder_mode);
        multipleModeSwitch = findViewById(R.id.switch_multiple_mode);
        cameraOnlySwitch = findViewById(R.id.switch_camera_only);
        acceptImageSwitch = findViewById(R.id.switch_accept_image);
        acceptVideoSwitch = findViewById(R.id.switch_accept_video);
        Button startPickerButton = findViewById(R.id.button_start_picker);
        Button launchFragmentButton = findViewById(R.id.button_launch_fragment);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        acceptImageSwitch.setChecked(true);
        acceptImageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean acceptImage = acceptImageSwitch.isChecked();
                boolean acceptVideo = acceptVideoSwitch.isChecked();
                if (!acceptVideo && !acceptImage) {
                    acceptVideoSwitch.setChecked(true);
                }
                if (acceptVideo && acceptImage) {
                    acceptVideoSwitch.setChecked(false);
                }
            }
        });
        acceptVideoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean acceptImage = acceptImageSwitch.isChecked();
                boolean acceptVideo = acceptVideoSwitch.isChecked();
                if (!acceptVideo && !acceptImage) {
                    acceptImageSwitch.setChecked(true);
                }
                if (acceptVideo && acceptImage) {
                    acceptImageSwitch.setChecked(false);
                }
            }
        });
        startPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        launchFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment();
            }
        });

        adapter = new ImageAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void start() {
        boolean folderMode = folderModeSwitch.isChecked();
        boolean multipleMode = multipleModeSwitch.isChecked();
        boolean cameraOnly = cameraOnlySwitch.isChecked();
        boolean acceptImage = acceptImageSwitch.isChecked();
        boolean acceptVideo = acceptVideoSwitch.isChecked();

        ImagePicker.with(this)
                .setFolderMode(folderMode)
                .setCameraOnly(cameraOnly)
                .setFolderTitle("Album")
                .setMultipleMode(multipleMode)
                .setSelectedImages(images)
                .setAcceptImage(acceptImage)
                .setAcceptVideo(acceptVideo)
                .setMaxSize(10)
                .setBackgroundColor("#212121")
                .setAlwaysShowDoneButton(true)
                .setRequestCode(100)
                .setKeepScreenOn(true)
                .start();

    }

    private void launchFragment() {
        boolean folderMode = folderModeSwitch.isChecked();
        boolean multipleMode = multipleModeSwitch.isChecked();
        boolean cameraOnly = cameraOnlySwitch.isChecked();
        boolean acceptImage = acceptImageSwitch.isChecked();
        boolean acceptVideo = acceptVideoSwitch.isChecked();

        Config config = new Config();
        config.setCameraOnly(cameraOnly);
        config.setMultipleMode(multipleMode);
        config.setFolderMode(folderMode);
        config.setShowCamera(true);
        config.setMaxSize(Config.MAX_SIZE);
        config.setDoneTitle(getString(com.nguyenhoanglam.imagepicker.R.string.imagepicker_action_done));
        config.setFolderTitle(getString(com.nguyenhoanglam.imagepicker.R.string.imagepicker_title_folder));
        config.setImageTitle(getString(com.nguyenhoanglam.imagepicker.R.string.imagepicker_title_image));
        config.setSavePath(SavePath.DEFAULT);
        config.setSelectedImages(new ArrayList<Image>());
        config.setAcceptImage(acceptImage);
        config.setAcceptVideo(acceptVideo);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance(config))
                .commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            adapter.setData(images);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            finish();
        } else {
            fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }
}
