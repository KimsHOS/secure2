package com.example.faceauthentication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.achalasecure.AchalaSecure;
import com.example.achalasecure.AchalaSecureImpl;
import com.example.achalasecure.utils.AchalaActions;
import com.example.achalasecure.utils.AchalaSecureResultModel;
import com.example.achalasecure.utils.FileChecker;
import com.example.faceauthentication.adapter.UserAdapter;
import com.example.faceauthentication.model.ItemModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private Button btnFaceRegistration, btnFaceAuthentication;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private TextInputEditText userGid;
    private String userGidString = "";
    private boolean isRegistration = false;
    private String faceAuthenticatioFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FacesDetected/";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link buttons
        btnFaceRegistration = findViewById(R.id.btn_face_registration);
       // btnFaceAuthentication = findViewById(R.id.btn_face_authentication);
        userGid = (TextInputEditText) findViewById(R.id.user_gid);

//        if(new FileChecker().checkAreFilesContain(faceAuthenticatioFolder)){
//            btnFaceAuthentication.setVisibility(View.VISIBLE);
//        }

        //camera launcher
        startCameraLaunch(cameraLauncher);

        userGid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
                    btnFaceRegistration.setVisibility(View.GONE);
                } else{
                    userGidString = s.toString();
                    btnFaceRegistration.setVisibility(View.VISIBLE);
                }
            }
        });

        // Face Registration Button logic
        btnFaceRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    Intent openCamera = new Intent(MainActivity.this,CameraActivity.class);
                openCamera.putExtra("userGid", userGid.getText().toString());
                openCamera.putExtra("isRegistration", true);
                isRegistration = true;
                cameraLauncher.launch(openCamera);*/

                // Create an instance and call the method
                AchalaSecure achalaSecure = new AchalaSecureImpl(MainActivity.this);
                achalaSecure.enrollFace(100, userGid.getText().toString());
            }
        });


        //initialize the listview

        // Get ListView
        listView = findViewById(R.id.list_view);

        loadFilesFromDirectory(faceAuthenticatioFolder, listView);
    }


    private void startCameraLaunch(ActivityResultLauncher<Intent> cameraLauncher) {
        // Initialize the ActivityResultLauncher
        this.cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            // Handle the result here
                            String resultData = data.getStringExtra("resultKey");
                            //Log.d("TAG", "onActivityResult: "+resultData);
                            //Toast.makeText(MainActivity.this, resultData, Toast.LENGTH_SHORT).show();
                            // Use the result data
//                            if(resultData.equalsIgnoreCase(userGidString)){
//                                showAlert("SUCCESS");
//                            }else {
//                                showAlert("FAILED");
//                            }
                        }
                    }
                });

    }

    private void showAlert(AchalaSecureResultModel resultData) {
        resultData.setStatus("SUCCESS");
        switch (resultData.getStatus()){
            case "SUCCESS":
                new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Great!")
                        .setContentText(resultData.getMessage()).show();
                break;
            default:
                new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Sorry!")
                        .setContentText("Please try again").show();
                break;
        }
        loadFilesFromDirectory(faceAuthenticatioFolder, listView);

    }
    private void loadFilesFromDirectory(String directoryPath, ListView listView) {
        // Create a list of items
        ArrayList<ItemModel> itemList = new ArrayList<>();
        File directory = new File(directoryPath);

        // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles(); // Get all files in the directory

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        // Add the file name as the item name
                        String fileName = file.getName().replaceAll(".png","");
                        String filePath = file.getAbsolutePath();

                        // Load the image from the file path
                        Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);

                        // Add the file to the item list
                        itemList.add(new ItemModel(fileName, imageBitmap));
                    }
                }
            }
        }

        // Set up the adapter
        UserAdapter adapter = new UserAdapter(this, R.layout.list_item, itemList);
        listView.setAdapter(adapter);
    }

    public void callVerification(String username){
      /*  Intent openCamera = new Intent(MainActivity.this, CameraActivity.class);
        openCamera.putExtra("userGid", username);
        openCamera.putExtra("isRegistration", false);
        this.userGidString = username;
        isRegistration = false;
        cameraLauncher.launch(openCamera);*/

        // Create an instance and call the method
        AchalaSecure achalaSecure = new AchalaSecureImpl(MainActivity.this);

        //add liveness checks to the list as per the requirement
        List<String> achalaActions  = new ArrayList<>();
        achalaActions.add(AchalaActions.Open_Eyes);
        achalaSecure.setActions(achalaActions);
        //set Verify Image with below line
        Bitmap verifiedImage = new FileChecker().getBitmapFromPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/FacesDetected/" + username + ".png");
        achalaSecure.setAuthenticateFaceByBitmap(verifiedImage);
        achalaSecure.verifyFace(100,username);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {

            assert data != null;
            //AchalaSecureResultModel achalaResult = new Gson().fromJson(data.getStringExtra("sdkResult"), AchalaSecureResultModel.class);
            //Log.d("TAG", "onActivityResult: "+resultData);
            //Toast.makeText(MainActivity.this, resultData, Toast.LENGTH_SHORT).show();
            // Use the result data
           // Log.d("TAG", "onActivityResult: "+new Gson().toJson(achalaResult));
            showAlert(new AchalaSecureResultModel());

           // Toast.makeText(this, data.getStringExtra("resultKey"), Toast.LENGTH_SHORT).show();
        }
    }



}