package com.fanxi.zeronews.baidumap;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.search.poi.PoiResult;

/**
 * 用于显示POI的Overlay
 * 
 * @author Fanxi
 * 
 */
public class PoiOverlay extends OverlayManager {
	private static final int MAX_POI_SIZE = 10;
	private PoiResult mPoiResult = null;

	public PoiOverlay(BaiduMap baiduMap) {
		super(baiduMap);
	}

	public void setData(PoiResult poiResult) {
		this.mPoiResult = poiResult;
	}

	@Override
	public List<OverlayOptions> getOverlayOptions() {
		if (mPoiResult == null || mPoiResult.getAllPoi() == null) {
			return null;
		}
		List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
		int markerSize = 0;
		for (int i = 0; i < mPoiResult.getAllPoi().size()
				&& markerSize < MAX_POI_SIZE; i++) {
			if (mPoiResult.getAllPoi().get(i).location == null) {
				continue;
			}
			markerSize++;
			Bundle bundle = new Bundle();
			bundle.putInt("index", i);
			markerList.add(new MarkerOptions()
					.icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
							+ markerSize + ".png")).extraInfo(bundle)
					.position(mPoiResult.getAllPoi().get(i).location));
		}
		return markerList;
	}
	public boolean onPoiClick(int i){
//		if (mPoiResult.getAllPoi() != null
//              && mPoiResult.getAllPoi().get(i) != null) {
//          Toast.makeText(BMapManager.getInstance().getContext(),
//                  mPoiResult.getAllPoi().get(i).name, Toast.LENGTH_LONG)
//                  .show();
//      }
		return false;
	}
	/**
	 * 获取该PoiOverlay的Poi数据
	 * @return
	 */
	public PoiResult getPoiResult(){
		return mPoiResult;
	}
	@Override
	public boolean onMarkerClick(Marker marker) {
		if(mOverlayList.contains(marker)){
			return false;
		}
		if(marker.getExtraInfo()!=null){
			return onPoiClick(marker.getExtraInfo().getInt("index"));
		}
		return false;
	}

	@Override
	public boolean onPolylineClick(Polyline arg0) {
		return false;
	}
}
