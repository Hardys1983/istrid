package com.istrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

public class BoxedImageView extends ImageView {
	public boolean paintRounds = false;
	Paint p = new Paint();
	Rect rect = new Rect();

	public BoxedImageView(Context context) {
		super(context);
		p.setStrokeWidth(2);
		p.setColor(Color.RED);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		this.getDrawingRect(rect);
		super.onDraw(canvas);
		if( paintRounds ){
			canvas.drawLine(rect.left, rect.top, rect.right, rect.top, p);
 			canvas.drawLine(rect.left, rect.bottom, rect.left, rect.top, p);
			canvas.drawLine(rect.right, rect.top, rect.right, rect.bottom, p);
			canvas.drawLine(rect.left, rect.bottom, rect.right, rect.bottom, p);
		}
	}
}
