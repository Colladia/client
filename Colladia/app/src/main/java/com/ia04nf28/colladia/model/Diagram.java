package com.ia04nf28.colladia.model;

import android.content.Context;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableMap;

import com.ia04nf28.colladia.R;
import com.ia04nf28.colladia.model.Elements.CircleElement;
import com.ia04nf28.colladia.model.Elements.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mar on 21/05/2016.
 */
public class Diagram {
    /** All available elements */
    private ObservableMap<String,Element> listElement = new ObservableArrayMap();
    private static Map<String, String> typeElementClassNameMap = new HashMap<>();

    public Diagram(Context ctx) {
        typeElementClassNameMap.put(ctx.getString(R.string.circle), CircleElement.class.getSimpleName());
    }

    public static Map<String, String> getTypeElementClassNameMap() {
        return typeElementClassNameMap;
    }
}
