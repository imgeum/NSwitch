package com.neoguri.nswitch

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes

class NSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener, View.OnTouchListener {

    private var xDelta: Int = 0

    private var xStart: Int = 0
    private var xCenter: Int = 0
    private var xEnd: Int = 0

    private lateinit var mStandardStart: TextView
    private lateinit var mStandardEnd: TextView
    private lateinit var mBtnOpenClose: LinearLayout
    private lateinit var mLockBg: LinearLayout
    private lateinit var mLockImg: ImageView
    private lateinit var mLockTxt: TextView

    private var mBackBorder: Drawable? = null
    private var mOnLayout: Drawable? = null
    private var mOffLayout: Drawable? = null

    private var mOnText = ""
    private var mOffText = ""

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.standard_start -> {
                animationLeft()
            }
            R.id.standard_end -> {
                animationRight()
            }
        }
    }

    init {
        if (attrs != null) {
            val typedArr = context.obtainStyledAttributes(attrs, R.styleable.NSwitch)

            mBackBorder = typedArr.getDrawable(R.styleable.NSwitch_switchBackBorder)
            mOnLayout = typedArr.getDrawable(R.styleable.NSwitch_switchOnlayout)
            mOffLayout = typedArr.getDrawable(R.styleable.NSwitch_switchOfflayout)

            mOnText = typedArr.getString(R.styleable.NSwitch_switchBackOnText).toString()
            mOffText = typedArr.getString(R.styleable.NSwitch_switchBackOffText).toString()

            val infService = Context.LAYOUT_INFLATER_SERVICE
            val li = getContext().getSystemService(infService) as LayoutInflater
            val v: View = li.inflate(R.layout.switch_layout, this, false)

            val btnOpenCloseGetCenter = v.findViewById<FrameLayout>(R.id.btn_open_close_get_center)
            btnOpenCloseGetCenter.background = mBackBorder

            mStandardStart = v.findViewById(R.id.standard_start)
            mStandardEnd = v.findViewById(R.id.standard_end)
            mBtnOpenClose = v.findViewById(R.id.btn_open_close)
            mLockBg = v.findViewById(R.id.lock_bg)
            mLockImg = v.findViewById(R.id.lock_img)
            mLockTxt = v.findViewById(R.id.lock_txt)

            mLockBg.background = mOnLayout
            mLockImg.setBackgroundResource(R.drawable.on)

            mStandardStart.text = mOnText
            mStandardEnd.text = mOffText
            mLockTxt.text = mOnText

            mStandardStart.post {
                xStart = mStandardStart.translationX.toInt()
                xCenter = mStandardStart.translationX.toInt() + mStandardStart.width / 2
                xEnd = mStandardStart.translationX.toInt() + mStandardStart.width
            }

            mStandardStart.setOnClickListener(this)
            mStandardEnd.setOnClickListener(this)

            mBtnOpenClose.setOnTouchListener(this)

            addView(v)
            setWillNotDraw(false)

        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val xx = event.rawX.toInt()

        val blue: Float
        val gray: Float

        //val r = Rect()

        //mBinding.btnOpenCloseGetCenter.getGlobalVisibleRect(r) // RootoView 레이아웃을 기준으로한 좌표.

        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = (xx - mBtnOpenClose.translationX).toInt()
            }

            MotionEvent.ACTION_UP -> {
                if(mBtnOpenClose.translationX.toInt() <= xStart){
                    animationLeft()
                } else if(mBtnOpenClose.translationX.toInt() >= xEnd){
                    animationRight()
                } else {
                    if(mBtnOpenClose.translationX.toInt() < xCenter){
                        animationLeft()
                    } else if(xCenter < mBtnOpenClose.translationX.toInt()){
                        animationRight()
                    }
                }
                v.performClick()
            }

            MotionEvent.ACTION_MOVE -> {
                mBtnOpenClose.translationX = (xx - xDelta).toFloat()
                when {
                    mBtnOpenClose.translationX.toInt() in xStart..xEnd -> {
                        if(mBtnOpenClose.translationX.toInt() < xCenter){
                            mLockBg.background = mOnLayout
                            mLockImg.setBackgroundResource(R.drawable.on)
                            mLockTxt.text = mOnText
                            if(mBtnOpenClose.translationX.toInt() < xCenter - 20){
                                blue = (xCenter - mBtnOpenClose.translationX.toInt()).toFloat()
                                mBtnOpenClose.alpha = (blue / xCenter)
                            }
                        } else if(xCenter < mBtnOpenClose.translationX.toInt()){
                            mLockBg.background = mOffLayout
                            mLockImg.setBackgroundResource(R.drawable.off)
                            mLockTxt.text = mOffText
                            if(mBtnOpenClose.translationX.toInt() > xCenter + 20){
                                gray = (mBtnOpenClose.translationX.toInt()).toFloat() - xCenter
                                mBtnOpenClose.alpha = (gray / (xCenter))
                            }
                        }
                    }
                    mBtnOpenClose.translationX.toInt() <= xStart -> {
                        mBtnOpenClose.translationX = xStart.toFloat()
                        mBtnOpenClose.alpha = 1f
                    }
                    mBtnOpenClose.translationX.toInt() >= xEnd -> {
                        mBtnOpenClose.translationX = xEnd.toFloat()
                        mBtnOpenClose.alpha = 1f
                    }
                }

            }
        }
        return true
    }

    private fun animationLeft() {
        mBtnOpenClose.animate().translationX(xStart.toFloat()).setDuration(150).withEndAction {
            mBtnOpenClose.alpha = 1f
            mBtnOpenClose.translationX = xStart.toFloat()
            mLockBg.background = mOnLayout
            mLockImg.setBackgroundResource(R.drawable.on)
            mLockTxt.text = mOnText
        }.start()
    }

    private fun animationRight() {
        mBtnOpenClose.animate().translationX(xEnd.toFloat()).setDuration(150).withEndAction {
            mBtnOpenClose.alpha = 1f
            mBtnOpenClose.translationX = xEnd.toFloat()
            mLockBg.background = mOffLayout
            mLockImg.setBackgroundResource(R.drawable.off)
            mLockTxt.text = mOffText
        }.start()
    }

}