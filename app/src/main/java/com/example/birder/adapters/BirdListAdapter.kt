package com.example.birder.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.birder.R
import com.example.birder.data.Bird
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
        holder.itemTime.text = bird.time
        holder.imageThumbnail.setImageURI(Uri.parse(bird.imageUri))

        holder.itemView.setOnClickListener {
            val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager
            val singleBirdFragment = SingleBirdFragment(bird)

            manager.beginTransaction().apply {
                replace(R.id.fl_wrapper, singleBirdFragment)
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
        val itemTime: TextView = itemView.findViewById(R.id.itemTime)
        val imageThumbnail: ImageView = itemView.findViewById(R.id.imageThumbnail)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
        }
    }

    fun setData(bird: List<Bird>) {
        this.birdList = bird
        notifyDataSetChanged()
    }
}