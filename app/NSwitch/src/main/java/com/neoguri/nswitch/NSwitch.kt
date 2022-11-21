package com.neoguri.nswitch

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

class NSwitch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener, View.OnTouchListener {

    enum class SWITCHMODE {
        NONE,
        CENTER_GONE
    }

    private var mCutMode: SWITCHMODE = SWITCHMODE.NONE

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
    private var mOffLayout: Drawable? = null
    private var mOnLayout: Drawable? = null

    private var mOffImage: Drawable? = null
    private var mOnImage: Drawable? = null

    private var mOffText = ""
    private var mOnText = ""

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
            mOffLayout = typedArr.getDrawable(R.styleable.NSwitch_switchLayoutOff)
            mOnLayout = typedArr.getDrawable(R.styleable.NSwitch_switchLayoutOn)
            mOffImage = typedArr.getDrawable(R.styleable.NSwitch_switchImageOff)
            mOnImage = typedArr.getDrawable(R.styleable.NSwitch_switchImageOn)

            mOffText = if(typedArr.getString(R.styleable.NSwitch_switchTextOff) == null){
                ""
            } else {
                typedArr.getString(R.styleable.NSwitch_switchTextOff)!!
            }

            mOnText = if(typedArr.getString(R.styleable.NSwitch_switchTextOn) == null){
                ""
            } else {
                typedArr.getString(R.styleable.NSwitch_switchTextOn)!!
            }

            mCutMode = SWITCHMODE.values()[typedArr.getInt(R.styleable.NSwitch_switchMode, 0)]

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

            mLockBg.background = mOffLayout
            mLockImg.background = mOffImage

            mStandardStart.text = if(typedArr.getString(R.styleable.NSwitch_switchBackTextOff) == null){
                ""
            } else {
                typedArr.getString(R.styleable.NSwitch_switchBackTextOff)!!
            }
            mStandardStart.setTextColor(typedArr.getColor(
                R.styleable.NSwitch_switchBackTextColor, ContextCompat.getColor(context, R.color.black)
            ))
            mStandardStart.textSize = typedArr.getDimensionPixelSize(
                R.styleable.NSwitch_switchBackTextSize, 16
            ).toFloat()

            mStandardEnd.text = if(typedArr.getString(R.styleable.NSwitch_switchBackTextOn) == null){
                ""
            } else {
                typedArr.getString(R.styleable.NSwitch_switchBackTextOn)!!
            }
            mStandardEnd.setTextColor(typedArr.getColor(
                R.styleable.NSwitch_switchBackTextColor, ContextCompat.getColor(context, R.color.black)
            ))
            mStandardEnd.textSize = typedArr.getDimensionPixelSize(
                R.styleable.NSwitch_switchBackTextSize, 16
            ).toFloat()

            mLockTxt.text = mOffText
            mLockTxt.setTextColor(typedArr.getColor(
                R.styleable.NSwitch_switchTextColor, ContextCompat.getColor(context, R.color.black)
            ))
            mLockTxt.textSize = typedArr.getDimensionPixelSize(
                R.styleable.NSwitch_switchTextSize, 16
            ).toFloat()

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
                            mLockBg.background = mOffLayout
                            mLockImg.background = mOffImage
                            mLockTxt.text = mOffText
                            if(mBtnOpenClose.translationX.toInt() < xCenter - 20 && mCutMode == SWITCHMODE.CENTER_GONE){
                                blue = (xCenter - mBtnOpenClose.translationX.toInt()).toFloat()
                                mBtnOpenClose.alpha = (blue / xCenter)
                            }
                        } else if(xCenter < mBtnOpenClose.translationX.toInt()){
                            mLockBg.background = mOnLayout
                            mLockImg.background = mOnImage
                            mLockTxt.text = mOnText
                            if(mBtnOpenClose.translationX.toInt() > xCenter + 20 && mCutMode == SWITCHMODE.CENTER_GONE){
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
            mLockBg.background = mOffLayout
            mLockImg.background = mOffImage
            mLockTxt.text = mOffText
        }.start()
        mSwitchEvent?.onOnOff(this, false)
    }

    private fun animationRight() {
        mBtnOpenClose.animate().translationX(xEnd.toFloat()).setDuration(150).withEndAction {
            mBtnOpenClose.alpha = 1f
            mBtnOpenClose.translationX = xEnd.toFloat()
            mLockBg.background = mOnLayout
            mLockImg.background = mOnImage
            mLockTxt.text = mOnText
        }.start()
        mSwitchEvent?.onOnOff(this, true)
    }

    private var mSwitchEvent: SwitchEventListener? = null

    fun setOnSwitchEvent(listener: SwitchEventListener) {
        mSwitchEvent = listener
    }

    interface SwitchEventListener {
        fun onOnOff(view: View, boolean: Boolean)
    }

}