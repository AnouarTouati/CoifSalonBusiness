package com.example.coifsalonbusiness.shop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;

import com.example.coifsalonbusiness.ActivityWithLocation;
import com.example.coifsalonbusiness.CustomFragmentPagerAdapter;
import com.example.coifsalonbusiness.R;
import com.example.coifsalonbusiness.SignInActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShopActivity extends ActivityWithLocation  {

    ArrayList<ClientPending> pendingList = new ArrayList<>();
    CustomFragmentPagerAdapter customFragmentPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView errorText;
    ProgressBar progressBar;

    private ShopMenuFrag1 shopMenuFrag1;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        context=this;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestoreSettings firestoreSettings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();

        firebaseFirestore.setFirestoreSettings(firestoreSettings);

        if (isUserNotSignedIn()) {
            Intent goBACKtoSignInActivity = new Intent(this, SignInActivity.class);
            startActivity(goBACKtoSignInActivity);
        }

        getViewsReferences();
        setUpViews();
        getPendingList();

    }

    private Boolean isUserNotSignedIn() {
        return firebaseUser == null;
    }

    private void getViewsReferences() {
        progressBar = findViewById(R.id.progressBarShopActivity);
        errorText = findViewById(R.id.ErrorTextView_ShopActivity);
        viewPager = findViewById(R.id.viewPagerShopMenu);
        tabLayout = findViewById(R.id.tabLayoutShopMenu);
    }

    private void setUpViews() {
        customFragmentPagerAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        shopMenuFrag1 = new ShopMenuFrag1(this);
        customFragmentPagerAdapter.addFragment(shopMenuFrag1, "ShopMenuFrag1");
        customFragmentPagerAdapter.addFragment(new ShopMenuFrag2(this), "ShopMenuFrag2");
        viewPager.setAdapter(customFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                try{
                    tabLayout.getTabAt(i).select();
                }catch (Exception e){
                    Log.e("AppFilter", getString(R.string.Unexpected_error_occurred)+" "+e);
                    notSuccessful(getString(R.string.Unexpected_error_occurred));
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onLocationResult(final Location location){
        turnOnProgressBar();
        Map<String,Object> map=new HashMap<>();
        map.put("ShopLatitude",location.getLatitude());
        map.put("ShopLongitude",location.getLongitude());

        firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).update(map).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                addUpdateLocationMapSuccessful(location.getLatitude(),location.getLongitude());
            }
        }).addOnFailureListener(e -> {
            Log.e("AppFilter",getString(R.string.Failed_to_update_location_info)+" "+e);
            notSuccessful(getString(R.string.Failed_to_update_location_info));
        });
    }
   private  void addUpdateLocationMapSuccessful(double Latitude, double Longitude){

       Toast.makeText(context, "Successfully Added the map to your shop", Toast.LENGTH_LONG).show();
       turnOffProgressBar();
   }

    public  void serverRemovePersonFromPending(final ClientPending personToRemove){
        turnOnProgressBar();
        String personToRemoveUidOnFirestore;
        if(!personToRemove.getClientFirebaseUid().equals("null")){//means added from client phone(client app)
            personToRemoveUidOnFirestore=personToRemove.getClientFirebaseUid();
        }else{//means added from shop owner phone (business app)
             personToRemoveUidOnFirestore=personToRemove.getClientFakeFirebaseUid();
        }
       firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").document(personToRemoveUidOnFirestore).delete().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               turnOffProgressBar();
           }
       }).addOnFailureListener(e -> {
           Log.e("AppFilter",getString(R.string.Could_not_delete_person_from_pending)+" "+e);
           notSuccessful(getString(R.string.Could_not_delete_person_from_pending));
       });
    }

    public void serverAddPersonToPending(){
        final AlertDialog.Builder alertBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.addperson_alerdialog_layout, null);
        alertBuilder.setView(dialogView);

        alertBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                turnOnProgressBar();
                EditText personToAdd= dialogView.findViewById(R.id.editText);
                final String PersonName=personToAdd.getText().toString();
                final String clientFakeFirebaseUid=UUID.randomUUID().toString();
                Map<String,Object> map=new HashMap<>();
               //we may want to add want kind of service this client
                map.put("PersonName", PersonName);
                map.put("ClientFakeFirebaseUid",clientFakeFirebaseUid);
                map.put("ClientFireBaseUid","null");
                map.put("Services","null");

                firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").document(clientFakeFirebaseUid).set(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        turnOffProgressBar();
                    }
                }).addOnFailureListener(e -> {
                    Log.e("AppFilter", getString(R.string.Could_not_add_the_person_to_the_waiting_list) +" "+ e);
                notSuccessful(getString(R.string.Could_not_add_the_person_to_the_waiting_list));
                });
            }
        });

        AlertDialog alertDialog=alertBuilder.create();
        alertDialog.show();

    }

   public void getPendingList(){
        turnOnProgressBar();
       firebaseFirestore.collection("Shops").document(firebaseUser.getUid()).collection("ClientsPending").addSnapshotListener((value, error) -> {
           if(error!=null) {
               Log.e("AppFilter", getString(R.string.Could_not_get_pending_list) +" "+ error);
               notSuccessful(getString(R.string.Could_not_get_pending_list));
               return;
           }
          if(value!=null){
              extractPendingListFromServerResponse(value.getDocuments());
          }
           dataChangedNotifyRecyclerView();
           turnOffProgressBar();
       });
   }
   private  void extractPendingListFromServerResponse(List<DocumentSnapshot> clientsPendingList) {
      try{
          pendingList.clear();
          for(int i=0;i<clientsPendingList.size();i++){
              pendingList.add(new ClientPending(clientsPendingList.get(i).get("PersonName").toString(),clientsPendingList.get(i).get("ClientFireBaseUid").toString(),clientsPendingList.get(i).get("Services").toString(),clientsPendingList.get(i).get("ClientFakeFirebaseUid").toString()));
          }
      }catch(Exception exception){
         Log.e("AppFilter", getString(R.string.Could_not_get_pending_list) +" "+ exception);
         notSuccessful(getString(R.string.Could_not_get_pending_list));
      }

   }
   private void dataChangedNotifyRecyclerView(){
        if(shopMenuFrag1.customRecyclerViewAdapterShop!=null){
            shopMenuFrag1.customRecyclerViewAdapterShop.notifyDataSetChanged();
        }

   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    private void notSuccessful(String message){
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        turnOffProgressBar();
    }
    void turnOnProgressBar(){
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    void turnOffProgressBar(){
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

}
