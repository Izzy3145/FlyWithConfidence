package uk.airbyte.fwc.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.RealmResults;
import uk.airbyte.fwc.R;
import uk.airbyte.fwc.fragments.TopicsFragment;
import uk.airbyte.fwc.model.Module;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {

    private static final String TAG = TopicsFragment.class.getSimpleName();

    private final Context mContext;
    private static ArrayList<RealmResults<Module>> mData;
    private static RecyclerView horizontalList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        private ModulesAdapter modulesAdapter;

        public ViewHolder(View view) {
            super(view);
            Context context = itemView.getContext();
            title = (TextView) view.findViewById(R.id.horizTopicTitle);
            horizontalList = (RecyclerView) itemView.findViewById(R.id.horizTopicsRv);
            horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            modulesAdapter = new ModulesAdapter(null , 0);
            horizontalList.setAdapter(modulesAdapter);
        }
    }

    public TopicsAdapter(ArrayList<RealmResults<Module>> modulesTopics, Context context) {
        mContext = context;
        if (modulesTopics != null)
            mData = new ArrayList<>(modulesTopics);
        else mData = new ArrayList<>();
    }

    public TopicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.topic_modules_rv, parent, false);
        TopicsAdapter.ViewHolder viewholder = new TopicsAdapter.ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            if(mData != null && mData.get(position).where().findFirst() != null) {
           holder.title.setText(mData.get(position).where().findFirst().getTopic().getName());
            holder.modulesAdapter.setData(mData.get(position));
            //holder.modulesAdapter.setRowIndex(position);
        }
    }

    public void setData(ArrayList<RealmResults<Module>> data){
        mData = data;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
