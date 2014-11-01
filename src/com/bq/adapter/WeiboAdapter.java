package com.bq.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bq.models.Status;
import com.bq.models.StatusList;
import com.bq.util.DateUtil;
import com.bq.util.ReadImage;
import com.bq.weibo.CommentActivity;
import com.bq.weibo.R;

public class WeiboAdapter extends BaseAdapter {
	private ArrayList<Status> weibos;
	private LayoutInflater inflater;
	private Context context;

	public WeiboAdapter(Context context, StatusList statuses) {
		this.context = context;
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
		final Status weibo = weibos.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.weibo_list, null);
			viewHolder = new ViewHolder();
			viewHolder.user = (TextView) convertView.findViewById(R.id.user);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.date = (TextView) convertView.findViewById(R.id.date);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
			viewHolder.layout = (LinearLayout) convertView
					.findViewById(R.id.sublayout);
			viewHolder.sub_Text = (TextView) convertView
					.findViewById(R.id.sub_content);
			viewHolder.sub_Pic = (ImageView) convertView
					.findViewById(R.id.sub_pic);
			viewHolder.reposts = (TextView) convertView
					.findViewById(R.id.reposts);
			viewHolder.comments = (TextView) convertView
					.findViewById(R.id.comments);
			viewHolder.attitudes = (TextView) convertView
					.findViewById(R.id.attitudes);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setImageBitmap(ReadImage
				.getImage(weibo.user.profile_image_url));
		viewHolder.user.setText(weibo.user.name);
		Date date = DateUtil.parse(weibo.created_at);

		DateFormat sdf = new SimpleDateFormat("mm:ss", Locale.US);
		viewHolder.date.setText(sdf.format(date));
		viewHolder.text.setText(weibo.text);
		Bitmap imageRes = ReadImage.getImage(weibo.bmiddle_pic);
		if (imageRes != null) {
			viewHolder.image.setImageBitmap(imageRes);
		} else {
			viewHolder.image.setImageBitmap(null);
		}
		if (weibo.retweeted_status != null) {
			viewHolder.layout.setVisibility(View.VISIBLE);
			viewHolder.sub_Text.setText("@" + weibo.retweeted_status.user.name
					+ ":" + weibo.retweeted_status.text);
			Bitmap imageSub = ReadImage
					.getImage(weibo.retweeted_status.bmiddle_pic);
			if (imageRes != null) {
				viewHolder.sub_Pic.setImageBitmap(imageSub);
				viewHolder.sub_Pic.setVisibility(View.VISIBLE);
			} else {
				viewHolder.image.setImageBitmap(null);
			}

		} else {
			viewHolder.layout.setVisibility(View.GONE);
		}
		viewHolder.reposts.setText("转发 " + weibo.reposts_count);
		viewHolder.comments.setText("评论 " + weibo.comments_count);
		viewHolder.attitudes.setText("点赞 " + weibo.attitudes_count);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.text:
				case R.id.image:
					Intent intent = new Intent(context, CommentActivity.class);
					intent.putExtra("text", weibo.text);
					intent.putExtra("id", weibo.id);
					context.startActivity(intent);
					break;
				default:
					break;
				}

			}

		});
		return convertView;
	}

	public void addItem(Status weibo) {
		weibos.add(weibo);
	}

	private static class ViewHolder {
		public ImageView icon;
		public TextView user;
		public TextView date;
		public TextView text;
		public ImageView image;
		public TextView reposts;
		public TextView comments;
		public TextView attitudes;
		public LinearLayout layout;
		public TextView sub_Text;
		public ImageView sub_Pic;
	}

}
