package com.ihuntech.poshakwaale.Adapter;

import com.ihuntech.poshakwaale.Common.Common;
import com.ihuntech.poshakwaale.Model.Banner;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class BannerSliderAdapter extends SliderAdapter {

    List<Banner> sliderList;


    public BannerSliderAdapter(List<Banner> sliderList) {
        this.sliderList = sliderList;
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder
                .bindImageSlide(
                        new StringBuilder(Common.IMAGE_URL)
                        .append(sliderList.get(position).photo).toString()
                );
    }
}