package com.dreamfish.com.autocalc.fragment;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamfish.com.autocalc.R;
import com.dreamfish.com.autocalc.config.ConstConfig;
import com.dreamfish.com.autocalc.core.AutoCalc;
import com.dreamfish.com.autocalc.item.converter.ConverterData;
import com.dreamfish.com.autocalc.item.converter.ConverterDataGroup;
import com.dreamfish.com.autocalc.item.converter.ConverterItem;
import com.dreamfish.com.autocalc.item.ConvertsListItem;
import com.dreamfish.com.autocalc.item.FunctionsListItem;
import com.dreamfish.com.autocalc.item.adapter.ConvertsListAdapter;
import com.dreamfish.com.autocalc.item.adapter.FunctionsListAdapter;
import com.dreamfish.com.autocalc.utils.AlertDialogTool;
import com.dreamfish.com.autocalc.utils.DateUtils;
import com.dreamfish.com.autocalc.utils.PixelTool;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import static android.content.Context.VIBRATOR_SERVICE;

public class ConverterFragment extends Fragment {

  public static ConverterFragment newInstance(){
    return new ConverterFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_converter, null);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

    initResources();
    initUnitData();
    initControls(view);
    initChooseDialog();

    layout_root = view.findViewById(R.id.layout_root);
    layout_root.postDelayed(() -> {

      loadSettings();

      initLayout(view);
      setConverter(0);
      setCurrentInput(0);
      updateAllConverter();
    }, 200);

  }

  private void vibratorVibrate() {
    if(useTouchVibrator) vibrator.vibrate(30);
  }

  private AutoCalc autoCalc = new AutoCalc();
  private Vibrator vibrator;

  private Resources resources;
  private int unit_text_orange;
  private int convert_unit_value;
  private String text_error;
  private String text_out_of_range;

  private LinearLayout layout_root;

  private View view_cv_two;
  private View view_cv_one;
  private TextView text_input_one;
  private TextView text_input_two;
  private Button btn_pad_number_negative;
  private Button btn_unit_choose_one;
  private Button btn_unit_choose_two;

  private TextView text_err;
  private View view_err;
  private Button btn_retry;

  private ConverterItem currentInputItem = null;
  private List<ConverterItem> currentInputs = new ArrayList<>();
  private int currentSetUnitItemIndex = 0;
  private int currentConverter = 0;

  private void setConverterUnit(ConverterItem converterItem, int choosedIndex) {
    converterItem.updateUnitData(currentConvertGroup.getGroup().get(choosedIndex));
  }
  private void setConverter(int converter) {
    currentConverter = converter;

    if(onModeChangedListener != null)
      onModeChangedListener.onModeChanged(converts_texts[converter]);

    currentConvertGroup = convertData.get(converter);
    if(currentConvertGroup.getGroup().size() > 2) {
      btn_unit_choose_one.setEnabled(true);
      btn_unit_choose_two.setEnabled(true);
    }else {
      btn_unit_choose_one.setEnabled(false);
      btn_unit_choose_two.setEnabled(false);
    }

    buildSelectUnitDialog();
    hideErr();

    for (int i1 = 0; i1 < currentInputs.size() && i1 < ConverterDataGroup.MAX_ONEPAGE_CONVERTER_COUNT; i1++) {
      ConverterItem i = currentInputs.get(i1);
      setConverterUnit(i, currentConvertGroup.getDefalutIndex()[i1]);
    }

    setCurrentInput(0);
    currentInputs.get(0).clearText();
    updateAllConverter();
  }
  private void setCurrentInput(int currentInput) {

    ConverterItem inputItem = currentInputs.get(currentInput);
    if(inputItem.getCanSelect()) {
      currentInputItem = inputItem;
      btn_pad_number_negative.setVisibility(inputItem.getCanBeNegative() ? View.VISIBLE : View.GONE);
      switch (currentInput) {
        case 0:
          text_input_two.setTextColor(convert_unit_value);
          text_input_one.setTextColor(unit_text_orange);
          break;
        case 1:
          text_input_two.setTextColor(unit_text_orange);
          text_input_one.setTextColor(convert_unit_value);
          break;
      }
    }
  }

  private void updateAllConverter() {
    BigDecimal benchmark;
    try {
      benchmark = currentInputItem.calculate();
      for (ConverterItem i: currentInputs) {
        if(i != currentInputItem) {
          i.fromBenchmark(benchmark);
          i.updateToView(true);
        }else {
          if(i.isValueOverflow()) i.updateToView(false, text_out_of_range);
          else i.updateToView(false);
        }
      }
    } catch (Exception e) {
      for (ConverterItem i: currentInputs) {
        if(i == currentInputItem) i.updateToView(false, text_error + " " +  e.getMessage());
        else i.forceUpdateToView( text_error + " " +  e.getMessage());
      }
    }

  }

  private void inputText(String s) {
    currentInputItem.writeText(s);
    updateAllConverter();
    vibratorVibrate();
  }
  private void delText() {
    currentInputItem.delText();
    updateAllConverter();
    vibratorVibrate();
  }
  private void clearText() {
    currentInputItem.clearText();
    updateAllConverter();
    vibratorVibrate();
  }

  private void initResources() {
    resources = getResources();
    unit_text_orange = resources.getColor(R.color.unit_text_orange, null);
    convert_unit_value = resources.getColor(R.color.convert_unit_value, null);
    text_error = resources.getString(R.string.text_error);
    text_out_of_range = resources.getString(R.string.text_out_of_range);
  }
  private void initControls(View view) {

    text_input_one = view.findViewById(R.id.text_input_one);
    text_input_two = view.findViewById(R.id.text_input_two);

    btn_unit_choose_one = view.findViewById(R.id.btn_unit_choose_one);
    btn_unit_choose_two = view.findViewById(R.id.btn_unit_choose_two);

    currentInputs.add(new ConverterItem(autoCalc, text_input_one, view.findViewById(R.id.text_unit_one), btn_unit_choose_one));
    currentInputs.add(new ConverterItem(autoCalc, text_input_two, view.findViewById(R.id.text_unit_two), btn_unit_choose_two));

    View view_input_one = view.findViewById(R.id.view_input_one);
    View view_input_two = view.findViewById(R.id.view_input_two);
    view_cv_one = view.findViewById(R.id.view_cv_one);
    view_cv_two = view.findViewById(R.id.view_cv_two);

    view_input_one.setOnClickListener(v -> setCurrentInput(0));
    view_input_two.setOnClickListener(v -> setCurrentInput(1));
    text_input_one.setOnClickListener(v -> setCurrentInput(0));
    text_input_two.setOnClickListener(v -> setCurrentInput(1));

    view.findViewById(R.id.btn_pad_ac).setOnClickListener(v -> clearText());
    view.findViewById(R.id.btn_pad_del).setOnClickListener(v -> delText());

    btn_pad_number_negative = view.findViewById(R.id.btn_pad_number_negative);

    btn_unit_choose_one.setOnClickListener(v -> {
      currentSetUnitItemIndex = 0;
      chooseUnitDialog.show();
    });
    btn_unit_choose_two.setOnClickListener(v -> {
      currentSetUnitItemIndex = 1;
      chooseUnitDialog.show();
    });

    text_err = view.findViewById(R.id.text_err);
    view_err = view.findViewById(R.id.view_err);
    btn_retry = view.findViewById(R.id.btn_retry);
  }
  private void initLayout(View view) {

    LinearLayout view_pad = view.findViewById(R.id.view_pad);
    LinearLayout view_pad_right = view.findViewById(R.id.view_pad_right);

    layout_root.measure(0,0);

    int width = layout_root.getWidth();
    int height = layout_root.getHeight();

    int pad_height = (int)((double)height * 0.53);

    int btn_width = width / 4;
    int btn_height = pad_height / 4;

    view_pad.getLayoutParams().height = pad_height;

    TableLayout view_pad_left = view.findViewById(R.id.view_pad_left);
    view_pad_left.getLayoutParams().width = btn_width * 3;

    for (int i = 0; i < view_pad_left.getChildCount(); i++) {
      TableRow row = (TableRow) view_pad_left.getChildAt(i);
      row.getLayoutParams().height = btn_height;
      for (int j = 0; j < row.getChildCount(); j++) {
        Button btn = (Button) row.getChildAt(j);
        btn.getLayoutParams().width = btn_width;
        btn.getLayoutParams().height = btn_height;
        final String text = btn.getText().toString();
        if(!text.equals("+/-")) btn.setOnClickListener(v -> inputText(text));
        else btn.setOnClickListener(v -> {
          currentInputItem.negate();
          updateAllConverter();
        });
      }
    }

    view_pad_right.getLayoutParams().width = btn_width;
    for (int i = 0; i < view_pad_right.getChildCount(); i++) {
      Button btn = (Button) view_pad_right.getChildAt(i);
      btn.getLayoutParams().width = btn_width;
      btn.getLayoutParams().height = btn_height * 2;
    }

    view_cv_one.getLayoutParams().height = (height - pad_height) / 2;
    view_cv_two.getLayoutParams().height = (height - pad_height) / 2;

  }

  private List<ConverterDataGroup> convertData = new ArrayList<>();
  private ConverterDataGroup currentConvertGroup = null;
  private String[] converts_texts;
  private TypedArray converts_icons;

  private void initUnitData() {
    converts_texts = resources.getStringArray(R.array.converts_texts);
    converts_icons = resources.obtainTypedArray(R.array.converts_icons);

    try {
      InputStream is = resources.getAssets().open("unit_data.xml");
      XmlPullParser parser = Xml.newPullParser();
      parser.setInput(is, "utf-8");
      int eventType = parser.getEventType();

      ConverterDataGroup currentGroup = null;

      while (eventType != XmlPullParser.END_DOCUMENT) {
        switch (eventType) {
          case XmlPullParser.START_TAG:
            String name = parser.getName();
            if (name.equals("group")) {
              currentGroup = new ConverterDataGroup();
              currentGroup.setName(parser.getAttributeValue(null, "name"));
              String def = parser.getAttributeValue(null, "default");
              if(def != null) {
                String[] defs = def.split(",");
                int[] idefs = new int[defs.length];
                for (int i = 0; i < defs.length; i++) idefs[i] = Integer.parseInt(defs[i]);
                currentGroup.setDefalutIndex(idefs);
              }
            } else if (name.equals("unit")) {
              ConverterData data = new ConverterData();
              data.unitName = parser.getAttributeValue(null, "name");
              data.unitNameShort = parser.getAttributeValue(null, "short");
              data.unitBase = Double.parseDouble(parser.getAttributeValue(null, "base"));
              data.isTitle = Boolean.parseBoolean(parser.getAttributeValue(null, "isTitle"));
              currentGroup.getGroup().add(data);
            }
            break;
          case XmlPullParser.END_TAG:
            if (parser.getName().equals("group")) {
              convertData.add(currentGroup);
            }
            break;
        }
        eventType = parser.next();
      }
    } catch (IOException | XmlPullParserException e) {
      e.printStackTrace();
    }
  }

  private AlertDialog chooseConvertDialog = null;
  private List<ConvertsListItem> convertsListItems = new ArrayList<>();
  private ConvertsListAdapter convertsListAdapter;

  private void initChooseDialog() {
    convertsListAdapter = new ConvertsListAdapter(getContext(), R.layout.item_convert, convertsListItems);
    for (int i = 0; i < converts_texts.length; i++) {
      convertsListItems.add(new ConvertsListItem(converts_texts[i], converts_icons.getDrawable(i)));
    }

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View v = inflater.inflate(R.layout.dialog_convert_list, null);
    chooseConvertDialog = AlertDialogTool.buildCustomBottomPopupDialog(getContext(), v);

    ListView list_all_converts = v.findViewById(R.id.list_all_converts);
    list_all_converts.setAdapter(convertsListAdapter);
    list_all_converts.setOnItemClickListener((parent, view, position, id) -> {
      setConverter(position);
      chooseConvertDialog.dismiss();
    });

    v.findViewById(R.id.btn_cancel).setOnClickListener(view -> chooseConvertDialog.dismiss());
  }

  public void showChooseConvertDialog() {
    chooseConvertDialog.show();
  }

  private OnModeChangedListener onModeChangedListener;
  public void setOnModeChangedListener(OnModeChangedListener onModeChangedListener) {
    this.onModeChangedListener = onModeChangedListener;
  }
  public interface OnModeChangedListener {
    void onModeChanged(String mode);
  }

  private void showErr(String err, boolean showRetry, View.OnClickListener retryClick) {
    text_err.setText(err);
    view_err.setVisibility(View.VISIBLE);
    btn_retry.setVisibility(showRetry ? View.VISIBLE : View.GONE);
    btn_retry.setOnClickListener(retryClick);
    view_cv_one.setVisibility(View.GONE);
    view_cv_two.setVisibility(View.GONE);
  }
  private void hideErr() {
    view_err.setVisibility(View.GONE);
    view_cv_one.setVisibility(View.VISIBLE);
    view_cv_two.setVisibility(View.VISIBLE);
  }

  private AlertDialog chooseUnitDialog = null;
  private List<FunctionsListItem> functionsListItems = new ArrayList<>();
  private FunctionsListAdapter functionsListAdapter;

  private void buildSelectUnitDialog() {

      functionsListItems.clear();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      View v = inflater.inflate(R.layout.dialog_funs_list, null);
      functionsListAdapter = new FunctionsListAdapter(getContext(), R.layout.item_function, functionsListItems);
      chooseUnitDialog = AlertDialogTool.buildCustomBottomPopupDialog(getContext(), v);

      ListView list_all_functions = v.findViewById(R.id.list_all_functions);

      for (ConverterData value : currentConvertGroup.getGroup()) {
        if(value.isTitle) functionsListItems.add(new FunctionsListItem(value.unitName));
        else functionsListItems.add(new FunctionsListItem(value.unitName, value.unitNameShort));
      }

      list_all_functions.setAdapter(functionsListAdapter);
      list_all_functions.setOnItemClickListener((parent, view, position, id) -> {
        ConverterData data = currentConvertGroup.getGroup().get(position);
        if(!data.isTitle) {
          setConverterUnit(currentInputs.get(currentSetUnitItemIndex), position);
          chooseUnitDialog.dismiss();
          updateAllConverter();
        }
      });

      functionsListAdapter.notifyDataSetChanged();

      ((TextView) v.findViewById(R.id.text_title)).setText(resources.getString(R.string.text_choose_unit));
      v.findViewById(R.id.text_sub_title).setVisibility(View.GONE);
      v.findViewById(R.id.btn_cancel).setOnClickListener(view -> chooseUnitDialog.dismiss());

  }

  private boolean useTouchVibrator = true;

  private void loadSettings() {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    autoCalc.setRecordStep(prefs.getBoolean("calc_record_step", false));
    autoCalc.setNumberScale(prefs.getInt("calc_computation_accuracy", 8));
    autoCalc.setUseDegree(prefs.getBoolean("calc_use_deg", true));
    autoCalc.setAutoScientificNotation(prefs.getBoolean("calc_auto_scientific_notation", true));
    autoCalc.setScientificNotationMax(prefs.getInt("calc_scientific_notation_max", 100000));
    useTouchVibrator = prefs.getBoolean("calc_use_vibrator", true);
  }
  public void updateSettings() {
    loadSettings();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    converts_icons.recycle();
  }

}
