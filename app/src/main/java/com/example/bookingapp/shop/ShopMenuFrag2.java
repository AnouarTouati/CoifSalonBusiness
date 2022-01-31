package com.example.bookingapp.shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bookingapp.R;
import com.example.bookingapp.shop.portfolio.Portfolio;

public class ShopMenuFrag2 extends Fragment {

    View view;
    ShopActivity shopActivityReference;
    Button addPortfolioImagesButton;
    Button addUpdateShopMap;

    public ShopMenuFrag2(ShopActivity shopActivityReference) {
        this.shopActivityReference = shopActivityReference;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.shopmenufrag2_layout, container, false);

        getViewsReferences();
        setUpViews();

        return view;
    }

    private void getViewsReferences() {
        addPortfolioImagesButton = view.findViewById(R.id.AddPortfolioImagesButton_ShopMenuFrag2);
        addUpdateShopMap = view.findViewById(R.id.Add_UpdateShopMap);
    }

    private void setUpViews() {
        addPortfolioImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToPortfolioImagesSubActivity = new Intent(getContext(), Portfolio.class);
                startActivity(goToPortfolioImagesSubActivity);
            }
        });

        addUpdateShopMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopActivityReference.findLocationUsingGPS();
            }
        });
    }


}
