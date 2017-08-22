package com.ms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ms.entity.GoodsSizeInfo_spec;
import com.ms.ks.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/18.
 */

public class GoodsSize_specAdapter extends SectionedBaseAdapter {
    private Context mContext;
    private ArrayList<GoodsSizeInfo_spec> getmGoodsSizeInfo_speclist;

    public GoodsSize_specAdapter(Context mContext, ArrayList<GoodsSizeInfo_spec> getmGoodsSizeInfo_speclist) {
        this.mContext = mContext;
        this.getmGoodsSizeInfo_speclist = getmGoodsSizeInfo_speclist;
    }

    @Override
    public Object getItem(int section, int position) {
        return getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return getmGoodsSizeInfo_speclist.size();
    }

    @Override
    public int getCountForSection(int section) {
        return getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
        ItemViewHolder itemViewHolder=null;
        if(convertView==null){
            convertView=View.inflate(mContext, R.layout.item_goods_size_view,null);
            itemViewHolder=new ItemViewHolder();
            itemViewHolder.check_goods_size_spec= (CheckBox) convertView.findViewById(R.id.check_goods_size_spec);
            itemViewHolder.tv_goods_size_spec= (TextView) convertView.findViewById(R.id.tv_goods_size_spec);
            convertView.setTag(itemViewHolder);
        }else {
            itemViewHolder= (ItemViewHolder) convertView.getTag();
        }
        itemViewHolder.tv_goods_size_spec.setText(getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().get(position).getSpec_value());
        itemViewHolder.check_goods_size_spec.setChecked(getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().get(position).isCheck());
        final ItemViewHolder finalItemViewHolder = itemViewHolder;
        itemViewHolder.check_goods_size_spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalItemViewHolder.check_goods_size_spec.isChecked()){
                    getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().get(position).setCheck(true);
                }else {
                    getmGoodsSizeInfo_speclist.get(section).getmGoodsSizeInfo_spec_infolist().get(position).setCheck(false);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        HeaderViewHolder headerViewHolder=null;
        if(convertView==null){
            convertView=View.inflate(mContext,R.layout.header_item,null);
            headerViewHolder=new HeaderViewHolder();
            headerViewHolder.textItem= (TextView) convertView.findViewById(R.id.textItem);
            convertView.setTag(headerViewHolder);
        }else {
            headerViewHolder= (HeaderViewHolder) convertView.getTag();
        }
        headerViewHolder.textItem.setText(getmGoodsSizeInfo_speclist.get(section).getSpec_name());
        headerViewHolder.textItem.setTextSize(14);
        headerViewHolder.textItem.setTextColor(Color.parseColor("#ffff8905"));
        return convertView;
    }

    private class ItemViewHolder{
        CheckBox check_goods_size_spec;
        TextView tv_goods_size_spec;
    }
    private class HeaderViewHolder{
        TextView textItem;

    }
}
