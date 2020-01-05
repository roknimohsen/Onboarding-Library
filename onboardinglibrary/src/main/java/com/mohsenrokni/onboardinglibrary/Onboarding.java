package com.mohsenrokni.onboardinglibrary;

import android.animation.ArgbEvaluator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mohsenrokni.onboardinglibrary.util.CustomViewPager;

import java.util.ArrayList;
import java.util.List;


public class Onboarding {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    ViewPagerAdapter adapter ;

    private int[] colors;
    ArgbEvaluator evaluator ;

    private Onboarding0 board0 ;
    private Onboarding1 board1 ;
    private Onboarding2 board2 ;
    private Onboarding3 board3 ;
    private static List<BoardSetting> boardSettings = new ArrayList<>() ;

    private View rootView ;

    private AppCompatActivity activity ;

    public Onboarding withActivity(AppCompatActivity activity) {
        this.activity = activity ;
        return this ;
    }


    public Onboarding withColor(int c1, int c2, int c3, int c4) {

        int color1 = ContextCompat.getColor(activity, c1);
        int color2 = ContextCompat.getColor(activity, c2);
        int color3 = ContextCompat.getColor(activity, c3);
        int color4 = ContextCompat.getColor(activity, c4);

        colors = new int[]{color1, color2, color3, color4} ;

        return this ;

    }

    public Onboarding withBoardSettings(List<BoardSetting> settings) {

        for(BoardSetting setting : settings)
            this.boardSettings.add(setting);

        return this ;

    }

    public View getView() {

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        rootView = activity.getLayoutInflater().inflate(R.layout.activity_onboarding, null);


        evaluator = new ArgbEvaluator();


        viewPager = rootView.findViewById(R.id.onboarding_viewpager);
        setupViewPager(viewPager);
        viewPager.setPagingEnabled(true);
        viewPager.setBackgroundColor(colors[0]);

        TabLayout tabLayout = rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

        final View next = rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(viewPager.getCurrentItem() == 3){
                    /*next.setClickable(false);
                    Intent intent = new Intent(Onboarding.this, MapsActivity.class);
                    finish();
                    startActivity(intent);*/
                    return ;
                }


                int nextPage = viewPager.getCurrentItem() + 1 ;
                viewPager.setCurrentItem(nextPage);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int SCROLLING_RIGHT = 0 ;
            int SCROLLING_LEFT = 1 ;
            int SCROLLING_UNDETERMINED = 2 ;

            int currentScrollDirection = 2 ;
            private float prevOffset = 0 ;
            private int currentItem = 0 ;

            //0 : 0 <-> 1
            //1 : 1 <-> 2
            //2 : 2 <-> 3
            //3 : 3 <-> 4
            int animationIndex = 0 ;
            int scrollState = 0 ;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (isScrollDirectionUndetermined() || anomaly(positionOffset)){
                    setScrollingDirection(positionOffset);
                }


                if (positionOffset > 0) {
                    updateColor(positionOffset);

                    //show animation
                    //between board0 and board1
                    if((currentItem == 0 && isScrollingRight()) || ((currentItem == 1 && isScrollingLeft()))) {
                        animationIndex = 0 ;

                    }
                    //between board1 and board2
                    if((currentItem == 1 && isScrollingRight()) || ((currentItem == 2 && isScrollingLeft()))) {
                        animationIndex = 1 ;

                    }
                    //between board2 and board3
                    if((currentItem == 2 && isScrollingRight()) || ((currentItem == 3 && isScrollingLeft()))) {
                        animationIndex = 2 ;

                    }

                    prevOffset = positionOffset ;

                    switch (animationIndex) {
                        case 0 : {
                            board0.rightSideAnimation(positionOffset);
                            board1.leftSideAnimation(positionOffset);
                            break;
                        }

                        case 1 : {
                            board1.rightSideAnimation(positionOffset);
                            board2.leftSideAnimation(positionOffset);
                            break;
                        }
                        case 2 : {
                            board2.rightSideAnimation(positionOffset);
                            board3.leftSideAnimation(positionOffset);
                            break;
                        }
                    }


                }

            }

            private boolean anomaly(float offset) {
                return (Math.abs(offset - prevOffset) > 0.85) ;
            }

            private void updateColor(float offset) {
                int currentColor = colors[currentItem];
                int position = currentItem ;

                if (isScrollingRight()){
                    position = currentItem + 1;
                }else if (isScrollingLeft()){
                    position = currentItem - 1;
                    offset = 1 - offset;
                }

                position = Math.max(0, position);
                position = Math.min(colors.length - 1, position);
                int nextColor = colors[position];
                int colorUpdate = (Integer) evaluator.evaluate(offset, currentColor, nextColor) ;
                viewPager.setBackgroundColor(colorUpdate);

            }

            private void setScrollingDirection(float positionOffset){
                if ((1-positionOffset)>= 0.5){
                    this.currentScrollDirection = SCROLLING_RIGHT;
                }
                else if ((1-positionOffset)<= 0.5){
                    this.currentScrollDirection =  SCROLLING_LEFT;
                }
            }

