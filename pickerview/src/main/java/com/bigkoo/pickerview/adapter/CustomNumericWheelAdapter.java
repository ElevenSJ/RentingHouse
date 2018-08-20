package com.bigkoo.pickerview.adapter;

import java.util.ArrayList;

/**
 * Numeric Wheel adapter.
 */
public class CustomNumericWheelAdapter implements WheelAdapter {
	ArrayList<Integer> minList;
	public CustomNumericWheelAdapter(ArrayList<Integer> minList) {
		this.minList = minList;
	}

	@Override
	public Object getItem(int index) {
		if (index >= 0 && index < getItemsCount()) {
			return minList.get(index);
		}
		return 0;
	}

	@Override
	public int getItemsCount() {
		return minList.size();
	}

	@Override
	public int indexOf(Object o) {
		return Integer.parseInt(o.toString());
	}
	
	
}
