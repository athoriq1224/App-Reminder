package com.google.developers.lettervault.ui.add

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.developers.lettervault.R
import com.google.developers.lettervault.util.DataViewModelFactory
import com.google.developers.lettervault.util.Event
import com.google.developers.lettervault.util.toDateTimeString
import kotlinx.android.synthetic.main.activity_letter.*
import kotlinx.android.synthetic.main.content_add_letter.*
import java.util.*

class AddLetterActivity : AppCompatActivity() {
    private lateinit var viewModel: AddLetterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val factory = DataViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory).get(AddLetterViewModel::class.java)

        title = viewModel.created.toDateTimeString()

        viewModel.saved.observe(this) {
            runEvent(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                val subject = edt_subject.text.toString()
                val message = edt_content.text.toString()
                viewModel.save(subject, message)
                true
            }
            R.id.action_time -> {
                val calendar = Calendar.getInstance()
                val timePicker = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    viewModel.setExpirationTime(hour, minute)
                }
                TimePickerDialog(
                    this,
                    R.style.ThemeDialog,
                    timePicker,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun runEvent(eventAdd: Event<Boolean>) {
        val isSaved = eventAdd.getContentIfNotHandled() ?: return
        val message =
            resources.getString(if (isSaved) {R.string.letter_saved} else R.string.cannot_save_message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        if(isSaved) finish()
    }
}