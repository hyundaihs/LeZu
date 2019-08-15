package com.cyf.team.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.ArrayList;
import java.util.List;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2019/8/9/009.
 */
public class PickerUtil {
    private static List<Integer> singleData = new ArrayList<>();

    public static void initChooseType(List<Integer> data) {
        singleData.clear();
        singleData.addAll(data);
    }

    public static void showChooseType(Context context, String title, OnOptionsSelectListener onOptionsSelectListener) {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(context, onOptionsSelectListener)
                .setTitleText(title)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK)
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(singleData);
        pvOptions.show();
    }
}
