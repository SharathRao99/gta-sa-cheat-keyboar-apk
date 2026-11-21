package com.sharath.sacheatboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView

/**
 * Reusable cheat list: category chips, optional search box, list with favorite stars.
 * Used by the app screen, the floating overlay and the keyboard.
 */
class CheatPanel(
    context: Context,
    withSearch: Boolean,
    private val onTap: (Cheat) -> Unit,
    private val onLong: ((Cheat) -> Unit)? = null
) : LinearLayout(context) {

    private var category = "All"
    private var query = ""
    private var shown: List<Cheat> = Cheats.all
    private val chips = mutableMapOf<String, TextView>()
    private val adapter = Adapter()
    private val list = ListView(context)

    init {
        orientation = VERTICAL
        setBackgroundColor(Pal.panel)

        if (withSearch) {
            val search = EditText(context).apply {
                hint = "Search cheats or codes"
                setHintTextColor(Pal.dim)
                setTextColor(Pal.text)
                textSize = 15f
                isSingleLine = true
                setPadding(context.dp(12), context.dp(8), context.dp(12), context.dp(8))
                background = round(Pal.row, context.dp(8).toFloat())
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                    override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        query = s?.toString()?.trim()?.lowercase() ?: ""
                        refresh()
                    }
                })
            }
            addView(search, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(context.dp(8), context.dp(8), context.dp(8), context.dp(4))
            })
        }

        val chipRow = LinearLayout(context).apply { orientation = HORIZONTAL }
        Cheats.categories.forEach { cat ->
            val chip = TextView(context).apply {
                text = cat
                textSize = 13f
                setTextColor(Pal.text)
                gravity = Gravity.CENTER
                setPadding(context.dp(12), context.dp(6), context.dp(12), context.dp(6))
                setOnClickListener { category = cat; refresh() }
            }
            chips[cat] = chip
            chipRow.addView(chip, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(context.dp(4), context.dp(6), context.dp(4), context.dp(6))
            })
        }
        addView(HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
            addView(chipRow)
        })

        list.adapter = adapter
        list.divider = null
        list.setBackgroundColor(Pal.panel)
        addView(list, LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f))
        refresh()
    }

    private fun round(color: Int, r: Float) = GradientDrawable().apply {
        setColor(color); cornerRadius = r
    }

    private fun refresh() {
        val favs = Prefs.favorites(context)
        shown = Cheats.all.filter { c ->
            val catOk = when (category) {
                "All" -> true
                "★" -> c.id in favs
                else -> c.category == category
            }
            catOk && (query.isEmpty() ||
                c.name.lowercase().contains(query) ||
                c.code.lowercase().contains(query) ||
                c.phrase.lowercase().contains(query))
        }
        chips.forEach { (cat, v) ->
            v.background = round(if (cat == category) Pal.accent else Pal.key, context.dp(16).toFloat())
            v.setTextColor(if (cat == category) Color.BLACK else Pal.text)
        }
        adapter.notifyDataSetChanged()
    }

    private inner class Adapter : BaseAdapter() {
        override fun getCount() = shown.size
        override fun getItem(i: Int) = shown[i]
        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, convert: View?, parent: ViewGroup?): View {
            val c = shown[i]
            val ctx = context
            val fav = c.id in Prefs.favorites(ctx)

            val row = LinearLayout(ctx).apply {
                orientation = HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = round(Pal.row, ctx.dp(10).toFloat())
                setPadding(ctx.dp(12), ctx.dp(8), ctx.dp(4), ctx.dp(8))
                setOnClickListener { onTap(c) }
                if (onLong != null) setOnLongClickListener { onLong.invoke(c); true }
            }
            val col = LinearLayout(ctx).apply { orientation = VERTICAL }
            col.addView(TextView(ctx).apply {
                text = c.name; setTextColor(Pal.text); textSize = 14f
            })
            col.addView(TextView(ctx).apply {
                text = if (c.phrase.isEmpty()) c.code else c.code + "  ·  " + c.phrase
                setTextColor(Pal.accent); textSize = 12f; typeface = Typeface.MONOSPACE
            })
            if (c.note.isNotEmpty()) col.addView(TextView(ctx).apply {
                text = c.note; setTextColor(Pal.dim); textSize = 11f
            })
            row.addView(col, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
            row.addView(TextView(ctx).apply {
                text = if (fav) "★" else "☆"
                textSize = 20f
                setTextColor(if (fav) Pal.accent else Pal.dim)
                gravity = Gravity.CENTER
                isFocusable = false
                setPadding(ctx.dp(12), ctx.dp(4), ctx.dp(12), ctx.dp(4))
                setOnClickListener { Prefs.toggleFavorite(ctx, c.id); refresh() }
            })

            // small gap between rows
            return LinearLayout(ctx).apply {
                setPadding(ctx.dp(8), ctx.dp(3), ctx.dp(8), ctx.dp(3))
                addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
            }
        }
    }
}
