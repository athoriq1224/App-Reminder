package com.google.developers.lettervault.ui.list

import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.google.developers.lettervault.R
import com.google.developers.lettervault.data.Letter
import com.google.developers.lettervault.util.toDateTimeString
import kotlinx.android.synthetic.main.letter_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * View holds a letter for RecyclerView.
 */
class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var letter: Letter

    fun bindData(letter: Letter, clickListener: (Letter) -> Unit) {
        this.letter = letter
        itemView.run {
            setOnClickListener { clickListener(letter) }
            txt_title.text = letter.subject
            if (letter.expires < System.currentTimeMillis() && letter.opened != 0L) {
                val opened =
                    context.getString(R.string.title_opened, letter.opened.toDateTimeString())
                txt_status.text = opened
                txt_content.text = letter.content
                txt_content.visibility = View.VISIBLE
                img_lock_open.visibility = View.VISIBLE
                img_lock.visibility = View.GONE
            } else {
                if (letter.expires < System.currentTimeMillis()) {
                    val ready = context.getString(R.string.letter_ready)
                    txt_status.text = ready
                } else {
                    val opening =
                        context.getString(
                            R.string.letter_opening,
                            letter.expires.toDateTimeString()
                        )
                    txt_status.text = opening
                }
            }
        }
    }

    /**
     * This method is used during automated tests.
     *
     * DON'T REMOVE THIS METHOD
     */
    @VisibleForTesting
    fun getLetter(): Letter = letter

}
