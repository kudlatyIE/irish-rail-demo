package ie.droidfactory.irishrails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kudlaty on 2017-10-15.
 */

public class HelpFragment extends MainFragment {

    private final static String TAG = HelpFragment.class.getSimpleName();

    public static HelpFragment newInstance(Bundle args) {
        HelpFragment fragment = new HelpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }
}
