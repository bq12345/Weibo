package com.bq.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bq.models.Comment;
import com.bq.models.CommentList;
import com.bq.util.DateUtil;
import com.bq.weibo.R;

public class CommentAdapter extends BaseAdapter {
	private ArrayList<Comment> comments;
	private LayoutInflater inflater;

	public CommentAdapter(Context context, CommentList comments) {
		this.inflater = LayoutInflater.from(context);
		this.comments = comments.commentList;
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Comment comment = comments.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.comment_list, null);
			viewHolder = new ViewHolder();
			viewHolder.user = (TextView) convertView.findViewById(R.id.user);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.user.setText(comment.user.name);
		Date date = DateUtil.parse(comment.created_at);

		DateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.US);
		viewHolder.date.setText(sdf.format(date));
		viewHolder.text.setText(comment.text);
		return convertView;
	}

	public void addItem(Comment comment) {
		comments.add(comment);
	}

	private static class ViewHolder {
		public TextView user;
		public TextView date;
		public TextView text;
	}

}
