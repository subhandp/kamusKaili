package com.uya.drawyerlayout;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class dashboard extends Fragment {

    private SQLiteDatabase db = null;
    private Cursor kamusCursor = null;
    private EditText EditTextIndonesia;
    private TextView txtKaili;
    private Button btnTerjemahkan;
    private ImageButton btnSpeechToText;
    private ImageButton btnTextToSpeech;
    private ImageButton btnImageClear;
    private TextToSpeech textToSpeech;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private DatabaseHelper datakamus = null;

    public static int binarySearch(String[][] a, String x) {
        int low = 0;
        int high = a.length - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;
            String kataDb = a[mid][1].toLowerCase();
            String cari = x.toLowerCase();
            if (kataDb.compareTo(cari) < 0) {
                low = mid + 1;
            } else if (kataDb.compareTo(cari) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;
    }

    public void getTerjemahan(View view){
        String bhskaili = "";
        String indonesiaInput = EditTextIndonesia.getText().toString();


        kamusCursor = db.rawQuery("SELECT * FROM kamus", null);
        String[][] array = new String[kamusCursor.getCount()][3]; // Dynamic string array
        int i = 0;
        if (kamusCursor.moveToFirst()){
            for (; !kamusCursor.isAfterLast(); kamusCursor.moveToNext()){
                array[i][0] = kamusCursor.getString(0);
                array[i][1] = kamusCursor.getString(1);
                array[i][2] = kamusCursor.getString(2);
                i++;
            }

            indonesiaInput = indonesiaInput.toLowerCase();
            int row = binarySearch(array, indonesiaInput);
            if(row != -1){
                bhskaili = array[row][2];
                txtKaili.setVisibility(view.VISIBLE);
                btnTextToSpeech.setVisibility(view.VISIBLE);
            }
            else{
                txtKaili.setVisibility(view.INVISIBLE);
                btnTextToSpeech.setVisibility(view.INVISIBLE);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Data Tidak Ada.");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }

        }
        else{
            Toast.makeText(getActivity().getBaseContext(), "Data Tidak Ada",
                    Toast.LENGTH_SHORT).show();
        }


        txtKaili.setText(bhskaili);
    }


    // Untuk menampilkan Google speech input dialog
    public void tanyaInputSuara() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, new Locale("id","ID"));
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hei Bicara sesuatu ");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datakamus = new DatabaseHelper(getActivity(), getActivity().getBaseContext());
        db = datakamus.getWritableDatabase();
        EditTextIndonesia = (EditText) getActivity().findViewById(R.id.EditIndonsia);
        txtKaili = (TextView) getActivity().findViewById(R.id.txtKaili);
        btnTerjemahkan = (Button) view.findViewById(R.id.btnTerjemah);
        btnSpeechToText = (ImageButton) view.findViewById(R.id.imageBtnSpeechText);
        btnTextToSpeech = (ImageButton) view.findViewById(R.id.imageBtnTextSpeech);
        btnImageClear = (ImageButton) view.findViewById(R.id.imageClear);

        txtKaili.setVisibility(view.INVISIBLE);
        btnTextToSpeech.setVisibility(view.INVISIBLE);
        btnImageClear.setVisibility(view.INVISIBLE);

        EditTextIndonesia.addTextChangedListener(new dashboard.MyTextWatcher(EditTextIndonesia));

        btnTerjemahkan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getTerjemahan(v);
            }
        });

        btnSpeechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tanyaInputSuara();
            }
        });

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                //method untuk mendeteksi suara dari text

                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("id","ID"));
                }
            }
        });

        btnTextToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = txtKaili.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null,null);

                } else {
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        btnImageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTextIndonesia.setText("");
                txtKaili.setVisibility(view.INVISIBLE);
                btnTextToSpeech.setVisibility(view.INVISIBLE);
                btnImageClear.setVisibility(view.INVISIBLE);

            }
        });


    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length() > 0){
                btnImageClear.setVisibility(view.VISIBLE);
            }
            else{
                btnImageClear.setVisibility(view.INVISIBLE);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getActivity(), "Hasil suara ditampilkan", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditTextIndonesia.setText(result.get(0));
                    getTerjemahan(getView());
                }
                break;
            }

        }
    }

    public dashboard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dashboard.
     */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static dashboard newInstance(String param1, String param2) {
        dashboard fragment = new dashboard();
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
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
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
        try{
            kamusCursor.close();
            db.close();
        }catch (Exception e){}
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
