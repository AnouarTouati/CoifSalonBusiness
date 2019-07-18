package com.example.bookingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SignUpFrag4 extends Fragment {
   public Bitmap SelectedImage;
   public String StorePhoneNumber;
   public String FacebookLink;
   public String InstagramLink;

    View view;
    final int IMG_REQ=1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.signupfrag4_layout, container,false);
        Button uploadImageAkaSelect=view.findViewById(R.id.upLoadPhotoFrag4);
        uploadImageAkaSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        Button submit=view.findViewById(R.id.submitFrag4);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoneFillingFieldsGoNextFrag();
            }
        });
        return view;
    }
    void ShowImage(Bitmap ImageBitmap){
        ImageView imageView=view.findViewById(R.id.imageViewFrag4);
        imageView.setImageBitmap(ImageBitmap);
    }
    void SelectImage(){
        Intent selectImageIntent=new Intent();
        selectImageIntent.setType("image/*");
        selectImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(selectImageIntent, IMG_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQ && resultCode==getActivity().RESULT_OK && data!=null){
            Uri path=data.getData();
            try {
                SelectedImage= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                ShowImage(SelectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void DoneFillingFieldsGoNextFrag(){
        Boolean SomethingWentWrong=false;

        EditText storePhoneNumberEditText=view.findViewById(R.id.storePhoneNumberFrag4);
        StorePhoneNumber=storePhoneNumberEditText.getText().toString();
        if(StorePhoneNumber==null){
            SomethingWentWrong=true;
        }else if (StorePhoneNumber==""){
            SomethingWentWrong=true;
        }else if(!(StorePhoneNumber.indexOf("0")==0 && (StorePhoneNumber.indexOf("2")==1||StorePhoneNumber.indexOf("5")==1 || StorePhoneNumber.indexOf("6")==1 || StorePhoneNumber.indexOf("7")==1))){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Incorrect Store Phone Number", Toast.LENGTH_SHORT).show();
        }

        if(SelectedImage==null){
            SomethingWentWrong=true;
            Toast.makeText(getActivity(), "Please Choose an Image", Toast.LENGTH_LONG).show();
        }
        EditText facebookEditText=view.findViewById(R.id.facebookFrag4);
        FacebookLink="www.facebook.com/"+facebookEditText.getText().toString();
        EditText instagramEditText=view.findViewById(R.id.instagramFrag4);
        InstagramLink="www.instagram.com/"+instagramEditText.getText().toString();

        if(!SomethingWentWrong){

            ((SignUpActivity)getActivity()).SelectedImage=SelectedImage;
            ((SignUpActivity)getActivity()).StorePhoneNumber=StorePhoneNumber;
            ((SignUpActivity)getActivity()).FacebookLink=FacebookLink;
            ((SignUpActivity)getActivity()).InstagramLink=InstagramLink;

            ((SignUpActivity)getActivity()).SetCurrentItemViewPager(4);

        }
    }
}