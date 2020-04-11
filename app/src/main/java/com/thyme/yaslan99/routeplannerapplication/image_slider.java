package com.thyme.yaslan99.routeplannerapplication;

import android.app.Activity;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.data.RtlMode;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class image_slider extends Activity {

    SliderView sliderView;
    private SliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature( Window.FEATURE_NO_TITLE );
//        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN );

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
//                0
                );

        for (Integer image: images) {
            SliderItem sliderItem = new SliderItem();
            sliderItem.setDescription("");
            sliderItem.setDrawable(image);
            sliderItemList.add(sliderItem);
        }
        adapter.renewItems(sliderItemList);
    }

//    public void removeLastItem() {
//        if (adapter.getCount() - 1 >= 0)
//            adapter.deleteItem(adapter.getCount() - 1);
//    }
//
//    public void addNewItem() {
//        SliderItem sliderItem = new SliderItem();
//        sliderItem.setDescription("Slider Item Added Manually");
//        sliderItem.setImageUrl("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260");
//        adapter.addItem(sliderItem);
//    }
}

