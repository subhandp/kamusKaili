package com.uya.drawyerlayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.w3c.dom.Text;


public class TambahKata extends Fragment {
    private SQLiteDatabase db = null;
    private DatabaseHelper datakamus = null;
    private EditText txtIndonesia;
    private EditText txtKaili;
    private TextInputLayout layoutTxtIndonesia;
    private TextInputLayout layoutTxtKaili;
    private Button tambahKata;
    public static final String INDONESIA = "indonesia";
    public static final String KAILI = "kaili";

    private Cursor kamusCursor = null;

    public void saveData(View view){
        String bhsindonesia = txtIndonesia.getText().toString();
        String bhskaili = txtKaili.getText().toString();

        ContentValues cv = new ContentValues();
        cv.put(INDONESIA, bhsindonesia.toLowerCase());
        cv.put(KAILI, bhskaili.toLowerCase());
        if (db.insert("kamus", null, cv) > 0){
            Toast.makeText(getActivity().getBaseContext(), "Save Data Success", Toast.LENGTH_SHORT).show();
            urutDataKamus(view);
        }
        else{
            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(getActivity());
            builder1.setMessage("Save Data Fail");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });


            android.app.AlertDialog alert11 = builder1.create();
            alert11.show();

        }
    }


    public void urutDataKamus(View view){

        datakamus = new DatabaseHelper(getActivity(), getActivity().getBaseContext());
        datakamus.urutData(datakamus.getWritableDatabase());

        txtIndonesia.setText("");
        txtIndonesia.clearFocus();
        txtKaili.setText("");
        txtKaili.clearFocus();
        layoutTxtIndonesia.setError(null);
        layoutTxtKaili.setError(null);

    }




    private ProgressBar mProgressBar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datakamus = new DatabaseHelper(getActivity(), getActivity().getBaseContext());
        db = datakamus.getWritableDatabase();
        txtIndonesia = (EditText) getActivity().findViewById(R.id.input_indonesia);
        txtKaili = (EditText) getActivity().findViewById(R.id.input_kaili);
        layoutTxtIndonesia = (TextInputLayout) getActivity().findViewById(R.id.input_layout_indonesia);
        layoutTxtKaili = (TextInputLayout) getActivity().findViewById(R.id.input_layout_kaili);
        tambahKata = (Button) getActivity().findViewById(R.id.btn_tambahKata);

        txtIndonesia.addTextChangedListener(new MyTextWatcher(txtIndonesia));
        txtKaili.addTextChangedListener(new MyTextWatcher(txtKaili));

        tambahKata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitForm(view);
            }
        });
    }

    private void submitForm(View view) {
        if (!validateIndonesia()) {
            return;
        }

        if (!validateKaili()) {
            return;
        }

        saveData(view);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateIndonesia() {
        if (txtIndonesia.getText().toString().trim().isEmpty()) {
            layoutTxtIndonesia.setError(getString(R.string.err_msg_indonesia));
            requestFocus(txtIndonesia);
            return false;
        } else {
            layoutTxtIndonesia.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateKaili() {
        if (txtKaili.getText().toString().trim().isEmpty()) {
            layoutTxtKaili.setError(getString(R.string.err_msg_kaili));
            requestFocus(txtKaili);
            return false;
        } else {
            layoutTxtKaili.setErrorEnabled(false);
        }

        return true;
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_indonesia:
                    validateIndonesia();
                    break;
                case R.id.input_kaili:
                    validateKaili();
                    break;
            }
        }


    }

    private OnFragmentInteractionListener mListener;

    public TambahKata() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tambah_kata, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        db.close();
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
