package injectview.lym.org.sampleproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Desp.
 *
 * @author yaoming.li
 * @since 2017-09-03 18:35
 */
public class TitleFragment extends Fragment {

    private View view;
    private RecyclerView mRv;
    private List<String> data = new ArrayList<String>();
    private TextView mTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout,container,false);
        initData();
        initView();
        return view;
    }

    private void initData() {
        for(int i =0; i < 100;i++){
            data.add(""+i);
        }
    }

    private void initView() {
        mRv = view.findViewById(R.id.rv);
        mTv = view.findViewById(R.id.tv_click);
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv.setHasFixedSize(true);
        mRv.setLayoutManager(manager);
        mRv.setAdapter(new RvListAdapter(data));
        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.scrollToPositionWithOffset(0, 0);
                manager.setStackFromEnd(true);
            }
        });
    }
}
