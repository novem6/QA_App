package jp.techacademy.rei.tanaka.qa_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoritesListAdapter extends BaseAdapter{
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<Question> mFavoritesArrayList;

    public FavoritesListAdapter(Context context) {
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mFavoritesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFavoritesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_questions, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.titleTextView);
        titleText.setText(mFavoritesArrayList.get(position).getTitle());

        TextView nameText = (TextView) convertView.findViewById(R.id.nameTextView);
        nameText.setText(mFavoritesArrayList.get(position).getName());

        TextView resText = (TextView) convertView.findViewById(R.id.resTextView);
        int resNum = mFavoritesArrayList.get(position).getAnswers().size();
        resText.setText(String.valueOf(resNum));

        byte[] bytes = mFavoritesArrayList.get(position).getImageBytes();
        if (bytes.length != 0) {
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            imageView.setImageBitmap(image);
        }

        return convertView;
    }

    public void setQuestionArrayList(ArrayList<Question> favoritesArrayList) {
        mFavoritesArrayList = favoritesArrayList;
    }
}