package com.example.masterdetailexample.list

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.masterdetailexample.R
import com.example.masterdetailexample.room.Item
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_item.view.*

class ItemAdapter(var items: List<Item>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val clicks = PublishSubject.create<Item>()
    private val deletes = PublishSubject.create<Item>()

    fun clicks(): Observable<Item> {
        return clicks
    }

    fun deletes(): Observable<Item> {
        return deletes
    }

    fun updateItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as ViewHolder).bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.view_item, parent, false)
        return ViewHolder(view)
    }

    private inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val selectedColor = ContextCompat.getColor(itemView.context, R.color.colorAccent)
        private val backgroundColor = ContextCompat.getColor(itemView.context, R.color.itemBackground)

        init {
            itemView.clicks()
                    .subscribe { clicks.onNext(items[adapterPosition]) }

            itemView.deleteButton
                    .clicks()
                    .subscribe { deletes.onNext(items[adapterPosition]) }
        }

        fun bind(item: Item) {
            itemView.taskId.text = item.id.toString()
            itemView.itemText.text = item.text

            if (item.selected) {
                itemView.setBackgroundColor(selectedColor)
            }
            else
            {
                itemView.setBackgroundColor(backgroundColor)
            }
        }
    }
}