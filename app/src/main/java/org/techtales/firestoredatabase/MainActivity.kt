package org.techtales.firestoredatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.addBtn.setOnClickListener {
            val title = binding.enterEtxt.text.toString()
            val description = binding.enterEdes.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                addData(title, description)
            }
        }
        fetchData()
    }

    private fun fetchData() {
        dataCollection.get()
            .addOnSuccessListener {
                data.clear()
                for (document in it) {
                    val item = document.toObject(Data::class.java)
                    item.id = document.id
                    data.add(item)
                }
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener{
                Toast.makeText(this, "Data fetch failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addData(title: String, description: String) {
        val newData = Data(title = title, description = description)
        dataCollection.add(newData)
            .addOnSuccessListener {
                newData.id = it.id
                data.add(newData)
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Data added Successfully", Toast.LENGTH_SHORT).show()

                binding.enterEtxt.text?.clear()
                binding.enterEdes.text?.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Data added Failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onEditClick(data: Data) {
        binding.enterEtxt.setText(data.title)
        binding.enterEtxt.setText(data.description)
        binding.addBtn.text = "Update"

        binding.addBtn.setOnClickListener {
            val updateTitle = binding.enterEtxt.text.toString()
            val updateDescription = binding.enterEdes.text.toString()

            if (updateTitle.isNotEmpty() && updateDescription.isNotEmpty()) {
                val updateData = Data(data.id, updateTitle, updateDescription)

                dataCollection.document(data.id!!)
                    .set(updateData)
                    .addOnSuccessListener {
                        binding.enterEtxt.text?.clear()
                        binding.enterEdes.text?.clear()
                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                        Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data Updated Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


        override fun onDeleteItemClick(data: Data) {
            dataCollection.document(data.id!!)
                .delete()
                .addOnSuccessListener {
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show()
                    fetchData()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Data deleted failed", Toast.LENGTH_SHORT).show()
                }

        }


    }