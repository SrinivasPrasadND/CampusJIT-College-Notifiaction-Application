package com.example.srinivasprasad.campus_jit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<ProfileRecyclerAdapter.ViewHolder> {

    private List<BlogPost> pBlog_list;
    private Context context;

    public ProfileRecyclerAdapter(List<BlogPost> pBlog_list){
        this.pBlog_list=pBlog_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context=parent.getContext();
        View view= LayoutInflater.from(context).inflate(R.layout.profile_post_list,parent,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String image=pBlog_list.get(position).getImage_url();
        String thumb=pBlog_list.get(position).getImage_thumb();
        String imgDesc=pBlog_list.get(position).getDesc();

        holder.setProfilePostList(imgDesc,image,thumb);
    }

    @Override
    public int getItemCount() {
        return pBlog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView postdesc;
        private ImageView PostImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setProfilePostList(String imgDesc, String image, String thumb) {

            postdesc=mView.findViewById(R.id.profile_image_description);
            PostImage=mView.findViewById(R.id.profile_blog_img);

            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderRequest).load(image)
            .thumbnail(Glide.with(context).load(thumb)).into(PostImage);

            postdesc.setText(imgDesc);

        }
    }
}
