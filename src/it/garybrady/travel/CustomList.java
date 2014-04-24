package it.garybrady.travel;

/**
 * Created by Gary on 24/04/14.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] menuItem;
    private final Integer[] colourBlock;
    public CustomList(Activity context,
                      String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.menuItem = web;
        this.colourBlock = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.tvText);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.ivImg);
        txtTitle.setText(menuItem[position]);
        imageView.setBackgroundColor(colourBlock[position]);
        return rowView;
    }
}
