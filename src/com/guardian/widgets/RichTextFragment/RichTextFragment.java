package com.guardian.widgets.RichTextFragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.*;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.QuoteSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RichTextFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "RichTextFragment";
    private EditText text;
    private TextView html;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rich_text_fragment, container);
        Button bold = (Button) root.findViewById(R.id.bold);
        bold.setOnClickListener(this);
        Button italic = (Button) root.findViewById(R.id.italic);
        italic.setOnClickListener(this);
        Button quote = (Button) root.findViewById(R.id.quote);
        quote.setOnClickListener(this);
        Button clear = (Button) root.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        html = (TextView) root.findViewById(R.id.html);
        text = (EditText) root.findViewById(R.id.text);
        text.getText().setSpan(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Log.v(TAG, editable.toString());
                html.setText(Html.toHtml(editable));
            }
        }, 0, 0, 0);
        return root;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bold){
            Log.d(TAG, "Bold button clicked");
            addStyleSpan(Typeface.BOLD);
        }else if(view.getId() == R.id.italic){
            Log.d(TAG, "Italic button clicked");
            addStyleSpan(Typeface.ITALIC);
        }else if(view.getId() == R.id.clear){
            Log.d(TAG, "Clear button clicked");
            clearStyles();
        }else if(view.getId() == R.id.quote){
            Log.d(TAG, "Quote button clicked");
            addBlockQuote();
        }

    }

    protected void clearStyles(){
        Editable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        MetricAffectingSpan[] styles = str.getSpans(start, end, MetricAffectingSpan.class);

        Log.d(TAG, "Found " + styles.length + " open spans");
        for(MetricAffectingSpan span : styles){
            //str.setFilters();
            int spanStart = str.getSpanStart(span);
            int spanEnd = str.getSpanEnd(span);
            str.removeSpan(span);
            str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        LeadingMarginSpan[] margins = str.getSpans(start, end, LeadingMarginSpan.class);
        for(LeadingMarginSpan span : margins){
            //str.setFilters();
            int spanStart = str.getSpanStart(span);
            int spanEnd = str.getSpanEnd(span);
            str.removeSpan(span);
            str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }
    protected void addStyleSpan(int typeFace){
        Spannable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        Log.v(TAG, "Selection start: " + start + ", Selection end: " + end);
        str.setSpan(new StyleSpan(typeFace), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
    protected void addBlockQuote(){
        Editable str = text.getText();
        str.append('\n');
        clearStyles();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        Log.v(TAG, "Selection start: " + start + ", Selection end: " + end);
        Activity activity = getActivity();
        if(activity == null) return;


        QuoteSpan quote = new QuoteSpan(activity.getResources().getColor(R.color.quote_text_colour));

        str.setSpan(quote, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }
}
