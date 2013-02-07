package com.guardian.widgets.richtext;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.*;
import android.text.style.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.guardian.widgets.R;

public class RichTextFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, LinkDialogFragment.LinkPickListener {
    private static final String TAG = "richtext";
    private EditText text;
    private TextView html;
    private CheckBox bold;
    private CheckBox italic;
    private CheckBox quote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.rich_text_fragment, container);
        bold = (CheckBox) root.findViewById(R.id.bold);
        bold.setOnCheckedChangeListener(this);
        italic = (CheckBox) root.findViewById(R.id.italic);
        italic.setOnCheckedChangeListener(this);
        quote = (CheckBox) root.findViewById(R.id.quote);
        quote.setOnCheckedChangeListener(this);
        Button link = (Button) root.findViewById(R.id.link);
        link.setOnClickListener(this);
        html = (TextView) root.findViewById(R.id.html);
        text = (EditText) root.findViewById(R.id.text);
        text.getText().setSpan(this, 0, 0, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        text.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text) {
            Log.d(TAG, "EditText clicked");
            setButtonState();
        } else if (view.getId() == R.id.link) {
            Log.d(TAG, "Link button clicked");
            LinkDialogFragment fragment = new LinkDialogFragment();
            fragment.setLinkPicklistener(this);
            fragment.show(getFragmentManager(), LinkDialogFragment.TAG);

        }
    }

    private void setButtonState() {
        Editable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();

        StyleSpan[] styles = getStyleSpans();
        boolean boldChecked = false;
        boolean italicChecked = false;
        for (StyleSpan span : styles) {
            if (span.getStyle() == Typeface.BOLD) {
                boldChecked = true;
            }
            if (span.getStyle() == Typeface.ITALIC) {
                italicChecked = true;
            }
        }
        bold.setChecked(boldChecked);
        italic.setChecked(italicChecked);

        QuoteSpan[] quotes = text.getText().getSpans(start, end, QuoteSpan.class);
        quote.setChecked(quotes.length > 0);

    }

    protected StyleSpan[] getStyleSpans() {
        Editable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        return str.getSpans(start, end, StyleSpan.class);
    }

    protected void clearStyles() {
        Editable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        MetricAffectingSpan[] styles = str.getSpans(start, end, MetricAffectingSpan.class);

        Log.d(TAG, "Found " + styles.length + " open spans");
        for (MetricAffectingSpan span : styles) {
            //str.setFilters();
            int spanStart = str.getSpanStart(span);
            int spanEnd = str.getSpanEnd(span);
            str.removeSpan(span);
            str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        LeadingMarginSpan[] margins = str.getSpans(start, end, LeadingMarginSpan.class);
        for (LeadingMarginSpan span : margins) {
            //str.setFilters();
            int spanStart = str.getSpanStart(span);
            int spanEnd = str.getSpanEnd(span);
            str.removeSpan(span);
            str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

    protected void addStyleSpan(boolean checked, int typeFace) {
        Spannable str = text.getText();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        Log.v(TAG, "Selection start: " + start + ", Selection end: " + end);
        if (checked) {
            str.setSpan(new StyleSpan(typeFace), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            StyleSpan[] styles = str.getSpans(start, end, StyleSpan.class);
            for (StyleSpan span : styles) {
                if (span.getStyle() == typeFace) {
                    int spanStart = str.getSpanStart(span);
                    int spanEnd = str.getSpanEnd(span);
                    Log.v(TAG, "Span start: " + spanStart + ", Span end: " + spanEnd);
                    str.removeSpan(span);
                    if (spanEnd == start)
                        str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    protected void addBlockQuote(boolean checked) {
        Activity activity = getActivity();
        if (activity == null) return;

        int colour = activity.getResources().getColor(R.color.quote_text_colour);

        Editable str = text.getText();

        clearStyles();
        int start = text.getSelectionStart();
        int end = text.getSelectionEnd();
        Log.v(TAG, "Selection start: " + start + ", Selection end: " + end);

        if (checked) {
            if (str.length() > 0) {
                str.append('\n');
                start += 1;
                end += 1;
            }
            str.setSpan(new QuoteSpan(colour), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            QuoteSpan[] quotes = str.getSpans(start, end, QuoteSpan.class);
            for (QuoteSpan span : quotes) {

                int spanStart = str.getSpanStart(span);
                int spanEnd = str.getSpanEnd(span);
                Log.v(TAG, "Span start: " + spanStart + ", Span end: " + spanEnd);
                str.removeSpan(span);
                if (spanEnd == start)
                    str.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            str.append('\n');
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.getId() == R.id.bold) {
            Log.d(TAG, "Bold button clicked");
            addStyleSpan(checked, Typeface.BOLD);
        } else if (compoundButton.getId() == R.id.italic) {
            Log.d(TAG, "Italic button clicked");
            addStyleSpan(checked, Typeface.ITALIC);
        } else if (compoundButton.getId() == R.id.quote) {
            Log.d(TAG, "Quote button clicked");
            addBlockQuote(checked);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        Log.v(TAG, "beforeTextChanged " + charSequence.toString() + " start=" + start + ", count=" + count + ", after=" + after);

        if (charSequence.length() > start && charSequence.charAt(start) == ' ') {
            Log.d(TAG, "beforeTextChanged - Deleting back past start of word");
            Editable str = text.getText();
            StyleSpan[] spans = str.getSpans(start + 1, start + 1, StyleSpan.class);
            for (StyleSpan span : spans) {

                int spanStart = str.getSpanStart(span);
                int spanEnd = str.getSpanEnd(span);
                Log.v(TAG, "Span start: " + spanStart + ", Span end: " + spanEnd);
                str.removeSpan(span);
                //if(spanEnd == start)
                str.setSpan(span, spanStart + 1, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            }
            Log.d(TAG, "number of spans at start =" + spans.length);
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        //Log.v(TAG, "onTextChanged ");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.v(TAG, "afterTextChanged " + editable.toString());
        html.setText(Html.toHtml(editable));

        //mergeSpans();
        //setButtonState();
    }

    private void mergeSpans() {
        for (StyleSpan span : getStyleSpans()) {
            Log.v(TAG, "Span type = " + span.getStyle() + ", Span start: " + text.getText().getSpanStart(span) + ", Span end: " + text.getText().getSpanEnd(span));
        }

    }

    @Override
    public void onLinkPicked(String url) {
        URLSpan span = new URLSpan(url);
        Editable str = text.getText();
        int start = text.getSelectionStart();
        str.insert(start, url);
        text.getText().setSpan(span, start, start + url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public Spanned getText(){
        return text.getText();
    }

    public String getHtml(){
        return Html.toHtml(text.getText());
    }
}
