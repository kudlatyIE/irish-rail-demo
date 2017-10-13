package ie.droidfactory.irishrails.utils;

import android.support.v4.app.Fragment;
/**
 * Created by kudlaty on 02/06/2016.
 */
public class PagerChildItem {
	private String itemName;
	private int itemId;
	private Fragment childItem;
	
	public PagerChildItem(int id, String title, Fragment item){
		this.itemId=id;
		this.itemName=title;
		this.childItem=item;
	}

	public String getItemName() {
		return itemName;
	}

	public int getItemId() {
		return itemId;
	}

	public Fragment getChildItem() {
		return childItem;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public void setChildItem(Fragment childItem) {
		this.childItem = childItem;
	}

}
