package com.fiberlink.maas360.android.richtexteditor;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.fiberlink.maas360.android.richtexteditor.colorpicker.AmbilWarnaDialog;

/**
 * -----------------------------------------------------------------------------
 * "© Copyright IBM Corp 2016"
 * -----------------------------------------------------------------------------
 */

public class RichEditText extends RelativeLayout
{
    private Context mContext;
    private LayoutInflater mInflater;
    private Handler mHandler;
    private String mPreviewText;

    private RichWebView mEditor;
    private RichTextActions mActions;

    private ChangeListener mListener;

    private ImageButton mBoldButton;
    private ImageButton mItalicButton;
    private ImageButton mUnderlineButton;
    private ImageButton mStrikethroughButton;
    private ImageButton mTextColorButton;
    private ImageButton mTextBackgroundColorButton;

    private boolean mBoldEnabled;
    private boolean mBoldAllowed;
    private boolean mItalicEnabled;
    private boolean mItalicAllowed;
    private boolean mUnderlineEnabled;
    private boolean mUnderlineAllowed;
    private boolean mStrikethroughEnabled;
    private boolean mStrikethroughAllowed;
    private boolean mTextColorAllowed;
    private boolean mBackgroundColorAllowed;

    private int mSelectedTextColor = -1;
    private int mSelectedTextBackgroundColor = -1;

    public RichEditText(Context context)
    {
        super(context);
        setupView(context);
    }

