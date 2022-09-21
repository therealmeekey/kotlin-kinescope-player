package io.kinescope.sdk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.kinescope.sdk.R

class KinescopeSettingsAdapter(private val options:Array<String>, private val callback:((String) -> Unit)?) : RecyclerView.Adapter<KinescopeSettingsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KinescopeSettingsAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false))
    }

    override fun onBindViewHolder(holder: KinescopeSettingsAdapter.ViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int {
        return options.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view.rootView), View.OnClickListener {
        private var option:String? = null
        private val optionView:TextView = view.findViewById(R.id.tv_option)

        fun bind(value:String) {
            this.option = value
            optionView.text = option
        }

        override fun onClick(v: View?) {
            callback?.invoke(option!!)
        }
    }
}