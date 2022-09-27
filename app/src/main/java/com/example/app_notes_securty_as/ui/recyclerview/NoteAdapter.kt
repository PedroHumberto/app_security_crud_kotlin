package com.example.app_notes_securty_as.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.app_notes_securty_as.R
import com.example.app_notes_securty_as.domain.models.Note

class NoteAdapter(noteListener: NoteListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {


    var noteList = listOf<Note>()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    private var noteListener: NoteListener = noteListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.row_notes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(noteList[position], noteListener, position)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(note: Note, noteListener: NoteListener, position: Int) {
            itemView.findViewById<TextView>(R.id.txt_title).text = note.title
            itemView.findViewById<TextView>(R.id.txt_msg_home).text = note.note
            itemView.findViewById<TextView>(R.id.txt_date_home).text = note.dateTime
            val img = itemView.findViewById<ImageView>(R.id.img_home)

            Glide.with(itemView).load(note.url).into(img)



        }


    }

}