            private boolean isScrollDirectionUndetermined(){
                return currentScrollDirection == SCROLLING_UNDETERMINED;
            }

            private boolean isScrollingRight(){
                return currentScrollDirection == SCROLLING_RIGHT;
            }

            private boolean isScrollingLeft(){
                return currentScrollDirection == SCROLLING_LEFT;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE){
                    this.currentScrollDirection = SCROLLING_UNDETERMINED;
                    currentItem = viewPager.getCurrentItem();
                    prevOffset = 0 ;
                    scrollState = state ;
                }
                //scrollState = state ;
            }


        });

    return rootView ;

    }

    public static class Onboarding0 extends Fragment {
        ImageView object1 ;
        ImageView object2 ;
        ImageView object3 ;
        ImageView object4 ;

        TextView title ;
        TextView text ;

        //initial margins to come back to
        int object1MarginTop ;
        int object2MarginLeft ;
        int object4MarginRight ;

        BoardSetting setting ;


        public Onboarding0() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_onboarding_0, container, false);
            object1 = rootView.findViewById(R.id.onboarding_0_obj_1);
            object2 = rootView.findViewById(R.id.onboarding_0_obj_2);
            object3 = rootView.findViewById(R.id.onboarding_0_obj_3);
            object4 = rootView.findViewById(R.id.onboarding_0_obj_4);

            object1.setImageDrawable(boardSettings.get(0).drawableList.get(0));
            object2.setImageDrawable(boardSettings.get(0).drawableList.get(1));
            object3.setImageDrawable(boardSettings.get(0).drawableList.get(2));
            object4.setImageDrawable(boardSettings.get(0).drawableList.get(3));

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object2.getLayoutParams();
            object2MarginLeft = params.leftMargin ;
            params = (ViewGroup.MarginLayoutParams) object4.getLayoutParams();
            object4MarginRight = params.rightMargin ;
            params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            object1MarginTop = params.topMargin ;

            title = rootView.findViewById(R.id.onboarding_0_title) ;
            title.setText(boardSettings.get(0).title);

            text = rootView.findViewById(R.id.onboarding_0_text) ;
            text.setText(boardSettings.get(0).text);

            return rootView ;
        }


        //coming from or leaving to right side
        private void rightSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object2.getLayoutParams();
            params.setMargins((int)((offset) * (-1000) + object2MarginLeft), params.topMargin, params.rightMargin, params.bottomMargin);
            object2.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object4.getLayoutParams();
            params.setMargins((int)((offset) * (-2000)), params.topMargin, params.rightMargin, params.bottomMargin);
            object4.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            params.setMargins(params.leftMargin, (int)((offset) * (-1000) + object1MarginTop), params.rightMargin, params.bottomMargin);
            object4.requestLayout();

            title.setAlpha((float)(1 - (offset / 0.15)));
            text.setAlpha((float)(1 - (offset / 0.15)));
        }


    }

    public static class Onboarding1 extends Fragment {
        ImageView object1 ;
        ImageView object2 ;
        ImageView object3 ;

        TextView title ;
        TextView text ;

        //initial margins to come back to
        int object1MarginLeft;
        int object3MarginRight;

        public Onboarding1() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_onboarding_1, container, false);
            object1 = rootView.findViewById(R.id.onboarding_1_obj_1);
            object2 = rootView.findViewById(R.id.onboarding_1_obj_2);
            object3 = rootView.findViewById(R.id.onboarding_1_obj_3);

            object1.setImageDrawable(boardSettings.get(1).drawableList.get(0));
            object2.setImageDrawable(boardSettings.get(1).drawableList.get(1));
            object3.setImageDrawable(boardSettings.get(1).drawableList.get(2));


            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            object1MarginLeft = params.leftMargin ;
            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            object3MarginRight = params.rightMargin ;

            title = rootView.findViewById(R.id.onboarding_1_title) ;
            title.setText(boardSettings.get(1).title);

            text = rootView.findViewById(R.id.onboarding_1_text) ;
            text.setText(boardSettings.get(1).text);


            return rootView ;
        }

        //coming from or leaving to left side
        private void leftSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-2000)), params.bottomMargin);
            object1.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-1000) + object3MarginRight), params.bottomMargin);
            object3.requestLayout();

            title.setAlpha((float)((offset / 0.15) - (85 / 15)));
            text.setAlpha((float)((offset / 0.15) - (85 / 15)));
        }

        //coming from or leaving to right side
        private void rightSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            params.setMargins((int)((offset) * (-1000) + object1MarginLeft), params.topMargin, params.rightMargin, params.bottomMargin);
            object1.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins((int)((offset) * (-2000)), params.topMargin, params.rightMargin, params.bottomMargin);
            object3.requestLayout();

            title.setAlpha((float)(1 - (offset / 0.15)));
            text.setAlpha((float)(1 - (offset / 0.15)));
        }


    }

    public static class Onboarding2 extends Fragment {

        ImageView object1 ;
        ImageView object2 ;
        ImageView object3 ;
        ImageView object4 ;

        TextView title ;
        TextView text ;

        //initial margins to come back to
        int object2MarginRight ;
        int object3MarginRight ;
        int object4MarginTop ;

        public Onboarding2() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_onboarding_2, container, false);
            object1 = rootView.findViewById(R.id.onboarding_2_obj_1);
            object2 = rootView.findViewById(R.id.onboarding_2_obj_2);
            object3 = rootView.findViewById(R.id.onboarding_2_obj_3);
            object4 = rootView.findViewById(R.id.onboarding_2_obj_4);

            object1.setImageDrawable(boardSettings.get(2).drawableList.get(0));
            object2.setImageDrawable(boardSettings.get(2).drawableList.get(1));
            object3.setImageDrawable(boardSettings.get(2).drawableList.get(2));
            object4.setImageDrawable(boardSettings.get(2).drawableList.get(3));

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object2.getLayoutParams();
            object2MarginRight = params.rightMargin ;
            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            object3MarginRight = params.rightMargin ;
            params = (ViewGroup.MarginLayoutParams) object4.getLayoutParams();
            object4MarginTop = params.topMargin ;

            title = rootView.findViewById(R.id.onboarding_2_title) ;
            title.setText(boardSettings.get(2).title);

            text = rootView.findViewById(R.id.onboarding_2_text) ;
            text.setText(boardSettings.get(2).text);



            return rootView ;
        }

        //coming from or leaving to left side
        private void leftSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object2.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-2000) + object2MarginRight), params.bottomMargin);
            object2.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-1000) + object3MarginRight), params.bottomMargin);
            object3.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object4.getLayoutParams();
            params.setMargins(params.leftMargin, (int)((1 - offset) * (-1000)) + object4MarginTop, params.rightMargin, params.bottomMargin);
            object4.requestLayout();

            title.setAlpha((float)((offset / 0.15) - (85 / 15)));
            text.setAlpha((float)((offset / 0.15) - (85 / 15)));
        }

        //coming from or leaving to right side
        private void rightSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object2.getLayoutParams();
            params.setMargins((int)((offset) * (-1000)), params.topMargin, params.rightMargin, params.bottomMargin);
            object2.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins((int)((offset) * (-2000)), params.topMargin, params.rightMargin, params.bottomMargin);
            object3.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object4.getLayoutParams();
            params.setMargins(params.leftMargin, (int)((offset) * (-1000) + object4MarginTop), params.rightMargin, params.bottomMargin);
            object4.requestLayout();

            title.setAlpha((float)(1 - (offset / 0.15)));
            text.setAlpha((float)(1 - (offset / 0.15)));
        }

    }

    public static class Onboarding3 extends Fragment {
        ImageView object1 ;
        ImageView object2 ;
        ImageView object3 ;

        TextView title ;
        TextView text ;

        //initial margins to come back to
        int object1MarginLeft;
        int object3MarginRight;

        public Onboarding3() {
            // Required empty public constructor
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_onboarding_3, container, false);
            object1 = rootView.findViewById(R.id.onboarding_3_obj_1);
            object2 = rootView.findViewById(R.id.onboarding_3_obj_2);
            object3 = rootView.findViewById(R.id.onboarding_3_obj_3);

            object1.setImageDrawable(boardSettings.get(3).drawableList.get(0));
            object2.setImageDrawable(boardSettings.get(3).drawableList.get(1));
            object3.setImageDrawable(boardSettings.get(3).drawableList.get(2));

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            object1MarginLeft = params.leftMargin ;
            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            object3MarginRight = params.rightMargin ;

            title = rootView.findViewById(R.id.onboarding_3_title) ;
            title.setText(boardSettings.get(3).title);

            text = rootView.findViewById(R.id.onboarding_3_text) ;
            text.setText(boardSettings.get(3).text);


            return rootView ;
        }

        //coming from or leaving to left side
        private void leftSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-2000)), params.bottomMargin);
            object1.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, (int)((1- offset) * (-1000) + object3MarginRight), params.bottomMargin);
            object3.requestLayout();

            title.setAlpha((float)((offset / 0.15) - (85 / 15)));
            text.setAlpha((float)((offset / 0.15) - (85 / 15)));
        }

        //coming from or leaving to right side
        private void rightSideAnimation(float offset) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) object1.getLayoutParams();
            params.setMargins((int)((offset) * (-1000) + object1MarginLeft), params.topMargin, params.rightMargin, params.bottomMargin);
            object1.requestLayout();

            params = (ViewGroup.MarginLayoutParams) object3.getLayoutParams();
            params.setMargins((int)((offset) * (-2000)), params.topMargin, params.rightMargin, params.bottomMargin);
            object3.requestLayout();

            title.setAlpha((float)(1 - (offset / 0.15)));
            text.setAlpha((float)(1 - (offset / 0.15)));
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(activity.getSupportFragmentManager());

        board0 = new Onboarding0();
        board1 = new Onboarding1();
        board2 = new Onboarding2();
        board3 = new Onboarding3();

        adapter.addFrag(board0);
        adapter.addFrag(board1);
        adapter.addFrag(board2);
        adapter.addFrag(board3);
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
            // mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return "" ;
        }
    }

    public static class BoardSetting {

        public String title = "title" ;
        public String text = "text" ;
        public List<Drawable> drawableList ;

    }
}
