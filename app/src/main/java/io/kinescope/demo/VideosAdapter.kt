package io.kinescope.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.kinescope.sdk.models.videos.KinescopeVideo

class VideosAdapter(val callback: (KinescopeVideo)-> Unit) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    private val arr = ArrayList<KinescopeVideo>()

    fun updateData(value:List<KinescopeVideo>) {
        arr.clear()
        arr.addAll(value)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false))
    }

    override fun onBindViewHolder(holder: VideosAdapter.ViewHolder, position: Int) {
            holder.bind(arr[position])
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    inner class ViewHolder(private val view:View) :  RecyclerView.ViewHolder(view.rootView), View.OnClickListener {
        private var video: KinescopeVideo? = null
        val title:TextView = view.findViewById(R.id.tv_title)

        init {
            view.setOnClickListener(this)
        }

        fun bind(video: KinescopeVideo) {
            this.video = video
            title.text = video.title
        }

        override fun onClick(p0: View?) {
            callback.invoke(video!!)
        }

    }
}