package com.dreamfish.com.autocalc.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreamfish.com.autocalc.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MyTitleBar extends ConstraintLayout {

  private Button ivBack;
  private TextView tvTitle;
  private TextView tvMore;
  private Button ivMore;

  public MyTitleBar(Context context, AttributeSet attrs) {
    super(context, attrs);

    initView(context,attrs);
  }

  
  private void initView(final Context context, AttributeSet attributeSet) {
    View inflate = LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);
    ivBack = inflate.findViewById(R.id.btn_back);
    tvTitle = inflate.findViewById(R.id.text_title);
    tvMore = inflate.findViewById(R.id.text_more);
    ivMore = inflate.findViewById(R.id.btn_more);

    init(context,attributeSet);
  }

  
  public void init(Context context, AttributeSet attributeSet){
    TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MyTitleBar);
    String title = typedArray.getString(R.styleable.MyTitleBar_title);
    int leftIcon = typedArray.getResourceId(R.styleable.MyTitleBar_left_icon, R.drawable.ic_back);
    int rightIcon = typedArray.getResourceId(R.styleable.MyTitleBar_right_icon, R.drawable.ic_more);
    String rightText = typedArray.getString(R.styleable.MyTitleBar_right_text);
    int titleBarType = typedArray.getInt(R.styleable.MyTitleBar_titlebar_type, 10);

    
    tvTitle.setText(title);
    ivBack.setBackgroundResource(leftIcon);
    tvMore.setText(rightText);
    ivMore.setBackgroundResource(rightIcon);

    
    if (titleBarType == 10) {
      ivMore.setVisibility(View.GONE);
      tvMore.setVisibility(View.VISIBLE);
    } else if(titleBarType == 11) {
      tvMore.setVisibility(View.GONE);
      ivMore.setVisibility(View.VISIBLE);
    } else {
      tvMore.setVisibility(View.GONE);
      ivMore.setVisibility(View.VISIBLE);
    }
  }

  
  public void setLeftIconOnClickListener(OnClickListener l){
    ivBack.setOnClickListener(l);
  }

  
  public void setRightIconOnClickListener(OnClickListener l){
    ivBack.setOnClickListener(l);
  }

  
  public void setRightTextOnClickListener(OnClickListener l){
    ivBack.setOnClickListener(l);
  }

  public void setTitle(String s) {
    this.tvTitle.setText(s);
  }
  public void setTitle(CharSequence s) {
    this.tvTitle.setText(s);
  }
}