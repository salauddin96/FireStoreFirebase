package org.techtales.firestoredatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.techtales.firestoredatabase.databinding.ActivityMainBinding

@Suppress("OVERRIDE_DEPRECATION")
class MainActivity : AppCompatActivity(), DataAdapter.ItemClickListener {
    private var doubleBackToExitPressedOnce = false
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




    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish() // Finish the app when back is pressed twice
            return
        }

        this.doubleBackToExitPressedOnce= true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 1000) // Reset the flag after 1 seconds
    }


    override fun onEditClick(data: Data) {
        binding.enterEtxt.setText(data.title)
        binding.enterEdes.setText(data.description)
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

                        Toast.makeText(this, "Data Updated", Toast.LENGTH_SHORT).show()
                        fetchData()

                        goToMainActivity()


                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Data Updated Failed", Toast.LENGTH_SHORT).show()
                    }

            }
            else{
                Toast.makeText(this@MainActivity, "Title and Description are required", Toast.LENGTH_SHORT).show()


            }
        }
    }

    private fun goToMainActivity() {
        // Navigate to MainActivity
        val intent = Intent(this@MainActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to provide back button navigation
    }


        override fun onDeleteItemClick(data: Data) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Delete Files")
            dialog.setMessage("Do you want to delete files?")
            dialog.setIcon(R.drawable.delete)



            dialog.setPositiveButton("YES"){dialogInterface, which->
                dataCollection.document(data.id!!)
                    .delete()
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                fetchData()
            }

            dialog.setNegativeButton("NO"){dialogInterface, which->
                Toast.makeText(this, "Clicked No", Toast.LENGTH_SHORT).show()
            }

            dialog.setNeutralButton("CANCEL"){dialogInterface, which->
                Toast.makeText(this, "Clicked Cancel", Toast.LENGTH_SHORT).show()
            }

            val alertDialog:AlertDialog = dialog.create()
            alertDialog.setCancelable(false)
            alertDialog.show()



        }


    }