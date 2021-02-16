package com.example.birder

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.File

class BirdListAdapter : RecyclerView.Adapter<BirdListAdapter.RecyclerViewHolder>() {
    private var birdList = emptyList<Bird>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val bird = birdList[position]
        holder.itemName.text = bird.name
        holder.itemDesc.text = bird.description

        holder.itemView.setOnClickListener {
            Log.d("testi", "${bird.name} CLICKEDD")
        }

        val bitmap = BitmapFactory.decodeFile(bird.imageFilePath)
        Log.d("testi", "${bird.name} filepath: ${bird.imageFilePath}")
        if (bitmap == null) {
            Log.d("testi", "${bird.name} null bitmap :-(")
        }
        holder.imageThumbnail.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return birdList.size
    }

    inner class RecyclerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDesc: TextView = itemView.findViewById(R.id.itemDesc)
        val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("testi","CLICKED")
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setData(bird: List<Bird>) {
        this.birdList = bird
        notifyDataSetChanged()
    }
}