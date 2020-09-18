package chessbet.app.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import chessbet.app.com.R;

public class OnBoardingSliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    int[] slide_images= {
            R.drawable.chessbetlogo,
            R.drawable.purse,
            R.drawable.chess,
            R.drawable.success,
            R.drawable.withdrawal
    };

    int[] slide_headings= {
            R.string.welcome_to_chess_bet,
            R.string.step_one,
            R.string.step_two,
            R.string.step_three,
            R.string.step_four
    };

    int[] slide_descriptions = {
            R.string.welcome_to_chess_bet_description,
            R.string.step_one_description,
            R.string.step_two_description,
            R.string.step_three_description,
            R.string.step_four_description
    };

    public OnBoardingSliderAdapter(Context context) {
        this.context = context;
    }
    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.onboarding_slide_layout, container, false);

        ImageView imageView = (ImageView)  view.findViewById(R.id.imgIcon);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);

        imageView.setImageResource(slide_images[position]);
        txtDescription.setText(slide_descriptions[position]);
        txtTitle.setText(slide_headings[position]);

        container.addView(view);

        return  view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
