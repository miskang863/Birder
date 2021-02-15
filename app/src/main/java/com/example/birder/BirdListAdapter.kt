package com.example.birder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

 class BirdListAdapter(
    private val birds: MutableList<Bird>,
    // private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<BirdListAdapter.RecyclerViewHolder>() {

        override fun getItemViewType(position: Int): Int {
           return R.layout.item
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
           val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return RecyclerViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
            val bird: Bird = birds[position]
            holder.itemName.text = bird.name
            holder.itemDesc.text = bird.description
            holder.imageThumbnail.setImageBitmap(bird.image)
        }

        override fun getItemCount(): Int {
            return GlobalModel.birds.size
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
                val position: Int = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
          //        listener.onItemClick(position)
                }
            }
        }

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

    }