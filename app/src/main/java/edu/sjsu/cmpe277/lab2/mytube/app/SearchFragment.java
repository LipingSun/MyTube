package edu.sjsu.cmpe277.lab2.mytube.app;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import edu.sjsu.cmpe277.lab2.mytube.app.content.FragmentContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class SearchFragment extends ListFragment {


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "SearchTab";


    private EditText searchInput;
    private ListView videos;

    private OnFragmentInteractionListener mListener;


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the adapter to convert the array to views
        //replace FragmentContent.ITEMS with arraylist from youtube interface
        //SearchAdapter adapter = new SearchAdapter(getActivity(), FragmentContent.ITEMS);

        // Attach the adapter to a ListView
        //setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchInput = (EditText) view.findViewById(R.id.search_box);
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateVideo();
                    return true;
                }
                return false;
            }

        });


        return view;
    }

    private void updateVideo() {
        SearchAdapter adapter = new SearchAdapter(getActivity().getApplicationContext(), R.layout.search_list_view, FragmentContent.ITEMS);
        setListAdapter(adapter);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(TAG, FragmentContent.ITEMS.get(position).getId());
        }
    }


}
