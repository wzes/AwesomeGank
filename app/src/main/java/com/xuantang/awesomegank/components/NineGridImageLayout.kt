package com.xuantang.awesomegank.components

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.xuantang.basemodule.extentions.dp

import kotlin.math.ceil

class NineGridImageLayout : ViewGroup {
    //行
    private var rowCount = 3
    //列
    private var columnCount = 3
    //间距
    var spacing = context.dp(6)
        set(value) {
            field = value
            requestLayout()
        }
    //单张图片最大宽高
    var singleImgSize = context.dp(210)
        set(value) {
            field = value
            requestLayout()
        }
    //单网格宽高
    private var gridSize = dp(100)
    //url list
    private var imageList = arrayListOf<String>()
    //单图宽高比
    var ratio = 0f

    private var itemClickListener: ((v: View, position: Int) -> Unit)? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val availableWidth = width - paddingLeft - paddingRight
        gridSize = (availableWidth - spacing * 2) / 3
        val height =
            if (imageList.size == 1) {
                if (ratio > 1)
                    (singleImgSize / ratio).toInt() + paddingTop + paddingBottom
                else
                    singleImgSize + paddingTop + paddingBottom
            } else if (imageList.size > 1) {
                gridSize * rowCount + spacing * (rowCount - 1) + paddingTop + paddingBottom
            } else {
                MeasureSpec.getSize(heightMeasureSpec)
            }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, p0: Int, p1: Int, p2: Int, p3: Int) {
        if (imageList.isEmpty())
            return
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        setGone()
        when (imageList.size) {
            1 -> {
                val view = getChildAt(0) as ImageView
                view.visibility = View.VISIBLE
                right = paddingLeft + view.layoutParams.width
                bottom = paddingTop + view.layoutParams.height
                view.layout(paddingLeft, paddingTop, right, bottom)
                view.setOnClickListener {
                    itemClickListener?.invoke(it, 0)
                }
                view.transitionName = "Image 0"
            }
            else -> {
                var row: Int
                var column: Int
                imageList.forEachIndexed { index, _ ->
                    val view = getChildAt(index) as ImageView
                    view.visibility = View.VISIBLE
                    //图片数量为4的时候第二张需要换行
                    row = index / (if (imageList.size == 4) 2 else 3)
                    column = index % columnCount
                    left = (gridSize + spacing) * column + paddingLeft
                    top = (gridSize + spacing) * row + paddingTop
                    right = left + gridSize
                    bottom = top + gridSize
                    view.layout(left, top, right, bottom)
                    view.setOnClickListener {
                        itemClickListener?.invoke(it, index)
                    }
                    view.transitionName = "Image $index"
                }
            }
        }
    }

    fun loadImages(loader: (v: ImageView, url: String) -> Unit) {
        (0 until imageList.size).forEach {
            loader(getChildAt(it) as ImageView, imageList[it])
        }
    }

    fun clearImages(cleaner: (v: ImageView)->Unit) {
        (0 until childCount).forEach {
            cleaner(getChildAt(it) as ImageView)
        }
    }


    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    private fun ImageView.addViewIn() {
        addView(this)
    }

    private fun setGone() {
        (0 until childCount).forEach {
            getChildAt(it).visibility = View.INVISIBLE
        }
    }

    fun setData(imageList: List<String>, ratio: Float, id: String) {
        //最多九张
        this.imageList =
            if (imageList.size > 9)
                ArrayList(imageList.subList(0, 8))
            else
                ArrayList(imageList)
        this.ratio = ratio
        //行数
        rowCount = ceil(imageList.size / 3f).toInt()
        //列数
        columnCount = if (imageList.size == 4) 2 else 3
        //不要重复addView以免造成过多的性能损耗
        if (imageList.size == 1) {
            if (getChildAt(0) == null)
                generateImageView("$id-0").addViewIn()
            getChildAt(0).layoutParams = if (ratio > 1) {
                val height = (singleImgSize / ratio).toInt()
                val width = singleImgSize
                LayoutParams(width, height)
            } else {
                val height = singleImgSize
                val width = (singleImgSize * ratio).toInt()
                LayoutParams(width, height)
            }
        } else {
            imageList.forEachIndexed { index, _ ->
                if (getChildAt(index) == null)
                    generateImageView("$id-$index").addViewIn()
                getChildAt(0).layoutParams = LayoutParams(gridSize, gridSize)
            }
        }
    }

    private fun generateImageView(viewTag: String) =
        RoundCornerImageView(context).apply {
            borderColor = Color.parseColor("#DBDBDB")
            borderWidth = dp(0.4f).toFloat()
            radius = dp(3).toFloat()
            scaleType = ImageView.ScaleType.CENTER_CROP
            tag = viewTag
        }


    fun onItemClick(click: (v: View, position: Int) -> Unit) {
        itemClickListener = click
    }
}