package chessbet.app.com.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import chessbet.api.AccountAPI;
import chessbet.app.com.R;
import chessbet.app.com.activities.MainActivity;
import chessbet.domain.Account;
import chessbet.domain.User;
import chessbet.services.AccountListener;
import chessbet.services.UserListener;
import chessbet.utils.EventBroadcast;
import chessbet.utils.Permissions;
import chessbet.utils.Util;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener,
        AccountListener , EventBroadcast.UserLoaded , UserListener {
    @BindView(R.id.img_profile_photo) CircleImageView profile_photo;
    @BindView(R.id.iv_camera) CircleImageView iv_camera;
    @BindView(R.id.nameTv) TextView nameTv;
    @BindView(R.id.phoneTv) TextView phoneTv;
    @BindView(R.id.editIv) ImageView editIv;
    @BindView(R.id.gallery_layout) LinearLayout gallery_layout;
    @BindView(R.id.camera_layout) LinearLayout camera_layout;
    @BindView(R.id.cancel_layout) LinearLayout cancel_layout;
    @BindView(R.id.bottom_sheet_layout) LinearLayout bottom_sheet_layout;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //arrays of permission to be request
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;
    private ProgressDialog loader;
    private FirebaseUser firebaseUser;
    private Permissions permissions;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this ,view);
        firebaseUser = AccountAPI.get().getFirebaseUser();
        loader = new ProgressDialog(requireContext());
        loader.setMessage("Please Wait..");
        iv_camera.setOnClickListener(this);
        editIv.setOnClickListener(this);
        gallery_layout.setOnClickListener(this);
        camera_layout.setOnClickListener(this);
        cancel_layout.setOnClickListener(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_layout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        AccountAPI.get().setAccountListener(this);
        AccountAPI.get().getAccount();
        AccountAPI.get().getUser();
        AccountAPI.get().setUserListener(this);
        permissions = new Permissions(getActivity());

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Handle Back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP
                    && keyCode == KeyEvent.KEYCODE_BACK) {

                Util.switchContent(R.id.frag_container,
                        Util.GAMES_FRAGMENT,
                        ((MainActivity) (requireContext())),
                        Util.AnimationType.SLIDE_LEFT);
            }
            return true;
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(iv_camera)){
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
        if (v.equals(cancel_layout)){
                bottomSheetBehavior.setHideable(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (v.equals(editIv)){
            showUsernameDialog();
        }
        if (v.equals(gallery_layout)){
            if (!permissions.checkStoragePermission()){
                requestStoragePermission();
            }
            else {
                pickFromGallery();
            }
        }
        if (v.equals(camera_layout)){
            if (!permissions.checkCameraPermission()){
                requestCameraPermission();
            }
            else {
                pickFromCamera();
            }
        }
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void showUsernameDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Update name");

        //set Layout Linear Layout
        LinearLayout linearLayout = new LinearLayout(getActivity());
        // Views to set in dialog
        final EditText nameEt = new EditText(getActivity());
        nameEt.setHint("Enter name");
        /*sets the main width of EditView to fit a text of n 'M' letters regardless of the actual
        text extension and text size*/
        nameEt.setMinEms(16);
        linearLayout.addView(nameEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            if (!TextUtils.isEmpty(nameEt.getText().toString())){
                updateUsername(nameEt.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        //create and show dialog
        builder.create().show();
    }

    private void updateUsername(String name) {
        this.loader.show();
        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
        builder.setDisplayName(name);
        AccountAPI.get().getCurrentUser().setUser_name(name);
        firebaseUser.updateProfile(builder.build()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                this.loader.dismiss();
                AccountAPI.get().updateUser();
                EventBroadcast.get().broadcastUserUpdate();
                this.nameTv.setText(name);
                Toasty.success(requireContext(),"Username successfully changed", Toasty.LENGTH_LONG).show();
            }
            this.loader.dismiss();
        });
    }

    @Override
    public void onAccountReceived(Account account) {

    }

    @Override
    public void onUserReceived(User user) {
        phoneTv.setText(AccountAPI.get().getFirebaseUser().getPhoneNumber());
    }

    @Override
    public void onAccountUpdated(boolean status) {

    }

    @Override
    public void onUserLoaded() {
        init();
    }

    private void init(){
        User user = AccountAPI.get().getCurrentUser();
        if(user != null) {
            try{
                if(user.getProfile_photo_url() != null){
                    Glide.with(this).asBitmap().load(user.getProfile_photo_url()).into(profile_photo);
                }
                if(user.getUser_name() != null) {
                    nameTv.setText(user.getUser_name());
                }
            }catch (Exception ex){
                Log.d(getClass().getSimpleName(), Objects.requireNonNull(ex.getMessage()));
            }

        }
    }

    @Override
    public void onUserUpdated(boolean status) {
        if(getContext() != null){
            this.loader.dismiss();
            if(status){
                Toasty.success(getContext(), "User Updated", Toasty.LENGTH_LONG).show();
            } else {
                Toasty.error(getContext(), "User Not Updated", Toasty.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        // Make sure soft keyboard does not adjust layout
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onStart();
        init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        //This method will be called after picking image form camera or gallery
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                // image is picked from gallery, get uri of image
                loader.show();
                Uri selectedImageUri = data.getData();
                Glide.with(this).asBitmap().load(selectedImageUri)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                uploadProfilePhoto(resource);
                                return false;
                            }
                        })
                        .submit();

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                // image is picked from Camera, get uri of image
                Glide.with(this).asBitmap().load(image_uri)
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                uploadProfilePhoto(resource);
                                return false;
                            }
                        })
                        .into(profile_photo);

            }
        }
    }

    private UploadTask uploadProfilePhotoTask(Bitmap bitmap){
        byte[] bytes = {};
        try {
            profile_photo.setDrawingCacheEnabled(true);
            profile_photo.buildDrawingCache();
//            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return storageReference.putBytes(bytes);
    }

    /**
     * Just a callback for when account photo has been updated on the database
     * @return {OnCompleteListener<Void>}
     */
    public OnCompleteListener<Void> onAccountPhotoUrlUpdated() {
        return task -> {
            try {
                Toast.makeText(getActivity(),R.string.upload_profile_photo,Toast.LENGTH_LONG).show();
                loader.dismiss();
                AccountAPI.get().getUser();
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }
        };
    }

    private void uploadProfilePhoto(Bitmap bitmap){
        storageReference = firebaseStorage.getReference(FirebaseAuth.getInstance().getUid() + "/" + "profile_photo");
        uploadProfilePhotoTask(bitmap).addOnFailureListener(e -> {
            loader.dismiss();
            Crashlytics.logException(e);
        }).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {
            try {
                final Uri uri = task.getResult();
                Map<String,Object> map = new HashMap<>();
                assert uri != null;
                Glide.with(requireContext()).asBitmap().load(uri).into(profile_photo);
                map.put("profile_photo_url", uri.toString());
                AccountAPI.get().getUserPath().update(map).addOnCompleteListener(onAccountPhotoUrlUpdated()).addOnCanceledListener(() ->  loader.dismiss());
            } catch (Exception ex) {
                Crashlytics.logException(ex);
            }
        }));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*This method called when the user press Allow or Deny from permission request dialog
         * Im handling permission cases (allowed or denied)*/

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // picking from, camera first check if camera and storage are allowed or not
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        //Permission enabled
                    } else {
                        //permission denied
                        Toasty.warning(requireActivity(), "Please enable camera && storage permission ", Toasty.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {

                // picking from, gallery first check if storage are allowed or not
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        //Permission enabled
                        pickFromGallery();
                    } else {
                        //permission denied
                        Toasty.warning(requireActivity(), "Please enable storage permission ", Toasty.LENGTH_LONG).show();
                    }

                }
                break;
            }

        }
    }
}
