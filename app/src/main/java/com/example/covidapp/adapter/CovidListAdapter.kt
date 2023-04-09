package com.example.covidapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.covidapp.databinding.ItemCovidBinding
import com.example.covidapp.model.Country
import javax.inject.Inject

class CovidListAdapter  @Inject constructor() : RecyclerView.Adapter<CovidListAdapter.ViewHolder>() {

    private lateinit var binding: ItemCovidBinding
    private lateinit var context: Context

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Country) {
            binding.apply {
                tvCountryName.text = item.Country
                tvTotalConfirmed.text = "Toplam vaka sayısı: ${item.TotalConfirmed}"
                tvTotalRecovered.text =  "Toplam iyileşen sayısı: ${item.TotalRecovered}"
                tvTotalDeath.text = "Toplam ölüm sayısı: ${item.TotalDeaths}"

                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCovidBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun getItemViewType(position: Int): Int = position

    private var onItemClickListener: ((Country) -> Unit)? = null

    fun setOnItemClickListener(listener: (Country) -> Unit) {
        onItemClickListener = listener
    }

    private val differCallback = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(
            oldItem: Country,
            newItem: Country
        ): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areContentsTheSame(
            oldItem: Country,
            newItem: Country
        ): Boolean {
            return oldItem == newItem
        }
    }

    var covidlist:List<Country>
        get() = differ.currentList
        @SuppressLint("NotifyDataSetChanged")
        set(value){
            differ.submitList(value)
            notifyDataSetChanged()
        }

    private val differ = AsyncListDiffer(this, differCallback)
}