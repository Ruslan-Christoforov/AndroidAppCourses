package com.example.test

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class ItemAdapter(
    private var items: List<Item>,
    private var onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private var textView: TextView = view.findViewById(R.id.itemText)
        //private var button: Button = view.findViewById(R.id.itemButton)

        private var textView: TextView = view.findViewById(R.id.itemcourseTitle)
        private var text: TextView = view.findViewById(R.id.courseDescription)
        private var bt: Button = view.findViewById(R.id.itemclick)
        private var rate: Button = view.findViewById(R.id.itemratingButton)
        private var date: Button = view.findViewById(R.id.itemdateButton)
        private var price: TextView = view.findViewById(R.id.itemcoursePrice)

        private var imgV: ImageView = view.findViewById(R.id.backgroundImage)

        private var count:Int = 2

        fun bind(item: Item, clickListener: (Item) -> Unit) {
            textView.text = item.title
            text.maxLines = 2 // Ограничиваем до одной строки
            text.ellipsize = TextUtils.TruncateAt.END // Добавляем многоточие в конце, если текст
            text.text = item.text
            rate.text = item.rate
            date.text = item.publishDate
            price.text = item.price


            //получение случайных картинок так как в запросе так как в
            // json нет изображения и придется брать скачанное в приложение из макета
            if(item.id % 3 == 0)
                imgV.setImageResource(R.drawable.im1);
            if(item.id % 3 == 1)
                imgV.setImageResource(R.drawable.im2);
            if(item.id % 3 == 2)
                imgV.setImageResource(R.drawable.im3);

            count = count + 1
            if (count >= 3)
                count = 0

            if (item.hasLike==true)
                bt.foreground = ContextCompat.getDrawable(itemView.context, R.drawable.bookmark_fill)

            bt.setOnClickListener {

                // Изменяем текст TextView
                //textView.text = "${item.title} клик"
                bt.foreground = ContextCompat.getDrawable(itemView.context, R.drawable.bookmark_fill)
                item.hasLike = true
                clickListener(item)
            }

            /*button.setOnClickListener {

                // Изменяем текст TextView
                textView.text = "${item.title} клик"
                clickListener(item)
            }*/

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }


    override fun getItemCount(): Int = items.size
}