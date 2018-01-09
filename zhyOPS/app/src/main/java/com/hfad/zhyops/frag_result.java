package com.hfad.zhyops;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link frag_result.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link frag_result} factory method to
 * create an instance of this fragment.
 */
public class frag_result extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

   // private LocalMusicAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    public frag_result() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_frag_result, container, false);

//
//        TableFixHeaders tableFixHeaders = (TableFixHeaders) view.findViewById(R.id.table_result);
//        tableFixHeaders.setAdapter(new MyAdapter(getContext()));

        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
    }


    //MyAdapter .....end

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    //@Override
//    protected void setListener() {
//        frag_storage.setOnItemClickListener(this);
//    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //保存数据，防止软件退出后台，数据丢失
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        int position = lvLocalMusic.getFirstVisiblePosition();
//        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
//        outState.putInt(Keys.LOCAL_MUSIC_POSITION, position);
//        outState.putInt(Keys.LOCAL_MUSIC_OFFSET, offset);
//    }
//
//    public void onRestoreInstanceState(final Bundle savedInstanceState) {
//        lvLocalMusic.post(new Runnable() {
//            @Override
//            public void run() {
//                int position = savedInstanceState.getInt(Keys.LOCAL_MUSIC_POSITION);
//                int offset = savedInstanceState.getInt(Keys.LOCAL_MUSIC_OFFSET);
//                lvLocalMusic.setSelectionFromTop(position, offset);
//            }
//        });
//    }
}
