package com.bmw.android.indexdata;

import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2204743540787327738L;
	public String text;
	public int page;
	public Result() {

	}
	
	private Result(Parcel in){
		this.text = in.readString();
		this.page = in.readInt();
	}
	
	public static final Parcelable.Creator<Result> CREATOR = new Parcelable.Creator<Result>() {
		public Result createFromParcel(Parcel in) {
			return new Result(in);
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};
	
	public Result(int page, String text) {
		this.text = text;
		this.page = page;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(text);
		out.writeInt(page);
	}

}
