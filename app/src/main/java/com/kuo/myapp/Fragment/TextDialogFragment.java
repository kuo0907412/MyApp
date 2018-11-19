package com.kuo.myapp.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kuo.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class TextDialogFragment extends DialogFragment{

    TextSelectListener mListener;

    public static TextDialogFragment newInstance(ArrayList<String> arr) {
        TextDialogFragment frag = new TextDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("textArr",arr);
        frag.setArguments(args);
        return frag;
    }

    public void setTextSelectListener(TextSelectListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<String> itemList = getArguments().getStringArrayList("textArr");
        View view = View.inflate(getActivity(), R.layout.dialog_text_list , null);
        CustomAdapter adapter = new CustomAdapter(getActivity(), 0, itemList);
        ListView listView = (ListView)view.findViewById(R.id.text_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String)adapterView.getAdapter().getItem(i);
                if(mListener != null)
                    mListener.onSelectPosition(item);
                TextDialogFragment.this.dismiss();
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setTitle("一覧")
                .setView(view)
                .create();
    }
    private class CustomAdapter extends ArrayAdapter<String> {
        private LayoutInflater inflater;

        CustomAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, View v, final ViewGroup parent) {
            String item = getItem(position);
            if (null == v) {
                v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView intTextView = (TextView) v.findViewById(android.R.id.text1);
            intTextView.setText(item);
            return v;
        }
    }

    interface TextSelectListener{
        public void onSelectPosition(String selectedString);
    }
}
