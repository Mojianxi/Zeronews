package com.fanxi.zeronews.baidumap;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.model.LatLngBounds;

public abstract class OverlayManager implements OnMarkerClickListener,OnPolylineClickListener{
	BaiduMap mBaiduMap=null;
	private List<OverlayOptions> moOverlayOptionList=null;
	List<Overlay> mOverlayList=null;
	/**
	 * 通过一个BaiduMap对象构造
	 * @param baiduMap
	 */
	public OverlayManager(BaiduMap baiduMap){
		mBaiduMap=baiduMap;
		if(moOverlayOptionList==null){
			moOverlayOptionList=new ArrayList<OverlayOptions>();
		}
		if(mOverlayList==null){
			mOverlayList=new ArrayList<Overlay>();
		}
	}
	/**
	 * 覆写此方法设置要管理的Overlay列表
	 * @return
	 */
	public abstract List<OverlayOptions> getOverlayOptions();
	public final void addTopMap(){
		if(mBaiduMap==null){
			return;
		}
		removeFromMap();
		List<OverlayOptions> overlayOptions=getOverlayOptions();
		if(overlayOptions!=null){
			moOverlayOptionList.addAll(getOverlayOptions());
		}
		for (OverlayOptions option:moOverlayOptionList){
			mOverlayList.add(mBaiduMap.addOverlay(option));
		}
	}
	/**
	 * 将所有Overlay从地图上删除
	 */
	public final void removeFromMap(){
		if(mBaiduMap==null){
			return;
		}
		for (Overlay marker:mOverlayList) {
			marker.remove();
		}
		moOverlayOptionList.clear();
		mOverlayList.clear();
	}
	public void zoomToSpan(){
		if(mBaiduMap==null){
			return;
		}
		if(mOverlayList.size()>0){
			LatLngBounds.Builder builder=new LatLngBounds.Builder();
			for (Overlay overlay:mOverlayList) {
				if(overlay instanceof Marker){
					builder.include(((Marker)overlay).getPosition());
				}
			}
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.build()));
		}
	}
}
