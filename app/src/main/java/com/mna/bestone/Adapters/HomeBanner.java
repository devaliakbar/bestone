package com.mna.bestone.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mna.bestone.R;

public class HomeBanner  extends PagerAdapter {
    private Context context;

    public HomeBanner(Context context,String[] bannerText) {
        this.context = context;
        this.sliderText = bannerText;
    }

    private int[] sliderImages = {
            R.drawable.banner,
            R.drawable.banner,
            R.drawable.banner
    };
private String[] sliderText;
    @Override
    public int getCount() {
        return sliderImages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_slide_home, container, false);

        ImageView imageView = view.findViewById(R.id.img_id);

        imageView.setImageResource(sliderImages[position]);

        TextView bannerText = view.findViewById(R.id.txt_id);
        bannerText.setText(sliderText[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
