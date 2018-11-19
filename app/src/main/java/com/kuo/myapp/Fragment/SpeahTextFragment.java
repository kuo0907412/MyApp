package com.kuo.myapp.Fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.kuo.myapp.MainActivity;
import com.kuo.myapp.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class SpeahTextFragment extends BaseFragment implements TextToSpeech.OnInitListener{

    private final String TAG = SpeahTextFragment.class.getSimpleName();
    private EditText mEditText;
    private String mArticle;
    private String string;
    private SpeechRecognizer mRecognizer;
    private TextToSpeech tts;
    private AlertDialog mAlertDialog;

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {}
        @Override
        public void onBeginningOfSpeech() {
            mAlertDialog.show();
        }
        @Override
        public void onRmsChanged(float v) {}
        @Override
        public void onBufferReceived(byte[] bytes) {}
        @Override
        public void onEndOfSpeech() {
            if (mAlertDialog.isShowing()) mAlertDialog.dismiss();
        }
        @Override
        public void onError(int i) {
            if ((i == SpeechRecognizer.ERROR_NO_MATCH) ||
                    (i == SpeechRecognizer.ERROR_SPEECH_TIMEOUT)) {
                startSpeechRecognition();
            }
        }
        @Override
        public void onResults(Bundle results) {
            ArrayList<String> values = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            if(values == null) return;
            if(values.get(0).equals("コピー")
                    || values.get(0).equals("copy")) {
                ClipboardManager clipboardManager =
                        (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                if (null == clipboardManager) {
                    return;
                }
                clipboardManager.setPrimaryClip(ClipData.newPlainText("text_data", mArticle));
                return;
            } else if(values.get(0).equals("検索")){
                Uri uri = Uri.parse("http://www.google.com/#q=" + mEditText.getText().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                startSpeechRecognition();
                return;
            } else if(values.get(0).startsWith("ライン") || values.get(0).startsWith("Line")
                    || values.get(0).startsWith("LINE")){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("line:///msg/text/" + mArticle));
                startActivity(intent);
                startSpeechRecognition();
                return;
            } else if(values.get(0).equals("グーグルマップ")
                    || values.get(0).equals("Google Map")
                    || values.get(0).equals("Google マップ")
                    || values.get(0).equals("Google Maps")){
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setClassName("com.google.android.apps.maps", "com.google.android.maps.driveabout.app.NavigationActivity");
                Uri uri = Uri.parse("google.navigation:///?ll=lat,lon&q=" + string);
                i.setData(uri);
                startActivity(i);
                startSpeechRecognition();
                return;
            } else if(values.get(0).endsWith("メール")
                    || values.get(0).endsWith("mail")
                    || values.get(0).endsWith("Mail")){
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, mArticle);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                startSpeechRecognition();
                return;
            } else if(values.get(0).endsWith("クリア")
                    || values.get(0).endsWith("clear")
                    || values.get(0).endsWith("Clear")
                    || values.get(0).endsWith("リセット")
                    || values.get(0).endsWith("reset")){
                mEditText.setText("");
                startSpeechRecognition();
                return;
            }
            TextDialogFragment textDialogFragment = TextDialogFragment.newInstance(values);
            textDialogFragment.show(getFragmentManager(),"dialog");
            textDialogFragment.setTextSelectListener(new TextDialogFragment.TextSelectListener() {
                @Override
                public void onSelectPosition(String selectedString) {
                    mArticle = mEditText.getText().toString() + selectedString;
                    mEditText.setText(mArticle);
                    mEditText.setSelection(mEditText.getText().length());
                }
            });
            startSpeechRecognition();
        }
        @Override
        public void onPartialResults(Bundle bundle) {
        }
        @Override
        public void onEvent(int i, Bundle bundle) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage("Listening．．．");
        mAlertDialog = b.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speachtext,container,false);
        mEditText = (EditText) rootView.findViewById(R.id.edit_text);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tts = new TextToSpeech(getActivity(), this);
        startSpeechRecognition();
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if("".equals(editable.toString())){
                    mEditText.setGravity(Gravity.CENTER);
                } else {
                    mEditText.setGravity(Gravity.CENTER_VERTICAL);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopSpeechRecognition();
        mRecognizer.destroy();
    }

    @Override
    public void onInit(int i) {
        if (TextToSpeech.SUCCESS == i) {
            Locale locale = Locale.JAPAN;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.e(TAG, "Error SetLocale");
            }
        } else {
            Log.e(TAG, "Error Init");
        }

    }

    private void startSpeechRecognition() {
        // Need to destroy a recognizer to consecutive recognition?
        if (mRecognizer != null) {
            mRecognizer.destroy();
        }
        // Create a recognizer.
        mRecognizer = SpeechRecognizer.createSpeechRecognizer((MainActivity)getActivity());
        mRecognizer.setRecognitionListener(mRecognitionListener);
        // Start recognition.
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.JAPAN.toString() );
        mRecognizer.startListening(intent);
    }

    private void stopSpeechRecognition() {
        mRecognizer.stopListening();
    }

}


