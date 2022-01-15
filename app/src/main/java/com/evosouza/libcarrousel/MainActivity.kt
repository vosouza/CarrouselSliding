package com.evosouza.libcarrousel

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.Px
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: CarrouselAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var snapHelper: SnapHelper
    private var data: List<Image> = Data.images
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.carrouselRecycler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecyclerView()

    }

    private fun setRecyclerView() {
        adapter = CarrouselAdapter(data)
        layoutManager = ProminentLayoutManager(this)
        snapHelper = PagerSnapHelper()
        recyclerView.apply {
            setItemViewCacheSize(4)
            layoutManager = this@MainActivity.layoutManager
            adapter = this@MainActivity.adapter

            val spacing = resources.getDimensionPixelSize(R.dimen.carrousel_spacing)
            addItemDecoration(LinearItemDecorator(spacing))
            addItemDecoration(BoundOffsetDecoration())

            snapHelper.attachToRecyclerView(this)
        }
        initRecyclerViewPosition(4)
    }

    private fun initRecyclerViewPosition(position: Int) {
        layoutManager.scrollToPosition(position)
        recyclerView.doOnPreDraw {
            val targetView = layoutManager.findViewByPosition(position) ?: return@doOnPreDraw
            val distanceToFinalSnap = snapHelper.calculateDistanceToFinalSnap(
                layoutManager,
                targetView
            ) ?: return@doOnPreDraw

            layoutManager.scrollToPositionWithOffset(position, -distanceToFinalSnap[0])
        }
    }

    class LinearItemDecorator(@Px private val innerSpacing: Int): RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val itemPosition = parent.getChildAdapterPosition(view)

            outRect.left = if (itemPosition == 0) 0 else innerSpacing / 2
            outRect.right = if (itemPosition == state.itemCount - 1) 0 else innerSpacing / 2
        }
    }

    class BoundOffsetDecoration(): RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val itemPosition = parent.getChildAdapterPosition(view)
            val itemWidth = view.layoutParams.width
            val offset = (parent.width - itemWidth) / 2

            if(itemPosition == 0){
                outRect.left = offset
            }else if(itemPosition == state.itemCount - 1){
                outRect.right = offset
            }
        }
    }
}