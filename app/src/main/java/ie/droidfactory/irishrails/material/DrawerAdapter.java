package ie.droidfactory.irishrails.material;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.droidfactory.irishrails.R;

/**
 * Created by kudlaty on 05/06/2016.
 * not used now....
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.MyHolder> {
    private ArrayList<DrawerItems> drawerList = new ArrayList<>();
//    private RecyclerView recyclerView;

    public DrawerAdapter(ArrayList<DrawerItems> list){
        this.drawerList=list;
//        this.recyclerView=view;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_adapter,
                parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        DrawerItems item = this.drawerList.get(position);
        holder.tvDraverItemName.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.drawerList.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        public TextView tvDraverItemName;
        public ImageView imgDrawerItemIcon;

        public MyHolder(View holderView){
            super(holderView);
            tvDraverItemName = (TextView) holderView.findViewById(R.id.drawer_adapter_text_title);
            imgDrawerItemIcon = (ImageView) holderView.findViewById(R.id.drawer_adapter_image_icon);
        }
    }
}
