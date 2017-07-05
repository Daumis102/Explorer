package com.example.daumantas.explorer;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.app.Activity.RESULT_OK;


@RuntimePermissions
public class AddDescriptionFragment extends Fragment {

    ImageButton image1;
    ImageButton image2;
    ImageButton image3;
    ImageButton currentImage = null;
    final int RESULT_LOAD_IMAGE = 1;

    String mCurrentPhotoPath;
    File mCurrentImage;


    public AddDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    void openGaleryWithCheck(){
        AddDescriptionFragmentPermissionsDispatcher.openGaleryWithCheck(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_description, container, false);


    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private boolean isEmptyImage(ImageButton image){
        return image.getTag()=="plus";
    }

    private boolean entriesGood(EditText text1, EditText text2, EditText text3, ImageButton image){
        boolean allGood = true;

        if(isEmpty(text1)){
            allGood = false;
            text1.setBackgroundResource(R.drawable.red_border);
        }else{
            text1.setBackgroundResource(R.drawable.black_border);
        }

        if(isEmpty(text2)){
            allGood = false;
            text2.setBackgroundResource(R.drawable.red_border);
        }else{
            text2.setBackgroundResource(R.drawable.black_border);
        }

        if(isEmpty(text3)){
            allGood = false;
            text3.setBackgroundResource(R.drawable.red_border);
        }else{
            text3.setBackgroundResource(R.drawable.black_border);
        }

        if(isEmptyImage(image)){
            allGood = false;
            image.setImageResource(R.drawable.main_picture_frame_red);
        }else{
            //image.setImageResource(R.drawable.main_picture_frame);
        }
        return allGood;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image1 = (ImageButton)getActivity().findViewById(R.id.image1);
        image2 = (ImageButton)getActivity().findViewById(R.id.image2);
        image3 = (ImageButton)getActivity().findViewById(R.id.image3);


        final EditText description = (EditText)getActivity().findViewById(R.id.description);
        final EditText title = (EditText)getActivity().findViewById(R.id.title);
        final EditText goodFor = (EditText)getActivity().findViewById(R.id.goodFor);
        final EditText hint1 = (EditText)getActivity().findViewById(R.id.hint1);
        final EditText hint2 = (EditText)getActivity().findViewById(R.id.hint2);
        final EditText hint3 = (EditText)getActivity().findViewById(R.id.hint3);
        Button nextBtn = (Button)getActivity().findViewById(R.id.descriptionNext);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entriesGood(title, description, goodFor, image1)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Complete");
                    builder.setMessage("Would you like to add this location?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String descriptionText = description.getText().toString();
                            String titleText = title.getText().toString();
                            String goodForText = goodFor.getText().toString();
                            String hint1Text = hint1.getText().toString();
                            String hint2Text = hint2.getText().toString();
                            String hint3Text = hint3.getText().toString();
                            //String imagePath1 = image1.getTag().toString();
                            //String imagePath2 = image2.getTag().toString();
                            //String imagePath3 = image3.getTag().toString();

                            Drawable image1Drawable = image1.getDrawable();
                            Drawable image2Drawable;
                            Drawable image3Drawable;
                            if (isEmptyImage(image2)) {
                                image2Drawable = null;
                            } else {
                                image2Drawable = image2.getDrawable();
                            }

                            if (isEmptyImage(image3)) {
                                image3Drawable = null;
                            } else {
                                image3Drawable = image3.getDrawable();
                            }
                            ((MainActivity) getActivity()).handleDescription(titleText, descriptionText, goodForText, hint1Text, hint2Text, hint3Text,
                                    image1Drawable, image2Drawable, image3Drawable);
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.show();
                }
            }
        });

        //PICTURE FRAMES

        if (savedInstanceState != null) {
            //Load text fields



            //Load previous images if they exist or add default ones
            String image1Path = savedInstanceState.getString("image1Path");
            String image2Path = savedInstanceState.getString("image2Path");
            String image3Path = savedInstanceState.getString("image3Path");
            if(!image1Path.equals("plus")&&image1Path!=null){
                Picasso.with(getContext())
                        .load(new File(image1Path))
                        .placeholder(R.drawable.ic_loading) // optional
                        .resize(400,400)
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .into(image1);
                image1.setTag(image1Path);
            }else{
                image1.setImageResource(R.drawable.main_picture_frame);
                image1.setTag("plus");
            }
            if(!image2Path.equals("plus")&&image2Path!=null){
                Picasso.with(getContext())
                        .load(new File(image2Path))
                        .placeholder(R.drawable.ic_loading) // optional
                        .resize(400,400)
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .into(image2);
                image2.setTag(image2Path);
            }else{
                image1.setImageResource(R.drawable.picture_frame);
                image2.setTag("plus");
            }
            if(!image3Path.equals("plus")&&image3Path!=null){
                Picasso.with(getContext())
                        .load(new File(image3Path))
                        .placeholder(R.drawable.ic_loading) // optional
                        .resize(400,400)
                        .centerCrop()
                        .error(R.drawable.ic_error)
                        .into(image3);
                image3.setTag(image3Path);
            }else{
                image1.setImageResource(R.drawable.picture_frame);
                image3.setTag("plus");
            }

        }else{
            image1.setTag("plus");
            image2.setTag("plus");
            image3.setTag("plus");
            image1.setImageResource(R.drawable.main_picture_frame);
            image2.setImageResource(R.drawable.picture_frame);
            image3.setImageResource(R.drawable.picture_frame);
        }

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image1.getTag() == "plus"){
                    openGaleryWithCheck();
                    currentImage = image1;
                }else{
                    image1.setImageResource(R.drawable.main_picture_frame);
                    image1.setTag("plus");
                    currentImage = null;
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image2.getTag() == "plus"){
                    openGaleryWithCheck();
                    currentImage = image2;
                }else{
                    image2.setImageResource(R.drawable.picture_frame);
                    currentImage = null;
                    image2.setTag("plus");
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image3.getTag() == "plus"){
                    openGaleryWithCheck();
                    currentImage = image3;
                }else{
                    image3.setImageResource(R.drawable.picture_frame);
                    currentImage = null;
                    image3.setTag("plus");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @NeedsPermission({android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void openGalery() {
        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentImage = image;
        return image;
    }

    private void dispatchTakePictureIntent() {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();



        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        getContext().getPackageName() + ".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }

        targetedShareIntents.add(takePictureIntent);
        targetedShareIntents.add(pickPhotoIntent);

        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
        startActivityForResult(chooserIntent, RESULT_LOAD_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

            Uri selectedImage;
            if(imageData == null){
            }else{
                selectedImage = imageData.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();

            }

            Picasso.with(getContext())
                    .load(new File(mCurrentPhotoPath))
                    .placeholder(R.drawable.ic_loading) // optional
                    .resize(400,400)
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .into(currentImage);
            currentImage.setTag(mCurrentPhotoPath);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AddDescriptionFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnShowRationale({android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void openGaleryOnShowRationale(final PermissionRequest request) {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnPermissionDenied({android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void openGaleryOnPermissionDenied() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnNeverAskAgain({android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void openGaleryOnNeverAskAgain() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("image1Path",image1.getTag().toString());
        outState.putString("image2Path",image2.getTag().toString());
        outState.putString("image3Path",image3.getTag().toString());
        super.onSaveInstanceState(outState);

    }

}
