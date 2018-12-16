package com.cyf.team.activities;

import android.support.v7.widget.RecyclerView;

import com.cyf.team.entity.PersonalQualTemp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */

public abstract class AnimalAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private ArrayList<PersonalQualTemp> items = new ArrayList<PersonalQualTemp>();

    public AnimalAdapter() {
        setHasStableIds(true);
    }

    public void add(PersonalQualTemp object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, PersonalQualTemp object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends PersonalQualTemp> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }

    public void addAll(PersonalQualTemp... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(PersonalQualTemp object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public PersonalQualTemp getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
