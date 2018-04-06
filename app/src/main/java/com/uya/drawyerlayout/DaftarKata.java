package com.uya.drawyerlayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DaftarKata.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DaftarKata#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DaftarKata extends Fragment {

    private Cursor kamusCursor = null;
    private CustomCursorAdapter adapter;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db = null;
    private ListView listContent = null;
    private DatabaseHelper datakamus = null;
    private static final int DELETE_ID = Menu.FIRST + 1;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new DatabaseHelper(getActivity(), getActivity().getBaseContext());
        listContent = (ListView) getActivity().findViewById(R.id.list1);
        isDataListView();
        registerForContextMenu(listContent);
    }

    private void isDataListView(){
        try {
            db = dbHelper.getWritableDatabase();

            kamusCursor = db.query("kamus", new String[]{"_id", "indonesia", "kaili"}, "_id > 0",null, null, null, null);

            //create array to specify the fields want to display in the list
            String[] from = new String[]{
                    "indonesia", "kaili"
            };

            //array of the fields we want to bind those fields to (in this case just the textView 'inggris,indonesia,jerman' from our new row.xml layout above)
            int[] to = new int[] {R.id.indonesia, R.id.kaili};

            adapter = new CustomCursorAdapter(getActivity(), R.layout.row, kamusCursor, from, to);

            //listView.setAdapter(adapter)
            listContent.setAdapter(adapter);
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (db != null && db.isOpen()){
                //db.close();
            }
        }
    }

    protected class CustomCursorAdapter extends SimpleCursorAdapter {
        private int layout;
        private LayoutInflater inflater;
        private Context context;

        public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.layout = layout;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //return super.newView(context, cursor, parent);
            Log.d("NewView", "*****xxx");
            View v = inflater.inflate(R.layout.row, parent, false);

            return v;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //super.bindView(view, context, cursor);
            //1 is the column where you're getting your data from
            String indonesia = cursor.getString(1);
            String kaili = cursor.getString(2);

            //Next set the name of the entry.
            TextView ind_text = (TextView) view.findViewById(R.id.indonesia);
            TextView kai_text = (TextView) view.findViewById(R.id.kaili);
            TextView id_text = (TextView) view.findViewById(R.id.indonesia);
            ind_text.setText(indonesia);
            kai_text.setText(kaili);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, DELETE_ID, Menu.NONE, " Hapus").setIcon(R.drawable.ic_launcher_background).setAlphabeticShortcut('e');
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                delete(info.id);
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    private void delete(final long rowId){
        if (rowId > 0){
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.delete_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            processDelete(rowId);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //ignore, just dismiss
                }
            }).show();
        }
    }

    private void processDelete(long rowId){
        String[] args = { String.valueOf(rowId)};

        db.delete("kamus", "_ID=?", args);
        kamusCursor.requery();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DaftarKata() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DaftarKata newInstance(String param1, String param2) {
        DaftarKata fragment = new DaftarKata();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daftar_kata, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        kamusCursor.close();
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
}
