package com.bq.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bq.util.ReadImage;
import com.bq.weibo.R;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

public class WeiboAdapter extends BaseAdapter {
	private ArrayList<Status> weibos;
	private LayoutInflater inflater;

	public WeiboAdapter(Context context, StatusList statuses) {
		this.inflater = LayoutInflater.from(context);
		this.weibos = statuses.statusList;
	}

	@Override
	public int getCount() {
		return weibos.size();
	}

	@Override
	public Object getItem(int position) {
		return weibos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Status weibo = weibos.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.weibo_list, null);
			viewHolder = new ViewHolder();
			viewHolder.user = (TextView) convertView.findViewById(R.id.user);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			viewHolder.reposts = (TextView) convertView
					.findViewById(R.id.reposts);
			viewHolder.comments = (TextView) convertView
					.findViewById(R.id.comments);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageBitmap(ReadImage.getImage(weibo.user.profile_image_url));
		//viewHolder.icon.setImageBitmap(null);
		viewHolder.user.setText(weibo.user.name);
		viewHolder.date.setText(weibo.created_at);
		viewHolder.text.setText(weibo.text);
		viewHolder.reposts.setText("转发数:" + weibo.reposts_count);
		viewHolder.comments.setText("评论数:" + weibo.comments_count);
		return convertView;
	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView user;
		public TextView date;
		public TextView text;
		public TextView reposts;
		public TextView comments;
	}

}
