package ru.semper_viventem.findtheairroute.ui.changecity

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_city.view.*
import ru.semper_viventem.exchangerates.extensions.inflate
import ru.semper_viventem.findtheairroute.R
import ru.semper_viventem.findtheairroute.domain.City

class CitiesAdapter(
    private val cityChanged: (city: City) -> Unit
) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private var items: List<City> = emptyList()

    fun setItems(items: List<City>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_city))
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var item: City

        init {
            itemView.setOnClickListener { cityChanged.invoke(item) }
        }

        fun bind(city: City) {
            this.item = city
            itemView.name.text = city.fullName
        }

    }
}