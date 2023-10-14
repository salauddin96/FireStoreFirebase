package org.techtales.firestoredatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(private val data:List<Data>, private val itemClickListener: ItemClickListener): RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    interface ItemClickListener{
        fun onEditClick(data:Data)
        fun onDeleteItemClick(data:Data)
    }



    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.titleTxt)
        val description = itemView.findViewById<TextView>(R.id.desTxt)
        val edit = itemView.findViewById<ImageButton>(R.id.editBtn)
        val delete = itemView.findViewById<ImageButton>(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lits_item,parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val item = data[position]
        holder.title.text = item.title
        holder.description.text = item.description

        holder.edit.setOnClickListener{
            itemClickListener.onEditClick(item)

        }
        holder.delete.setOnClickListener{
            itemClickListener.onDeleteItemClick(item)
        }
    }


}