package com.google.developers.lettervault.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.developers.lettervault.R
import com.google.developers.lettervault.ui.add.AddLetterActivity
import com.google.developers.lettervault.ui.detail.LetterDetailActivity
import com.google.developers.lettervault.ui.setting.SettingActivity
import com.google.developers.lettervault.util.DataViewModelFactory
import com.google.developers.lettervault.util.LETTER_ID
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*

class ListActivity : AppCompatActivity() {

    private lateinit var viewModel: LetterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener {
            val intent = Intent(this, AddLetterActivity::class.java)
            startActivity(intent)
        }
        val factory = DataViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory).get(LetterViewModel::class.java)

        val adapter = LetterAdapter {
            val intent = Intent(this, LetterDetailActivity::class.java)
            intent.putExtra(LETTER_ID, it.id)
            startActivity(intent)
        }

        val pixelSize = resources.getDimensionPixelSize(R.dimen.item_decoration_margin)
        recycler.run {
            addItemDecoration(ItemDecoration(pixelSize))
            layoutManager = GridLayoutManager(this@ListActivity, 2)
            this.adapter = adapter
        }

        viewModel.letters.observe(this, {
            adapter.submitList(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.future, R.id.opened, R.id.all -> {
                item.isChecked = !item.isChecked
                val itemName = resources.getResourceEntryName(item.itemId)
                try {
                    viewModel.filter(itemName)
                } catch (e: IllegalArgumentException) {
                    Log.e(this.javaClass.name, "Invalid application state: ${e.message}")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
