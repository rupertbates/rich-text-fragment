package com.guardian.widgets.richtext;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.guardian.widgets.R;

class LinkDialogFragment extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "LinkDialogFragment";
    private TextView linkText;
    private LinkPickListener listener;

    public interface LinkPickListener {
        public void onLinkPicked(String url);
    }

    public void setLinkPicklistener(LinkPickListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Insert a link");
        View root = inflater.inflate(R.layout.link_fragment, container, false);
        Button ok = (Button) root.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        Button cancel = (Button) root.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        linkText = (TextView) root.findViewById(R.id.link);
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ok && listener != null) {
            listener.onLinkPicked(linkText.getText().toString());
        }
        dismiss();
    }
}
