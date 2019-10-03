package kibaan.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintSet
import kibaan.android.AndroidUnique
import kibaan.android.R
import kibaan.android.extension.getStringOrNull
import kibaan.android.framework.OnKeyboardVisibilityListener
import kibaan.android.ios.*
import kibaan.android.util.DeviceUtils

interface UITextFieldDelegate {
    fun textFieldShouldReturn(textField: SmartTextField): Boolean {
        return false
    }

    /**
     * return NO to disallow editing
     */
    fun textFieldShouldBeginEditing(textField: SmartTextField): Boolean {
        return true
    }

    /**
     * became first responder
     */
    fun textFieldDidBeginEditing(textField: SmartTextField) {
    }

    fun textFieldDidEndEditing(textField: SmartTextField) {
    }
}

open class SmartTextField : RoundedConstraintLayout {

    // region -> Constants

    companion object {
        /** 太字のスタイル */
        private const val textStyleBold = 0b01
    }

    // endregion

    // region -> Variables

    private lateinit var leftParentView: RoundedFrameLayout
    lateinit var editText: CustomEditText
    private lateinit var rightParentView: RoundedFrameLayout
    var rightView: View? by didSet(null) {
        rightParentView.visibility = View.VISIBLE
        addChildView(rightParentView, rightView)
    }
    var leftView: View? by didSet(null) {
        leftParentView.visibility = View.VISIBLE
        addChildView(leftParentView, leftView)
    }
    private lateinit var clearButton: SmartButton
    var leftViewWidthPercent: Float by didSet(0.0f) {
        updateConstraintPercentWidth(leftParentView.id, leftViewWidthPercent)
    }
    var isLeftViewWrapContent: Boolean by didSet(false) {
        leftParentView.layoutParams.width = if (isLeftViewWrapContent) ViewGroup.LayoutParams.WRAP_CONTENT else 0
    }
    var rightViewWidthPercent: Float? by didSet(null) {
        updateConstraintPercentWidth(rightParentView.id, rightViewWidthPercent)
    }
    var onTextFieldEditingChanged: ((AppCompatEditText) -> Unit)? = null
    var text: String?
        get() = editText.text.toString()
        set(value) {
            editText.setText(value)
        }
    var delegate: UITextFieldDelegate? = null
    var isSecureTextEntry: Boolean by didSet(false) {
        if (isSecureTextEntry) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        editText.isEnabled = enabled
        alpha = if (enabled) 1.0f else 0.5f
    }

    var placeholder: String
        get() = editText.hint.toString()
        set(value) {
            editText.hint = value
        }

    val isFirstResponder: Boolean
        get() = editText.isFocused

