package injectview.lym.org.sampleproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaoming.li
 * @since 2017-09-03 11:15
 */
public class RvListAdapter extends RecyclerView.Adapter<RvListAdapter.ListHolder> {

    private List<String> mData = new ArrayList<>();

    public RvListAdapter(List<String> list) {
        this.mData.addAll(list);
    }


    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListHolder holder, final int position) {
        holder.mTv.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ListHolder extends RecyclerView.ViewHolder {

        private TextView mTv;

        public ListHolder(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.tv);
        }
    }

}
