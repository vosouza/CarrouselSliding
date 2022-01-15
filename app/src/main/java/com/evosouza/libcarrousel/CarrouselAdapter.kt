package com.evosouza.libcarrousel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import kotlin.math.abs
import kotlin.math.roundToInt

class CarrouselAdapter(
    private val data: List<Image>
) : Adapter<CarrouselAdapter.CarrouselViewHolder>() {

    private var hasInitDimensions = false
    private var maxImageWidth: Int = 0
    private var maxImageHeight: Int = 0
    private var maxImageAspectRatio: Float = 1f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrouselViewHolder {

        if (!hasInitDimensions){
            maxImageWidth = (parent.width * 0.75f).roundToInt()
            maxImageHeight = parent.height
            maxImageAspectRatio = maxImageWidth.toFloat() / maxImageHeight.toFloat()
            hasInitDimensions = true

        }

        return CarrouselViewHolder(OverlayableImageView(parent.context))
    }

    override fun onBindViewHolder(holder: CarrouselViewHolder, position: Int) {
        val item = data[position]
        val aspectRatio = item.aspectRatio
        val targetImageWidth = if(aspectRatio > maxImageAspectRatio){
            (maxImageHeight * aspectRatio).roundToInt()
        }else{
            maxImageWidth
        }

        holder.imageView.layoutParams = RecyclerView.LayoutParams(
            targetImageWidth,
            RecyclerView.LayoutParams.MATCH_PARENT
        )

        holder.imageView.image = item

        holder.imageView.setOnClickListener {
            val rv =holder.imageView.parent as RecyclerView
            rv.smoothScrollToCenterPosition(position)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }
    class CarrouselViewHolder(val imageView: OverlayableImageView): RecyclerView.ViewHolder(imageView)
}

private fun RecyclerView.smoothScrollToCenterPosition(position: Int){
    val smoothScroller = object: LinearSmoothScroller(context){
        override fun calculateDxToMakeVisible(view: View?, snapPreference: Int): Int {
            val dxStart = super.calculateDxToMakeVisible(view, SNAP_TO_START)
            val dxEnd = super.calculateDxToMakeVisible(view, SNAP_TO_END)

            return (dxStart + dxEnd)/ 2
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

internal class ProminentLayoutManager(
    context: Context,
    private val minScaleDistanceFactor: Float = 1.5f,
    private val scaleDownBy: Float = 0.5f
) : LinearLayoutManager(context, HORIZONTAL, false){

    private val prominentThreshold = context.resources
        .getDimensionPixelSize(R.dimen.prominent_threshold)

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state).also { scaleChildren() }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollHorizontallyBy(dx, recycler, state).also {
            if (orientation == HORIZONTAL) scaleChildren()
        }
    }

    private fun scaleChildren() {
        val containerCenter = width/2
        val scaleDistanceThreshold = minScaleDistanceFactor * containerCenter
        var translationXForward = 0f

        for(i in 0 until childCount){
            val child = getChildAt(i)!!
            val childCenter = (child.left + child.right)/2
            val distanceToCenter = abs(childCenter - containerCenter)

            child.isActivated = distanceToCenter < prominentThreshold

            val scaleDownAmount = (distanceToCenter / scaleDistanceThreshold).coerceAtMost(1f)
            val scale = 1f - scaleDownBy * scaleDownAmount

            child.scaleX = scale
            child.scaleY = scale

            val translationDirection = if (childCenter > containerCenter) -1 else 1
            val translationXFromScale = translationDirection * child.width * (1 - scale) / 2f
            child.translationX = translationXFromScale + translationXForward

            translationXForward = 0f

            if(translationXFromScale > 0 && i>=1){
                getChildAt(i -1)!!.translationX += 2*translationXFromScale
            }else if(translationXFromScale<0){
                translationXForward = 2 * translationXFromScale
            }
        }

    }

    override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
        return (width / (1 - scaleDownBy)).roundToInt()
    }
}