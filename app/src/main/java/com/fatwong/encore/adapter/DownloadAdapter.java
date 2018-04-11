package com.fatwong.encore.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fatwong.encore.R;
import com.fatwong.encore.net.LogDownloadListener;
import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FINISHED = 1;
    public static final int TYPE_DOWNLOADING = 2;

    private List<DownloadTask> values;
    private LayoutInflater inflater;
    private Context mContext;
    private int localType;

    public DownloadAdapter(Context context) {
        this.mContext = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadViewHolder(this.inflater.inflate(R.layout.download_listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadViewHolder holder, int position) {
        DownloadTask downloadTask = this.values.get(position);
        String tag = createTag(downloadTask);
        downloadTask.register(new ListDownloadListener(tag, holder)).register(new LogDownloadListener());
        holder.setTag(tag);
        holder.setTask(downloadTask);
        holder.setDownloadTitle();
        holder.refresh(downloadTask.progress);
    }

    public void unRegister() {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        for (DownloadTask task : taskMap.values()) {
            task.unRegister(createTag(task));
        }
//        Iterator iterator = OkDownload.getInstance().getTaskMap().values().iterator();
//        while (iterator.hasNext()) {
//            DownloadTask downloadTask = (DownloadTask) iterator.next();
//            downloadTask.unRegister(createTag(downloadTask));
//        }
    }

    private String createTag(DownloadTask downloadTask) {
        return localType + "_" + downloadTask.progress.tag;
    }

    @Override
    public int getItemCount() {
        if (this.values == null) {
            return 0;
        }
        return this.values.size();
    }

    public void updateData(int type) {
        localType = type;
        if (type == TYPE_ALL) values = OkDownload.restore(DownloadManager.getInstance().getAll());
        if (type == TYPE_FINISHED)
            values = OkDownload.restore(DownloadManager.getInstance().getFinished());
        if (type == TYPE_DOWNLOADING)
            values = OkDownload.restore(DownloadManager.getInstance().getDownloading());
        notifyDataSetChanged();
    }

    private class ListDownloadListener extends DownloadListener {

        private DownloadViewHolder holder;

        public ListDownloadListener(Object tag, DownloadViewHolder holderBean) {
            super(tag);
            this.holder = holderBean;
        }

        @Override
        public void onStart(Progress progress) {

        }

        @Override
        public void onProgress(Progress progress) {
            if (this.tag == this.holder.getTag()) {
                this.holder.refresh(progress);
            }
        }

        @Override
        public void onError(Progress progress) {
            if (progress.exception != null) {
                progress.exception.printStackTrace();
            }
        }

        @Override
        public void onFinish(File file, Progress progress) {
            Toast.makeText(DownloadAdapter.this.mContext, "下载完成:" + progress.filePath, Toast.LENGTH_SHORT).show();
            DownloadAdapter.this.updateData(DownloadAdapter.this.localType);
        }

        @Override
        public void onRemove(Progress progress) {

        }
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.download_img)
        SimpleDraweeView downloadImg;
        @BindView(R.id.download_title)
        TextView downloadTitle;
        @BindView(R.id.download_size)
        TextView downloadSize;
        @BindView(R.id.download_speed)
        TextView downloadSpeed;
        @BindView(R.id.download_speed_layout)
        RelativeLayout downloadSpeedLayout;
        @BindView(R.id.download_complete_img)
        ImageView downloadCompleteImg;
        @BindView(R.id.download_artist)
        TextView downloadArtist;
        @BindView(R.id.start_download)
        ImageView startDownload;
        @BindView(R.id.remove_download)
        ImageView removeDownload;
        private String tag;
        private DownloadTask task;

        public DownloadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            if (DownloadAdapter.this.localType == TYPE_FINISHED) {
                this.startDownload.setVisibility(View.GONE);
                this.downloadSpeedLayout.setVisibility(View.GONE);
            }
        }

        public void setDownloadTitle() {
            Progress progress = this.task.progress;
            this.downloadTitle.setText(progress.fileName);
        }

        public String getTag() {
            return this.tag;
        }

        public void refresh(Progress progress) {
            String downloadedSize = Formatter.formatFileSize(DownloadAdapter.this.mContext, progress.currentSize);
            String totalSize = Formatter.formatFileSize(DownloadAdapter.this.mContext, progress.totalSize);
            downloadSize.setText(downloadedSize + "/" + totalSize);
            switch (progress.status) {
                case Progress.NONE:
                    downloadSpeed.setText("已停止");
                    break;
                case Progress.PAUSE:
                    downloadSpeed.setText("已暂停");
                    break;
                case Progress.ERROR:
                    downloadSpeed.setText("下载错误");
                    break;
                case Progress.WAITING:
                    downloadSpeed.setText("等待中");
                    break;
                case Progress.FINISH:
                    downloadSpeed.setText("已完成");
                    break;
                case Progress.LOADING:
                    String speed = Formatter.formatFileSize(mContext, progress.speed);
                    downloadSpeed.setText(String.format("%s/s", speed));
                    break;
            }
        }


        @OnClick({R.id.start_download, R.id.remove_download})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.start_download:
                    Progress progress = task.progress;
                    switch (progress.status) {
                        case Progress.NONE:
                        case Progress.PAUSE:
                        case Progress.ERROR:
                            task.start();
                            break;
                        case Progress.LOADING:
                            task.pause();
                            break;
                        case Progress.FINISH:
                            break;
                    }
                    break;
                case R.id.remove_download:
                    task.remove(true);
                    updateData(localType);
                    break;
            }
        }

        public void setTag(String paramString) {
            this.tag = paramString;
        }

        public void setTask(DownloadTask paramDownloadTask) {
            this.task = paramDownloadTask;
        }

        public void start() {
            Progress localProgress = this.task.progress;
        }
    }
}