    private val textWatcher = object : TextWatcher {
        var currentLength: Int = 0
        var start: Int = 0
        var editTextCount: Int = 0
        var beforeText: String? = null
        @SuppressLint("SetTextI18n")
        override fun afterTextChanged(s: Editable?) {
            val text = s ?: return
            val maxLength = maxLength ?: return
            if (text.toString().length <= maxLength) return
            var unfixed = false
            text.getSpans(0, s.length, Any::class.java)?.forEach { obj ->
                // UnderlineSpan での判定から getSpanFlags への判定に変更。
                if ((text.getSpanFlags(obj) and Spanned.SPAN_COMPOSING) == Spanned.SPAN_COMPOSING) {
                    unfixed = true
                }
            }
            if (!unfixed) {
                val overCount = text.length - maxLength
                val addTextCount = this.editTextCount - overCount

                // editTextCountよりoverCountが大きくなってしまった場合は無視する
                if (addTextCount < 0) {
                    return
                }

                val first = text.substring(0 until start)
                val middle = text.substring((start) until start + editTextCount).substring(0 until addTextCount)
                val last = text.substring((start + editTextCount) until text.length)
                val selectPosition = this.start + addTextCount
                editText.setText("$first$middle$last")
                editText.setSelection(selectPosition)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            currentLength = s.toString().length
            beforeText = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (before != count || beforeText != s.toString()) {
                onTextFieldEditingChanged?.invoke(editText)
            }
            this.start = start
            this.editTextCount = count
        }
    }
    // endregion

    // region -> IB Variables
    @IBInspectable
    private var maxLength: Int? = null

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setupSmartTextField(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupSmartTextField(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupSmartTextField(context, attrs)
    }

    // endregion

    // region -> Initializer

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    private fun setupSmartTextField(context: Context, attrs: AttributeSet? = null) {
        createViews(context)
        var showClearButton = true

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SmartTextField)
            leftViewWidthPercent = array.getFloat(R.styleable.SmartTextField_left_view_width_percent, 0.0f)
            rightViewWidthPercent = array.getFloat(R.styleable.SmartTextField_right_view_width_percent, 0.0f)
            editText.gravity = array.getInt(R.styleable.SmartTextField_android_gravity, editText.gravity)
            editText.hint = array.getString(R.styleable.SmartTextField_android_hint)

            val textStyle = array.getInt(R.styleable.SmartTextField_android_textStyle, 0)
            val isBold = (0 < textStyle and textStyleBold)
            if (isBold) {
                editText.typeface = Typeface.DEFAULT_BOLD
            }

            // placeholderがあればhintを上書き
            val placeholder = array.getStringOrNull(R.styleable.SmartTextField_placeholder)
            if (placeholder != null) {
                editText.hint = placeholder
            }

            if (array.hasValue(R.styleable.SmartTextField_android_inputType)) {
                editText.inputType = array.getInt(R.styleable.SmartTextField_android_inputType, InputType.TYPE_CLASS_TEXT)
            }
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimensionPixelSize(R.styleable.SmartTextField_android_textSize, editText.textSize.toInt()).toFloat())
            editText.imeOptions = array.getInt(R.styleable.SmartTextField_android_imeOptions, EditorInfo.IME_ACTION_DONE) or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            if (array.hasValue(R.styleable.SmartTextField_android_maxLength)) {
                maxLength = array.getInt(R.styleable.SmartTextField_android_maxLength, 0)
            }
            showClearButton = array.getBoolean(R.styleable.SmartTextField_showClearButton, true)

            array.recycle()
        }
        if (showClearButton) {
            clearButton = SmartButton(context)
            clearButton.backgroundColor = UIColor.clear
            clearButton.setImage(R.drawable.clear, UIControlState.normal)
            clearButton.setOnClickListener {
                editText.text = null
            }
            val layoutParams = FrameLayout.LayoutParams(DeviceUtils.toPx(context, 48), DeviceUtils.toPx(context, 48))
            layoutParams.gravity = Gravity.CENTER_VERTICAL
            clearButton.layoutParams = layoutParams
            rightView = clearButton
        }
        setupEditTextListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEditTextListener() {
        editText.addTextChangedListener(textWatcher)
        editText.setOnEditorActionListener { _, actionId, _ ->
            val isTapEnter = actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
            if (isTapEnter) {
                resignFirstResponder(isTapEnter = true)
            }
            return@setOnEditorActionListener isTapEnter
        }
        editText.onCloseKeyBoard = {
            onCloseKeyBoard()
        }
        editText.setOnTouchListener { _, motionEvent ->
            if (motionEvent?.action == MotionEvent.ACTION_UP && isEnabled) {
                editText.isCursorVisible = true
                return@setOnTouchListener !(delegate?.textFieldShouldBeginEditing(this) ?: isEnabled)
            }
            false
        }
        editText.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    delegate?.textFieldDidBeginEditing(this@SmartTextField)
                } else {
                    delegate?.textFieldDidEndEditing(textField = this@SmartTextField)
                }
            }
        }
    }

    @AndroidUnique
    private fun onCloseKeyBoard(isTapEnter: Boolean = false) {
        if (isTapEnter) {
            delegate?.textFieldShouldReturn(this)
        }
        editText.isCursorVisible = false
        editText.clearFocus()
        delegate?.textFieldDidEndEditing(this)
    }

    // endregion

    // region -> Life cycle

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    // endregion


    // endregion

    // region -> Other

    private fun createViews(context: Context) {
        editText = CustomEditText(context)
        editText.layoutParams = ViewGroup.LayoutParams(0, 0)
        editText.id = context.resources.getIdentifier("smart_text_field_edit_text", "id", context.packageName)
        editText.setTextColor(Color.BLACK)
        editText.setHintTextColor(Color.GRAY)
        editText.setBackgroundColor(Color.TRANSPARENT)
        editText.setPadding(0, 0, 0, 0)
        editText.setSingleLine(true)
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        addView(editText)

        leftParentView = RoundedFrameLayout(context)
        leftParentView.layoutParams = LayoutParams(0, 0)
        leftParentView.id = context.resources.getIdentifier("smart_text_field_left_parent", "id", context.packageName)
        addView(leftParentView)
        leftParentView.visibility = View.GONE

        rightParentView = RoundedFrameLayout(context)
        rightParentView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, 0)
        rightParentView.id = context.resources.getIdentifier("smart_text_field_right_parent", "id", context.packageName)
        addView(rightParentView)
        leftParentView.visibility = View.GONE

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        // Setting constraint for leftView
        constraintSet.connect(leftParentView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(leftParentView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(leftParentView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
        constraintSet.constrainPercentWidth(leftParentView.id, leftViewWidthPercent)

        // Setting constraint for editText
        constraintSet.connect(editText.id, ConstraintSet.LEFT, leftParentView.id, ConstraintSet.RIGHT)
        constraintSet.connect(editText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(editText.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(editText.id, ConstraintSet.RIGHT, rightParentView.id, ConstraintSet.LEFT)

        // Setting constraint for rightView
        constraintSet.connect(rightParentView.id, ConstraintSet.LEFT, editText.id, ConstraintSet.RIGHT)
        constraintSet.connect(rightParentView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(rightParentView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(rightParentView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        rightViewWidthPercent?.let { percent ->
            constraintSet.constrainPercentWidth(rightParentView.id, percent)
        }
        constraintSet.applyTo(this)
    }

    @Suppress("NAME_SHADOWING")
    private fun updateConstraintPercentWidth(targetId: Int, value: Float?) {
        value?.let { value ->
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.constrainPercentWidth(targetId, value)
            constraintSet.applyTo(this)
        }
    }

    private fun addChildView(targetView: ViewGroup, childView: View?) {
        targetView.removeAllViews()
        childView?.let {
            targetView.addView(it)
        }
    }

    // TODO:判定無しでキーボードを閉じているため、他のTextFieldで表示したキーボードも閉じてしまう問題を抱えている
    fun resignFirstResponder(isTapEnter: Boolean = false) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        onCloseKeyBoard(isTapEnter = isTapEnter)
    }

    fun becomeFirstResponder() {
        editText.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        editText.setSelection(editText.text?.length ?: 0)
        editText.isCursorVisible = true
    }

    @AndroidUnique
    fun clear() {
        editText.removeTextChangedListener(textWatcher)
        editText.text = null
        editText.addTextChangedListener(textWatcher)
    }

    // endregion

    // region -> Inner Class

    /**
     * カスタムのテキスト入力クラス
     */
    class CustomEditText : AppCompatEditText, OnKeyboardVisibilityListener {

        // region -> Constructor

        constructor(context: Context) : super(context)

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

        // endregion

        // region -> Variables

        var onCloseKeyBoard: (() -> Unit)? = null

        // endregion

        // region -> OnKeyboardVisibilityListener

        override fun onKeyboardVisibilityChanged(isVisible: Boolean) {
            if (!isVisible) {
                onCloseKeyBoard?.invoke()
            }
        }

        // endregion
    }

    // endregion
}