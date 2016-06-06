package ie.droidfactory.fragstations;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ie.droidfactory.fragstations.material.DrawerAdapter;
import ie.droidfactory.fragstations.material.DrawerItems;

/**
 * Created by kudlaty on 05/06/2016.
 */
public class DrawerFragment extends Fragment {

    public interface DrawerListener{
        public void onDrawerItemSelected(View view, int position);
    }
    private DrawerItems items;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private DrawerAdapter drawerAdapter;
    private View containerView;
    private static String[] itemsTitle;
    private DrawerListener drawerListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.activity_navigation_drawer,container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.drawer_list);

        drawerAdapter = new DrawerAdapter(new ArrayList<DrawerItems>());
        recyclerView.setAdapter(drawerAdapter);


        return v;
    }
}
