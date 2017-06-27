package com.example.daumantas.explorer;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image1 = (ImageButton)getActivity().findViewById(R.id.image1);
        image1.setTag("plus");
        image2 = (ImageButton)getActivity().findViewById(R.id.image2);
        image2.setTag("plus");
        image3 = (ImageButton)getActivity().findViewById(R.id.image3);
        image3.setTag("plus");

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image1.getTag() == "plus"){
                    openGaleryWithCheck();
                    currentImage = image1;
                }else{
                    image1.setImageResource(R.drawable.picture_frame);
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

                    /*
                    Uri pickedImage = imageData.getData();
                    // Let's read picked image path using content resolver
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(pickedImage, filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                    */

        //Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, imageData);

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
            currentImage.setTag("photo");

        }
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);

        Log.d("mytag", options.inSampleSize + " sample method bitmap ... " +
                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());

        return actuallyUsableBitmap;
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
}