    public RichEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setupView(context);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setupView(context);
    }

    @TargetApi (Build.VERSION_CODES.LOLLIPOP)
    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
    }

    public void setRichTextActionsView(RichTextActions actionsView)
    {
        mActions = actionsView;
        setupActions();
    }

    public void setPreviewText(String previewText)
    {
        mPreviewText = previewText;
    }

    public void setHtml(String html)
    {
        if (html == null) {
            return;
        }
        mEditor.setHtml(html);
    }

    public String getHtml()
    {
        return mEditor.getHtml();
    }

    public void setHint(String hint)
    {
        if (!TextUtils.isEmpty(hint) && mEditor != null) {
            mEditor.setPlaceholder(hint);
        }
    }

    public void addChangeListener(ChangeListener listener)
    {
        mListener = listener;
    }

    public void removeChangeListener()
    {
        mListener = null;
    }

    private void setupView(Context context)
    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.rich_edit_text, this, true);
        mHandler = new Handler();

        setupEditor();
    }

    private void setupEditor()
    {
        mEditor = (RichWebView) findViewById(R.id.editor);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(16, 16, 16, 16);

        mEditor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (!hasFocus) {
                    mActions.setVisibility(GONE);
                    blockAndDisableAllButtons();
                }
                else {
                    mActions.setVisibility(VISIBLE);
                }
            }
        });

        mEditor.setStateChangeListener(new RichWebView.OnStateChangeListener() {
            @Override
            public void onStateChanged(final String text, final List<RichWebView.Type> types,
                    final RichWebView.StateType stateType)
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        handleStateChange(text, types, stateType);
                    }
                });
            }
        });
    }

    private void handleStateChange(String text, List<RichWebView.Type> types, RichWebView.StateType stateType)
    {
        notifyChangeListener();

        mSelectedTextColor = -1;
        mSelectedTextBackgroundColor = -1;

        switch (stateType)
        {
        case ALLOW:
            if (types.contains(RichWebView.Type.BOLD)) {
                allowBoldButton();
            }
            else {
                blockBoldButton();
            }
            if (types.contains(RichWebView.Type.ITALIC)) {
                allowItalicButton();
            }
            else {
                blockItalicButton();
            }
            if (types.contains(RichWebView.Type.UNDERLINE)) {
                allowUnderlineButton();
            }
            else {
                blockUnderlineButton();
            }
            if (types.contains(RichWebView.Type.STRIKETHROUGH)) {
                allowStrikethroughButton();
            }
            else {
                blockStrikethroughButton();
            }
            if (types.contains(RichWebView.Type.FORECOLOR)) {
                allowTextColorButton();
            }
            else {
                blockTextColorButton();
            }
            if (types.contains(RichWebView.Type.HILITECOLOR)) {
                allowBackgroundColorButton();
            }
            else {
                blockTextBackgroundColorButton();
            }
            break;
        case ENABLE:
            if (types.contains(RichWebView.Type.BOLD)) {
                enableBoldButton();
            }
            else {
                disableBoldButton();
            }
            if (types.contains(RichWebView.Type.ITALIC)) {
                enableItalicButton();
            }
            else {
                disableItalicButton();
            }
            if (types.contains(RichWebView.Type.UNDERLINE)) {
                enableUnderlineButton();
            }
            else {
                disableUnderlineButton();
            }
            if (types.contains(RichWebView.Type.STRIKETHROUGH)) {
                enableStrikethroughButton();
            }
            else {
                disableStrikethroughButton();
            }
            break;
        }
    }

    private void setupActions()
    {
        mActions.setVisibility(GONE);

        setupBoldButton();
        setupItalicButton();
        setupUnderlineButton();
        setupStrikethroughButton();
        setupTextColorButton();
        setupTextBackgroundColorButton();
    }

    private void notifyChangeListener()
    {
        if (mListener != null) {
            mListener.onChange();
        }
    }

    private void blockAndDisableAllButtons()
    {
        blockBoldButton();
        disableBoldButton();
        blockItalicButton();
        disableItalicButton();
        blockUnderlineButton();
        disableUnderlineButton();
        blockStrikethroughButton();
        disableStrikethroughButton();
        blockTextColorButton();
        blockTextBackgroundColorButton();
    }

    private void setupBoldButton()
    {
        mBoldButton = (ImageButton) mActions.findViewById(R.id.action_bold);
        blockBoldButton();
        disableBoldButton();
        mBoldButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleBoldButton();
                mEditor.setBold();
            }
        });
    }

    private void setupItalicButton()
    {
        mItalicButton = (ImageButton) mActions.findViewById(R.id.action_italic);
        blockItalicButton();
        disableItalicButton();
        mItalicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleItalicButton();
                mEditor.setItalic();
            }
        });
    }

    private void setupUnderlineButton()
    {
        mUnderlineButton = (ImageButton) mActions.findViewById(R.id.action_underline);
        blockUnderlineButton();
        disableUnderlineButton();
        mUnderlineButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleUnderlineButton();
                mEditor.setUnderline();
            }
        });
    }

    private void setupStrikethroughButton()
    {
        mStrikethroughButton = (ImageButton) mActions.findViewById(R.id.action_strikethrough);
        blockStrikethroughButton();
        disableStrikethroughButton();
        mStrikethroughButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                toggleStrikethroughButton();
                mEditor.setStrikethrough();
            }
        });
    }

    private void setupTextColorButton()
    {
        mTextColorButton = (ImageButton) mActions.findViewById(R.id.action_txt_color);
        blockTextColorButton();
        mTextColorButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if (mTextColorAllowed) {
                    showTextColorChooser();
                }
            }
        });
    }

    private void setupTextBackgroundColorButton()
    {
        mTextBackgroundColorButton = (ImageButton) mActions.findViewById(R.id.action_txt_bg_color);
        blockTextBackgroundColorButton();
        mTextBackgroundColorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mBackgroundColorAllowed) {
                    showTextBackgroundColorChooser();
                }
            }
        });
    }

    private void showTextColorChooser()
    {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), Color.BLACK,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog)
                    {
                        // Dialog cancelled
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color)
                    {
                        mSelectedTextColor = color;
                        if (mSelectedTextBackgroundColor == -1) {
                            mEditor.setTextColor(mSelectedTextColor);
                        }
                        else {
                            mEditor.setTextAndBackgroundColor(mSelectedTextColor, mSelectedTextBackgroundColor);
                        }
                    }
                }, AmbilWarnaDialog.ColorType.TEXT, mPreviewText);
        dialog.show();
    }

    private void showTextBackgroundColorChooser()
    {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(getContext(), Color.WHITE,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog)
                    {
                        // Dialog cancelled
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color)
                    {
                        mSelectedTextBackgroundColor = color;
                        if (mSelectedTextColor == -1) {
                            mEditor.setTextBackgroundColor(mSelectedTextBackgroundColor);
                        }
                        else {
                            mEditor.setTextAndBackgroundColor(mSelectedTextColor, mSelectedTextBackgroundColor);
                        }
                    }
                }, AmbilWarnaDialog.ColorType.BACKGROUND, mPreviewText);
        dialog.show();
    }

    private void allowBoldButton()
    {
        mBoldAllowed = true;
        mBoldButton.setImageDrawable(getResources().getDrawable(R.drawable.bold_48));
    }

    private void blockBoldButton()
    {
        mBoldAllowed = false;
        mBoldButton.setImageDrawable(getResources().getDrawable(R.drawable.bold_grey_48));
    }

    private void toggleBoldButton()
    {
        if (mBoldAllowed) {
            if (mBoldEnabled) {
                disableBoldButton();
            }
            else {
                enableBoldButton();
            }
        }
    }

    private void enableBoldButton()
    {
        mBoldEnabled = true;
        if (mBoldAllowed) {
            mBoldButton.setBackgroundColor(getResources().getColor(R.color.colorEnabledAndAllowedButtonBackground));
        }
        else {
            mBoldButton.setBackgroundColor(getResources().getColor(R.color.colorEnabledAndNotAllowedButtonBackground));
        }
    }

    private void disableBoldButton()
    {
        mBoldEnabled = false;
        mBoldButton.setBackgroundColor(getResources().getColor(R.color.colorDisabledButtonBackground));
    }

    private void allowItalicButton()
    {
        mItalicAllowed = true;
        mItalicButton.setImageDrawable(getResources().getDrawable(R.drawable.italic_48));
    }

    private void blockItalicButton()
    {
        mItalicAllowed = false;
        mItalicButton.setImageDrawable(getResources().getDrawable(R.drawable.italic_grey_48));
    }

    private void toggleItalicButton()
    {
        if (mItalicAllowed) {
            if (mItalicEnabled) {
                disableItalicButton();
            }
            else {
                enableItalicButton();
            }
        }
    }

    private void enableItalicButton()
    {
        mItalicEnabled = true;
        if (mItalicAllowed) {
            mItalicButton.setBackgroundColor(getResources().getColor(R.color.colorEnabledAndAllowedButtonBackground));
        }
        else {
            mItalicButton
                    .setBackgroundColor(getResources().getColor(R.color.colorEnabledAndNotAllowedButtonBackground));
        }
    }

    private void disableItalicButton()
    {
        mItalicEnabled = false;
        mItalicButton.setBackgroundColor(getResources().getColor(R.color.colorDisabledButtonBackground));
    }

    private void allowUnderlineButton()
    {
        mUnderlineAllowed = true;
        mUnderlineButton.setImageDrawable(getResources().getDrawable(R.drawable.underline_48));
    }

    private void blockUnderlineButton()
    {
        mUnderlineAllowed = false;
        mUnderlineButton.setImageDrawable(getResources().getDrawable(R.drawable.underline_grey_48));
    }

    private void toggleUnderlineButton()
    {
        if (mUnderlineAllowed) {
            if (mUnderlineEnabled) {
                disableUnderlineButton();
            }
            else {
                enableUnderlineButton();
            }
        }
    }

    private void enableUnderlineButton()
    {
        mUnderlineEnabled = true;
        if (mUnderlineAllowed) {
            mUnderlineButton
                    .setBackgroundColor(getResources().getColor(R.color.colorEnabledAndAllowedButtonBackground));
        }
        else {
            mUnderlineButton.setBackgroundColor(getResources().getColor(
                    R.color.colorEnabledAndNotAllowedButtonBackground));
        }
    }

    private void disableUnderlineButton()
    {
        mUnderlineEnabled = false;
        mUnderlineButton.setBackgroundColor(getResources().getColor(R.color.colorDisabledButtonBackground));
    }

    private void allowStrikethroughButton()
    {
        mStrikethroughAllowed = true;
        mStrikethroughButton.setImageDrawable(getResources().getDrawable(R.drawable.strikethrough_48));
    }

    private void blockStrikethroughButton()
    {
        mStrikethroughAllowed = false;
        mStrikethroughButton.setImageDrawable(getResources().getDrawable(R.drawable.strikethrough_grey_48));
    }

    private void toggleStrikethroughButton()
    {
        if (mStrikethroughAllowed) {
            if (mStrikethroughEnabled) {
                disableStrikethroughButton();
            }
            else {
                enableStrikethroughButton();
            }
        }
    }

    private void enableStrikethroughButton()
    {
        mStrikethroughEnabled = true;
        if (mStrikethroughAllowed) {
            mStrikethroughButton.setBackgroundColor(getResources().getColor(
                    R.color.colorEnabledAndAllowedButtonBackground));
        }
        else {
            mStrikethroughButton.setBackgroundColor(getResources().getColor(
                    R.color.colorEnabledAndNotAllowedButtonBackground));
        }
    }

    private void disableStrikethroughButton()
    {
        mStrikethroughEnabled = false;
        mStrikethroughButton.setBackgroundColor(getResources().getColor(R.color.colorDisabledButtonBackground));
    }

    private void allowTextColorButton()
    {
        mTextColorAllowed = true;
        mTextColorButton.setImageDrawable(getResources().getDrawable(R.drawable.fore_color_48));
    }

    private void blockTextColorButton()
    {
        mTextColorAllowed = false;
        mTextColorButton.setImageDrawable(getResources().getDrawable(R.drawable.fore_color_grey_48));
    }

    private void allowBackgroundColorButton()
    {
        mBackgroundColorAllowed = true;
        mTextBackgroundColorButton.setImageDrawable(getResources().getDrawable(R.drawable.hilite_color_48));
    }

    private void blockTextBackgroundColorButton()
    {
        mBackgroundColorAllowed = false;
        mTextBackgroundColorButton.setImageDrawable(getResources().getDrawable(R.drawable.hilite_color_grey_48));
    }

    public interface ChangeListener
    {
        void onChange();
    }
}
