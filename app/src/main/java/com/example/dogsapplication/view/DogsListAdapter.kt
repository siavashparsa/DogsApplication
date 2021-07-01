package com.example.dogsapplication.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.dogsapplication.R
import com.example.dogsapplication.databinding.ItemDogBinding
import com.example.dogsapplication.model.DogBreed
import com.example.dogsapplication.util.getProgressDrawable
import com.example.dogsapplication.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>) :
    RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(),DogClickListener {

    fun updateDogList(newDogsList: List<DogBreed>) {

        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()

    }

    class DogViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {

        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_dog, parent, false)

        val view =
            DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {

        holder.view.dog = dogsList[position]
        holder.view.listener = this

    }

    override fun getItemCount() = dogsList.size


    override fun onDogClicked(v: View) {

        val uuid = v.dogId.text.toString().toInt()
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = uuid
        Navigation.findNavController(v).navigate(action)

    }


}