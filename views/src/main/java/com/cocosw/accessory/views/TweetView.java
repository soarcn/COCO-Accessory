package com.cocosw.accessory.views;

import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TweetView extends TextView {
	public TweetView(final Context context) {
		super(context);
	}

	public TweetView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		final TextView widget = this;
		final Object text = widget.getText();
		if (text instanceof Spanned) {
			Spannable buffer;
			try {
				buffer = (Spannable) text;
			} catch (final ClassCastException cce) {
				cce.printStackTrace();
				return false;
			}

			final int action = event.getAction();

			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				try {
					x -= widget.getTotalPaddingLeft();
					y -= widget.getTotalPaddingTop();
				} catch (final Exception e) {

				}
				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				final Layout layout = widget.getLayout();
				final int line = layout.getLineForVertical(y);
				final int off = layout.getOffsetForHorizontal(line, x);

				final ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}
					return true;
				}
			}

		}

		return false;
	}
}
