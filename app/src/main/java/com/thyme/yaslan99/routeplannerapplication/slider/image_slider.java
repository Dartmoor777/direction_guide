package com.thyme.yaslan99.routeplannerapplication.slider;

import android.app.Activity;
import android.os.Bundle;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.thyme.yaslan99.routeplannerapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class image_slider extends Activity {

    SliderView sliderView;
    private SliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_slider);

        sliderView = findViewById(R.id.imageSlider);

        adapter = new SliderAdapter(this);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        renewItems();
    }

    public void renewItems() {
        List<SliderItem> sliderItemList = new ArrayList<>();

        List<Integer> images = Arrays.asList(
                R.drawable.start_0,
                R.drawable.start_1,
                R.drawable.start_2,
                R.drawable.start_3,
                R.drawable.start_4
                );

        for (Integer image: images) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDrawable(image);
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }
}

