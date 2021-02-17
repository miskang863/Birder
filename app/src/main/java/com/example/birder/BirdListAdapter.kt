package com.example.birder

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.fragments.SingleBirdFragment


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

        Log.d("testi", "${bird.name} filepath: ${bird.imageUri}")
        holder.imageThumbnail.setImageURI(Uri.parse(bird.imageUri))

        holder.itemView.setOnClickListener {
            Log.d("testi", "${bird.name} CLICKEDD")


            val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager

            val singleBirdFragment = SingleBirdFragment(bird)

            manager.beginTransaction().apply {
                replace(R.id.favoritelayout, singleBirdFragment)
                addToBackStack(null)
                commit()
            }

        }
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

        override fun onClick(v: View?, ) {
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