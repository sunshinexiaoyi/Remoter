package com.gospell.wildwolf.remoter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/**
 * Created by Administrator on 2017/5/31.
 */

public class programAdapter extends BaseAdapter{

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private Context mContext;
    public int curPosition = -1;

    private static class ViewHolder{
        private TextView Lcn;
        private TextView ProgramName;
        private TextView ServiceId;
    }
    public programAdapter(Context context,List<Map<String, Object>>  list)
    {
        mContext = context;
        data = list;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder myviewHolder = null;
        if (null == view)
        {
            myviewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.programlist,null);
            myviewHolder.Lcn = (TextView) view.findViewById(R.id.lcn_text);
            myviewHolder.ProgramName = (TextView) view.findViewById(R.id.programe_name_text);
            //myviewHolder.ServiceId = (TextView) view.findViewById(R.id.service_id_text);
            view.setTag(myviewHolder);
        }

        myviewHolder = (ViewHolder) view.getTag();
        Map<String, Object> lineItem = new HashMap<String, Object>();
        lineItem = data.get(position);
        myviewHolder.Lcn.setText(lineItem.get(mContext.getResources().getString(R.string.pro_lcn)).toString());
        myviewHolder.ProgramName.setText(lineItem.get(mContext.getResources().getString(R.string.pro_name)).toString());
        //myviewHolder.ServiceId.setText(lineItem.get(mContext.getResources().getString(R.string.pro_service_id)).toString());

        // 只有当更新的位置等于当前位置时，更改颜色
        if(curPosition == position){
           // myviewHolder.Lcn.setBackgroundColor(Color.rgb(35, 154, 237));
            view.setBackgroundColor(Color.rgb(35, 154, 237));

        } else {
            view.setBackgroundColor(Color.TRANSPARENT);

        }
        return view;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setnotifyDataSetChanged(List<Map<String, Object>>  list) {
        this.data =  new ArrayList<Map<String, Object>>();
        this.data = list;
        super.notifyDataSetChanged();
    }
}
