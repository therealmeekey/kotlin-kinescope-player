package io.kinescope.sdk.menu

import android.content.Context
import android.view.Gravity
import android.view.Menu
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import io.kinescope.sdk.R
import io.kinescope.sdk.utils.dip
import me.saket.cascade.CascadePopupWindow

import java.util.*

class KinescopeOptionsBackStackMenu(
    private val context: Context,
    private val anchor: View,
    private var gravity: Int = Gravity.NO_GRAVITY,
    private val fixedWidth: Int = context.dip(196),
    private val xoff: Int,
    private val yoff: Int,
) {
    private val backstack = Stack<Menu>()
    val popup = CascadePopupWindow(context, android.R.style.Widget_Material_PopupMenu)


    fun show() {
        popup.width = fixedWidth
        popup.height = fixedWidth
        //popup.height = ViewGroup.LayoutParams.WRAP_CONTENT // Doesn't work on API 21 without this.

        popup.setMargins(
            start = context.dip(4),
            end = context.dip(4),
            bottom = context.dip(4)
        )


        popup.contentView.background = AppCompatResources.getDrawable(context, R.drawable.cascade_ic_round_arrow_right_24)


        //showMenu(menuBuilder, goingForward = true)


        popup.showAsDropDown(anchor, 0, 0, gravity)

    }

    private fun showMenu(menu: MenuBuilder, goingForward: Boolean) {
        /*val menuList = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context).also {
                it.recycleChildrenOnDetach = true
                setRecycledViewPool(sharedViewPool)
            }
            isVerticalScrollBarEnabled = true
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            styler.menuList(this)

            addOnScrollListener(OverScrollIfContentScrolls())
            adapter = CascadeMenuAdapter(
                items = buildModels(menu, canNavigateBack = backstack.isNotEmpty()),
                styler = styler,
                themeAttrs = themeAttrs,
                onTitleClick = { navigateBack() },
                onItemClick = { handleItemClick(it) }
            )
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        backstack.push(menu)
        popup.contentView.show(menuList, goingForward)*/
    }

}