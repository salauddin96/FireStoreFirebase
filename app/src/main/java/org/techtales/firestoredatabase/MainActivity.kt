package org.techtales.firestoredatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.techtales.firestoredatabase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), DataAdapter.ItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()
    private val dataCollection = db.collection("data")
    private val data = mutableListOf<Data>()
    private lateinit var adapter: DataAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        adapter = DataAdapter(data, this)
        binding.recyclerView.adapter=adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addBtn.setOnClickListener{
            val title = binding.enterEtxt.text.toString()
            val description = binding.enterEdes.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()){
                addData(title,description)
            }
        }
        fetchData()
    }

    private fun fetchData() {
        dataCollection.get()
            .addOnSuccessListener {
                data.clear()
                for (document in it){
                    val item = document.toObject(Data::class.java)
                    item.id = document.id
                    data.add(item)
                }

            }
    }

    private fun addData(title: String, description: String) {
        val newData = Data(title=title, description = description)
        dataCollection.add(newData)
            .addOnSuccessListener {
                newData.id = it.id
                data.add(newData)
                adapter.notifyDataSetChanged()

                binding.enterEtxt.text?.clear()
                binding.enterEdes.text?.clear()
            }
    }

    override fun onEditClick(data: Data) {
        TODO("Not yet implemented")
    }

    override fun onDeleteItemClick(data: Data) {
        TODO("Not yet implemented")
    }


}