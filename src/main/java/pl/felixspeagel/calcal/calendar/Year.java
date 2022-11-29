package pl.felixspeagel.calcal.calendar;

import java.util.LinkedList;

public class Year {
	
	private final LinkedList<Month> months;
	public final SpecialFeature feature;
	private int feature_month_index;
	
	public Year(SpecialFeature year_feature) {
		feature = year_feature;
		months = new LinkedList<>();
		feature_month_index = -1;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Year other) {
			return equals( other, true );
		}
		return false;
	}
	public boolean equals(Year other, boolean strict) {
		if( ! feature.equals( other.feature ) )
			return false;
		if( strict && feature_month_index != other.feature_month_index )
			return false;
		
		if( months.size() != other.months.size() )
			return false;
		
		for(int i=0; i<months.size(); i++) {
			if( ! months.get( i ).equals( other.months.get( i ), strict ) )
				return false;
		}
		
		return true;
	}
	
	public void addMonth(String name, int normal_days, int leap_days) {
		months.add( new Month( name, normal_days, leap_days ) );
	}
	public void addMonth(Month month) {
		months.add( month );
	}
	
	public Month getMonth(int index) {
		if( index < 0 || index >= months.size() ) {
			return null;
		} else {
			return months.get( index );
		}
	}
	
	public int getMonthCount() {
		return months.size();
	}
	public int getLastMonthIndex() {
		if( months.isEmpty() ) {
			return -1;
		} else {
			return months.size() - 1;
		}
	}
	
	public void removeMonth(int index) {
		if( index < 0 || index >= months.size() ) {
			return;
		}
		if( feature_month_index == index ) {
			feature_month_index = -1;
		}
		months.remove( index );
	}
	
	public String[] copyMonthNames() {
		var names = new LinkedList<String>();
		for(var month : months) {
			names.add( month.name );
		}
		return names.toArray(new String[0]);
	}
	
	public void setFeatureMonth(int index) {
		if( index < 0 || index >= months.size() ) {
			return;
		}
		feature_month_index = index;
	}
	public boolean isMonthFeature(int index) {
		if( index < 0 || index >= months.size() ) {
			return false;
		}
		return feature_month_index == index;
	}
	public void removeMonthFeature() {
		feature_month_index = -1;
	}
	
	public void overwriteMonthNames(String[] names) {
		int monthIndex = 0;
		int nameIndex = 0;
		
		for( ; nameIndex < names.length ; monthIndex++, nameIndex++) {
			if( months.size() <= monthIndex ) {
				break;
			}
			months.get( monthIndex ).name = names[nameIndex];
		}
	}
	
}
