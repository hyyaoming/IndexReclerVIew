package injectview.lym.org.sampleproject;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yaoming.li
 * @since 2017-09-02 19:01
 */
public class IndexDetailViewGroup  extends DetailBaseHeadedViewPager implements View.OnClickListener {

    private TabLayout mTablayout;
    private View one,two;
    List<String> data = new ArrayList<String>();


    public IndexDetailViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        for(int i =0; i < 100;i++){
            data.add(""+i);
        }
    }

    @Override
    protected void getHeadView() {
        headView = mInflater.inflate(R.layout.headview_layout,null);
        one = headView.findViewById(R.id.view_head_one);
        two = headView.findViewById(R.id.view_head_two);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        List<String> list = new ArrayList<>();
        for(int i =0 ;i < 8; i ++){
            list.add("hello");
        }
        headScrollerView = headView.findViewById(R.id.head_scrollview);
    }

    private void initView() {
        mTablayout = headView.findViewById(R.id.tablayout);
        mTablayout.setupWithViewPager(mainViewPager);
        mainViewPager.setAdapter(new PagerAdapter() {
            @Override
            public void destroyItem(ViewGroup container, int position, Object obj) {
                container.removeView((mainViewPager.findViewFromObject(position)));
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view.equals(obj);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.layout,null);
                RecyclerView rv = view.findViewById(R.id.rv);
                view.setPadding(0,getHeadHeight(),0,0);
                FullyLinearLayoutManager manager = new FullyLinearLayoutManager(mContext);
                manager.setOrientation(LinearLayoutManager.VERTICAL);
                rv.setLayoutManager(manager);
                rv.setAdapter(new RvListAdapter(data));
                container.addView(view, RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT);
                mainViewPager.setObjectForPosition(view, position);
                return view;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "hello";
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_head_one:
                Toast.makeText(mContext,"one",Toast.LENGTH_SHORT).show();
                break;
            case R.id.view_head_two:
                Toast.makeText(mContext,"two",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
