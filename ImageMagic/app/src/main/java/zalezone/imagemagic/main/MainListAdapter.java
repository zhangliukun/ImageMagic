package zalezone.imagemagic.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import zalezone.imagemagic.R;

/**
 * Created by zale on 16/1/14.
 */
public class MainListAdapter extends ArrayAdapter {

    Context mContext;
    int res;

    public MainListAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        res = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String text = (String) getItem(position);

        View view = View.inflate(mContext,res,null);
        TextView textView = (TextView) view.findViewById(R.id.function_item);
        textView.setText(text);

        return view;
    }
}
